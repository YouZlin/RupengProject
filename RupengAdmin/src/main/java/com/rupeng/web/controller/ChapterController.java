package com.rupeng.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.Chapter;
import com.rupeng.service.ChapterService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;

@Controller
@RequestMapping(value = "/chapter")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @RequestMapping(value = "/add.do", method = RequestMethod.GET)
    public ModelAndView addPage(Long cardId) {

        ModelAndView modelAndView = new ModelAndView("chapter/add");
        modelAndView.addObject("cardId", cardId);

        return modelAndView;
    }

    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult addSubmit(Chapter chapter) {

        if (CommonUtils.isEmpty(chapter.getName())) {
            return AjaxResult.errorInstance("篇章名称不能是空");
        }
        chapterService.insert(chapter);
        return AjaxResult.successInstance("添加成功！");
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ModelAndView updatePage(Long id) {

        ModelAndView modelAndView = new ModelAndView("chapter/update");
        modelAndView.addObject("chapter", chapterService.selectOne(id));

        return modelAndView;
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult updateSubmit(Chapter chapter) {

        if (CommonUtils.isEmpty(chapter.getName())) {
            return AjaxResult.errorInstance("篇章名称不能是空");
        }
        Chapter oldChapter = chapterService.selectOne(chapter.getId());
        oldChapter.setDescription(chapter.getDescription());
        oldChapter.setName(chapter.getName());
        oldChapter.setSeqNum(chapter.getSeqNum());

        chapterService.update(oldChapter);
        return AjaxResult.successInstance("修改成功！");
    }

    @RequestMapping(value = "/delete.do")
    public @ResponseBody AjaxResult delete(Long id) {

        chapterService.delete(id);
        return AjaxResult.successInstance("删除成功！");

    }

    /**
     * 数据量不会很多，不需要分页，但需要排序
     */
    @RequestMapping("/list.do")
    public ModelAndView list(Chapter chapter) {

        ModelAndView modelAndView = new ModelAndView("chapter/list");
        modelAndView.addObject("chapterList", chapterService.selectList(chapter, "seqNum asc"));
        modelAndView.addObject("cardId", chapter.getCardId());
        return modelAndView;
    }
}
