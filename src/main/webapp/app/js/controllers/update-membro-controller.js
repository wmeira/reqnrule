angular.module('reqnrule')
    .controller('UpdateMembroController', ['SERVER_CONFIG','$scope', '$modalInstance', '$location', 'ProjetoService', 'membro',
        function (SERVER_CONFIG, $scope, $modalInstance, $location, ProjetoService, membro) {

            var self = this;
            self.membro = membro;
            self.novoPapel = 4;

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

            self.alterarMembro = function() {
                var promise = ProjetoService.alterarMembro(self.membro, self.novoPapel);
                promise.then(
                    function(response) {
                        result = {tipo:'sucesso', msg:'Membro alterado com sucesso!'};
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
                            alert("Problema ao alterar membro. Erro interno do servidor.");
                        } else if(response.status == "0") {
                            $modalInstance.dismiss();
                            alert("Servidor inacess\u00edvel. Tente mais tarde.");
                            $location.path('/');
                            $location.replace();
                        }
                    }
                );
            };

            self.excluirMembro = function() {
                var promise = ProjetoService.alterarMembro(self.membro, 5);
                promise.then(
                    function(response) {
                        result = {tipo:'sucesso', msg:'Membro exclu\u00eddo com sucesso!'};
                        $modalInstance.close(result);
                    },
                    function(response) {
                        if(response.status == "403") { //forbidden - violation
                            result = {tipo:'erro', msg:response.data.permissao};
                            $modalInstance.close(result);
                        } else if(response.status == "401") { //unauthorized
                            $modalInstance.dismiss();
                            alert("Usu\u00e1rio n\u00e3o autorizado.");
                            $location.path('/login');
                            $location.replace();
                        } else if(response.status == "500") {
                            alert("Problema ao excluir membro. Erro interno do servidor.");
                        } else if(response.status == "0") {
                            $modalInstance.dismiss();
                            alert("Servidor inacess\u00edvel. Tente mais tarde.");
                            $location.path('/');
                            $location.replace();
                        }
                    }
                );
            };

            self.getPapel = function() {
                return SERVER_CONFIG.PAPEL[self.membro.papel];
            }
        }
    ]);