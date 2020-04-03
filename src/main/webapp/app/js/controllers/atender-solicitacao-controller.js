angular.module('reqnrule')
    .controller('AtenderSolicitacaoController', ['SERVER_CONFIG', '$scope', '$modalInstance', '$location', 'solicitacao', 'membro',
        function (SERVER_CONFIG, $scope, $modalInstance, $location, solicitacao, membro) {

            var self = this;
            self.solicitacao = solicitacao;
            self.membro = membro;

            $scope.close = function () {
                $modalInstance.close();
            };

            $scope.confirmar = function () {
                $modalInstance.close();
            };

            $scope.cancel = function () {
                self.solicitacao.observacoes = null;
                $modalInstance.dismiss();
            };

            self.atenderSolicitacao = function() {
                self.solicitacao.estado = 'ATENDIDO';
                $modalInstance.close();
            };

            self.rejeitarSolicitacao = function() {
                self.solicitacao.estado = 'REJEITADO';
                $modalInstance.close();
            };


            self.getDataFormatada = function(milis) {
                var data = new Date(milis);
                return ("0" + data.getHours()).slice(-2) + ":" + ("0" + data.getMinutes()).slice(-2) + " " +
                    ("0" + data.getDate()).slice(-2) + "/" + ("0" + (data.getMonth() + 1)).slice(-2) + "/" + data.getFullYear();

            };

            self.podeAtender = function() {
                var papel = SERVER_CONFIG.PAPEL[self.membro.papel]
                return papel == SERVER_CONFIG.PAPEL.GERENTE_DE_PROJETO || papel == SERVER_CONFIG.PAPEL.GERENTE_DE_REQUISITOS;
            }
        }
    ]);