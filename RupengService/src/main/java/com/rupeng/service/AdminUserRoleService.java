package com.rupeng.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rupeng.mapper.AdminUserRoleMapper;
import com.rupeng.pojo.AdminUser;
import com.rupeng.pojo.AdminUserRole;
import com.rupeng.pojo.Role;

@Service
public class AdminUserRoleService extends ManyToManyBaseService<AdminUserRole, AdminUser, Role> {

    @Autowired
    private AdminUserRoleMapper adminUserRoleMapper;

    //检查权限
    public boolean checkPermission(Long adminUserId, String path) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("adminUserId", adminUserId);
        params.put("path", path);

        int count = adminUserRoleMapper.checkPermission(params);
        return count > 0;
    }

}
