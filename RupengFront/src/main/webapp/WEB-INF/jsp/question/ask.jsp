<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/WEB-INF/jsp/header.jsp" %>
    <script type="text/javascript" src="<%=ctxPath %>/lib/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" src="<%=ctxPath %>/lib/ueditor/ueditor.all.min.js"></script>
    <title>我要提问</title>
</head>
<body>
    <%@include file="/WEB-INF/jsp/nav.jsp" %>
    
    <div  id="mainDiv"  class="container"   style="margin-top: 35px;">
        <script type="text/javascript">
             
             function submitAsk(formDom){
                 var params = $(formDom).serialize();
                 $.post(formDom.action, params, function(ajaxResult) {
                     alert(ajaxResult.data);
                     if (ajaxResult.status == "success") {
                         window.close()
                     }
                 }, 'json');
                 event.preventDefault();
             }
             //实现三级联动下拉菜单
             function changeCard(cardId){
                 $.post('<%=ctxPath%>/chapter/list.do', 'cardId='+cardId, function(ajaxResult) {
                     if (ajaxResult.status == "success") {
                         var chapters = ajaxResult.data;
                         $("#chapter").html('');
                         for(var i=0;i<chapters.length;i++){
                             $("#chapter").append('<option value="'+chapters[i].id+'">'+chapters[i].seqNum +' '+ chapters[i].name+'</options');
                         }
                         //当chapters元素个数为0时，会导致第三级下拉菜单不能联动，可以在这个地方先主动清空第三级下拉菜单
                         $("#segment").html('');
                         changeChapter($("#chapter").val());
                     }
                 }, 'json');
             }
             
             function changeChapter(chapterId){
                 $.post('<%=ctxPath%>/segment/list.do', 'chapterId='+chapterId, function(ajaxResult) {
                     if (ajaxResult.status == "success") {
                         var segments = ajaxResult.data;
                         $("#segment").html('');
                         for(var i=0;i<segments.length;i++){
                             $("#segment").append('<option value="'+segments[i].id+'">'+segments[i].seqNum +' '+ segments[i].name+'</options');
                         }
                     }
                 }, 'json');
             }
        </script>
        <div class="panel panel-info">
            <div class="panel-heading"> 
                <h3 class="panel-title">我要提问</h3> 
            </div> 
            <div class="panel-body">
                <form onsubmit="submitAsk(this)" action="<%=ctxPath %>/question/ask.do" method="post">
                    <div class="row">
                        <div class="col-sm-4">
                            <select class="form-control" onchange="changeCard(this.value)">
                                <c:forEach items="${cardList }" var="card">
                                    <option value="${card.id }" 
                                        <c:if test="${card.id==latestChapter.cardId }">selected="selected"</c:if>
                                    >${card.name }学习卡</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-sm-4">
                            <select class="form-control" id="chapter" onchange="changeChapter(this.value)">
                                <c:forEach items="${chapterList }" var="chapter">
                                    <option value="${chapter.id }" 
                                        <c:if test="${chapter.id==latestChapter.id }">selected="selected"</c:if>
                                    >${chapter.seqNum } ${chapter.name }</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-sm-4">
                            <select class="form-control" id="segment" name="segmentId">
                                <c:forEach items="${segmentList }" var="segment">
                                    <option value="${segment.id }" 
                                        <c:if test="${segment.id==latestSegment.id }">selected="selected"</c:if>
                                    >${segment.seqNum } ${segment.name }</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    
                    <br/>
                    <span class="label label-danger">全部！！报错信息：</span>
                    <script id="errorInfo" name="errorInfo"  type="text/plain"></script>
                    <script type="text/javascript">
                        <!-- 实例化编辑器 -->
                        var ue = UE.getEditor('errorInfo',{
                            "serverUrl":"<%=ctxPath%>/upload.do",
                            "elementPathEnabled" : false,
                            "wordCount":false,
                            "initialFrameHeight":200,
                            "toolbars":[ ['simpleupload','emotion','attachment'] ]
                        });
                    </script>                        
                    
                    <br/>
                    <span class="label label-danger">相关代码：</span>
                    <script id="errorCode" name="errorCode"  type="text/plain"></script>
                    <script type="text/javascript">
                        <!-- 实例化编辑器 -->
                        var ue = UE.getEditor('errorCode',{
                            "serverUrl":"<%=ctxPath%>/upload.do",
                            "elementPathEnabled" : false,
                            "wordCount":false,
                            "initialFrameHeight":200,
                            "toolbars":[ ['simpleupload','emotion','attachment'] ]
                        });
                    </script>                        
                    
                    <br/>
                    <span class="label label-danger">问题描述：</span>
                    <script id="description" name="description"  type="text/plain"></script>
                    <script type="text/javascript">
                        <!-- 实例化编辑器 -->
                        var ue = UE.getEditor('description',{
                            "serverUrl":"<%=ctxPath%>/upload.do",
                            "elementPathEnabled" : false,
                            "wordCount":false,
                            "initialFrameHeight":200,
                            "toolbars":[ ['simpleupload','emotion','attachment'] ]
                        });
                    </script>
                    <br/>
                    <input type="submit"  class="btn btn-default" value="提交问题" />
                </form>
            </div> 
        </div>
    
    </div>

    <%@include file="/WEB-INF/jsp/footer.jsp" %>
</body>
</html>