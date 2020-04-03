angular.module('reqnrule')
    .controller('UsuarioController', ['AuthService', 'UserService', '$location', '$scope', function(AuthService, UserService, $location, $scope) {
        var self = this;
        self.erro = {};
        self.usuario = AuthService.getUser();

        self.alterar = function() {
            var promise = UserService.alterar(self.usuario);
            promise.then(
                function(response) {
                    self.alteracaoInvalida = false;
                    self.alteracaoValida = true;
                    $scope.usuarioForm.$setPristine();
                },
                function(response) {
                    self.erro = {};
                    self.alteracaoInvalida = true;
                    self.alteracaoValida = false;

                    if(response.status == "403") { //constraint violation
                        self.erro.companhia = [response.data.companhia];
                        self.erro.nome = [response.data.nome];
                    } else if(response.status == "401") { //auth_token inválido
                        alert("Usu\u00e1rio n\u00e3o autorizado.")
                        $location.path('/login');
                        $location.replace();
                    }else if(response.status == "500") { //internal server
                        alert("Problema ao alterar perfil de usu\u00e1rio. Erro interno do servidor.");
                        self.resetAlterarForm();
                    } else if(response.status == "0") { //server não encontrado
                        alert("Servidor inacess\u00edvel. Tente mais tarde.");
                        $location.path('/');
                        $location.replace();
                    }
                });
        }

        self.alterarSenha = function() {
            UserService.alterarSenha(self.senhaAntiga, self.senhaNova)
                .then(
                    function(response) {
                        self.senhaInvalida = false;
                        self.senhaValida = true;
                        self.clearSenhaForm();
                    },
                    function(response) {
                    self.erro = {};
                    self.senhaInvalida = true;
                    self.senhaValida = false;

                    if(response.status == "400") {  //bad_request
                        self.erro.senhaAntiga = [response.data.senhaantiga];
                        self.erro.senhaNova = [response.data.senhanova];
                        self.clearSenhaForm();
                    } else if(response.status == "401") { //auth_token inválido
                        alert("Usu\u00e1rio n\u00e3o autorizado.")
                        $location.path('/login');
                        $location.replace();
                    } else if(response.status == "500") { //internal server
                        alert("Problema ao alterar senha. Erro interno do servidor.");
                        self.clearSenhaForm();
                    } else if(response.status == "0") { //server não encontrado
                        alert("Servidor inacess\u00edvel. Tente mais tarde.");
                        $location.path('/');
                        $location.replace();
                    }
                });
        }

        self.resetAlterarForm = function() {
            self.usuario = AuthService.getUser();
            $scope.usuarioForm.$setPristine();
        }

        self.clearSenhaForm = function() {
            self.senhaAntiga = "";
            self.senhaNova = "";
            $scope.senhaForm.$setPristine();
        }
    }]);