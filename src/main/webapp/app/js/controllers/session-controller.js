angular.module('reqnrule')
    .controller('SessionController', ['AuthService', function(AuthService) {
        var self = this;

        self.getUser = function() {
            return AuthService.getUser();
        }

        self.hasUser = function() {
            return AuthService.hasUser();
        };

        self.logout = function() {
            return AuthService.logout();
        };

        self.getPrimeiroNome = function() {
            var nome = self.getUser().nome;
            return nome.split(" ")[0];
        };

    }]);