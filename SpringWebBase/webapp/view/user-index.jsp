<%@page import="common.user.User"%>
<%@page import="common.user.Group"%>
<%@page import="common.user.UserService"%>
<%@page import="common.user.AuthMap"%>
<%@page import="java.util.List"%>
<%
List<AuthMap> authList=UserService.authList;
List<Group> group=UserService.groupContent;
User user=(User)session.getAttribute("user");
String searchKey=request.getParameter("searchKey");
List<User> userList=UserService.getUserList(user,searchKey);
%>

<div class="page-header">
  <h6 class="title-font">权限 <small style="title-font">管理</small></h6>
</div>

<%if(user!=null&&user.hasAuth(2)){%>
<table class="table table-striped">
	<tr> 
		<td><input class="form-control" id="username" type="text" placeholder="用户名"/></td>
		<td><input class="form-control" id="password" type="password" placeholder="密 码"/></td>
		<td><input class="form-control" id="repassword" type="password" placeholder="确认密码"/></td>
		<td>
			<select id="groupid" class="select">
				<%for(Group g:group){if(g.getId()==0)continue;%>
					<option value="<%=g.getId() %>"><%=g.getName() %></option>
				<%} %>
			</select>
		</td>
		<td>
			<input id="select-power" type="button" class="btn btn-primary" value="权限选择" />
			<input id="create-user" type="button" class="btn btn-success" value="添加用户"/>
		</td>
	</tr>
</table>

<div id="power-content" class="well" style="display: none;">
	<%for(AuthMap auth:authList){%>
		<label style="border: 1px dotted;margin: 5px;padding:0 5px;">
			<input type="checkbox" name="power" value="<%=auth.getAuthCode() %>"/>
			<%=auth.getName() %>
		</label>
	<%} %>
</div>
<%} %>

<%if(user!=null&&user.hasAuth(3)){%>
<div class="input-group" style="width: 200px;margin-bottom: 10px;margin-left: 10px;">
	<input class="form-control" id="search-user-input" type="search" placeholder="Search">
	<span class="input-group-btn">
		<button type="button" class="btn" id="search-user-btn"><span class="fui-search"></span></button>
	</span>
</div>

<%if(userList!=null && userList.size()>0){%>
<table class="table table-striped table-bordered table-hover">
	<tr> 
		<td>用户名</td>
		<td>密码</td>
		<td>用户角色</td>
		<td>操作</td>
	</tr>
	<%for(User u:userList){%>
		<tr> 
			<td><%=u.getName() %></td>
	        <td>******</td>
	        <td><%=UserService.getGroupName(u.getGroupid()) %></td>
	       	<td>
	    		<a href="#" name="lookuser" userid="<%=u.getId() %>">查看 </a>
	       		<%if(u!=null&&u.getId()!=0){%>
	    		<%if(user!=null&&user.hasAuth(4)){%>
	    		|<a href="#" name="update-user" userid="<%=u.getId() %>">修改 </a>
	    		<%} %>
	    		<%if(user!=null&&user.hasAuth(5)){%>
				|<a href="#" name="deluser" userid="<%=u.getId() %>">删除 </a>
				<%} %>
				<%} %>
	    	</td>
	      </tr>
	<%} %>
</table>	
<%}else{%>
	无数据
<%} %>
<%} %>

<!-- 查看用户详细信息 -->
<div id="userpanel" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title">用户信息</h4>
      </div>
      <div class="modal-body">
        <table class="table table-bordered table-hover">
		  <tr>
		    <td>用户名:<span id="userpanel_username"></span></td>
		    <td>密码:<span id="userpanel_password"></span></td>
		    <td>用户组:<span id="userpanel_group"></td>
		  </tr>
		  <tr>
		    <td colspan="3">拥有权限：
		    	<div id="userpanel_power"><span>12345</span></div>
			</td>
		  </tr>
		</table>
      </div>
    </div>
  </div>
</div>

<!-- 修改用户详细信息 -->
<div id="update-user-panel" class="modal fade" userid="">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title">修改用户信息</h4>
      </div>
      <div class="modal-body">
        <table class="table table-bordered table-hover">
          <tr>
		    <td style="width:80px;">用户名</td>
		    <td>密码</td>
		    <td>确认密码</td>
		    <td>用户组</td>
		  </tr>
		  <tr>
		    <td><span id="update-userpanel-username"></span></td>
		    <td><input id="update-userpanel-password" class="form-control" type="password" placeholder="密 码"/></td>
		    <td><input id="update-userpanel-repassword" class="form-control" type="password" placeholder="确认密 码"/></td>
		    <td>
			    <select id="update-userpanel-groupid" class="select">
					<%for(Group g:group){%>
						<option value="<%=g.getId() %>"><%=g.getName() %></option>
					<%} %>
				</select>
			</td>
		  </tr>
		  <tr>
		    <td colspan="4">选择权限：
		    	<div>
		    		<%for(AuthMap auth:authList){%>
						<label style="border: 1px dotted;margin: 5px;padding:0 5px;">
							<input type="checkbox" name="update-userpanel-power" value="<%=auth.getAuthCode() %>"/>
							<%=auth.getName() %>
						</label>
					<%} %>
				</div>
			</td>
		  </tr>
		</table>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
        <button id="udpate-user-submit" type="button" class="btn btn-success">修改</button>
      </div>
    </div>
  </div>
</div>

<script src="lib/Flat-UI-master/js/application.js"></script>
<script>
$(document).ready(function(){

	$("#select-power").on("click",function(){
		$("#power-content").toggle();
	});
	
	
	$("#create-user").on("click",function(){
		var username=$("#username").val();
		var password=$("#password").val();
		var repassword=$("#repassword").val();
		var groupid=$("#groupid").val();
		var power="";
		$("input[name='power']:checkbox:checked").each(function(){
			power+=$(this).val()+',';
		});
		if(username==""){
			$.myalert("用户名不能为空");
			return;
		}
		if(password==""||repassword==""){
			$.myalert("密码不能为空");
			return;
		}
		if(password!=repassword){
			$.myalert("两次密码不一样");
			return;
		}
		
		$.myconfirm("确定创建: ["+username+"] ?",function(){
			var requestData={username:username,password:password,repassword:repassword,groupid:groupid,power:power};
			$.post("/user/create.do",requestData,function(data, textStatus, jqXHR){
				var json=data;
				if(typeof(data)!="object")
					json=JSON.parse(data);
				$.myalert(json.data);
				if(json.code==0){
					$(".main").load("/view/user-index.jsp");
				}
			},"json");
		});
	});
	
	$("#search-user-btn").on("click",function(){
		var key=$("#search-user-input").val();
		$(".main").load("/view/user-index.jsp?searchKey="+key);
	});
	
	$("a[name='lookuser']").on("click",function(){
		var userid=$(this).attr("userid");
		var requestData={userid:userid};
		$.post("/user/look.do",requestData,function(data, textStatus, jqXHR){
			var json=data;
			if(typeof(data)!="object")
				json=JSON.parse(data);
			if(json.code==0){
				var userpanel=$("#userpanel");
				var userpanel_username=userpanel.find("#userpanel_username");
				var userpanel_password=userpanel.find("#userpanel_password");
				var userpanel_group=userpanel.find("#userpanel_group");
				var userpanel_power=userpanel.find("#userpanel_power");
				userpanel_username.empty();
				userpanel_password.empty();
				userpanel_group.empty();
				userpanel_power.empty();
				
				userpanel_username.text(json.data.name);
				userpanel_password.text(json.data.password);
				userpanel_group.text(json.data.group);
				
				$.each(json.data.power, function (i, item) {  
					userpanel_power.append("<span style='border: 1px dotted;display: inline-block;padding: 2px;margin: 4px;'>"+item+"</span>");
		        });
				$("#userpanel").modal("show");
			}else{
				$.myalert(json.data);
			}
		},"json");
	});
	
	$("a[name='deluser']").on("click",function(){
		var id=$(this).attr("userid");
		$.myconfirm("确定删除 ?",function(){
			var requestData={id:id};
			$.post("/user/delete.do",requestData,function(data, textStatus, jqXHR){
				var json=data;
				if(typeof(data)!="object")
					json=JSON.parse(data);
				$.myalert(json.data);
				if(json.code==0){
					$(".main").load("/view/user-index.jsp");
				}
			},"json");
		});
	});
	
	$("a[name='update-user']").on("click",function(){
		var userid=$(this).attr("userid");
		var requestData={userid:userid};
		$.post("/user/pre-update.do",requestData,function(data, textStatus, jqXHR){
			var json=data;
			if(typeof(data)!="object")
				json=JSON.parse(data);
			if(json.code==0){
				var userpanel=$("#update-user-panel");
				var username=userpanel.find("#update-userpanel-username");
				var password=userpanel.find("#update-userpanel-password");
				var repassword=userpanel.find("#update-userpanel-repassword");
				var groupid=userpanel.find("#update-userpanel-groupid");
				username.empty();
				username.text(json.data.name);
				password.val(json.data.password);
				repassword.val(json.data.password);
				console.log("a");
				groupid.dropkick("select",json.data.group);
				
				$("input[name='update-userpanel-power']").removeAttr("checked");
				
				$.each(json.data.power, function (i, item) {  
					$("input[name='update-userpanel-power']").each(function (i, subitem) {
						if($(subitem).val()==item){
							$(subitem).prop("checked",true);
						}
					});
		        });
				userpanel.attr("userid",json.data.id);
				$("#update-user-panel").modal("show");
			}else{
				$.myalert(json.data);
			}
		});
	});
	
	$("#udpate-user-submit").on("click",function(){
		var userpanel=$("#update-user-panel");
		var username=userpanel.find("#update-userpanel-username");
		var password=userpanel.find("#update-userpanel-password");
		var repassword=userpanel.find("#update-userpanel-repassword");
		var groupid=userpanel.find("#update-userpanel-groupid");
		
		var id=userpanel.attr("userid");
		var pwd=password.val();
		var repwd=repassword.val();
		var gid=groupid.val();
		var power="";
		$("input[name='update-userpanel-power']:checkbox:checked").each(function(){
			power+=$(this).val()+',';
		});
		
		if(pwd==""||repwd==""){
			$.myalert("密码不能为空");
			return;
		}
		if(pwd!=repwd){
			$.myalert("两次密码不一样");
			return;
		}
		userpanel.off("hidden.bs.modal");
		userpanel.on("hidden.bs.modal",function (e) {
			$(".main").load("/view/user-index.jsp");
			e.preventDefault();
		});
		$.myconfirm("确定修改 ?",function(){
			var requestData={id:id,pwd:pwd,repwd:repwd,gid:gid,power:power};
			$.post("/user/update.do",requestData,function(data, textStatus, jqXHR){
				var json=data;
				if(typeof(data)!="object")
					json=JSON.parse(data);
				$.myalert(json.data);
				if(json.code==0){
					userpanel.modal("hide");
				}
			},"json");
		});
	});
});
</script>