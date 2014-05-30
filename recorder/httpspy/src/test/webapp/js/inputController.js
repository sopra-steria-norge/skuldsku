function InputController($scope,$http) {
    $scope.name = "Who Cares";

    $scope.postName = function() {
      var postData = {
          firstname: $scope.firstname,
          lastname: $scope.lastname
      };
      $http({
          method: "POST",
          url: "data",
          data: postData
      }).success(function(data) {
          $scope.name = data.name;
      });
    };
}