package com.rupeng.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.RolePermission;
import com.rupeng.service.PermissionService;
import com.rupeng.service.RolePermissionService;
import com.rupeng.util.AjaxResult;

@Controller
@RequestMapping(value = "/rolePermission")
public class RolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 数据量不会很多，不需要分页
     * 操作Permission和AdminRole的多对多关联关系时，一般都是以roleId去操作关联的Permission
     */
    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ModelAndView updatePage(Long roleId) {

        ModelAndView modelAndView = new ModelAndView("rolePermission/update");
        //查询出所有Permission
        modelAndView.addObject("permissionList", permissionService.selectList());

        //查询出roleId关联的管理数据
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        modelAndView.addObject("rolePermissionList", rolePermissionService.selectList(rolePermission));

        //把roleId也带上
        modelAndView.addObject("roleId", roleId);
        return modelAndView;
    }

    /**
     * 分配权限
     */
    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult updateSubmit(Long roleId, Long[] permissionIds) {

        rolePermissionService.updateFirst(roleId, permissionIds);

        return AjaxResult.successInstance("分配权限成功！");

    }
}
