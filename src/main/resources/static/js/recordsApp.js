var recordsApp = angular.module('recordsApp', []);

recordsApp.controller('RecordsCtrl', function RecordsController($scope, $http) {

  $scope.pageSize = 20;
  $scope.recordsCount = 0;

  $scope.records = [];

  $scope.loadRecords = function () {
    var httpRequest = $http({
      method: 'GET',
      url: '/records',
      params: {offset: $scope.recordsCount, limit: $scope.pageSize}
    }).success(function (data, status) {
      $scope.records = $.merge($scope.records, data);
      $scope.recordsCount = $scope.records.length;
      console.log("got records:" + data);
    });

  };

});