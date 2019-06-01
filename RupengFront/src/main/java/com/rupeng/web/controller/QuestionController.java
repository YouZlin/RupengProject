package com.rupeng.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageInfo;
import com.rupeng.pojo.Card;
import com.rupeng.pojo.Chapter;
import com.rupeng.pojo.Classes;
import com.rupeng.pojo.Question;
import com.rupeng.pojo.QuestionAnswer;
import com.rupeng.pojo.Segment;
import com.rupeng.pojo.User;
import com.rupeng.pojo.UserSegment;
import com.rupeng.service.CardService;
import com.rupeng.service.CardSubjectService;
import com.rupeng.service.ChapterService;
import com.rupeng.service.ClassesUserService;
import com.rupeng.service.QuestionAnswerService;
import com.rupeng.service.QuestionService;
import com.rupeng.service.SegmentService;
import com.rupeng.service.UserSegmentService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;
import com.rupeng.util.JedisUtils;
import com.rupeng.util.JsonUtils;

@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private UserSegmentService userSegmentService;

    @Autowired
    private SegmentService segmentService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private CardService cardService;

    @Autowired
    private ClassesUserService classesUserService;

    @Autowired
    private CardSubjectService cardSubjectService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionAnswerService questionAnswerService;

    @RequestMapping("/list.do")
    public ModelAndView list(Integer pageNum, String condition, HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");

        PageInfo<Question> pageInfo = null;
        if (CommonUtils.isEmpty(condition)) {
            condition = "myAsked";
        }
        if (pageNum == null) {
            pageNum = 1;
        }
        //我提问的问题
        if ("myAsked".equals(condition)) {

            Question params = new Question();
            params.setUserId(user.getId());
            pageInfo = questionService.page(pageNum, 10, params, "isResolved asc, resolvedTime desc");

        } else if ("myAnswered".equals(condition)) {
            //我回答的问题
            pageInfo = questionService.pageOfMyAnswered(pageNum, 10, user.getId());

            //去重
            List<Question> list = pageInfo.getList();
            if (list != null) {
                Set<Question> set = new LinkedHashSet<>(list);
                pageInfo.setList(new ArrayList<Question>(set));
            }

        } else if ("allUnresolved".equals(condition)) {
            //全部解决中的问题
            Question params = new Question();
            params.setIsResolved(false);
            pageInfo = questionService.page(pageNum, 10, params);
        } else if ("allResolved".equals(condition)) {
            //全部已解决的问题
            Question params = new Question();
            params.setIsResolved(true);
            pageInfo = questionService.page(pageNum, 10, params, "resolvedTime desc");
        }

        ModelAndView modelAndView = new ModelAndView("question/list");
        modelAndView.addObject("pageInfo", pageInfo);
        modelAndView.addObject("condition", condition);

        return modelAndView;

    }

    //采纳一个回答未标准答案
    @RequestMapping(value = "/adopt.do")
    public @ResponseBody AjaxResult adopt(Long questionAnswerId, HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");

        QuestionAnswer questionAnswer = questionAnswerService.selectOne(questionAnswerId);
        Question question = questionService.selectOne(questionAnswer.getQuestionId());

        if (!user.getIsTeacher() && user.getId() != question.getUserId()) {
            throw new RuntimeException("您不是提问者，也不是老师，不能采纳此答案");
        }
        question.setResolvedTime(new Date());
        question.setIsResolved(true);
        questionAnswer.setIsAdopted(true);

        questionService.adopt(question, questionAnswer);

        //给所有参与者发送通知信息（除了点击采纳的人）
        Map<String, Object> notification = new HashMap<String, Object>();
        notification.put("questionId", question.getId());

        //查询所有参与此问题的用户
        QuestionAnswer params = new QuestionAnswer();
        params.setQuestionId(question.getId());
        List<QuestionAnswer> questionAnswerList = questionAnswerService.selectList(params);

        //把所有用户id放入set中去重
        Set<Long> userIds = new HashSet<Long>();
        for (QuestionAnswer questionAnswer2 : questionAnswerList) {
            userIds.add(questionAnswer2.getUserId());
        }
        //问题提问者id
        userIds.add(question.getUserId());

        for (Long userId : userIds) {
            if (userId == user.getId()) {
                //不通知自己
                continue;
            }
            //更人性化的通知
            if (userId == question.getUserId()) {
                notification.put("content", "您提问的问题有回答被采纳了");
            } else {
                notification.put("content", "您参与的问题有回答被采纳了");
            }
            JedisUtils.sadd("notification_" + userId, JsonUtils.toJson(notification));
        }

        return AjaxResult.successInstance("采纳成功");
    }

    //回答问题
    @RequestMapping(value = "/answer.do")
    public @ResponseBody AjaxResult answer(Long questionId, Long parentId, String content, HttpServletRequest request) {

        if (CommonUtils.isEmpty(content)) {
            return AjaxResult.errorInstance("答案不能为空");
        }
        User user = (User) request.getSession().getAttribute("user");

        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setContent(content);
        questionAnswer.setCreateTime(new Date());
        questionAnswer.setParentId(parentId);
        questionAnswer.setQuestionId(questionId);
        questionAnswer.setUserId(user.getId());
        questionAnswer.setUsername(user.getName());

        questionAnswerService.insert(questionAnswer);

        //给所有参与者发送通知信息（除了回答者自己）
        //查询所有参与此问题的用户
        QuestionAnswer params = new QuestionAnswer();
        params.setQuestionId(questionAnswer.getQuestionId());

        List<QuestionAnswer> questionAnswerList = questionAnswerService.selectList(params);
        //把所有用户id放入set中去重
        Set<Long> userIds = new HashSet<Long>();
        for (QuestionAnswer questionAnswer2 : questionAnswerList) {
            userIds.add(questionAnswer2.getUserId());
        }

        //问题提问者
        Question question = questionService.selectOne(questionAnswer.getQuestionId());
        userIds.add(question.getUserId());

        Map<String, Object> notification = new HashMap<String, Object>();
        notification.put("questionId", questionAnswer.getQuestionId());

        for (Long userId : userIds) {
            if (userId == user.getId()) {
                //不通知自己
                continue;
            }
            //更人性化的通知
            if (userId == question.getUserId()) {
                notification.put("content", "您提问的问题有新回复");
            } else {
                notification.put("content", "您参与的问题有新回复");
            }
            JedisUtils.sadd("notification_" + userId, JsonUtils.toJson(notification));
        }

        return AjaxResult.successInstance("回答成功");
    }

    //查看问题
    @RequestMapping(value = "/detail.do")
    public ModelAndView detail(Long questionId, HttpServletRequest request) {

        Question question = questionService.selectOne(questionId);

        QuestionAnswer params = new QuestionAnswer();
        params.setQuestionId(questionId);
        List<QuestionAnswer> questionAnswerList = new ArrayList<>();

        /*要做成这样的效果，需要Map<QuestionAnswer, List<QuestionAnswer>>这样的数据结构
            一级回答1
                    二级回答1
                    二级回答2
                            三级回答1
            一级回答2
         */
        List<QuestionAnswer> tempList = questionAnswerService.selectList(params, "createTime desc");

        //先找到顶层的answer
        for (QuestionAnswer questionAnswer : tempList) {
            if (questionAnswer.getParentId() == null) {
                questionAnswerList.add(questionAnswer);
            }
        }
        //然后找到每个answer的子级answer
        if (tempList != null) {
            for (QuestionAnswer questionAnswer : tempList) {
                //再次遍历questionAnswerList，找到当前questionAnswer所有的子答案
                List<QuestionAnswer> childList = new ArrayList<>();
                for (QuestionAnswer child : tempList) {
                    if (child.getParentId() == questionAnswer.getId()) {
                        childList.add(child);
                    }
                }
                questionAnswer.setChildQuestionAnswerList(childList);
            }
        }

        Classes classes = classesUserService.selectFirstOneBySecondId(question.getUserId());

        ModelAndView modelAndView = new ModelAndView("question/detail");
        modelAndView.addObject("question", question);
        modelAndView.addObject("questionAnswerList", questionAnswerList);
        modelAndView.addObject("classes", classes);

        return modelAndView;
    }

    //提问问题页面
    @RequestMapping(value = "/ask.do", method = RequestMethod.GET)
    public ModelAndView askPage(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        //学生所属班级、班级所属学科
        Classes classes = classesUserService.selectFirstOneBySecondId(user.getId());
        //此学科所包含的全部学习卡，按升序排序
        List<Card> cardList = cardSubjectService.selectFirstListBySecondId(classes.getId(), "seqNum asc");

        //最近一次学习的课程
        UserSegment latestUserSegment = userSegmentService.selectLatest(user.getId());
        Segment latestSegment = null;
        Chapter latestChapter = null;
        List<Chapter> chapterList = null;
        List<Segment> segmentList = null;
        //如果有学习记录
        if (latestUserSegment != null) {
            latestSegment = segmentService.selectOne(latestUserSegment.getSegmentId());
            latestChapter = chapterService.selectOne(latestSegment.getChapterId());
            //当前学习卡所包含的Chapter
            Chapter chapterParams = new Chapter();
            chapterParams.setCardId(latestChapter.getCardId());
            chapterList = chapterService.selectList(chapterParams, "seqNum asc");
            //当前chapter所包含的segment
            Segment segmentParams = new Segment();
            segmentParams.setChapterId(latestChapter.getId());
            segmentList = segmentService.selectList(segmentParams, "seqNum asc");
        } else {
            Chapter chapterParams = new Chapter();
            chapterParams.setCardId(cardList.get(0).getId());
            chapterList = chapterService.selectList(chapterParams, "seqNum asc");

            //当前chapter所包含的segment
            if (!CommonUtils.isEmpty(chapterList)) {
                Segment segmentParams = new Segment();
                segmentParams.setChapterId(chapterList.get(0).getId());
                segmentList = segmentService.selectList(segmentParams, "seqNum asc");
            }
        }

        ModelAndView modelAndView = new ModelAndView("question/ask");
        modelAndView.addObject("latestSegment", latestSegment);
        modelAndView.addObject("latestChapter", latestChapter);
        modelAndView.addObject("segmentList", segmentList);
        modelAndView.addObject("chapterList", chapterList);
        modelAndView.addObject("cardList", cardList);

        return modelAndView;
    }

    //提问问题提交
    @RequestMapping(value = "/ask.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult askSubmit(Long segmentId, String errorCode, String errorInfo, String description, HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute("user");

        if (CommonUtils.isEmpty(errorCode) && CommonUtils.isEmpty(errorInfo) && CommonUtils.isEmpty(description)) {
            return AjaxResult.errorInstance("错误代码、错误信息、问题描述 不能全为空");
        }

        Question question = new Question();
        question.setCreateTime(new Date());
        question.setDescription(description);
        question.setErrorCode(errorCode);
        question.setErrorInfo(errorInfo);
        question.setSegmentId(segmentId);
        question.setUserId(user.getId());
        question.setUsername(user.getName());

        Segment segment = segmentService.selectOne(segmentId);
        if (segment != null) {
            Chapter chapter = chapterService.selectOne(segment.getChapterId());
            Card card = cardService.selectOne(chapter.getCardId());
            StringBuilder courseInfo = new StringBuilder();
            courseInfo.append(card.getName()).append(" >> ");
            courseInfo.append(chapter.getSeqNum()).append(" ").append(chapter.getName()).append(" >> ");
            courseInfo.append(segment.getSeqNum()).append(" ").append(segment.getName());
            question.setCourseInfo(courseInfo.toString());
        }

        //插入数据库
        questionService.insert(question);

        //向所有老师发生通知
        List<User> teacherList = classesUserService.selectTeacherByStudentId(user.getId());

        if (teacherList != null) {
            //再把刚刚插入的查询出来，目的是获取question的id
            Question params = new Question();
            params.setUserId(user.getId());
            question = questionService.page(1, 1, params, "createTime desc").getList().get(0);

            Map<String, Object> notification = new HashMap<String, Object>();
            notification.put("questionId", question.getId());
            notification.put("content", "学生提问了新问题");

            for (User teacher : teacherList) {
                JedisUtils.sadd("notification_" + teacher.getId(), JsonUtils.toJson(notification));
            }
        }

        return AjaxResult.successInstance("提问成功");

    }
}
