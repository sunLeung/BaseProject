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
	<title>GmTool</title>

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
	<style type="text/css">  

</style>  
	
</head>
<body>

<!-- load 
<div id="loading" class="loading">Loading pages...</div> 
 -->
 
 <button id="btn-test">test</button>
 
<script src="lib/jquery/jquery-2.1.1.min.js"></script>
<script src="lib/bootstrap-3.2.0-dist/js/bootstrap.min.js"></script>
<script src="lib/Flat-UI-master/js/flat-ui.min.js"></script>
<script src="lib/sockjs/sockjs.min.js"></script>

<script>
$(document).ready(function(){
	$("#btn-test").on("click",function(){
		var sock = new SockJS('/socket/test.do');
		sock.onopen = function() {
		    console.log('open');
		};
		sock.onmessage = function(e) {
		    console.log('message', e.data);
		};
		sock.onclose = function() {
		    console.log('close');
		};
	});
	
});
</script>
</html>