package com.rupeng.mapper;

import java.util.List;

import com.rupeng.pojo.Classes;
import com.rupeng.pojo.ClassesUser;
import com.rupeng.pojo.User;

public interface ClassesUserMapper extends IManyToManyMapper<ClassesUser, Classes, User> {

    List<ClassesUser> selectTeacherByStudentId(Long classesId);

}
