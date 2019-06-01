package com.rupeng.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rupeng.pojo.AdminUser;
import com.rupeng.pojo.AdminUserRole;
import com.rupeng.util.CommonUtils;

@Service
public class AdminUserService extends BaseService<AdminUser> {

    @Autowired
    private AdminUserRoleService adminUserRoleService;

    public void insert(AdminUser adminUser, Long[] roleIds) {

        insert(adminUser);

        //查询出来刚添加的user的id
        AdminUser params = new AdminUser();
        params.setAccount(adminUser.getAccount());
        adminUser = selectOne(params);

        adminUserRoleService.updateFirst(adminUser.getId(), roleIds);

    }

    public void update(AdminUser adminUser, Long[] roleIds) {
        update(adminUser);

        //简单粗暴的做法：
        //1 先把adminUser关联关系全部删除
        adminUserRoleService.deleteByFirstId(adminUser.getId());

        //2 插入新的关系
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                AdminUserRole adminUserRole = new AdminUserRole(adminUser.getId(), roleId);
                adminUserRoleService.insert(adminUserRole);
            }
        }
    }

    public AdminUser login(String account, String password) {
        AdminUser adminUser = new AdminUser();
        adminUser.setAccount(account);

        adminUser = selectOne(adminUser);
        if (adminUser != null) {
            if (adminUser.getPassword().equalsIgnoreCase(CommonUtils.calculateMD5(adminUser.getPasswordSalt() + password))) {
                return adminUser;
            }
        }
        return null;
    }
}