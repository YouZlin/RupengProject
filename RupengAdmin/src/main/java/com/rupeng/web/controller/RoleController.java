package com.rupeng.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.Role;
import com.rupeng.service.PermissionService;
import com.rupeng.service.RoleService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;

@Controller
@RequestMapping(value = "/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "/add.do", method = RequestMethod.GET)
    public ModelAndView addPage() {

        ModelAndView modelAndView = new ModelAndView("role/add");
        modelAndView.addObject("permissionList", permissionService.selectList());
        return modelAndView;
    }

    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult addSubmit(String name, String description, Long[] permissionIds) {

        Role role = new Role();
        role.setName(name);

        if (roleService.isExisted(role)) {
            return AjaxResult.errorInstance("角色名称已存在");
        }
        role.setDescription(description);

        roleService.insert(role, permissionIds);

        return AjaxResult.successInstance("添加成功！");

    }

    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ModelAndView updatePage(Long id) {

        ModelAndView modelAndView = new ModelAndView("role/update");
        modelAndView.addObject("role", roleService.selectOne(id));

        return modelAndView;
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult updateSubmit(Long id, String name, String description) {

        if (CommonUtils.isEmpty(name)) {
            return AjaxResult.errorInstance("名称不能为空");
        }

        Role role = roleService.selectOne(id);

        if (!role.getName().equals(name)) {
            Role params = new Role();
            params.setName(name);
            if (roleService.isExisted(params)) {
                return AjaxResult.errorInstance("此名称已存在");
            }
        }
        role.setName(name);
        role.setDescription(description);
        roleService.update(role);

        return AjaxResult.successInstance("修改成功");
    }

    @RequestMapping(value = "/delete.do")
    public @ResponseBody AjaxResult delete(Long id) {

        roleService.delete(id);
        return AjaxResult.successInstance("删除成功！");

    }

    /**
     * 数据量不会很多，不需要分页
     */
    @RequestMapping("/list.do")
    public ModelAndView list(Role role) {

        ModelAndView modelAndView = new ModelAndView("role/list");
        modelAndView.addObject("roleList", roleService.selectList(role));

        return modelAndView;
    }

}
