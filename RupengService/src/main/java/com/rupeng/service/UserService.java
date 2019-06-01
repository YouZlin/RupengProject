package com.rupeng.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rupeng.mapper.UserMapper;
import com.rupeng.pojo.User;
import com.rupeng.util.CommonUtils;

@Service
public class UserService extends BaseService<User> {

    @Autowired
    private UserMapper userMapper;

    /**
     * 模糊查询
     */
    public List<User> likeSelect(Map<String, Object> params) {
        return userMapper.search(params);
    }

    /**
     * 模糊的分页查询
     * @param pageNum 目标页
     * @param pageSize 每页的条数
     * @param params 查询条件
     */
    public PageInfo<User> search(int pageNum, int pageSize, Map<String, Object> params) {
        PageHelper.startPage(pageNum, pageSize);//注意pageNum表示页码，从1开始
        List<User> list = userMapper.search(params);
        return new PageInfo<User>(list);
    }

    public User login(String loginName, String password) {
        User user = new User();
        if (CommonUtils.isEmail(loginName)) {
            //尝试使用email登录
            user.setEmail(loginName);
        } else {
            //尝试使用phone登录
            user.setPhone(loginName);
        }
        user = selectOne(user);
        if (user != null) {
            if (user.getPassword().equalsIgnoreCase(CommonUtils.calculateMD5(user.getPasswordSalt() + password))) {
                return user;
            }
        }
        return null;
    }

}
