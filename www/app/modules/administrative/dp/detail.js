
angular.module('os.administrative.dp.detail', ['os.administrative.models'])
  .controller('DpDetailCtrl', function($scope, $q, distributionProtocol, DeleteUtil, $modal) {
    $scope.distributionProtocol = distributionProtocol;
    
    $scope.editDp = function(property, value) {
      var d = $q.defer();
      d.resolve({});
      return d.promise;
    }

    $scope.deleteDp = function() {
      DeleteUtil.delete($scope.distributionProtocol, {onDeleteState: 'dp-list'});
    }
    
    function init() {
      $scope.distSites = getDistSiteList();
    }
    
    function getDistSiteList() {
      var siteList = '';
      angular.forEach(distributionProtocol.distributingSites, function (allSites) {
        angular.forEach(allSites.sites, function (siteName) {
          if (siteList === '') {
            siteList += siteName;
          }
          else {
            siteList += ', ' + siteName;
          }
        });
      });
      return siteList;
    };
    
    $scope.closeDp = function () {
      var modalInstance = $modal.open({
        templateUrl: 'modules/administrative/dp/close.html',
        controller: 'DpCloseCtrl',
        resolve: {
          distributionProtocol: function () {
            return distributionProtocol;
          }
        }
      });
    }
    
    $scope.reopenDp = function () {
      distributionProtocol.reopen();
    }
    
    init();

  });
