package com.rupeng.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.Permission;
import com.rupeng.service.PermissionService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;

@Controller
@RequestMapping(value = "/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "/add.do", method = RequestMethod.GET)
    public ModelAndView addPage() {

        ModelAndView modelAndView = new ModelAndView("permission/add");

        return modelAndView;
    }

    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult addSubmit(Permission permission) {

        if (CommonUtils.isEmpty(permission.getPath())) {
            return AjaxResult.errorInstance("权限路径不能为空");
        }

        permissionService.insert(permission);
        return AjaxResult.successInstance("添加成功！");

    }

    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ModelAndView updatePage(Long id) {

        ModelAndView modelAndView = new ModelAndView("permission/update");
        modelAndView.addObject("permission", permissionService.selectOne(id));

        return modelAndView;
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult updateSubmit(Permission permission) {

        if (CommonUtils.isEmpty(permission.getPath())) {
            return AjaxResult.errorInstance("权限路径不能为空");
        }

        Permission oldPermission = permissionService.selectOne(permission.getId());
        oldPermission.setDescription(permission.getDescription());
        oldPermission.setPath(permission.getPath());
        permissionService.update(oldPermission);

        return AjaxResult.successInstance("修改成功！");
    }

    @RequestMapping(value = "/delete.do")
    public @ResponseBody AjaxResult delete(Long id) {

        permissionService.delete(id);
        return AjaxResult.successInstance("删除成功！");

    }

    /**
     * 数据量不会很多，不需要分页
     */
    @RequestMapping("/list.do")
    public ModelAndView list() {

        ModelAndView modelAndView = new ModelAndView("permission/list");
        modelAndView.addObject("permissionList", permissionService.selectList());

        return modelAndView;
    }

}
