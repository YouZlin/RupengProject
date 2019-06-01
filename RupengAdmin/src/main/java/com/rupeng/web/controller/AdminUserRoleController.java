package com.rupeng.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.AdminUserRole;
import com.rupeng.service.AdminUserRoleService;
import com.rupeng.service.RoleService;
import com.rupeng.util.AjaxResult;

@Controller
@RequestMapping(value = "/adminUserRole")
public class AdminUserRoleController {

    @Autowired
    private AdminUserRoleService adminUserRoleService;

    @Autowired
    private RoleService roleService;

    /**
     * 数据量不会很多，不需要分页
     * 操作AdminRole和AdminUser的多对多关联关系时，一般都是以adminUserId去操作关联的AdminRole
     */
    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ModelAndView updatePage(Long adminUserId) {

        ModelAndView modelAndView = new ModelAndView("adminUserRole/update");
        //查询出所有AdminRole
        modelAndView.addObject("roleList", roleService.selectList());

        //查询出adminUserId关联的管理数据
        AdminUserRole adminUserRole = new AdminUserRole();
        adminUserRole.setAdminUserId(adminUserId);
        modelAndView.addObject("adminUserRoleList", adminUserRoleService.selectList(adminUserRole));

        //把adminUserId也带上
        modelAndView.addObject("adminUserId", adminUserId);
        return modelAndView;
    }

    /**
     * 分配权限
     */
    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult updateSubmit(Long adminUserId, Long[] roleIds) {

        adminUserRoleService.updateFirst(adminUserId, roleIds);

        return AjaxResult.successInstance("分配角色成功！");

    }
}
