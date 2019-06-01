package com.rupeng.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.Card;
import com.rupeng.pojo.CardSubject;
import com.rupeng.pojo.Subject;
import com.rupeng.service.CardSubjectService;
import com.rupeng.service.SubjectService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;

@Controller
@RequestMapping(value = "/cardSubject")
public class CardSubjectController {

    @Autowired
    private CardSubjectService cardSubjectService;

    @Autowired
    private SubjectService subjectService;

    /**
     * 学习卡按学科分组进行排序
     */
    @RequestMapping(value = "/order.do", method = RequestMethod.GET)
    public ModelAndView orderPage(Long subjectId) {

        ModelAndView modelAndView = new ModelAndView("cardSubject/order");
        //查询出所有Subject
        List<Subject> subjectList = subjectService.selectList();

        //如果subjectId为null，则默认操作首个subject
        if (subjectId == null) {
            if (!CommonUtils.isEmpty(subjectList)) {
                subjectId = subjectList.get(0).getId();
            }
        }

        //查询出subjectId关联的card数据
        List<Card> cardList = cardSubjectService.selectFirstListBySecondId(subjectId);

        //查询出subjectId关联的cardsubject数据
        CardSubject cardSubject = new CardSubject();
        cardSubject.setSubjectId(subjectId);
        List<CardSubject> cardSubjectList = cardSubjectService.selectList(cardSubject, "seqNum asc");

        modelAndView.addObject("cardSubjectList", cardSubjectList);
        modelAndView.addObject("subjectList", subjectList);
        modelAndView.addObject("cardList", cardList);
        modelAndView.addObject("subjectId", subjectId);

        return modelAndView;
    }

    @RequestMapping(value = "/order.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult orderSubmit(Long[] cardSubjectIds, Integer[] seqNums) {

        cardSubjectService.order(cardSubjectIds, seqNums);

        return AjaxResult.successInstance("排序成功！");

    }
}
