package com.rupeng.web.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import com.rupeng.pojo.AdminUser;
import com.rupeng.service.AdminUserService;
import com.rupeng.util.CommonUtils;
import com.rupeng.util.JedisUtils;

public class KeepOnlineInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AdminUserService adminUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AdminUser adminUser = (AdminUser) request.getSession().getAttribute("adminUser");

        if (adminUser == null) {
            Cookie sessionIdCookie = WebUtils.getCookie(request, "JSESSIONID");
            if (sessionIdCookie == null) {
                return true;
            }
            String oldSessionId = sessionIdCookie.getValue();
            String adminUserId = JedisUtils.get("keepOnline_" + oldSessionId);
            if (CommonUtils.isEmpty(adminUserId)) {
                return true;
            }
            adminUser = adminUserService.selectOne(Long.parseLong(adminUserId));
            request.getSession().setAttribute("adminUser", adminUser);
        }

        if (adminUser != null) {
            JedisUtils.setex("keepOnline_" + request.getSession().getId(), 60 * 60 * 24, adminUser.getId() + "");
        }
        return true;
    }
}
