package com.rupeng.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rupeng.pojo.Chapter;
import com.rupeng.service.ChapterService;
import com.rupeng.util.AjaxResult;

@Controller
@RequestMapping("/chapter")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @RequestMapping("/list.do")
    public @ResponseBody AjaxResult list(Long cardId) {
        if (cardId == null) {
            throw new RuntimeException("需要指定cardId");
        }
        Chapter params = new Chapter();
        params.setCardId(cardId);

        return AjaxResult.successInstance(chapterService.selectList(params, "seqNum asc"));
    }
}
