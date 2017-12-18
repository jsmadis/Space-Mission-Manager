
controllers.controller('SpacecraftsCtrl', function ($scope, $spaceHttp, $rootScope, $location) {

    if (typeof $rootScope.globals === 'undefined' || typeof $rootScope.globals.currentUser === 'undefined') {
        $location.path('login');
        return;
    }

    console.log('calling /spacecrafts');
    $spaceHttp.getAllSpacecrafts().then(function (response) {
        $scope.spacecrafts = response.data;
    }, function (error) {
        console.error(error);
    });

    $scope.deleteSpacecraft = function (id) {
        $spaceHttp.deleteSpacecraft(id).then(function (response) {
            $scope.spacecrafts = response.data;
            $rootScope.successAlert = 'Removing was successful'
        }, function (error) {
            console.error(error);
        })
    };

    $scope.editSpacecraft = function (id) {
        $spaceHttp.getSpacecraft(id).then(function (response) {
            $scope.editedSpacecraft = response.data;
            $spaceHttp.getAllAvailableCraftComponents().then(function (res) {
               $scope.availableAndSelectedCC = res.data.concat(response.data.components);
            });
            $scope.edit = true;
        }, function (error) {   
            console.error(error);
        })
    };

    $scope.submitEdit = function () {
        var data = angular.copy($scope.editedSpacecraft);
        $spaceHttp.updateSpacecraft(data).then(function (res) {
            $spaceHttp.getAllSpacecrafts().then(function (response) {
                $scope.spacecrafts = response.data;
                $scope.edit = false;
            }, function (error) {
                console.error(error);
            });
            $rootScope.successAlert = 'A new spacecraft "' + data.name +'" was created'
        }, function (error) {
            console.error(error);
            $rootScope.errorAlert = 'Cannot update spacecraft!';

        })
    };

    $scope.createSpacecraft = function () {
        $scope.editedSpacecraft = {};
        $scope.create = true;
        $spaceHttp.getAllAvailableCraftComponents().then(function (value) {
            $scope.availableAndSelectedCC = value.data
        })
    };

    $scope.submitCreate = function () {
        var data = angular.copy($scope.editedSpacecraft);
        $spaceHttp.createSpacecraft(data).then(function (res) {
            $spaceHttp.getAllSpacecrafts().then(function (response) {
                $scope.spacecrafts = response.data;
                $scope.create = false;
            }, function (error) {
                console.error(error);
            });
            $rootScope.successAlert = 'A new spacecraft "' + data.name +'" was created'
        }, function (error) {
            console.error(error);
            $rootScope.errorAlert = 'Cannot create spacecraft!';
        })
    };

    $scope.cancelEdit = function () {
        $scope.create = false;
        $scope.edit = false;
    }

});
