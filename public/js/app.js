'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', [
  'ngRoute',
  'myApp.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/stream', {templateUrl: 'partials/stream.html', controller: 'StreamController'});
  $routeProvider.otherwise({redirectTo: '/stream'});
}]);