<%@page import="common.user.UserService"%>
<%@page import="common.user.Navigation"%>
<%@page import="java.util.List"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
List<Navigation> navigation=UserService.getNavigation(session);
%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Packer Manager</title>

	<!-- Bootstrap -->
	<link href="lib/bootstrap-3.2.0-dist/css/bootstrap.css" rel="stylesheet">
	<link href="lib/Flat-UI-master/css/flat-ui.css" rel="stylesheet">
	<link href="styles/main.css" rel="stylesheet">

	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	  <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	  <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
	<![endif]-->
</head>
<body>
	<!-- 导航条 -->
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container-fluid">
        <div class="navbar-header">
          <a class="navbar-brand" href="#">&nbsp;Packer Manager&nbsp;</a>
        </div>
        <div class="collapse navbar-collapse">
        	<p class="navbar-text pull-right">
              	${sessionScope.user.name} &nbsp;&nbsp;&nbsp;<a href="user/logout.do">注销</a>
            </p>
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#about">About</a></li>
            <li><a href="#contact">Contact</a></li>
          </ul>
        </div>
      </div>
    </div>
    
    <!-- 主体内容 -->
	<div class="container-fluid" style="margin-top:50px;">
		<div class="col-sm-3 col-md-2 sidebar">
          <ul class="nav nav-sidebar">
          	<%
          		for(int i=0;i<navigation.size();i++){
          			if(i==0){%>
          				<li class="active"><a href="#" uri="<%=navigation.get(i).getUri() %>"><%=navigation.get(i).getName() %></a></li>
          			<%}else{
          				if("#".equals(navigation.get(i).getUri().trim())){%>
	          				<li style="background-color: #fff;" disable>&nbsp;</li>
          				<%continue;} %>
          				<li><a href="#" uri="<%=navigation.get(i).getUri() %>"><%=navigation.get(i).getName() %></a></li>
          			<%}
          		}
          	%>
          </ul>
        </div>
		<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
		</div>
    </div>
    
<!-- tips 框 -->
<div id="tips" class="tips"></div>
<!-- 二次确认框 -->
<div id="myconfirm" class="modal fade">
  <div class="modal-dialog modal-sm">
    <div class="modal-content">
      <div class="modal-header" style="padding: 5px;">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <strong style="font-size: 14px;">提示框</strong>
      </div>
      <div class="modal-body" style="font-size: 14px;"></div>
      <div class="modal-footer" style="padding: 5px;">
        <button id="close" type="button" class="btn btn-default btn-sm" data-dismiss="modal">取消</button>
        <button id="confirm" type="button" class="btn btn-primary btn-sm">确定</button>
      </div>
    </div>
  </div>
</div>
<!-- loading 页面 -->
<div id="loading" class="loading" style="display: none;">Loading pages...</div> 
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="lib/jquery/jquery-2.1.1.min.js"></script>
<!-- Angular -->
<script src="lib/angular/angular.min.js" type="text/javascript"></script>
<script src="lib/angular/angular-route.min.js" type="text/javascript"></script>
<script src="scripts/app.js" type="text/javascript"></script>
<script src="scripts/controllers.js" type="text/javascript"></script>
<script src="scripts/services.js" type="text/javascript"></script>
<script src="scripts/directives.js" type="text/javascript"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="lib/bootstrap-3.2.0-dist/js/bootstrap.min.js"></script>
<script src="lib/Flat-UI-master/js/flat-ui.min.js"></script>
<script src="lib/Flat-UI-master/js/jquery.dropkick-1.0.0.js"></script>
<script src="lib/Flat-UI-master/js/application.js"></script>

<script>
$(document).ready(function(){
	jQuery.myload=function(container,uri){
		if(uri!="#"){
			container.empty();
			$("#loading").show();
			container.load(uri,function(responseTxt,statusTxt,xhr){
				$("#loading").hide();
			});
		}
	}
	
	$(".nav-sidebar li a").on("click",function(){
		var uri=$(this).attr("uri");
		if(uri!="#"){
			var c=$(".main");
			$.myload(c,uri);
		}
	});
	
	
	var uri=$(".nav-sidebar .active a").attr("uri");
	if(uri!="#"){
		var c=$(".main");
		$.myload(c,uri);
	}

	jQuery.myalert=function(data){
		$('#tips').html(data);
		var top= $(window).height()*0.75 + document.body.scrollTop;
		var left=document.body.clientWidth*0.5 - $('#tips').width()/2;
		$('#tips').css({
			  "top":top,
			  "left":left
		});
		$('#tips').slideDown(200).delay(3000).slideUp(200);
	}
	
	/**二次确认框*/
	jQuery.myconfirm=function(msg,callback){
		var myconfirm=$("#myconfirm");
		myconfirm.find(".modal-body").empty();
		myconfirm.find(".modal-body").html(msg);
		
		myconfirm.find("#confirm").off("click");
		myconfirm.find("#confirm").on("click",function(){
			callback();
			myconfirm.modal('hide');
		});
		
		myconfirm.find("#close").off("click");
		myconfirm.find("#close").on("click",function(){
			myconfirm.modal('hide');
		});
		myconfirm.modal('show');
	}
});
</script>
</html>