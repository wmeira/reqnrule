angular.module('reqnrule')
    .controller('VisualizarSolicitacaoController', ['$scope', '$modalInstance', 'solicitacao',
        function ($scope, $modalInstance, solicitacao) {

            var self = this;
            self.solicitacao = solicitacao;

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            self.getDataFormatada = function(milis) {
                var data = new Date(milis);
                return ("0" + data.getHours()).slice(-2) + ":" + ("0" + data.getMinutes()).slice(-2) + " " +
                    ("0" + data.getDate()).slice(-2) + "/" + ("0" + (data.getMonth() + 1)).slice(-2) + "/" + data.getFullYear();

            };
        }
    ]);