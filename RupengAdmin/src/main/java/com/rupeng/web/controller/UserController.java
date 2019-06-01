package com.rupeng.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.rupeng.pojo.User;
import com.rupeng.service.UserService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //参数true表示允许日期为空（null、""）
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ModelAndView updatePage(Long id) {

        ModelAndView modelAndView = new ModelAndView("user/update");

        modelAndView.addObject("user", userService.selectOne(id));

        return modelAndView;

    }

    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult updateSubmit(User user) {
        if (CommonUtils.isEmpty(user.getEmail())) {
            return AjaxResult.successInstance("邮箱不能为空");
        }
        if (!CommonUtils.isEmail(user.getEmail())) {
            return AjaxResult.successInstance("邮箱格式不正确");
        }

        User oldUser = userService.selectOne(user.getId());
        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        oldUser.setIsMale(user.getIsMale());
        oldUser.setPhone(user.getPhone());
        oldUser.setSchool(user.getSchool());

        userService.update(oldUser);

        return AjaxResult.successInstance("修改用户成功！");

    }

    /**
     * 修改是否是老师字段
     * @param id
     * @param isTeacher
     * @return
     */
    @RequestMapping(value = "/setTeacher.do")
    public @ResponseBody AjaxResult setTeacher(Long id, boolean isTeacher) {

        User oldUser = userService.selectOne(id);
        oldUser.setIsTeacher(isTeacher);
        userService.update(oldUser);

        return AjaxResult.successInstance(isTeacher ? "设置老师成功！" : "取消设置老师成功！");

    }

    @RequestMapping("/list.do")
    public ModelAndView list(Integer curr, String param, Date beginTime, Date endTime) {
        ModelAndView modelAndView = new ModelAndView("user/list");

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

        return modelAndView;
    }

}
