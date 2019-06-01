package com.rupeng.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.rupeng.pojo.AdminUser;
import com.rupeng.service.AdminUserRoleService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;
import com.rupeng.util.JsonUtils;

public class PermissionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AdminUserRoleService adminUserRoleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AdminUser adminUser = (AdminUser) request.getSession().getAttribute("adminUser");
        //如果用户还没有登录，让用户去登录
        if (adminUser == null) {
            //返回json格式的权限不足信息
            if (CommonUtils.isEmpty(request.getHeader("x-requested-with"))) {
                response.getWriter().print("需要重新登录");
            } else {
                response.getWriter().print(JsonUtils.toJson(AjaxResult.errorInstance("需要重新登录")));
            }
            return false;
        }
        return true;
        /* 为了方便开发、测试，先把权限检查注释掉
        //请求路径
        String servletPath = request.getServletPath();
        //检查权限
        boolean result = adminUserRoleService.checkPermission(adminUser.getId(), servletPath);
        if (result) {
            return true;
        } else {
            //返回json格式的权限不足信息
            if (CommonUtils.isEmpty(request.getHeader("x-requested-with"))) {
                response.getWriter().print("权限不足");
            } else {
                response.getWriter().print(JsonUtils.toJson(AjaxResult.errorInstance("权限不足")));
            }
            return false;
        }
        */
    }
}
