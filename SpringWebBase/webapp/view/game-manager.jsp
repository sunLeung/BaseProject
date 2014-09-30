<%@page import="java.util.Map"%>
<%@page import="service.gamemanager.GameManagerService"%>
<%
Map<String,Object> gamestate=GameManagerService.getServerState();
%>
<div class="page-header">
  <h6 class="title-font">游戏服 <small style="title-font">管理</small></h6>
</div>

<div>
	<div class="btn-group">
		<button id="btn-start-server" data-loading-text="开服中..." class="btn btn-primary">开服</button>
		<button id="btn-stop-server" data-loading-text="关服中..." class="btn btn-primary">关服</button>
		<button id="btn-force-stop-server" class="btn btn-primary" disabled="disabled">强制关服</button>
	</div>
</div>

<div class="content-box">
    <h4>服务器</h4>
    <p id="server-state" state="<%=gamestate.get("code") %>"><%=gamestate.get("msg") %></p>
</div>

<div style="margin: 20px 0;border: 1px dotted;padding: 20px;">
	<div style="margin: 20px 0;" class="btn btn-success fileinput-button">
		<i class="glyphicon glyphicon-plus"></i>
		<span>Upload File</span>
		<input id="fileupload" class="btn-success"  type="file" name="files[]" data-url="/gamemanager/upload.do" multiple>
	</div>
    <div class="progress">
        <div class="progress-bar" style="width: 0%;"></div>
    </div>
    <table style="margin: 20px 0;" id="uploaded-files" class="table table-striped table-bordered table-hover">
        <tr>
            <th>Name</th>
            <th>Size</th>
            <th>Type</th>
            <th>MD5</th>
        </tr>
    </table>
</div>

<!-- 
<span>控制台</span>
<div class="controller"></div>
 -->
 
<script src="lib/bootstrap-3.2.0-dist/js/bootstrap.min.js"></script>
<script src="lib/Flat-UI-master/js/application.js"></script>
<script src="/lib/jQuery-File-Upload-9.8.0/js/vendor/jquery.ui.widget.js"></script>
<script src="/lib/jQuery-File-Upload-9.8.0/js/jquery.iframe-transport.js"></script>
<script src="/lib/jQuery-File-Upload-9.8.0/js/jquery.fileupload.js"></script>
<script>
$(document).ready(function(){

	$("#btn-start-server").on("click",function(){
		$.myconfirm("确定开服？",function(){
		    var $btn = $("#btn-start-server").button('loading');
		    $("#server-state").text("正在开服...");
			$.get("/gamemanager/start-server.do",function(data, textStatus, jqXHR){
				var json=data;
				if(typeof(data)!="object")
					json=JSON.parse(data);
				$.myalert(json.data);
				$("#server-state").text(json.data);
				var timer=window.setInterval(function(){
					$.get("/gamemanager/server-state.do",function(d, Status, XHR){
						$("#server-state").text(d.msg);
						$("#server-state").attr("state",d.code);
						
						if(d.code==0){
							$btn.button("reset");
							clearInterval(timer);
						}
					},"json");
				},10000);
			},"json");
		});
	});
	
	$("#btn-stop-server").on("click",function(){
		$.myconfirm("确定关服？",function(){
		    var $btn = $("#btn-stop-server").button('loading');
		    $("#server-state").text("正在关服...");
			$.get("/gamemanager/stop-server.do",function(data, textStatus, jqXHR){
		   		$btn.button("reset");
				var json=data;
				if(typeof(data)!="object")
					json=JSON.parse(data);
				$.myalert(json.data);
				$("#server-state").text(json.data);
			},"json");
		});
	});
	
	$('#fileupload').fileupload({
        dataType: 'json',
        start: function (e) {
        	var progress = 0;
            $('.progress .progress-bar').css(
                'width',
                progress + '%'
            );
        },
        done: function (e, d) {
        	var json=JSON.parse(d.result);
        	if(json.code==0){
        		var data=json.data;
	            $.each(data, function (index, file) {
	                $("#uploaded-files").append(
	                   $('<tr/>')
	                   .append($('<td/>').text(file.fileName))
	                   .append($('<td/>').text(file.fileSize))
	                   .append($('<td/>').text(file.fileType))
	                   .append($('<td/>').text(file.fileMD5))
	                   )
	            }); 
        	}else{
        		$.myalert(json.data);
        	}
        	
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('.progress .progress-bar').css(
                'width',
                progress + '%'
            );
        },
    });
});
</script>