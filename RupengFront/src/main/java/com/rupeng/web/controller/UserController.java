package com.rupeng.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.Card;
import com.rupeng.pojo.ClassesUser;
import com.rupeng.pojo.Question;
import com.rupeng.pojo.User;
import com.rupeng.service.ClassesUserService;
import com.rupeng.service.QuestionService;
import com.rupeng.service.UserCardService;
import com.rupeng.service.UserService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;
import com.rupeng.util.EmailUtils;
import com.rupeng.util.ImageCodeUtils;
import com.rupeng.util.SMSUtils;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserCardService userCardService;

    @Autowired
    private ClassesUserService classesUserService;

    @Autowired
    private QuestionService questionService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //参数true表示允许日期为空（null、""）
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/register.do", method = RequestMethod.GET)
    public ModelAndView registerPage() {

        return new ModelAndView("user/register");

    }

    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    public ModelAndView registerSubmit(String email, String password, String emailCode, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("user/register");
        //检查邮箱
        if (!CommonUtils.isEmail(email)) {
            request.setAttribute("message", "邮箱格式不正确");
            return modelAndView;
        }
        //检查密码长度
        if (!CommonUtils.isLengthEnough(password, 6)) {
            request.setAttribute("message", "密码至少6位");
            return modelAndView;
        }
        //检查邮箱验证码
        int result = EmailUtils.checkEmailCode(request.getSession(), email, emailCode);
        if (result == EmailUtils.CHECK_RESULT_FLASE) {
            request.setAttribute("message", "验证码错误");
            return modelAndView;
        } else if (result == EmailUtils.CHECK_RESULT_INVALID) {
            request.setAttribute("message", "验证码已失效，请重新点击发送验证码");
            return modelAndView;
        }

        //邮箱唯一性检查
        User user = new User();
        user.setEmail(email);
        if (userService.isExisted(user)) {
            request.setAttribute("message", "此邮箱已被注册");
            return modelAndView;
        }

        //执行注册逻辑
        user.setPasswordSalt(UUID.randomUUID().toString());
        user.setPassword(CommonUtils.calculateMD5(user.getPasswordSalt() + password));
        user.setCreateTime(new Date());
        userService.insert(user);

        modelAndView.setViewName("user/registerSuccess");
        return modelAndView;
    }

    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    public ModelAndView loginPage() {

        return new ModelAndView("user/login");

    }

    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public ModelAndView loginSubmit(String loginName, String password, String imageCode, HttpServletRequest request, HttpServletResponse response) {

        ModelAndView modelAndView = new ModelAndView("user/login");

        //检查验证码
        if (!ImageCodeUtils.checkImageCode(request.getSession(), imageCode)) {
            request.setAttribute("message", "验证码不正确");
            return modelAndView;
        }
        //检查登录名是否是邮箱或者手机号
        if (!CommonUtils.isEmail(loginName) && !CommonUtils.isPhone(loginName)) {
            request.setAttribute("message", "请输入正确的邮箱或者手机号");
            return modelAndView;
        }

        //检查密码
        if (!CommonUtils.isLengthEnough(password, 6)) {
            request.setAttribute("message", "密码至少6位");
            return modelAndView;
        }

        User user = userService.login(loginName, password);
        //登录成功
        if (user == null) {
            request.setAttribute("message", "登录名或者密码错误");
            return modelAndView;
        }

        request.getSession().setAttribute("user", user);

        //添加cookie，方便自动登录
        int maxAge = 60 * 60 * 24 * 14;//14天

        Cookie loginNameCookie = new Cookie("loginName", loginName);
        loginNameCookie.setMaxAge(maxAge);
        loginNameCookie.setPath("/");
        response.addCookie(loginNameCookie);

        Cookie passwordCookie = new Cookie("password", user.getPassword());
        passwordCookie.setMaxAge(maxAge);
        passwordCookie.setPath("/");
        response.addCookie(passwordCookie);

        //重定向到首页
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @RequestMapping("/logout.do")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();

        //删除cookie，取消自动登录
        int maxAge = 0;//立即删除

        Cookie loginNameCookie = new Cookie("loginName", "");
        loginNameCookie.setMaxAge(maxAge);
        loginNameCookie.setPath("/");
        response.addCookie(loginNameCookie);

        Cookie passwordCookie = new Cookie("password", "");
        passwordCookie.setMaxAge(maxAge);
        passwordCookie.setPath("/");
        response.addCookie(passwordCookie);

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/passwordRetrieve.do", method = RequestMethod.GET)
    public ModelAndView passwordRetrievePage(HttpServletRequest request) {
        return new ModelAndView("user/passwordRetrieve");
    }

    @RequestMapping(value = "/passwordRetrieve.do", method = RequestMethod.POST)
    public ModelAndView passwordRetrieveSubmit(String email, String password, String emailCode, HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("user/passwordRetrieve");
        //检查邮箱
        if (!CommonUtils.isEmail(email)) {
            request.setAttribute("message", "邮箱格式不正确");
            return modelAndView;
        }
        //检查密码长度
        if (!CommonUtils.isLengthEnough(password, 6)) {
            request.setAttribute("message", "密码至少6位");
            return modelAndView;
        }
        //检查邮箱验证码
        int result = EmailUtils.checkEmailCode(request.getSession(), email, emailCode);
        if (result == EmailUtils.CHECK_RESULT_FLASE) {
            request.setAttribute("message", "验证码错误");
            return modelAndView;
        } else if (result == EmailUtils.CHECK_RESULT_INVALID) {
            request.setAttribute("message", "验证码已失效，请重新点击发送验证码");
            return modelAndView;
        }

        //修改密码
        User user = new User();
        user.setEmail(email);

        user = userService.selectOne(user);
        if (user == null) {
            request.setAttribute("message", "此邮箱没有被注册");
            return modelAndView;
        }
        user.setPassword(CommonUtils.calculateMD5(user.getPasswordSalt() + password));
        userService.update(user);

        modelAndView.setViewName("user/passwordRetrieveSuccess");
        return modelAndView;
    }

    //用户个人信息页面
    @RequestMapping(value = "/userInfo.do")
    public ModelAndView userInfo(HttpServletRequest request) {
        return new ModelAndView("user/userInfo");
    }

    //修改用户个人信息
    @RequestMapping(value = "/update.do")
    public @ResponseBody AjaxResult update(User user, HttpServletRequest request) {

        //从session中拿到当前登录用户的userId
        User loginUser = (User) request.getSession().getAttribute("user");
        //这里只可以修改性别、姓名、学校
        User oldUser = userService.selectOne(loginUser.getId());
        oldUser.setIsMale(user.getIsMale());
        oldUser.setName(user.getName());
        oldUser.setSchool(user.getSchool());

        userService.update(oldUser);
        //更新session中的user
        request.getSession().setAttribute("user", oldUser);

        return AjaxResult.successInstance("修改成功");
    }

    //验证手机号
    @RequestMapping(value = "/bindPhone.do")
    public @ResponseBody AjaxResult bindPhone(String phone, String smsCode, HttpServletRequest request) {

        //检查手机号格式
        if (!CommonUtils.isPhone(phone)) {
            return AjaxResult.errorInstance("手机号格式不正确");
        }

        //检查手机号验证码
        int result = SMSUtils.checkSMSCode(request.getSession(), phone, smsCode);
        if (result == SMSUtils.CHECK_RESULT_FLASE) {
            return AjaxResult.errorInstance("验证码错误");
        } else if (result == SMSUtils.CHECK_RESULT_INVALID) {
            return AjaxResult.errorInstance("验证码已失效，请重新点击发送");
        }

        //从session中拿userId
        User loginUser = (User) request.getSession().getAttribute("user");
        //这里只可以修改性别、姓名、学校
        User user = userService.selectOne(loginUser.getId());
        user.setPhone(phone);

        userService.update(user);

        //更新session中的user
        request.getSession().setAttribute("user", user);

        return AjaxResult.successInstance("绑定手机号成功");
    }

    //修改密码页面
    @RequestMapping(value = "/passwordUpdate.do", method = RequestMethod.GET)
    public ModelAndView passwordUpdatePage(HttpServletRequest request) {
        return new ModelAndView("user/passwordUpdate");
    }

    //修改密码
    @RequestMapping(value = "/passwordUpdate.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult passwordUpdateSubmit(String password, String newpassword, HttpServletRequest request) {

        //检查密码长度
        if (!CommonUtils.isLengthEnough(password, 6)) {
            return AjaxResult.errorInstance("原密码长度至少6位");
        }
        if (!CommonUtils.isLengthEnough(newpassword, 6)) {
            return AjaxResult.errorInstance("新密码长度至少6位");
        }
        //检查原密码是否正确
        //从session中取出user，使用其id查询数据库，可避免session中的user信息不是最新的情况，当然如果可以保证session中的user信息总是和数据库中的一致，也可以使用session中的user
        User user = (User) request.getSession().getAttribute("user");
        user = userService.selectOne(user.getId());
        if (!CommonUtils.calculateMD5(user.getPasswordSalt() + password).equalsIgnoreCase(user.getPassword())) {
            return AjaxResult.errorInstance("原密码错误");
        }
        user.setPassword(CommonUtils.calculateMD5(user.getPasswordSalt() + newpassword));

        userService.update(user);

        //更新session中的user
        request.getSession().setAttribute("user", user);

        return AjaxResult.successInstance("修改密码成功");
    }

    @RequestMapping("/teacherHome.do")
    public ModelAndView teacherHome(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");

        if (!user.getIsTeacher()) {
            return new ModelAndView("message", "message", "您还不是老师");
        }
        ModelAndView modelAndView = new ModelAndView("user/teacherHome");

        //查询自己所在班级的所有的学生提问的还没有解决的问题
        List<Question> questionList = questionService.selectUnresolvedQuestionByTeacherId(user.getId());

        modelAndView.setViewName("user/teacherHome");
        modelAndView.addObject("questionList", questionList);

        return modelAndView;
    }

    @RequestMapping("/studentHome.do")
    public ModelAndView studentHome(HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");

        //如果没有加入班级则进不来
        ClassesUser classesUser = new ClassesUser();
        classesUser.setUserId(user.getId());
        List<ClassesUser> classesUserList = classesUserService.selectList(classesUser);
        if (CommonUtils.isEmpty(classesUserList)) {
            ModelAndView modelAndView = new ModelAndView("message");
            modelAndView.addObject("message", "您还没有加入班级");
            return modelAndView;
        }

        ModelAndView modelAndView = new ModelAndView("user/studentHome");
        //学生的学习卡信息可以直接查询出来显示出来
        List<Card> cardList = userCardService.selectSecondListByFirstId(user.getId());

        //查询参与的（提问的和回复的）还未解决的问题
        List<Question> questionList = questionService.selectUnresolvedQuestionByStudentId(user.getId());
        //去重
        if (questionList != null) {
            Set<Question> set = new LinkedHashSet<>(questionList);
            questionList = new ArrayList<Question>(set);
        }
        modelAndView.addObject("cardList", cardList);
        modelAndView.addObject("questionList", questionList);

        return modelAndView;

    }
}
