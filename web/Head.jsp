<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  user: ZouKaifa
  Date: 2016/10/10
  Time: 23:18
  用户登录、注册，置于页面头
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<head>
    <link rel="stylesheet" type="text/css" href="css/head_style.css">
</head>
<%@include file="Bootstrap.jsp"%>
<div id="first">
    <li>
        <!--<img src="photo/hitfly.gif">-->
        <h1>&nbsp;工大圈子</h1>
        <!--
        <FONT face=华文彩云 color=#da70d6 size=6>
            <B>朋友，欢迎您的到来</B>
        </FONT>
        -->
    </li>
    <s:if test="#session.username == null">
    </s:if>
    <s:else>
        <a href="searchUser.action">用户搜索</a>
        <img src="../photo/mirror.jpg" style="width:24px;height:24px;float:right"/>
    </s:else>
</div>
<%--div class="row" id="first">
    <div class="col-sm-8">
        <!--<img src="photo/hitfly.gif"/>-->
        <h1>工大圈子</h1>
    </div>
    <div class="col-sm-4">
        <MARQUEE scrollAmount=2 direction=up height=60>
            <FONT face=华文彩云 color=#da70d6 size=6>
                <B>朋友，欢迎您的到来</B>
            </FONT>
        </MARQUEE>
        <P>&nbsp;</P>
    </div>
<<<<<<< HEAD
</div--%>

<nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="index.action">首页</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li><a href="showForum.action">论坛</a></li>
                <s:if test="#session.username != null">
                    <li><a href="showCircle.action">圈子<span class="badge">${session.circleInform}</span></a></li>
                </s:if>
            </ul>

            <ul class="nav navbar-nav navbar-right">
                <s:if test="#session.username == null">
                    <li><a href="login.action">登录</a></li>
                    <li><a href="" onclick="return false;"
                           data-toggle="modal" data-target="#feedback">
                        反馈
                    </a> </li>
                </s:if>
                <s:else>
                    <!-- 该按钮通往个人主页 -->
                    <s:if test="#session.power == 1">
                        <li><a href="showHome.action?id=${session.id}">${session.nickname}
                            <span class="badge">管理员</span>
                            <s:if test="#session.inform != 0">
                                <span class="badge">${session.inform}</span>
                            </s:if>
                        </a></li>
                    </s:if>
                    <s:else>
                        <li><a href="showHome.action?id=${session.id}">${session.nickname}
                            <s:if test="#session.inform != 0">
                                <span class="badge">${session.inform}</span>
                            </s:if>
                        </a></li>
                    </s:else>
                    <li><a href="friendList.action?id=${session.id}">好友</a></li>
                    <li><a href="logout.action">注销</a></li>
                    <li><a href="" onclick="return false;"
                    data-toggle="modal" data-target="#feedback">
                        反馈
                    </a> </li>
                    <div class="modal fade" id="feedback" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                                    <h4 class="modal-title" id="myModalLabel">反馈</h4>
                                </div>
                                <form action="sendFeedback.action" method="post">
                                    <div class="modal-body" style="text-align: center">
                                                <textarea id="area" rows="10" cols="50"
                                                          name="info" maxlength="1000" minlength="2"
                                                          placeholder="感谢您对我们的项目进行反馈，请输入反馈内容，2~1000字符"
                                                          class="form-control" required
                                                ></textarea>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                        <button type="submit" class="btn btn-primary">发送</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </s:else>
            </ul>
        </div>
    </div>

</nav>