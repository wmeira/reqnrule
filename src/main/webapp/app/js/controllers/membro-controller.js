angular.module('reqnrule')
    .controller('MembroController', ['SERVER_CONFIG', 'AuthService','$location', '$scope', '$modal', function(SERVER_CONFIG, AuthService, $location, $scope, $modal) {
        var self = this;

        self.getPapel = function(membro) {
            return SERVER_CONFIG.PAPEL[membro.papel];
        };
    }]);