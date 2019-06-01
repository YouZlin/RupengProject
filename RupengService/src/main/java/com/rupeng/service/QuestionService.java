package com.rupeng.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rupeng.mapper.QuestionMapper;
import com.rupeng.pojo.Question;
import com.rupeng.pojo.QuestionAnswer;

@Service
public class QuestionService extends BaseService<Question> {

    @Autowired
    private QuestionAnswerService questionAnswerService;

    @Autowired
    private QuestionMapper questionMapper;

    public PageInfo<Question> pageOfMyAnswered(int pageNum, int pageSize, Long userId) {
        PageHelper.startPage(pageNum, pageSize);
        List<Question> list = questionMapper.selectMyAnswered(userId);
        return new PageInfo<Question>(list);
    }

    //我参与的解决中的问题
    public List<Question> selectMyAnsweredAndUnresolved(Long userId) {

        return questionMapper.selectMyAnsweredAndUnresolved(userId);
    }

    /**
     *
     * @param question
     * @param questionAnswer
     */
    public void adopt(Question question, QuestionAnswer questionAnswer) {

        //采纳一个回答未标准答案
        update(question);
        questionAnswerService.update(questionAnswer);

    }

    //查询此老师所在所有班级的所有学生提问的没有老师回复的问题
    public List<Question> selectUnresolvedQuestionByTeacherId(Long teacherId) {
        return questionMapper.selectUnresolvedQuestionByTeacherId(teacherId);
    }

    public List<Question> selectUnresolvedQuestionByStudentId(Long studentId) {
        return questionMapper.selectUnresolvedQuestionByStudentId(studentId);
    }

}
