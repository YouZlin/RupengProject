package com.rupeng.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rupeng.annotation.RupengCacheable;
import com.rupeng.annotation.RupengUseCache;
import com.rupeng.pojo.Classes;
import com.rupeng.pojo.ClassesUser;
import com.rupeng.pojo.User;

@Service
@RupengCacheable
public class ClassesUserService extends ManyToManyBaseService<ClassesUser, Classes, User> {

    @RupengUseCache
    public List<User> selectTeacherByStudentId(Long studnetId) {
        //查询学生所在班级
        ClassesUser classesUser = new ClassesUser();
        classesUser.setUserId(studnetId);
        classesUser = selectOne(classesUser);

        List<User> userList = selectSecondListByFirstId(classesUser.getClassesId());
        if (userList == null) {
            return null;
        }
        List<User> teacherList = new ArrayList<>();
        for (User user : userList) {
            if (user.getIsTeacher()) {
                teacherList.add(user);
            }
        }
        return teacherList;
    }

}
