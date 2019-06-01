package com.rupeng.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.Card;
import com.rupeng.pojo.Chapter;
import com.rupeng.pojo.Segment;
import com.rupeng.pojo.User;
import com.rupeng.pojo.UserCard;
import com.rupeng.pojo.UserSegment;
import com.rupeng.service.CardService;
import com.rupeng.service.SegmentService;
import com.rupeng.service.UserCardService;
import com.rupeng.service.UserSegmentService;
import com.rupeng.util.CommonUtils;

@Controller
@RequestMapping("/card")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserCardService userCardService;

    @Autowired
    private UserSegmentService userSegmentService;

    @Autowired
    private SegmentService segmentService;

    @RequestMapping("/applyNext.do")
    public ModelAndView applyNext(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");

        boolean result = userCardService.applyNextCard(user.getId());

        ModelAndView modelAndView = new ModelAndView("message");
        if (result) {
            modelAndView.addObject("message", "新学习卡已经发放，请刷新学习中心页面");
        } else {
            modelAndView.addObject("message", "您已申请过全部的学习卡");
        }
        return modelAndView;
    }

    //最近一次看的课程
    @RequestMapping("/latest.do")
    public ModelAndView latest(HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");
        //最近一次看的课程
        UserSegment latestUserSegment = userSegmentService.selectLatest(user.getId());
        if (latestUserSegment == null) {
            ModelAndView modelAndView = new ModelAndView("message");
            modelAndView.addObject("message", "您最近没有学习过课程");
            return modelAndView;
        }

        //所属学习卡
        Card card = segmentService.selectBelongedCard(latestUserSegment.getSegmentId());

        //调用detail方法完成后续处理
        return detail(card.getId(), request);
    }

    @RequestMapping(value = "/detail.do")
    public ModelAndView detail(Long cardId, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("card/detail");

        User user = (User) request.getSession().getAttribute("user");
        //检查此学生是否有这张学习卡，是否过期
        UserCard userCard = new UserCard(user.getId(), cardId);
        userCard = userCardService.selectOne(userCard);

        if (userCard == null || userCard.getEndTime().getTime() < System.currentTimeMillis()) {
            modelAndView.setViewName("message");
            modelAndView.addObject("message", "您没有此学习卡，或者此学习卡已过期");
        }
        //计算学习卡剩余有效天数
        long remainValidDays = CommonUtils.calculateApartDays(userCard.getEndTime(), new Date());

        Card card = cardService.selectOne(cardId);
        UserSegment latestUserSegment = userSegmentService.selectLatest(user.getId());

        //查询此学习卡所有篇章的所有段落课程
        Map<Chapter, List<Segment>> chapterSegmentListMap = cardService.selectAllCourse(card.getId());

        modelAndView.addObject("card", card);
        modelAndView.addObject("remainValidDays", remainValidDays);
        modelAndView.addObject("latestUserSegment", latestUserSegment);
        modelAndView.addObject("chapterSegmentListMap", chapterSegmentListMap);

        return modelAndView;
    }
}
