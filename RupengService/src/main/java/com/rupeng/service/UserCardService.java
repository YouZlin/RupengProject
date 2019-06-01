package com.rupeng.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.rupeng.pojo.Card;
import com.rupeng.pojo.CardSubject;
import com.rupeng.pojo.Classes;
import com.rupeng.pojo.Setting;
import com.rupeng.pojo.User;
import com.rupeng.pojo.UserCard;
import com.rupeng.util.CommonUtils;

@Service
public class UserCardService extends ManyToManyBaseService<UserCard, User, Card> {

    @Autowired
    private CardSubjectService cardSubjectService;

    @Autowired
    private ClassesUserService classesUserService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private SettingService settingService;

    //最近得到的学习卡
    public UserCard selectLatestCreated(Long userId) {
        UserCard params = new UserCard();
        params.setUserId(userId);
        PageInfo<UserCard> pageInfo = page(1, 1, params, "createTime desc");
        if (CommonUtils.isEmpty(pageInfo.getList())) {
            return null;
        }
        return pageInfo.getList().get(0);
    }

    public void createFirstCard(Long classesId) {
        //查询出此班级的学生（老师）
        List<User> userList = classesUserService.selectSecondListByFirstId(classesId);
        if (CommonUtils.isEmpty(userList)) {
            return;
        }

        //查询出此班级所属学科
        Classes classes = classesService.selectOne(classesId);
        Long subjectId = classes.getSubjectId();
        //此班级还没选择学科
        if (subjectId == null) {
            return;
        }

        //查询此学科的第一张学习卡
        Long firstCardId = null;
        CardSubject params = new CardSubject();
        params.setSubjectId(subjectId);
        List<CardSubject> cardSubjectList = cardSubjectService.selectList(params, "seqNum asc");
        if (CommonUtils.isEmpty(cardSubjectList)) {
            return;
        }
        firstCardId = cardSubjectList.get(0).getCardId();

        //学习卡默认有效天数
        Setting setting = settingService.selectOneByName("card_valid_days");
        int validDays = Integer.parseInt(setting.getValue());

        for (User user : userList) {
            if (!user.getIsTeacher()) {
                UserCard userCard = new UserCard();
                userCard.setCardId(firstCardId);
                userCard.setUserId(user.getId());

                //如果已经存在，直接返回
                if (isExisted(userCard)) {
                    continue;
                }
                userCard.setCreateTime(new Date());
                userCard.setEndTime(new Date(System.currentTimeMillis() + validDays * 1000 * 60 * 60 * 24));

                insert(userCard);
            }
        }
    }

    //申请下一张学习卡
    public boolean applyNextCard(Long userId) {

        //查询出此用户的最近一次得到的学习卡
        UserCard latestUserCard = selectLatestCreated(userId);

        //查询出此用户所属的班级 所属的学科的所有学习卡
        Classes classes = classesUserService.selectFirstOneBySecondId(userId);

        CardSubject params = new CardSubject();
        params.setSubjectId(classes.getSubjectId());

        //按照seqNum倒序排列
        List<CardSubject> cardSubjectList = cardSubjectService.selectList(params, "seqNum desc");

        CardSubject nextCardSubject = null;
        for (CardSubject cardSubject2 : cardSubjectList) {
            if (cardSubject2.getCardId() == latestUserCard.getCardId()) {
                break;
            } else {
                nextCardSubject = cardSubject2;
            }
        }
        if (nextCardSubject == null) {
            return false;
        }

        //学习卡默认有效天数
        Setting setting = settingService.selectOneByName("card_valid_days");
        int validDays = Integer.parseInt(setting.getValue());

        UserCard userCard = new UserCard();
        userCard.setCardId(nextCardSubject.getCardId());
        userCard.setUserId(userId);
        userCard.setCreateTime(new Date());
        userCard.setEndTime(new Date(System.currentTimeMillis() + validDays * 1000 * 60 * 60 * 24));

        insert(userCard);

        return true;
    }

}