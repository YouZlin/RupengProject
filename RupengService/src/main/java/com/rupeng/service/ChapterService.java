package com.rupeng.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.rupeng.annotation.RupengCacheable;
import com.rupeng.annotation.RupengUseCache;
import com.rupeng.pojo.Chapter;

@Service
@RupengCacheable
public class ChapterService extends BaseService<Chapter> {

    @RupengUseCache
    public boolean isFirst(Long chapterId) {
        Chapter chapter = selectOne(chapterId);
        Chapter chapter2 = new Chapter();
        chapter2.setCardId(chapter.getCardId());
        PageInfo<Chapter> pageInfo = page(1, 1, chapter2, "seqNum asc");

        return pageInfo.getList().get(0).getId() == chapterId;
    }

    @RupengUseCache
    public Chapter next(Long chapterId) {
        Chapter currentChapter = selectOne(chapterId);
        //查询当前学习卡的所有chapter
        Chapter params = new Chapter();
        params.setCardId(currentChapter.getCardId());
        List<Chapter> chapterList = selectList(params, "seqNum desc");

        Chapter nextChapter = null;
        for (Chapter chapter : chapterList) {
            if (chapter.getId() == chapterId) {
                break;
            } else {
                nextChapter = chapter;
            }
        }
        return nextChapter;
    }

}
