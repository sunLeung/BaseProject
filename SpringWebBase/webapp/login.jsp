<!DOCTYPE html>
<html lang="zh-cn">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>GMTool</title>

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
<body style="background-color: #1abc9c;">
	<div style="width: 800px;margin:250px auto;">
          <div style="width: 250px;float: left;">
            <h1>CMGE</h1>
	            <h4 style="margin-left: 16px;">GMTool <small style="font-size: 50%;">V1.0</small></h4>
          </div>
		<form>
          <div class="login-form" style="width: 359px;height: 244px;float: left;padding: 33px 23px 20px;">
            <div class="form-group">
              <input autofocus type="text" name="username" class="form-control login-field" placeholder="Enter your name" required>
              <label class="login-field-icon fui-user" for="login-name"></label>
            </div>

            <div class="form-group">
              <input type="password" name="pwd" class="form-control login-field" placeholder="Password" required>
              <label class="login-field-icon fui-lock" for="login-pass"></label>
            </div>
            
            <input type="button" id="submit" class="btn btn-primary btn-large btn-block" value="Login"/>
            <a class="login-link" href="#">Lost your password?</a>
            
            <div id="loginErrorTip" class="tooltip fade right in" style="top: 95px;left: 340px; display: none;">
            	<div class="tooltip-arrow"></div>
            	<div class="tooltip-inner" style="width: 123px;">用户名密码错误</div>
            </div>
          </div>
         </form>
     </div>
</body>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="lib/jquery/jquery-2.1.1.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="lib/bootstrap-3.2.0-dist/js/bootstrap.min.js"></script>
<script src="lib/Flat-UI-master/js/flat-ui.min.js"></script>
<script src="lib/Flat-UI-master/js/jquery.dropkick-1.0.0.js"></script>
<script src="lib/Flat-UI-master/js/application.js"></script>

<script>
$(document).ready(function(){
	$("#submit").on("click",function(){
		login();
	});
	
	document.onkeydown = function(e){ 
	    var ev = document.all ? window.event : e;
	    if(ev.keyCode==13) {
	    	login();
	     }
	}
	
	function login(){
		var name=$("input[name='username']").val();
		var password=$("input[name='pwd']").val();
		$.ajax({
            type: "POST",
            url: "/user/login.do",
            data: {name:name,password:password},
            dataType: "json",
            success: function(data){
           		var dataObj=eval(data);
           		console.log(dataObj.code);
           		if(dataObj.code==1){
           			$("#loginErrorTip").show();
           		}else if(dataObj.code==0){
           			self.location="/admin.jsp";
           		}
            }
        });
	}
});
</script>

</html>