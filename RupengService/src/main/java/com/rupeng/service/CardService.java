package com.rupeng.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rupeng.annotation.RupengCacheable;
import com.rupeng.annotation.RupengClearCache;
import com.rupeng.pojo.Card;
import com.rupeng.pojo.CardSubject;
import com.rupeng.pojo.Chapter;
import com.rupeng.pojo.Segment;

@Service
@RupengCacheable
public class CardService extends BaseService<Card> {

    @Autowired
    private CardSubjectService cardSubjectService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private SegmentService segmentService;

    //由于此方法的返回值类型Map<Chapter, List<Segment>>的key是Chapter，不符合json格式，所以此方法不可使用缓存
    //但方法内部的 chapterService.selectList等方法会使用缓存
    public Map<Chapter, List<Segment>> selectAllCourse(Long cardId) {

        //查询此学习卡所有的篇章
        Chapter chapterParams = new Chapter();
        chapterParams.setCardId(cardId);
        List<Chapter> chapterList = chapterService.selectList(chapterParams, "seqNum asc");

        //查询此学习卡所有篇章的所有段落课程
        Map<Chapter, List<Segment>> chapterSegmentListMap = new LinkedHashMap<>();
        if (chapterList != null) {
            for (Chapter chapter : chapterList) {
                Segment segmentParams = new Segment();
                segmentParams.setChapterId(chapter.getId());
                chapterSegmentListMap.put(chapter, segmentService.selectList(segmentParams));
            }
        }

        return chapterSegmentListMap;

    }

    @RupengClearCache
    public void insert(Card card, Long[] subjectIds) {

        insert(card);

        //根据学习卡名称把新添加的学习卡查询出来，用来得到id
        Card params = new Card();
        params.setName(card.getName());
        card = selectOne(params);

        if (subjectIds != null) {
            for (Long subjectId : subjectIds) {
                CardSubject cardSubject = new CardSubject(card.getId(), subjectId);
                cardSubjectService.insert(cardSubject);
            }
        }
    }

    @RupengClearCache
    public void update(Card card, Long[] subjectIds) {
        update(card);
        cardSubjectService.updateFirst(card.getId(), subjectIds);
    }

}
