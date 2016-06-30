var recordsApp = angular.module('recordsApp', []);

recordsApp.controller('RecordsCtrl', function RecordsController($scope, $http) {

  $scope.pageSize = 20;
  $scope.recordsCount = 0;

  $scope.records = [];

  $scope.records.count = 0;

  $scope.sort = {sortField: "ID", sortDirection: "ASC"};

  $scope.filter = {id: null, number: null, date: null, amount: null};

  $scope.getCount = function () {
    var httpRequest = $http({
      method: 'GET',
      url: '/records/count',
      params: {
        "filterId": $scope.filter.id,
        "filterNumber": $scope.filter.number,
        "filterDate": $scope.filter.date,
        "filterAmount": $scope.filter.amount
      }
    }).success(function (data, status) {
      $scope.records.count = data;
      console.log("got records count:" + data);
    });

  };

  $scope.loadRecords = function () {
    var httpRequest = $http({
      method: 'GET',
      url: '/records',
      params: {
        "filterId": $scope.filter.id,
        "filterNumber": $scope.filter.number,
        "filterDate": $scope.filter.date,
        "filterAmount": $scope.filter.amount,
        "sortField": $scope.sort.sortField,
        "sortDirection": $scope.sort.sortDirection,
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
    $scope.getCount();

    $scope.loadRecords();
  };

  $scope.changeSort = function (field) {
    if ($scope.sort.sortField == field) {
      $scope.sort.sortDirection = $scope.sort.sortDirection == "ASC" ? "DESC" : "ASC";
    } else {
      $scope.sort.sortField = field;
      $scope.sort.sortDirection = "ASC";
    }

    $scope.reload();
  };

  $scope.changeFilter = function () {
    $scope.reload();
  }

});