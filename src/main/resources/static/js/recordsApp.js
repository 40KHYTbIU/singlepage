var recordsApp = angular.module('recordsApp', []);

recordsApp.controller('RecordsCtrl', function RecordsController($scope, $http) {

  $scope.pageSize = 20;
  $scope.recordsCount = 0;

  $scope.records = [];

  $scope.sortField = "NAME";
  $scope.sortDirection = "ASC";

  $scope.filter = {id: null, name: null, date: null};

  $scope.loadRecords = function () {
    var httpRequest = $http({
      method: 'GET',
      url: '/records',
      params: {
        idFilter: $scope.idFilter,
        nameFilter: $scope.nameFilter,
        dateFilter: $scope.dateFilter,
        sortField: $scope.sortField,
        sortDirection: $scope.sortDirection,
        offset: $scope.recordsCount,
        limit: $scope.pageSize
      }
    }).success(function (data, status) {
      $scope.records = $.merge($scope.records, data);
      $scope.recordsCount = $scope.records.length;
      console.log("got records:" + data);
    });

  };

  $scope.reload = function () {
    $scope.records = [];
    $scope.recordsCount = 0;
    $scope.loadRecords();
  };

  $scope.changeSort = function (field) {
    if ($scope.sortField == field) {
      $scope.sortDirection = $scope.sortDirection == "ASC" ? "DESC" : "ASC";
    } else {
      $scope.sortField = field;
      $scope.sortDirection = "ASC";
    }

    $scope.reload();
  };

  $scope.changeFilter = function () {
    $scope.reload();
  }

});