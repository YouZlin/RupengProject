<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<%@include file="/WEB-INF/jsp/header.jsp" %>
<title>修改段落</title>
</head>
<body>
<div class="pd-20">
    <form onsubmit="ajaxSubmitForm(this,true)" action="<%=ctxPath %>/segment/update.do" class="form form-horizontal">
        <input type="hidden" name="id" value="${segment.id }" />
        <div class="row cl">
            <label class="form-label col-2">段落名称</label>
            <div class="formControls col-5">
                <input type="text" class="input-text"  name="name" value="${segment.name }" />
            </div>
            <div class="col-5"></div>
        </div>
        
        <div class="row cl">
            <label class="form-label col-2">序号</label>
            <div class="formControls col-5">
                <input type="text" class="input-text"  name="seqNum" value="${segment.seqNum }" />
            </div>
            <div class="col-5"></div>
        </div>

        <div class="row cl">
            <label class="form-label col-2">描述</label>
            <div class="formControls col-5">
                <textarea name="description" class="textarea" >${segment.description }</textarea>
            </div>
            <div class="col-5"></div>
        </div>

        <div class="row cl">
            <label class="form-label col-2">视频代码</label>
            <div class="formControls col-5">
                <textarea name="videoCode" class="textarea" >${segment.videoCode }</textarea>
            </div>
            <div class="col-5"></div>
        </div>
        
        <div class="row cl">
            <div class="col-9 col-offset-2">
                <input class="btn btn-primary radius" type="submit" value="修改" />
                <input class="btn btn-default radius" type="button" value="关闭" onclick="parent.location.reload()" style="margin-left: 30px;" />
            </div>
        </div>
    </form>
</div>
</body>
</html>