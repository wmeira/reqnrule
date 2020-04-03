angular.module('reqnrule')
    .controller('SignupController', ['UserService', '$location', '$scope', function(UserService, $location, $scope) {
        var self = this;
        self.erro = {};
        self.usuario = {};

        self.signup = function() {
            var promise = UserService.signup(self.usuario);
            promise.then(
                function(response) {
                    $location.path("/login");
                    alert("Conta criada com sucesso!");
                },
                function(response) {
                    self.erro = {};
                    self.signupInvalido = true;
                    if(response.status == "400") {  //senha exception
                        self.erro.senha = [response.data.senha];
                    } else if(response.status == "403") { //constraint violation
                        self.erro.email = [response.data.email];
                        self.erro.companhia = [response.data.companhia];
                        self.erro.nome = [response.data.nome];
                        self.erro.senha = [response.data.senha];
                    } else if(response.status == "409") { //conflito email
                        self.erro.email = [response.data.email];
                    } else if(response.status == "500") { //internal server
                        alert("Problema ao solicitar acesso. Erro interno do servidor.");
                        self.clearForm();
                    } else if(response.status == "0") { //server não encontrado
                        alert("Servidor inacess\u00edvel. Tente mais tarde.");
                        $location.path('/');
                        $location.replace();
                    }
                });
        }

        self.clearForm = function() {
            self.usuario = {};
            $scope.signupForm.$setPristine();
        }
    }]);