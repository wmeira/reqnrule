angular.module('reqnrule')
    .controller('LoginController', ['AuthService','$location', '$scope', function(AuthService, $location, $scope) {
        var self = this;
        self.loginInvalido = "";

        self.login = function() {
            var promise = AuthService.login(self.email, self.senha);
            return promise.then(
                function(response) {
                    return AuthService.session().then(
                        function(response) {
                            $location.path('/usuario');
                        },
                        function(response) {}
                    );
                },
                function(response) {
                    if(response.status == "401") { //acesso nao autorizado
                        self.loginInvalido = response.data.erro;
                        self.clearForm();
                    } else if(response.status == "500") { //internal server
                        alert("Problema ao solicitar acesso. Erro interno do servidor.");
                        self.clearForm();
                    } else if(response.status == "0") {
                        alert("Servidor inacess\u00edvel. Tente mais tarde.");
                        $location.path('/');
                        $location.replace();
                    }
                });
        }

        self.clearForm = function() {
            self.email = "";
            self.senha = "";
            $scope.loginForm.$setPristine();
        }
    }]);