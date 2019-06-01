<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<%@include file="/WEB-INF/jsp/header.jsp" %>
<title>班级列表</title>
</head>
<body>
<div class="pd-20">
        <table class="table table-border table-bordered table-bg table-hover">
            <thead>
                <tr>
                    <th>班级</th>
                    <th>学科</th>
                    <th>操作 <button class="btn size-M radius" onclick="showLayer('添加新班级','<%=ctxPath%>/classes/add.do')"> 添加</button></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${classesList }" var="classes">
                    <tr>
                        <td>${classes.name }</td>
                        <td>
                            <c:forEach items="${subjectList }" var="subject">
                                <c:if test="${subject.id==classes.subjectId }">${subject.name }</c:if>
                            </c:forEach>
                        </td>
                        <td>
                            <button class="btn size-MINI radius" onclick="ajaxDelete('<%=ctxPath%>/classes/delete.do','id=${classes.id}')">删除</button>
                            <button class="btn size-MINI radius" onclick="showLayer('修改班级','<%=ctxPath%>/classes/update.do?id=${classes.id}')">修改</button>
                            <button class="btn size-MINI radius" onclick="showLayer('成员管理','<%=ctxPath%>/classesUser/update.do?classesId=${classes.id}')">成员管理</button>
                            <button class="btn size-MINI radius" onclick="ajaxSubmit('<%=ctxPath%>/userCard/activateFirstCard.do','classesId=${classes.id}')">发放第一张学习卡</button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
</div>
</body>
</html>