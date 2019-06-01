<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE HTML>
<html>
<head>
<%@include file="/WEB-INF/jsp/header.jsp" %>
<title>分配权限</title>
</head>
<body>
    
<div class="pd-20">
    <form onsubmit="ajaxSubmitForm(this,true)" action="<%=ctxPath%>/rolePermission/update.do">
        <input type="hidden" name="roleId" value="${roleId }" />
        <table class="table table-border table-bordered table-bg table-hover" >
            <thead>
                <tr>
                    <th>选中</th>
                    <th>权限请求路径</th>
                    <th>权限描述</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${permissionList }" var="permission">
                    <tr>
                        <td><input type="checkbox" name="permissionIds" value="${permission.id }"
                            <c:forEach items="${rolePermissionList }" var="rolePermission">
                                <c:if test="${rolePermission.permissionId == permission.id }">checked="checked"</c:if>
                            </c:forEach>
                        /></td>
                        <td>${permission.path }</td>
                        <td>${permission.description }</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <br/>
        <input class="btn btn-primary radius" type="submit" value="分配权限" />
        <input class="btn btn-default radius" type="button" value="关闭" onclick="parent.location.reload()" style="margin-left: 30px;" />
    </form>
    
</div>
</body>
</html>