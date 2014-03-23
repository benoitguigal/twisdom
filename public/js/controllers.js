'use strict';

/* Controllers */

var app = angular.module('myApp.controllers', []);

app.controller('StreamController', function($scope) {

  	$scope.quote = ""
  	$scope.author = ""
  	$scope.profile = ""
  	$scope.name = ""
  	$scope.screenName = ""
  	$scope.original_tweet = ""

	// open a websocket for streaming
	var url = 'ws://' +  window.location.hostname + ':' + window.location.port + '/stream';
	var ws = new WebSocket(url);
	ws.onmessage = function(event) {
	    var data = JSON.parse(event.data)
	    $scope.quote = data.quote;
	    $scope.author = data.author;
	   	$scope.profile = data.status.user.imageUrl 
	   	$scope.name = data.status.user.name 
	   	$scope.screenName = "@" + data.status.user.screenName 
	   	$scope.original_tweet = "https://twitter.com/intent/user?user_id=" + data.status.user.id 
	   	$('#profile').css("background", "url(" + data.status.user.imageUrl + ")");
	   	$scope.$apply();
	}

  });
