package com.rupeng.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.rupeng.annotation.RupengCacheable;
import com.rupeng.annotation.RupengClearCache;
import com.rupeng.annotation.RupengUseCache;
import com.rupeng.pojo.Card;
import com.rupeng.pojo.CardSubject;
import com.rupeng.pojo.Subject;

@Service
@RupengCacheable
public class CardSubjectService extends ManyToManyBaseService<CardSubject, Card, Subject> {

    @RupengUseCache
    public List<Card> selectFirstListBySecondId(Long secondId, String orderBy) {
        PageHelper.orderBy(orderBy);

        return selectFirstListBySecondId(secondId);
    }

    @RupengClearCache
    public void order(Long[] cardSubjectIds, Integer[] seqNums) {
        for (int i = 0; i < cardSubjectIds.length; i++) {
            Long cardSubjectId = cardSubjectIds[i];
            Integer seqNum = seqNums[i];

            CardSubject cardSubject = selectOne(cardSubjectId);
            cardSubject.setSeqNum(seqNum);

            update(cardSubject);
        }
    }

    @RupengUseCache
    public CardSubject selectNext(Long subjectId, Long cardId) {
        //查询条件
        CardSubject cardSubject = new CardSubject();
        cardSubject.setSubjectId(subjectId);

        List<CardSubject> cardSubjectList = selectList(cardSubject, "seqNum desc");

        CardSubject nextCardSubject = null;
        //找到上一张学习卡
        for (CardSubject cardSubject2 : cardSubjectList) {
            if (cardSubject2.getCardId() == cardId) {
                break;
            } else {
                nextCardSubject = cardSubject2;
            }
        }
        return nextCardSubject;
    }

}
