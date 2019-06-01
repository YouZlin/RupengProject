package com.rupeng.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.rupeng.pojo.ClassesUser;
import com.rupeng.pojo.User;
import com.rupeng.service.ClassesUserService;
import com.rupeng.service.UserService;
import com.rupeng.util.AjaxResult;

@Controller
@RequestMapping(value = "/classesUser")
public class ClassesUserController {

    @Autowired
    private ClassesUserService classesUserService;

    @Autowired
    private UserService userService;

    /**
     * 由于前台用户数量太多，所以不能像其他关联关系一样把所有行列出来
     */
    @RequestMapping("/update.do")
    public ModelAndView update(Long classesId) {

        ModelAndView modelAndView = new ModelAndView("classesUser/update");
        // 把classesId关联的User查询出来
        ClassesUser classesUser = new ClassesUser();
        classesUser.setClassesId(classesId);
        modelAndView.addObject("userList", classesUserService.selectSecondListByFirstId(classesId));
        //把classesId也带上
        modelAndView.addObject("classesId", classesId);

        return modelAndView;
    }

    /**
     * 添加关联关系
     */
    @RequestMapping(value = "/add.do", method = RequestMethod.GET)
    public ModelAndView addPage(Long classesId) {

        ModelAndView modelAndView = new ModelAndView("classesUser/add");
        modelAndView.addObject("classesId", classesId);

        return modelAndView;

    }

    /**
     * 添加关联关系，给班级添加学生（老师）
     */
    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult addPageSubmit(Long classesId, Long userId) {

        ClassesUser classesUser = new ClassesUser();
        classesUser.setUserId(userId);

        //学生和老师的添加原则不同，一个学生只能在一个班级中，而老师可以在不同的班级中
        User user = userService.selectOne(userId);
        if (!user.getIsTeacher()) {
            if (classesUserService.isExisted(classesUser)) {
                return AjaxResult.errorInstance("此学生已经在其他班级中！");
            }
        } else {
            //一个老师可以在多个班级中，但同一个班级只能添加一次
            classesUser.setClassesId(classesId);
            if (classesUserService.isExisted(classesUser)) {
                return AjaxResult.errorInstance("此老师已经在此班级中！");
            }
        }
        classesUser.setClassesId(classesId);
        classesUserService.insert(classesUser);
        return AjaxResult.successInstance("添加成功！");

    }

    /**
     * 删除关联关系
     */
    @RequestMapping("/delete.do")
    public @ResponseBody AjaxResult delete(ClassesUser classesUser) {

        classesUser = classesUserService.selectOne(classesUser);
        classesUserService.delete(classesUser.getId());

        return AjaxResult.successInstance("删除成功！");

    }

    /**
     * 查找要添加关系的用户
     * @param curr
     * @param param
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping("/listUser.do")
    public ModelAndView listUser(Integer curr, String param, Date beginTime, Date endTime, Long classesId) {
        ModelAndView modelAndView = new ModelAndView("classesUser/add");

        if (curr == null) {
            curr = 1;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        if (param != null && param.length() > 0) {
            params.put("param", "%" + param + "%");
        }
        params.put("beginTime", beginTime);
        if (endTime != null) {
            //比如如果要查询截止到9月22注册的用户，应该查询出来9月23号零点之前的数据
            endTime.setTime(endTime.getTime() + 1000 * 60 * 60 * 24 - 1);
        }
        params.put("endTime", endTime);

        PageInfo<User> pageInfo = userService.search(curr, 10, params);
        modelAndView.addObject("pageInfo", pageInfo);
        modelAndView.addObject("classesId", classesId);

        return modelAndView;
    }
}
