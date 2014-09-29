/**
 * angular 模块和路由
 */
var app = angular.module("app",['ngRoute']);

app.config(['$routeProvider',function($routeProvider){
	$routeProvider.when('/manageApp',{
		templateUrl:'view/manageApp.html'
	}).when('/releaseUnion',{
		templateUrl:'view/releaseUnion.html'
	}).otherwise({
		redirectTo:'/manageApp'
	});
}]);