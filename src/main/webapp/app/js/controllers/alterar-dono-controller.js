angular.module('reqnrule')
    .controller('AlterarDonoController', ['SERVER_CONFIG','$scope', '$modalInstance', '$location', 'ProjetoService', 'projeto',
        function (SERVER_CONFIG, $scope, $modalInstance, $location, ProjetoService, projeto) {

            var self = this;
            self.projeto = projeto;

            $scope.close = function () {
                $modalInstance.close();
            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };

            self.alterarDono = function() {
                var promise = ProjetoService.alterarDonoProjeto(self.novoDono);
                promise.then(
                    function(response) {
                        result = {tipo:'sucesso', msg:'Dono alterado com sucesso!'};
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
                            alert("Problema ao alterar dono do projeto. Erro interno do servidor.");
                        } else if(response.status == "0") {
                            $modalInstance.dismiss();
                            alert("Servidor inacess\u00edvel. Tente mais tarde.");
                            $location.path('/');
                            $location.replace();
                        }
                    }
                );
            };

            self.initGerentesDeProjeto = function() {
                self.gerentesDeProjeto = self.getGerentesDeProjeto();
            }

            self.getGerentesDeProjeto = function() {
                var listaGerentesDeProjeto = [];
                for(var m in self.projeto.membros) {
                    var membro = self.projeto.membros[m];
                    if(membro.papel == 'GERENTE_DE_PROJETO') {
                        if(membro.usuario.id != self.projeto.dono.id) {
                            listaGerentesDeProjeto.push(membro);
                        }
                    }
                }
                return listaGerentesDeProjeto;
            };

            self.getPapel = function() {
                return SERVER_CONFIG.PAPEL[self.membro.papel];
            };
        }
    ]);
