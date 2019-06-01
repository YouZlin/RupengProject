package com.rupeng.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rupeng.pojo.Role;

@Service
public class RoleService extends BaseService<Role> {

    @Autowired
    private RolePermissionService rolePermissionService;

    public void insert(Role role, Long[] permissionIds) {
        insert(role);

        Role params = new Role();
        params.setName(role.getName());

        role = selectOne(params);

        rolePermissionService.updateFirst(role.getId(), permissionIds);
    }

    public void update(Role role, Long[] permissionIds) {

        update(role);

        rolePermissionService.updateFirst(role.getId(), permissionIds);
    }

}
