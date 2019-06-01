package com.rupeng.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rupeng.service.UserCardService;
import com.rupeng.util.AjaxResult;

@Controller
@RequestMapping(value = "/userCard")
public class UserCardController {

    @Autowired
    private UserCardService userCardService;

    @RequestMapping(value = "/activateFirstCard.do")
    public @ResponseBody AjaxResult activateFirstCard(Long classesId) {

        userCardService.createFirstCard(classesId);

        return AjaxResult.successInstance("给此班级学生激活第一张学习卡成功！");

    }
}
