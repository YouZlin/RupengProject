package com.rupeng.web.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.Card;
import com.rupeng.pojo.Question;
import com.rupeng.pojo.Segment;
import com.rupeng.pojo.User;
import com.rupeng.pojo.UserCard;
import com.rupeng.pojo.UserSegment;
import com.rupeng.service.ChapterService;
import com.rupeng.service.QuestionService;
import com.rupeng.service.SegmentService;
import com.rupeng.service.UserCardService;
import com.rupeng.service.UserSegmentService;
import com.rupeng.util.AjaxResult;

@Controller
@RequestMapping("/segment")
public class SegmentController {

    @Autowired
    private SegmentService segmentService;

    @Autowired
    private UserCardService userCardService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private UserSegmentService userSegmentService;

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/list.do")
    public @ResponseBody AjaxResult list(Long chapterId) {
        if (chapterId == null) {
            throw new RuntimeException("需要指定chapterId");
        }

        Segment params = new Segment();
        params.setChapterId(chapterId);

        return AjaxResult.successInstance(segmentService.selectList(params, "seqNum asc"));
    }

    @RequestMapping("/detail.do")
    public ModelAndView detail(Long segmentId, HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("segment/detail");

        //检查学生是否有这节课，以及这节课所属学习卡是否过期
        User user = (User) request.getSession().getAttribute("user");
        Card card = segmentService.selectBelongedCard(segmentId);

        UserCard userCard = new UserCard(user.getId(), card.getId());
        userCard = userCardService.selectOne(userCard);

        if (userCard == null || userCard.getEndTime().getTime() < System.currentTimeMillis()) {
            modelAndView.setViewName("message");
            modelAndView.addObject("message", "您没有此学习卡，或者此学习卡已经过期");
            return modelAndView;
        }

        Segment segment = segmentService.selectOne(segmentId);

        //和此节课程相关的问题
        Question param = new Question();
        param.setSegmentId(segmentId);
        List<Question> questionList = questionService.selectList(param);

        modelAndView.addObject("segment", segment);
        modelAndView.addObject("chapter", chapterService.selectOne(segment.getChapterId()));
        modelAndView.addObject("card", card);
        modelAndView.addObject("questionList", questionList);

        //认为用户观看了此节课程
        UserSegment userSegment = new UserSegment();
        userSegment.setCreateTime(new Date());
        userSegment.setSegmentId(segmentId);
        userSegment.setUserId(user.getId());

        userSegmentService.insert(userSegment);

        return modelAndView;
    }

    @RequestMapping("/next.do")
    public ModelAndView next(Long currentSegmentId, HttpServletRequest request) {

        //获取下一节课
        Segment nextSegment = segmentService.next(currentSegmentId);

        if (nextSegment == null) {
            ModelAndView modelAndView = new ModelAndView("message");
            modelAndView.addObject("message", "没有下一节课了");
            return modelAndView;
        }

        //接着执行detail的逻辑
        return detail(nextSegment.getId(), request);
    }
}
