//验证填写应用名
app.directive('appNameValid',[function(){
	return{
		link:function(scope,element,attrs){
			scope.$watch('createAppForm.appname.$valid', function(newVal, oldVal) {
    			if(scope.createAppForm.appname.$dirty&&!newVal){
    				element.addClass('has-error');
    				scope.errorContent.appnameEmptyTip='应用名不能为空,且少于20个字符.';
    			}else{
    				$('input[name=appname]').removeClass('default-error');
    				element.removeClass('has-error');
    				scope.errorContent.appnameEmptyTip='';
    			}
    		});
		}
	};
}]);

//监控渠道填写是否完成
app.directive('unionCheck',['service',function(service){
	return{
		link:function(scope,element,attrs){
			element.on('blur',function(){
				var unionid=element.attr('unionid');
				scope.isAddUnion(unionid);
			});
		}
	}
}]);


//提交新应用
app.directive('submitApp',['service',function(service){
	return{
		link:function(scope,element,attrs){
			element.on('click',function(){
				var result=scope.submitApp();
				if(result){
					$('#myModal').modal('hide');
				}else{
					$('input[name=appname]').addClass('default-error');
    				scope.errorContent.appnameEmptyTip='应用名不能为空,且少于20个字符.';
    				scope.$apply();
				}
			});
		}
	}
}]);

