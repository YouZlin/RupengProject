package com.rupeng.web.controller;

import java.util.Date;
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
import com.rupeng.service.CardService;
import com.rupeng.service.CardSubjectService;
import com.rupeng.service.SubjectService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;

@Controller
@RequestMapping(value = "/card")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private CardSubjectService cardSubjectService;

    @Autowired
    private SubjectService subjectService;

    @RequestMapping(value = "/add.do", method = RequestMethod.GET)
    public ModelAndView addPage() {
        //添加的同时也选择所属学科
        List<Subject> subjectList = subjectService.selectList();

        ModelAndView modelAndView = new ModelAndView("card/add");
        modelAndView.addObject("subjectList", subjectList);

        return modelAndView;
    }

    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult addSubmit(Card card, Long[] subjectIds) {

        //有效性检查
        if (CommonUtils.isEmpty(card.getName())) {
            return AjaxResult.errorInstance("学习卡名称不能为空");
        }
        //唯一性检查
        Card param = new Card();
        param.setName(card.getName());
        if (cardService.isExisted(param)) {
            return AjaxResult.errorInstance("学习卡名称已存在");
        }

        card.setCreateTime(new Date());
        cardService.insert(card, subjectIds);
        return AjaxResult.successInstance("添加成功！");

    }

    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ModelAndView updatePage(Long id) {

        List<Subject> subjectList = subjectService.selectList();

        CardSubject params = new CardSubject();
        params.setCardId(id);
        List<CardSubject> cardSubjectList = cardSubjectService.selectList(params);

        ModelAndView modelAndView = new ModelAndView("card/update");
        modelAndView.addObject("card", cardService.selectOne(id));
        modelAndView.addObject("subjectList", subjectList);
        modelAndView.addObject("cardSubjectList", cardSubjectList);

        return modelAndView;
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult updateSubmit(Card card, Long[] subjectIds) {

        //有效性检查
        if (CommonUtils.isEmpty(card.getName())) {
            return AjaxResult.errorInstance("学习卡名称不能为空");
        }
        //唯一性检查
        Card param = new Card();
        param.setName(card.getName());
        if (cardService.isExisted(param)) {
            return AjaxResult.errorInstance("学习卡名称已存在");
        }

        Card oldCard = cardService.selectOne(card.getId());
        oldCard.setCourseware(card.getCourseware());
        oldCard.setDescription(card.getDescription());
        oldCard.setName(card.getName());

        cardService.update(oldCard, subjectIds);

        return AjaxResult.successInstance("修改成功！");
    }

    @RequestMapping(value = "/delete.do")
    public @ResponseBody AjaxResult delete(Long id) {

        cardService.delete(id);
        return AjaxResult.successInstance("删除成功！");

    }

    /**
     * 数据量不会很多，不需要分页
     */
    @RequestMapping("/list.do")
    public ModelAndView list(Card card) {

        ModelAndView modelAndView = new ModelAndView("card/list");
        modelAndView.addObject("cardList", cardService.selectList(card));

        return modelAndView;
    }
}
