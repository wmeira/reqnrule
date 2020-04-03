angular.module('reqnrule')
    .controller('SolicitarMudancaController', ['$scope', '$modalInstance', '$location', 'RequisitoService', 'requisito',
        function ($scope, $modalInstance, $location, RequisitoService, requisito) {

            var self = this;
            self.requisito = requisito;

            $scope.close = function () {
                $modalInstance.close();
            };

            $scope.confirmar = function () {
                $modalInstance.close();
            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            self.solicitarMudanca = function() {
                var promise = RequisitoService.solicitarMudanca(self.requisito.id, self.solicitacao);
                promise.then(
                    function() {
                        result = {tipo:'sucesso', msg:'Solicita\u00e7\u00e3o realizada com sucesso!'};
                        $modalInstance.close(result);
                    },
                    function(response) {
                        self.solicitacaoInvalida = true;
                    }
                );
            }
        }
    ]);