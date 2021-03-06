package com.rupeng.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.rupeng.annotation.RupengClearCache;
import com.rupeng.annotation.RupengUseCache;
import com.rupeng.mapper.IManyToManyMapper;

public class ManyToManyBaseService<T, F, S> extends BaseService<T> {

    @Autowired
    private IManyToManyMapper<T, F, S> manyToManyMapper;

    @RupengClearCache
    public int deleteByFirstId(long firstId) {
        return manyToManyMapper.deleteByFirstId(firstId);
    }

    @RupengClearCache
    public int deleteBySecondId(long secondId) {
        return manyToManyMapper.deleteBySecondId(secondId);
    }

    @RupengUseCache
    public List<F> selectFirstListBySecondId(Long secondId) {
        return manyToManyMapper.selectFirstListBySecondId(secondId);
    }

    @RupengUseCache
    public F selectFirstOneBySecondId(Long secondId) {
        List<F> list = manyToManyMapper.selectFirstListBySecondId(secondId);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    @RupengUseCache
    public List<S> selectSecondListByFirstId(Long firstId) {
        return manyToManyMapper.selectSecondListByFirstId(firstId);
    }

    @RupengUseCache
    public S selectSecondOneByFirstId(Long firstId) {
        List<S> list = manyToManyMapper.selectSecondListByFirstId(firstId);
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    @SuppressWarnings("all")
    @RupengClearCache
    public void updateFirst(long firstId, Long[] secondIds) {
        try {
            ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            //泛型类的Class对象
            Type[] genericTypes = type.getActualTypeArguments();

            Class tClass = (Class) genericTypes[0];//T
            Class fClass = (Class) genericTypes[1];//F
            Class sClass = (Class) genericTypes[2];//S

            //通过反射创建泛型T的对象
            T t = (T) tClass.newInstance();
            //获取并调用T的set(First)Id方法
            tClass.getDeclaredMethod("set" + fClass.getSimpleName() + "Id", Long.class).invoke(t, firstId);

            //查询旧的关联关系
            List<T> oldTList = selectList(t);

            //把数组中的数据转移到list中（方便下面的操作），并且把无效数据（secondId==null）剔除
            List<Long> secondIdList = new LinkedList<>();
            if (secondIds != null) {
                for (Long secondId : secondIds) {
                    if (secondId != null) {
                        secondIdList.add(secondId);
                    }
                }
            }

            //2 把没有变化的从secondIdList中清理掉
            if (oldTList != null) {
                for (T tempT : oldTList) {
                    //拿到tempT对象的secondId
                    Long tempSecondId = (Long) tClass.getDeclaredMethod("get" + sClass.getSimpleName() + "Id").invoke(tempT);
                    if (!secondIdList.remove(tempSecondId)) {
                        delete((Long) tClass.getDeclaredMethod("getId").invoke(tempT));
                    }
                }
            }
            //3 把secondIdList中剩余的添加到数据库
            for (Long secondId : secondIdList) {
                tClass.getDeclaredMethod("set" + sClass.getSimpleName() + "Id", Long.class).invoke(t, secondId);
                insert(t);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("all")
    @RupengClearCache
    public void updateSecond(long secondId, Long[] firstIds) {
        try {
            ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            //泛型类的Class对象
            Type[] genericTypes = type.getActualTypeArguments();

            Class tClass = (Class) genericTypes[0];//T
            Class fClass = (Class) genericTypes[1];//F
            Class sClass = (Class) genericTypes[2];//S

            //通过反射创建泛型T的对象
            T t = (T) tClass.newInstance();
            //获取并调用T的set(Second)Id方法
            tClass.getDeclaredMethod("set" + sClass.getSimpleName() + "Id", Long.class).invoke(t, secondId);

            //查询旧的关联关系
            List<T> oldTList = selectList(t);

            //把数组中的数据转移到list中（方便下面的操作），并且把无效数据（firstId==null）剔除
            List<Long> firstIdList = new LinkedList<>();
            if (firstIds != null) {
                for (Long firstId : firstIds) {
                    if (firstId != null) {
                        firstIdList.add(firstId);
                    }
                }
            }

            //2 把没有变化的从firstIdList中清理掉
            if (oldTList != null) {
                for (T tempT : oldTList) {
                    //拿到tempT对象的firstId
                    Long tempFirstId = (Long) tClass.getDeclaredMethod("get" + fClass.getSimpleName() + "Id").invoke(tempT);
                    if (!firstIdList.remove(tempFirstId)) {
                        delete((Long) tClass.getDeclaredMethod("getId").invoke(tempT));
                    }
                }
            }
            //3 把firstIdList中剩余的添加到数据库
            for (Long firstId : firstIdList) {
                tClass.getDeclaredMethod("set" + fClass.getSimpleName() + "Id", Long.class).invoke(t, firstId);
                insert(t);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
