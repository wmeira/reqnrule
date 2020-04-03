angular.module('reqnrule')
    .controller('NovoMembroController', ['$scope', '$modalInstance', '$location', 'ProjetoService', 'UserService', 'projeto',
        function ($scope, $modalInstance, $location, ProjetoService, UserService, projeto) {

            var self = this;
            self.projeto = projeto;
            self.usuario = {};
            self.erro = {};
            self.numeroPapel = 4;

            self.papeis = [
                {valor:1, nome:'Gerente de Projeto'},
                {valor:2, nome:'Gerente de Requisitos'},
                {valor:3, nome:'Verificador'},
                {valor:4, nome:'Observador'}
            ];

            $scope.close = function () {
                $modalInstance.close();
            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            self.getUsuarioPorEmail = function() {
                self.erro = {};
                self.procurarMembroInvalido = true;
                $scope.searchForm.$setPristine();

                var promise = UserService.encontrarPorEmail(self.searchEmail);
                promise.then(
                    function(usuario) {
                        if(self.isUsuarioDuplicado(usuario)) {
                            self.erro.procurarMembro = ["Usu\u00e1rio j\u00e1 pertence ao projeto."];
                        } else {
                            self.novoUsuario = usuario;
                            self.procurarMembroInvalido = false;
                        }
                        self.searchEmail = "";
                    }, function(response) {
                        if(response.status == "400") {
                            self.erro.procurarMembro = ["E-mail de usu\u00e1rio n\u00e3o cadastrado."];
                        } else if(response.status == "401") {
                            $modalInstance.dismiss();
                            alert("Usu\u00e1rio n\u00e3o autorizado.")
                            $location.path('/login');
                            $location.replace();
                        } else if(response.status == "500") {
                            alert("Problema ao procurar usu\u00e1rio. Erro interno do servidor.");
                            self.searchEmail = "";
                        } else if(response.status == "0") {
                            $modalInstance.dismiss();
                            alert("Servidor inacess\u00edvel. Tente mais tarde.");
                            $location.path('/');
                            $location.replace();
                        }
                    }
                );
            };

            self.adicionarMembro = function() {
                var promise = ProjetoService.adicionarMembro(self.projeto, self.novoUsuario, self.numeroPapel);
                promise.then(
                    function(response) {
                        result = {tipo:'sucesso', msg:'Membro adicionado com sucesso!'};
                        $modalInstance.close(result);
                    },
                    function(response) {
                        if(response.status == "403") { //forbidden - violation
                            result = {tipo:'erro', msg:response.data.permissao};
                            $modalInstance.close(result);
                        } else if(response.status == "401") { //unauthorized
                            $modalInstance.dismiss();
                            alert("Usu\u00e1rio n\u00e3o autorizado.")
                            $location.path('/login');
                            $location.replace();
                        } else if(response.status == "500") {
                            alert("Problema ao adicionar membro. Erro interno do servidor.");
                        } else if(response.status == "0") {
                            $modalInstance.dismiss();
                            alert("Servidor inacess\u00edvel. Tente mais tarde.");
                            $location.path('/');
                            $location.replace();
                        }
                    }
                );
            };

            self.isUsuarioDuplicado = function(usuario) {
                for(var m in projeto.membros) {
                    var membro = projeto.membros[m];
                    if(membro.usuario.id == usuario.id) {
                        return true;
                    }
                }
                return false;
            }
        }
    ]);