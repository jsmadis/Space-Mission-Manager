controllers.controller('MissionsCtrl', function ($scope, $spaceHttp, $rootScope, $location) {

    if (typeof $rootScope.globals === 'undefined' || typeof $rootScope.globals.currentUser === 'undefined') {
        $location.path('login');
        return;
    }

    console.log('calling  /missions');
    $spaceHttp.loadMissions().then(function (response) {
        console.log(response);
        $scope.missions = response.data;
    });
    $spaceHttp.loadAstronauts().then(function (response) {
        console.log(response);
        $scope.astronauts = response.data;
    });
    $spaceHttp.loadSpacecrafts().then(function (response) {
        console.log(response);
        $scope.spacecrafts = response.data;
    });

    $scope.createNewMission = function () {
        $scope.editedMission =  {
            'active': true,
            'astronauts': [],
            'spacecrafts': []
        };
        $scope.create = true;
        $spaceHttp.loadAstronauts().then(function (value) {
            $scope.selectedAstronauts = value.data
        });
        $spaceHttp.loadSpacecrafts().then(function (value) {
            $scope.selectedSpacecrafts = value.data
        })
    };

    $scope.submitCreate = function () {
        var data = angular.copy($scope.editedMission);
        $spaceHttp.createMission(data).then(function (res) {
            $spaceHttp.loadMissions().then(function (response) {
                $scope.missions = response.data;
                $scope.create = false;
            }, function (error) {
                console.error(error);
            });
            $rootScope.successAlert = 'A new mission "' + data.name +'" was created'
        }, function (error) {
            console.error(error);
            $rootScope.errorAlert = 'Cannot create mission!';
        })
    };

    $scope.deleteMission = function (id) {
        $spaceHttp.deleteMission(id).then(function (response) {
            $scope.missions = response.data;
            $rootScope.successAlert = 'Removing was successful'
        }, function (error) {
            console.error(error);
        })
    };

    $scope.editMission = function (id) {
        $spaceHttp.getMission(id).then(function (response) {
            $scope.editedMission = response.data;
            $spaceHttp.loadAstronauts().then(function (value) {
                $scope.selectedAstronauts = value.data.concat(response.data.astronauts);
            });
            $spaceHttp.loadSpacecrafts().then(function (value) {
                $scope.selectedSpacecrafts = value.data.concat(response.data.spacecrafts);
            });
            $scope.editedMission.eta = $scope.editedMission.eta === null || $scope.editedMission.eta === undefined
                ? null : new Date($scope.editedMission.eta.substring(0, 16));
            $scope.edit = true;
        }, function (error) {
            console.error(error);
        })
    };

    $scope.cancelEdit = function () {
        $scope.create = false;
        $scope.edit = false;
    };

    $scope.submitEdit = function () {
        var data = angular.copy($scope.editedMission);
        $spaceHttp.updateMission(data).then(function (res) {
            $spaceHttp.loadMissions().then(function (response) {
                $scope.missions = response.data;
                $scope.edit = false;
            }, function (error) {
                console.error(error);
            });
            $rootScope.successAlert = 'The "' + data.name +'" mission was successfully edited'
        }, function (error) {
            console.error(error);
            $rootScope.errorAlert = 'Cannot update mission!';
        })
    };
});