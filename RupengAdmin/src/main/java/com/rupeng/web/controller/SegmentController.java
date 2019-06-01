package com.rupeng.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.Segment;
import com.rupeng.service.SegmentService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;

@Controller
@RequestMapping(value = "/segment")
public class SegmentController {

    @Autowired
    private SegmentService segmentService;

    @RequestMapping(value = "/add.do", method = RequestMethod.GET)
    public ModelAndView addPage(Long chapterId) {

        ModelAndView modelAndView = new ModelAndView("segment/add");
        modelAndView.addObject("chapterId", chapterId);

        return modelAndView;
    }

    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult addSubmit(Segment segment) {

        if (CommonUtils.isEmpty(segment.getName())) {
            return AjaxResult.errorInstance("段落名称不能为空");
        }
        segmentService.insert(segment);
        return AjaxResult.successInstance("添加成功！");
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ModelAndView updatePage(Long id) {

        ModelAndView modelAndView = new ModelAndView("segment/update");
        modelAndView.addObject("segment", segmentService.selectOne(id));

        return modelAndView;
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult updateSubmit(Segment segment) {

        if (CommonUtils.isEmpty(segment.getName())) {
            return AjaxResult.errorInstance("段落名称不能为空");
        }

        Segment oldSegment = segmentService.selectOne(segment.getId());
        oldSegment.setSeqNum(segment.getSeqNum());
        oldSegment.setName(segment.getName());
        oldSegment.setDescription(segment.getDescription());
        oldSegment.setVideoCode(segment.getVideoCode());
        segmentService.update(oldSegment);

        return AjaxResult.successInstance("修改成功！");

    }

    @RequestMapping(value = "/delete.do")
    public @ResponseBody AjaxResult delete(Long id) {

        segmentService.delete(id);
        return AjaxResult.successInstance("删除成功！");

    }

    /**
     * 数据量不会很多，不需要分页，但需要排序
     */
    @RequestMapping("/list.do")
    public ModelAndView list(Segment segment) {

        ModelAndView modelAndView = new ModelAndView("segment/list");
        modelAndView.addObject("segmentList", segmentService.selectList(segment, "seqNum asc"));
        modelAndView.addObject("chapterId", segment.getChapterId());
        return modelAndView;
    }
}
