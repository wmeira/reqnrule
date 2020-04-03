angular.module('reqnrule')
    .controller('DeletarProjetoController', ['$scope', '$modalInstance', '$location', 'ProjetoService', 'projeto',
        function ($scope, $modalInstance, $location, ProjetoService, projeto) {

            var self = this;
            self.projeto = projeto;

            $scope.close = function () {
                $modalInstance.close();
            };


            $scope.confirmar = function () {
                self.deletarProjeto();
                $modalInstance.close();
            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            self.deletarProjeto = function() {
                var promise = ProjetoService.deletarProjeto(self.projeto.id);
                promise.then(
                    function(response) {
                        //alert("Projeto deletado com sucesso");
                        $location.path("/projetos");
                        $location.replace();
                    },
                    function(response) {

                        if(response.status == "403") { //forbidden - violation
                            alert(response.data.permissao);
                        } else if(response.status == "401") { //unauthorized
                            alert("Usu\u00e1rio n\u00e3o autorizado.")
                            $location.path('/login');
                            $location.replace();
                        } else if(response.status == "500") {
                            alert("Problema ao deletar projeto. Erro interno do servidor.");
                        } else if(response.status == "0") {
                            alert("Servidor inacess\u00edvel. Tente mais tarde.");
                            $location.path('/');
                            $location.replace();
                        }
                    }
                );
            };
        }
    ]);