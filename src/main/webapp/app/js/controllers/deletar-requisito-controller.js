angular.module('reqnrule')
    .controller('DeletarRequisitoController', ['SERVER_CONFIG', '$scope', '$modalInstance', '$location', 'RequisitoService', 'ProjetoService', 'projeto', 'requisito',
        function (SERVER_CONFIG, $scope, $modalInstance, $location, RequisitoService, ProjetoService, projeto, requisito) {

            var self = this;
            self.projeto = projeto;
            self.requisito = requisito;

            self.getEstado= function(requisito) {
                return SERVER_CONFIG.ESTADO_REQUISITO[self.requisito.estado];
            };

            self.getTipo = function(requisito) {
                return SERVER_CONFIG.TIPO_REQUISITO[self.requisito.tipo];
            };

            self.getPrioridade = function(requisito) {
                return SERVER_CONFIG.PRIORIDADE[self.requisito.prioridade];
            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            self.deletarRequisito = function() {
                var promise = RequisitoService.deletarRequisito(self.requisito);
                promise.then(
                    function() {
                        result = {tipo:'sucesso', msg:'Requisito deletado com sucesso!'};
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
                            alert("Problema ao deletar requisito. Erro interno do servidor.");
                        } else if(response.status == "0") {
                            $modalInstance.dismiss();
                            alert("Servidor inacess\u00edvel. Tente mais tarde.");
                            $location.path('/');
                            $location.replace();
                        }
                    }
                );
            };



        }]);