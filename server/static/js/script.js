
var receiptApp = angular.module('receiptApp', ["mcm.charts"]);

receiptApp.controller('mainController', function($scope, $http) {
		// create a message to display in our view
//		$scope.message = 'Everyone come and see how good I look!';
    $scope.piechart_data={terms: [{term:"A", count:20}, {term:"B", count:40}]};
    $scope.user_data = [];
    $scope.value = "total_spent";
    $scope.grouping = ["product_name"];
    $scope.email_address="m4jkel@gmail.com";
$scope.get_data = function(email){
	var promise = $http.get("http://172.27.0.61:5000/api/stats?email="+email);
      promise.then(function(data){
        $scope.user_data = data.data;
      var terms = [];
      for(var i=0; i<data.data.length; i++) {
                    terms.push({term: data.data[i]["product_name"], count: data.data[i]["total_spent"]})
        }
        $scope.piechart_data = {terms:terms};
      }, function(){
        console.log("ERROR")
    });

	// var promise = $http.get("/mcm/restapi/dashboard/get_stats/all");
	// promise.then(function(data, status){
	// 	$scope.dashboard_stats = data.data;
	//     }, function(data, status){
	// 	alert("Error getting stats "+status);
	//     });
    };

    $scope.get_data($scope.email_address);

    $scope.$watch("email_address", function() {
     $scope.get_data($scope.email_address);
    })
	});


