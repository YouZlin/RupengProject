package com.rupeng.web.tag;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.rupeng.pojo.Question;
import com.rupeng.pojo.QuestionAnswer;
import com.rupeng.pojo.User;

public class QuestionAnswerTag extends SimpleTagSupport {

    private List<QuestionAnswer> rootAnswers;

    private Question question;

    private User user;

    public List<QuestionAnswer> getRootAnswers() {
        return rootAnswers;
    }

    public void setRootAnswers(List<QuestionAnswer> rootAnswers) {
        this.rootAnswers = rootAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public void doTag() throws JspException, IOException {

        PageContext pageContext = (PageContext) this.getJspContext();
        user = (User) pageContext.getSession().getAttribute("user");

        StringBuilder builder = new StringBuilder();
        builder.append("<ul class=\"media-list\">");
        process(builder, rootAnswers);
        builder.append("</ul>");
        getJspContext().getOut().append(builder.toString());
    }

    //递归的方式生成页面字符串
    private void process(StringBuilder builder, List<QuestionAnswer> list) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (list != null) {

            for (QuestionAnswer questionAnswer : list) {

                /*  <!-- 一个回答的内容效果 -->
                     <div class="media">
                        <div class="pull-left" >
                            <span class="label label-success">蛋蛋</span>
                        </div>
                        <div class="media-body">
                            <div>
                                <span>2016-10-10 10:10:10</span>
                                <a class="btn btn-xs btn-default" onclick="showEditor(this,questionAnswerId)">补充回答、追问</a>
                            </div>
                            <div style="background-color:#F8F8F8;">xxxx</div>
                            <!--在此处再插入一个完整的此结构就可以创建一个子答案-->
                        </div>
                    </div>
                 */

                Long id = questionAnswer.getId();
                builder.append("<div class=\"media\">");
                builder.append("    <div class=\"pull-left\" >");
                builder.append("        <span class=\"label label-success\">").append(questionAnswer.getUsername());
                builder.append("        </span>");
                builder.append("    </div>");
                builder.append("    <div class=\"media-body\">");
                builder.append("        <div>");
                builder.append("            <span>").append(dateFormat.format(questionAnswer.getCreateTime()));
                builder.append("            </span>&nbsp;&nbsp;&nbsp;");
                //如果问题已经被解决
                if (question.getIsResolved() != null && question.getIsResolved()) {
                    if (questionAnswer.getIsAdopted() != null && questionAnswer.getIsAdopted()) {
                        builder.append("<img width=\"100\" src=\""
                                + ((PageContext) this.getJspContext()).getServletContext().getContextPath()
                                + "/images/correct.png\"/>");
                    }

                } else {
                    builder.append(
                            "            <a class=\"btn btn-xs btn-default\" reanswer=\"" + id + "\">补充回答、追问</a>");
                    //只有问题的提问者，或者老师可以采纳一个答案
                    if (user != null && (user.getIsTeacher() || user.getId() == question.getUserId())) {
                        builder.append("            <a class=\"btn btn-xs btn-default\"  adopt=\"" + id + "\">采纳</a>");
                    }
                }

                builder.append("        </div>");
                builder.append("        <div style=\"background-color:#F9F9F9;\">");
                builder.append(questionAnswer.getContent());
                builder.append("        </div>");
                process(builder, questionAnswer.getChildQuestionAnswerList());
                builder.append("    </div>");
                builder.append("</div>");
            }
        }
    }

}
