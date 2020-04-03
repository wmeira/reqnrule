angular.module('reqnrule')
    .controller('NovoProjetoController', ['SERVER_CONFIG', 'ProjetoService', '$location', '$scope',
        function(SERVER_CONFIG, ProjetoService, $location, $scope) {

            var self = this;
            self.erro = {};
            self.projeto = {};
            self.novoProjetoInvalido = false;

            self.novoProjeto = function() {
                var promise = ProjetoService.novoProjeto(self.projeto);
                promise.then(
                    function(projeto) {
                        alert("Projeto criado com sucesso. O usu\u00e1rio ser\u00e1 redirecionado para a p\u00e1gina do projeto para continuar o cadastro.")
                        $location.path("/projeto/" + projeto.id);
                        $location.replace();
                    },
                    function(response) {
                        self.erro = {};
                        self.novoProjetoInvalido = true;

                        if(response.status == "403") { //constraint violation
                            self.erro.nome = [response.data.nome];
                            self.erro.descricao = [response.data.descricao];
                        } else if(response.status == "401") { //auth_token inválido
                            alert("Usu\u00e1rio n\u00e3o autorizado.")
                            $location.path('/login');
                            $location.replace();
                        }else if(response.status == "500") { //internal server
                            alert("Problema ao criar novo projeto. Erro interno do servidor.");
                            self.resetNovoProjetoForm();
                        } else if(response.status == "0") { //server não encontrado
                            alert("Servidor inacess\u00edvel. Tente mais tarde.");
                            $location.path('/');
                            $location.replace();
                        }
                    }
                );
            };

            self.resetNovoProjetoForm = function() {
                self.projeto = {};
                $scope.novoProjetoForm.$setPristine();
            };

        }]);