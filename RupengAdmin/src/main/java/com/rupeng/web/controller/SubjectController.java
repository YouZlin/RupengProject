package com.rupeng.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.rupeng.pojo.Subject;
import com.rupeng.service.SubjectService;
import com.rupeng.util.AjaxResult;
import com.rupeng.util.CommonUtils;

@Controller
@RequestMapping("/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @RequestMapping(value = "/add.do", method = RequestMethod.GET)
    public ModelAndView addPage() {
        ModelAndView modelAndView = new ModelAndView("subject/add");
        return modelAndView;
    }

    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult addSubmit(Subject subject) {

        if (CommonUtils.isEmpty(subject.getName())) {
            return AjaxResult.errorInstance("学科名称不能为空");
        }
        subjectService.insert(subject);
        return AjaxResult.successInstance("添加成功！");
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public ModelAndView updatePage(Long id) {

        ModelAndView modelAndView = new ModelAndView("subject/update");
        modelAndView.addObject("subject", subjectService.selectOne(id));

        return modelAndView;
    }

    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    public @ResponseBody AjaxResult updateSubmit(Subject subject) {

        if (CommonUtils.isEmpty(subject.getName())) {
            return AjaxResult.errorInstance("学科名称不能为空");
        }

        //统一修改方式：因为有些字段可能不允许修改，修改操作时就只能先查询出旧数据，在旧数据基础上修改可以修改的字段
        Subject oldSubject = subjectService.selectOne(subject.getId());
        oldSubject.setName(subject.getName());

        subjectService.update(oldSubject);
        return AjaxResult.successInstance("更新成功！");
    }

    @RequestMapping(value = "/delete.do")
    public @ResponseBody AjaxResult delete(Long id) {

        subjectService.delete(id);
        return AjaxResult.successInstance("删除成功");

    }

    @RequestMapping("/list.do")
    public ModelAndView list(Subject subject) {

        ModelAndView modelAndView = new ModelAndView("subject/list");
        modelAndView.addObject("subjectList", subjectService.selectList(subject));

        return modelAndView;
    }

}
