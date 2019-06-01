<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/WEB-INF/jsp/header.jsp" %>
    <script type="text/javascript" src="<%=ctxPath %>/lib/validation/jquery.validate.min.js"></script>
    <script type="text/javascript" src="<%=ctxPath %>/lib/validation/additional-methods.min.js"></script>
    <script type="text/javascript" src="<%=ctxPath %>/lib/validation/localization/messages_zh.min.js"></script>
    <title>注册</title>
</head>
<body>
    <%@include file="/WEB-INF/jsp/nav.jsp" %>
    
    <div id="mainDiv"  class="container" style="margin-top: 35px;">
        <style type="text/css">
            .row {
                padding: 10px;
                font-size: 16px;
            }
            .row div {
                padding-left: 5px;
            }
        </style>
        <script type="text/javascript">
            $(function(){
                $("form").validate({
                    rules:{
                        email:{
                            required:true,
                            email:true
                        },
                        password:{
                            required:true,
                            minlength:6
                        },
                        repassword:{
                            required:true,
                            equalTo:"#password"
                        },
                        emailCode:{
                            required:true
                        }
                    },
                    messages:{
                        repassword:{
                            equalTo:"两次输入的密码不一致"
                        }
                    }
                });
            });
        </script>
        <form class="form-horizontal" action="<%=ctxPath %>/user/register.do" method="post">
            <div class="form-group">
                <label class="col-md-3 control-label">邮箱</label>
                <div class="col-md-6">
                    <input id="email" name="email" type="text" class="form-control" value="${param.email }" />
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-3 control-label">密码</label>
                <div class="col-md-6">
                    <input id="password" name="password" class="form-control" type="password" value="${param.password }" />
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-3 control-label">确认密码</label>
                <div class="col-md-6">
                    <input name="repassword" type="password" class="form-control" value="${param.repassword }" />
                </div>
            </div>
            <div class="form-group">
                <label class="col-md-3 control-label">验证码</label>
                <div class="col-md-6">
                    <div class="col-md-5" style="padding-left:0px;">
                        <input type="text" name="emailCode" class="form-control" />
                    </div>
                    <div class="col-md-4 text-center">
                        <input type="button" class="btn btn-default" onclick="sendEmailCode('<%=ctxPath%>/emailCode.do',$('#email').val(),this)" value="获取邮箱验证码" />
                    </div>
                    <div class="col-md-3"></div>
                </div>
            </div>
             <div class="form-group">
                <div class="col-md-offset-3 col-md-6" style="color:red">${message }</div>
            </div>
            <div class="form-group">
                <div class="col-md-offset-3 col-md-9">
                    <input class="btn btn-success" type="submit" value="注册" />
                    <a class="btn btn-link" href="<%=ctxPath %>/user/login.do">去登录&gt;&gt;</a>
                    <span class="glyphicon glyphicon-question-sign"></span>登录、注册遇到问题？<a href="#" target="_blank">点击此处联系客服</a>
                </div>
            </div>
            <div class="form-group">
                <div class="col-md-offset-3 col-md-9">
                    注册前请阅读<a href="#" target="_blank">《服务条款》</a>，您完成注册过程即表明同意服务条款中的全部内容。
                </div>
            </div>
        </form>
    </div>

    <%@include file="/WEB-INF/jsp/footer.jsp" %>
</body>
</html>