angular.module('reqnrule')
    .controller('SairProjetoController', ['$scope', '$modalInstance', '$location', 'ProjetoService', 'projeto', 'membro',
        function ($scope, $modalInstance, $location, ProjetoService, projeto, membro) {

            var self = this;
            self.projeto = projeto;
            self.membro = membro;

            $scope.close = function () {
                $modalInstance.close();
            };


            $scope.confirmar = function () {
                self.sairProjeto();
            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            self.sairProjeto = function() {
                var promise = ProjetoService.alterarMembro(self.membro, 5); //5 == excluido
                promise.then(
                    function(response) {
                        //alert("Usu\u00e1rio saiu do projeto com sucesso.");
                        $location.path("/projetos");
                        $location.replace();
                        $modalInstance.close();
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
                            alert("Problema ao sair do projeto. Erro interno do servidor.");
                        } else if(response.status == "0") {
                            $modalInstance.dismiss();
                            alert("Servidor inacess\u00edvel. Tente mais tarde.");
                            $location.path('/');
                            $location.replace();
                        }
                    }
                );
            };
        }
    ]);