
angular.module('openspecimen')
  .factory('AuthService', function($http, $window, ApiUtil, ApiUrls) {
    var url = function() {
      return ApiUrls.getUrl('sessions');
    };

    return {
      authenticate: function(loginData) {
        return $http.post(url(), loginData).then(ApiUtil.processResp);
      },

      logout: function() {
        var q = $http.delete(url());
        this.removeToken();
        return q.then(ApiUtil.processResp);
      },

      saveToken: function(token) {
        $window.localStorage['osAuthToken'] = token;
        $http.defaults.headers.common['X-OS-API-TOKEN'] = token;
        $http.defaults.withCredentials = true;
      },

      removeToken: function() {
        delete $window.localStorage['osAuthToken'];
        delete $http.defaults.headers.common['X-OS-API-TOKEN'];
        delete $http.defaults.headers.common['Authorization'];
      }
    }
  })
  .controller('LoginCtrl', function($scope, $rootScope, $state, $http, $location, AuthService) {

    function init() {
      $scope.loginData = {domainName: 'openspecimen'};
      
      if ($location.search().logout) {
        $scope.logout();
        return;
      }
 
      if ($http.defaults.headers.common['X-OS-API-TOKEN']) {
        $state.go('cp-list');
      }
    }

    function onLogin(result) {
      $scope.loginError = false;

      if (result.status == "ok" && result.data) {
        $rootScope.currentUser = {
          id: result.data.id,
          firstName: result.data.firstName,
          lastName: result.data.lastName,
          loginName: result.data.loginName
        };
        $rootScope.loggedIn = true;
        AuthService.saveToken(result.data.token);
        $state.go('cp-list');
      } else {
        $rootScope.currentUser = {};
        $rootScope.loggedIn = false;
        AuthService.removeToken();
        $scope.loginError = true;
      }
    };

    $scope.login = function() {
      AuthService.authenticate($scope.loginData).then(onLogin);
    }

    $scope.logout = function() {
      if ($http.defaults.headers.common['X-OS-API-TOKEN']) {
        AuthService.logout();
      }
    }

    init();
  });
