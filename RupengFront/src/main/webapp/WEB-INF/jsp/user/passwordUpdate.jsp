<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/WEB-INF/jsp/header.jsp" %>
    <title>修改密码</title>
</head>
<body>
    <%@include file="/WEB-INF/jsp/nav.jsp" %>

    <!-- 用户个人信息 -->
    <div id="mainDiv"  class="container" style="margin-top: 35px;">
    
        <style type="text/css">
            .row {
                padding: 5px;
                font-size: 16px;
            }
            .row div {
                padding-left: 5px;
            }
        </style>
    
        <div class="row">
            <!-- 左侧导航栏 -->
            <div class="col-md-3">
                <div class="list-group">
                    <a class="list-group-item" href="<%=ctxPath %>/user/userInfo.do"><span class="glyphicon glyphicon-user">&nbsp;</span><span>个人信息</span></a>
                    <a class="list-group-item" href="<%=ctxPath %>/user/passwordUpdate.do"><span class="glyphicon glyphicon-lock">&nbsp;</span><span>修改密码</span></a>
                    <a class="list-group-item" href="<%=ctxPath %>/user/home.do"><span class="glyphicon glyphicon-home">&nbsp;</span><span>${user.isTeacher?'教学中心':'学习中心' } &gt;&gt;</span></a>
                </div>
            </div>
            
            <!-- 右侧主题区域 -->
            <div class="col-md-9">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <form class="form-horizontal" onsubmit="ajaxSubmitForm(this)" action="<%=ctxPath %>/user/passwordUpdate.do">
                            <input type="hidden" name="id" value="${user.id }" />
                            <div class="form-group">
                                <label class="col-md-2 control-label">原密码</label>
                                <div class="col-md-7">
                                    <input name="password" type="password" class="form-control"/>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label class="col-md-2 control-label">新密码</label>
                                <div class="col-md-7">
                                    <input name="newpassword" type="password" class="form-control"/>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label class="col-md-2 control-label">重复新密码</label>
                                <div class="col-md-7">
                                    <input name="renewpassword" type="password" class="form-control"/>
                                </div>
                            </div>
            
                            <div class="form-group">
                                <div class="col-md-offset-2 col-md-7">
                                    <input class="btn btn-success" type="submit" value="保存" />
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <%@include file="/WEB-INF/jsp/footer.jsp" %>
</body>
</html>