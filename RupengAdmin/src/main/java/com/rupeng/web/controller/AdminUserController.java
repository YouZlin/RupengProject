package com.rupeng.web.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.AdminUser;
import com.rupeng.service.AdminUserService;
import com.rupeng.service.RoleService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;
import com.rupeng.util.ImageCodeUtils;
import com.rupeng.util.JedisUtils;

@Controller
@RequestMapping(value = "/adminUser")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/add.do", method = RequestMethod.GET)
    public ModelAndView addPage() {

        ModelAndView modelAndView = new ModelAndView("adminUser/add");
        modelAndView.addObject("roleList", roleService.selectList());

        return modelAndView;

    }

    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult addSubmit(String account, String password, Long[] roleIds) {

        AdminUser adminUser = new AdminUser();
        adminUser.setAccount(account);

        if (adminUserService.isExisted(adminUser)) {
            return AjaxResult.errorInstance("此账号已存在");
        }
        if (!CommonUtils.isLengthEnough(password, 6)) {
            return AjaxResult.errorInstance("密码长度至少6位");
        }

        adminUser.setPasswordSalt(UUID.randomUUID().toString());
        adminUser.setPassword(CommonUtils.calculateMD5(adminUser.getPasswordSalt() + password));

        adminUserService.insert(adminUser, roleIds);

        return AjaxResult.successInstance("添加成功！");

    }

    @RequestMapping(value = "/resetPassword.do", method = RequestMethod.GET)
    public ModelAndView resetPasswordPage(Long id) {

        ModelAndView modelAndView = new ModelAndView("adminUser/resetPassword");
        modelAndView.addObject("adminUser", adminUserService.selectOne(id));

        return modelAndView;
    }

    @RequestMapping(value = "/resetPassword.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult resetPasswordSubmit(long id, String password) {

        if (!CommonUtils.isLengthEnough(password, 6)) {
            return AjaxResult.errorInstance("新密码长度至少6位");
        }

        //把旧数据查出来
        AdminUser adminUser = adminUserService.selectOne(id);

        adminUser.setPassword(CommonUtils.calculateMD5(adminUser.getPasswordSalt() + password));
        adminUserService.update(adminUser);

        return AjaxResult.successInstance("重置密码成功！");
    }

    /**
     * 禁用、启用账号
     */
    @RequestMapping(value = "/toggleDisable.do")
    public @ResponseBody AjaxResult toggleDisable(Long id) {

        AdminUser adminUser = adminUserService.selectOne(id);
        adminUser.setIsDisabled(!adminUser.getIsDisabled());

        adminUserService.update(adminUser);

        return AjaxResult.successInstance(adminUser.getIsDisabled() ? "禁用成功！" : "启用成功！");

    }

    @RequestMapping(value = "/delete.do")
    public @ResponseBody AjaxResult delete(Long id) {

        adminUserService.delete(id);
        return AjaxResult.successInstance("删除成功！");

    }

    /**
     * 数据量不会很多，不需要分页
     */
    @RequestMapping("/list.do")
    public ModelAndView list(AdminUser adminUser) {

        ModelAndView modelAndView = new ModelAndView("adminUser/list");
        modelAndView.addObject("adminUserList", adminUserService.selectList(adminUser));

        return modelAndView;
    }

    /**
     * 管理员登录
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    public ModelAndView loginPage() {

        ModelAndView modelAndView = new ModelAndView("adminUser/login");

        return modelAndView;
    }

    /**
     * 管理员登录
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public ModelAndView loginSubmit(String account, String password, String imageCode, HttpServletRequest request, HttpServletResponse response) {

        ModelAndView modelAndView = new ModelAndView("redirect:/");
        //验证码检查
        if (!ImageCodeUtils.checkImageCode(request.getSession(), imageCode)) {
            modelAndView.setViewName("adminUser/login");
            modelAndView.addObject("message", "验证码错误");
            return modelAndView;
        }

        //尝试登录
        AdminUser adminUser = adminUserService.login(account, password);

        if (adminUser == null) {
            modelAndView.setViewName("adminUser/login");
            modelAndView.addObject("message", "账号或者密码错误");
            return modelAndView;
        }
        //检查账号是否被禁用
        if (adminUser.getIsDisabled()) {
            modelAndView.setViewName("adminUser/login");
            modelAndView.addObject("message", "此账号已被禁用！");
            return modelAndView;
        }

        //登录成功
        request.getSession().setAttribute("adminUser", adminUser);

        return modelAndView;
    }

    /**
     * 退出，销毁session
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/logout.do")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {

        //把保持在线所用的redis数据删掉
        JedisUtils.del("keepOnline_" + request.getSession().getId());

        request.getSession().invalidate();//让当前session失效

        return new ModelAndView("redirect:/");

    }

    @RequestMapping(value = "/updatePassword.do", method = RequestMethod.GET)
    public ModelAndView updatePasswordPage() {

        return new ModelAndView("adminUser/updatePassword");
    }

    @RequestMapping(value = "/updatePassword.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult updatePasswordSubmit(String password, String newpassword, String renewpassword, HttpServletRequest request) {
        if (newpassword == null || newpassword.length() < 6) {
            return AjaxResult.errorInstance("新密码长度至少6位！");
        }
        if (!newpassword.equals(renewpassword)) {
            return AjaxResult.errorInstance("两次输入密码不一致！");
        }
        AdminUser adminUser = (AdminUser) request.getSession().getAttribute("adminUser");

        if (!adminUser.getPassword().equalsIgnoreCase(CommonUtils.calculateMD5(adminUser.getPasswordSalt() + password))) {
            return AjaxResult.errorInstance("旧密码不正确！");
        }
        adminUser.setPassword(CommonUtils.calculateMD5(adminUser.getPasswordSalt() + newpassword));
        adminUserService.update(adminUser);
        return AjaxResult.successInstance("修改密码成功！");
    }
}
