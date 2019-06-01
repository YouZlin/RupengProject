package com.rupeng.service;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.rupeng.pojo.Segment;
import com.rupeng.pojo.User;
import com.rupeng.pojo.UserSegment;
import com.rupeng.util.CommonUtils;

@Service
public class UserSegmentService extends ManyToManyBaseService<UserSegment, User, Segment> {

    //查询最近一次学习的课程
    public UserSegment selectLatest(Long userId) {
        UserSegment params = new UserSegment();
        params.setUserId(userId);
        PageInfo<UserSegment> pageInfo = page(1, 1, params, "createTime desc");

        if (!CommonUtils.isEmpty(pageInfo.getList())) {
            return pageInfo.getList().get(0);
        }
        return null;
    }
}