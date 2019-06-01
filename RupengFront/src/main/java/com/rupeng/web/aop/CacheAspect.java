package com.rupeng.web.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.rupeng.annotation.RupengCacheable;
import com.rupeng.util.CommonUtils;
import com.rupeng.util.JedisUtils;
import com.rupeng.util.JsonUtils;
import com.rupeng.util.ReflectUtils;

/**
 * redis缓存结构：
 *  key的格式为 cache_全类名_方法签名_方法参数值列表，这样设计的目的是删除时可以使用 cache_全类名_* 删掉此类相关的所有缓存
 *  value是json格式的字符串数据
 *  同时指定key的过期时间
 *
 *  适合缓存的数据的特点（并不是绝对的）：
 *  1 很少更新的数据，如Card、Classes等
 *  2 经常被使用的数据
 *  3 数据量不大的数据（相反的例子是User，由于User的数据很多，不适合缓存）
 *
 *  此缓存类的特点：
 *  1 只有使用@RupengCacheable标注的类才支持缓存（作用是可灵活控制哪些service类需要缓存功能）
 *  2 只有使用@RupengUseCache标注的方法才支持查询缓存和存入缓存（作用是可灵活控制哪些方法可以从缓存中取得数据，以及把数据存入缓存）
 *  3 只有使用@RupengClearCache标注的方法才支持清空缓存（作用是可灵活控制哪些方法执行后会清空缓存）
 *  这三个注解配合使用即可满足我们对缓存的绝大部分要求
 *
 *   使用原则：
 *   1 只要是会更新数据的操作都要清理缓存
 *   2 由于表之间的关联关系，相关联的表进行更新操作时，就可能造成缓存中的数据和数据库中不一致，这时可以通过给缓存数据设置过期时间来解决。至于过期时间的长短由数据不一致可接受时间决定，比如60秒、10秒等，可以在@RupengCacheable中设置
 *   3 由于需要支持过期时间，就需要每条缓存使用一个独立的key，为了更新操作时方便删除一个表的全部缓存，就需要保证同一个类的缓存的key的前缀一致，比如cache_全类名_方法签名_方法参数值
 *   4 查询方法的返回值不符合json格式的不能使用缓存，如public Map<Chapter, List<Segment>> selectAllCourse(Long cardId)
 *
 *
 *  注意：同一个类中的一个方法调用另一个方法时，另一个方法不会被增强（对于查询方法，没被增强也没关系，但对于更新方法，务必要在调用者方法（不是被调用的方法）上使用@RupengClearCache）
 *
 */
@Aspect
public class CacheAspect {

    @Pointcut("@target(com.rupeng.annotation.RupengCacheable)")
    private void cacheable() {

    }

    @Pointcut("@annotation(com.rupeng.annotation.RupengUseCache)")
    private void useCache() {

    }

    @Pointcut("@annotation(com.rupeng.annotation.RupengClearCache)")
    private void clearCache() {

    }

    @SuppressWarnings("all")
    @Around("cacheable() && useCache()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        //目标类Class对象
        Class targetClass = joinPoint.getTarget().getClass();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        String key = createKey(joinPoint);
        String value = JedisUtils.get(key);//从缓存中取数据

        //如果缓存数据不为空，则直接使用缓存数据
        if (!CommonUtils.isEmpty(value)) {

            //方法返回值类型
            Class returnType = ReflectUtils.getActualReturnType(targetClass, method);
            //方法返回值类型的泛型类型
            Class[] parametricTypes = ReflectUtils.getActualParametricTypeOfReturnType(targetClass, method);

            return JsonUtils.toBean(value, returnType, parametricTypes);

        } else {
            //如果执行到这里就说明缓存里面没有数据，则执行正常逻辑（从数据库中查询数据）
            Object returnObject = joinPoint.proceed();
            RupengCacheable cacheable = (RupengCacheable) targetClass.getAnnotation(RupengCacheable.class);
            //把数据存入缓存
            JedisUtils.setex(key, cacheable.expire(), JsonUtils.toJson(returnObject));
            return returnObject;
        }
    }

    @After("cacheable() && clearCache()")
    public void after(JoinPoint joinPoint) {
        //清空和此key前缀匹配的所有缓存
        JedisUtils.del(createKeyPatternForDelete(joinPoint));
    }

    //生成要删除的redis key的正则表达式
    private String createKeyPatternForDelete(JoinPoint joinPoint) {
        StringBuilder keyPattern = new StringBuilder();
        keyPattern.append("cache_").append(joinPoint.getTarget().getClass().getName()).append("_*");
        return keyPattern.toString();
    }

    private String createKey(JoinPoint joinPoint) {
        StringBuilder key = new StringBuilder();//redis缓存key的前缀

        key.append("cache_").append(joinPoint.getTarget().getClass().getName()).append("_");

        String signature = joinPoint.getSignature().toString();
        signature = signature.substring(signature.lastIndexOf('.') + 1);

        key.append(signature).append("_");

        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {

            String argsStr = JsonUtils.toJson(args);

            //把:替换为_，避免生成命名空间
            argsStr = argsStr.replaceAll(":", "_");
            key.append(argsStr);
        }
        return key.toString();
    }

}
