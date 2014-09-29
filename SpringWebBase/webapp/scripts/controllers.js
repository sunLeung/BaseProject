app.controller('controller', ['$scope','service','$window', function($scope,service,$window) {
	//参数定义
	/**应用列表*/
	$scope.appsContent={};
	/**渠道列表(Map)*/
	$scope.unionMapContent = {};
	/**创建新应用实体*/
	$scope.appBean={};
	/**错误提示*/
	$scope.errorContent = {
		"gameListEmptyTip":"请配置相应道具发放服务器",
		"unionListEmptyTip":"请配置渠道",
		"appnameEmptyTip":"",
	};
	$scope.constant={
		AUTH_CREATE_APP:1,
		AUTH_UPDATE_APP:2,
		AUTH_DELETE_APP:3
	}
	/**登陆用户*/
	$scope.user={
		isLogin:false,
		username:"",
		auth:null
	};
	
	$scope.loadUser=function(){
		service.loadUser().success(function(data, status, headers, config) {
			var isLogin=data.isLogin;
			if(isLogin){
				$scope.user.isLogin=true;
				$scope.user.username=data.username;
				var auth=data.auth.trim();
				$scope.user.auth=auth.split(',');
			}else{
				$window.location.href='/login.html';
			}
		}).error(function(data, status, headers, config) {
	  		console.log('error loadUser status:'+status+'  data:'+data);
		});
	}
	/**初始化用户数据*/
	$scope.loadUser();
	
	/**初始化应用列表方法*/
	$scope.loadApps=function(){
		service.getApps().success(function(data, status, headers, config) {
			for(index in data){
				var unions=[];
				for(unionid in $scope.unionMapContent){
					var union=$scope.unionMapContent[unionid];
					var obj={
						"unionid":unionid,
						"name":union.name,
						"uri":union.uri,
						"paramsArray":union.paramsArray,
						"isCompleted":false
					};
					for(param in union.paramsArray){
						obj[union.paramsArray[param]]="";
					}
					unions.push(obj);
				}
				var dataunion=data[index].uniondata;
				for(du in dataunion){
					var du_id= dataunion[du].unionid;
					for(uu in unions){
						if(du_id==unions[uu].unionid){
							var mark=true;
							for(p in unions[uu].paramsArray){
								var key=unions[uu].paramsArray[p];
								unions[uu][key]=dataunion[du][key];
								if(dataunion[du][key]=undefined||dataunion[du][key]==null||dataunion[du][key]==""){
									mark=false;
								}
							}
							unions[uu].isCompleted=mark;
						}
					}
				}
				data[index].uniondata=unions;
			}
			$scope.appsContent = data;
	  	}).error(function(data, status, headers, config) {
	  		console.log('error getAppsInfo status:'+status+'  data:'+data);
		});
	}

	
	/**初始化渠道列表*/
	service.getUnions().success(function(data, status, headers, config) {
		$scope.unionMapContent = data;
		for(index in $scope.unionMapContent){
			//初始化渠道列表(Array)
			var union=$scope.unionMapContent[index];
			if(union.params==null||union.params==""){
				union.paramsArray=[];
			}else{
				union.paramsArray=union.params.split(',');
			}
		}
  	}).error(function(data, status, headers, config) {
  		console.log('error getUnions status:'+status+'  data:'+data);
	});
	
	/**初始化应用列表*/
	$scope.loadApps();
	
	/**添加新游戏服*/
	$scope.addServer=function(){
		var server={
			"serverid":null,
			"url":null
		};
		$scope.appBean.servers.push(server);
	};
	
	/**移除新游戏服*/
	$scope.removeServer=function(serverid){
		service.removeById(serverid,$scope.appBean.servers,'serverid',$scope);
	};
	
	/**判断是否配置好该渠道*/
	$scope.isAddUnion=function(unionid){
		var unions=$scope.appBean.uniondata;
		for(index in unions){
			if(unions[index].unionid==unionid){
				var union=unions[index];
				var params=union.paramsArray;
				var result=true;
				for(param in params){
					var p=union[params[param]];
					if(p==null||p==""){
						union.isCompleted=false;
						return;
					}
				}
				union.isCompleted=true;
			}
		}
	};
	
	/**初始化新应用数据*/
	$scope.initappBean=function(){
		var bean={
			appname:"",
			servers:[],
			uniondata:[]
		};
		
		for(unionid in $scope.unionMapContent){
			var union=$scope.unionMapContent[unionid];
			var obj={
				"unionid":unionid,
				"name":union.name,
				"uri":union.uri,
				"paramsArray":union.paramsArray,
				"isCompleted":false
			};
			for(param in union.paramsArray){
				obj[union.paramsArray[param]]="";
			}
			bean.uniondata.push(obj);
		}
		$scope.appBean=bean;
		$scope.appBean.action="create";
		$scope.appBean.actionInfo="添加";
	}
	
	/**提交新应用*/
	$scope.submitApp=function(){
		var bean=$scope.appBean;
		//判断bean是否符合提交条件
		if(bean!=undefined&&bean!=null&&bean.appname!=undefined&&bean.appname!=null&&bean.appname!=""){
			var postBean={
				appname:bean.appname,
				servers:bean.servers,
				uniondata:[]
			};
			var unions=bean.uniondata;
			for(index in unions){
				var union=unions[index];
				if(union.isCompleted){
					var tu={
						unionid:union.unionid,
					};
					for(param in union.paramsArray){
						tu[union.paramsArray[param]]=union[union.paramsArray[param]];
					}
					postBean.uniondata.push(tu);
				}
			}
			
			if(bean.action=='create'){
				service.createApp(postBean).success(function(data, status, headers, config) {
					console.log('[info] createApp response:'+JSON.stringify(data));
					var code=data.code;
					if(code==0){
						//reload数据
						$scope.loadUser();
						$scope.loadApps();
					}
			  	}).error(function(data, status, headers, config) {
			  		console.log('error createApp status:'+status+'  data:'+data);
				});
				return true;
			}else if(bean.action=='update'){
				postBean.appid=bean.appid;
				service.updateApp(postBean).success(function(data, status, headers, config) {
					console.log('[info] updateApp response:'+JSON.stringify(data));
					var code=data.code;
					if(code==0){
						//reload数据
						$scope.loadApps();
					}
			  	}).error(function(data, status, headers, config) {
			  		console.log('error updateApp status:'+status+'  data:'+data);
				});
				return true;
			}
		}
		return false;
	}
	
	/**打开应用信息界面*/
	$scope.openAppInfo=function(app){
		$scope.appBean=app;
		$scope.appBean.action="update";
		$scope.appBean.actionInfo="更新";
		$('#myModal').modal('show');
	};
	/**删除应用*/
	$scope.deleteApp=function(app){
		service.deleteAppById(app.appid).success(function(data, status, headers, config) {
			console.log('[info] deleteApp response:'+JSON.stringify(data));
			var code=data.code;
			if(code==0){
				//reload数据
				$scope.loadApps();
			}
	  	}).error(function(data, status, headers, config) {
	  		console.log('error deleteApp status:'+status+'  data:'+data);
		});
		return true;
	};
	
	/**
	 * 登出操作
	 * @returns {} 
	 */
	$scope.logout=function(){
		service.logout().success(function(data, status, headers, config) {
			console.log('[info] logout response:'+JSON.stringify(data));
			$window.location.href='/login.html';
	  	}).error(function(data, status, headers, config) {
	  		console.log('error logout status:'+status+'  data:'+data);
		});
	};
	
	/**
	 * 检查权限
	 * @param {} authCode
	 * @returns {} 
	 */
	$scope.checkAuth=function(authCode){
    	var user=$scope.user;
    	if(!angular.isUndefined(user)&&!angular.isUndefined(user.isLogin)&&user.isLogin&&!angular.isUndefined(user.auth)&&user.auth!=null){
    		for(var i=0;i<user.auth.length;i++){
				if(user.auth[i] == authCode)
					return true;
			}
			return false;  
    	}
    	return false;
    };
    
    /**
     * 检查是否可以操作更新权限
     * @returns {} 
     */
    $scope.canUpdate=function(){
    	if($scope.appBean.action=="update"){
    		if(!$scope.checkAuth($scope.constant.AUTH_UPDATE_APP)){
    			return false;
    		}
    	}
    	return true;
    }
}]);


app.controller('loginController', ['$scope','service','$window', function($scope,service,$window) {
	$scope.login=function(){
		var uname=$scope.username;
		var pwd=$scope.pwd;
		$scope.loginUser={
			isLogin:false,
		};
		
		if($scope.loginForm.username.$invalid){
			$("input[name=username]").addClass('default-error').next().addClass('error_foucs');
		}else{
			$("input[name=username]").removeClass('default-error').next().removeClass('error_foucs');
		}
		if($scope.loginForm.pwd.$invalid){
			$("input[name=pwd]").addClass('default-error').next().addClass('error_foucs');
		}else{
			$("input[name=pwd]").removeClass('default-error').next().removeClass('error_foucs');
		}
		
		if($scope.loginForm.$invalid){
			return;
		}
		var user={
			username:uname.trim(),
			password:pwd,
		}
		service.login(user).success(function(data, status, headers, config) {
			console.log('[info] login response:'+JSON.stringify(data));
			var code=data.code;
			if(code==0){
				$scope.loginUser.isLogin=true;
				$window.location.href='/admin.html';
			}else{
				$("#loginErrorTip").show(100);
			}
	  	}).error(function(data, status, headers, config) {
	  		console.log('error login status:'+status+'  data:'+data);
		});
	}
	
	$scope.loginBlur=function(){
		if($scope.loginForm.pwd.$invalid && !$scope.loginForm.pwd.$pristine){
			$("input[name=username]").addClass('default-error').next().addClass('error_foucs');
		}else{
			$("input[name=username]").removeClass('default-error').next().removeClass('error_foucs');
		}
		if($scope.loginForm.pwd.$invalid && !$scope.loginForm.pwd.$pristine){
			$("input[name=pwd]").addClass('default-error').next().addClass('error_foucs');
		}else{
			$("input[name=pwd]").removeClass('default-error').next().removeClass('error_foucs');
		}
	}
}]);