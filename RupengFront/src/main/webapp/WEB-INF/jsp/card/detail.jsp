<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/WEB-INF/jsp/header.jsp" %>
    <title>学习卡详情</title>
</head>
<body>
    <%@include file="/WEB-INF/jsp/nav.jsp" %>
    
    <div id="mainDiv" class="container" style="margin-top: 35px;">
        <!-- 此学习卡描述信息 -->
        <div class="well">
            <span style="font-size: 28px;">${card.name }</span>
            <span class="label label-danger" style="margin-left: 4%;">
                <c:if test="${remainValidDays==0 }">今天过期</c:if>
                <c:if test="${remainValidDays > 0 }">还有${remainValidDays }天过期</c:if>
            </span>
            <span class="label label-success"  style="margin-left: 4%;">课件资料：${card.courseware }</span>  
        </div>
        
        <!-- 此学习卡包含的课程章节列表 -->
        <div class="panel-group" id="accordion">
            <c:forEach items="${chapterSegmentListMap }" var="chapterSegmentList">
                <div class="panel panel-default">
                    <!-- 章节标题 -->
                    <div class="panel-heading"> 
                        <div class="panel-title"> 
                            <a style="display: block;" data-toggle="collapse" data-parent="#accordion"  href="#chapter${chapterSegmentList.key.id }"> 
                                ${chapterSegmentList.key.name } 
                            </a> 
                        </div> 
                    </div> 
                    <!-- 课程列表 -->
                    <div id="chapter${chapterSegmentList.key.id }" class="panel-collapse collapse 
                        <c:forEach items="${chapterSegmentList.value }" var="segment">
                            <c:if test="${latestUserSegment.segmentId==segment.id }">in</c:if>
                        </c:forEach>
                    "> 
                        <div class="panel-body">
                            <div class="list-group">
                                <c:forEach items="${chapterSegmentList.value }" var="segment">
                                    <a class="list-group-item" href="<%=ctxPath %>/segment/detail.do?segmentId=${segment.id}" target="_blank">
                                        <span>${segment.name }</span>
                                        <c:if test="${latestUserSegment.segmentId==segment.id }">
                                            <span class="label label-danger" style="margin-left: 4%;">上次学到这里 &gt;&gt;</span>
                                        </c:if>
                                    </a>
                                </c:forEach>
                            </div>
                        </div> 
                    </div> 
                </div>
            </c:forEach>
        </div>
    </div>
    
    <%@include file="/WEB-INF/jsp/footer.jsp" %>
</body>
</html>