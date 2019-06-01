package com.rupeng.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.util.ImageCodeUtils;

@Controller
public class OtherController {

    /**
     * 当访问首页时，如果用户没有登录，就重定向到/adminUser/login.do
     * @param request
     * @return
     */
    @RequestMapping("/")
    public ModelAndView index(HttpServletRequest request) {
        if (request.getSession().getAttribute("adminUser") == null) {
            return new ModelAndView("redirect:/adminUser/login.do");
        }
        return new ModelAndView("index");
    }

    /**
     * 图片验证码
     * @param request
     * @param response
     */
    @RequestMapping("/imageCode.do")
    public void imageCode(HttpServletRequest request, HttpServletResponse response) {
        ImageCodeUtils.sendImageCode(request.getSession(), response);
    }

    @RequestMapping("/welcome.do")
    public ModelAndView welcome(HttpServletRequest request) {
        return new ModelAndView("welcome");
    }

}
