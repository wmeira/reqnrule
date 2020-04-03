angular.module('reqnrule')
    .controller('RequisitoController', ['SERVER_CONFIG', 'ProjetoService', 'RequisitoService', '$location', '$scope', '$modalInstance', '$modal', 'projeto', 'membro', 'requisito', 'categorias',
        function(SERVER_CONFIG, ProjetoService, RequisitoService, $location, $scope, $modalInstance, $modal, projeto, membro, requisito, categorias) {

        var self = this;
        self.projeto = projeto;
        self.membro = membro;
        self.requisito = requisito;
        self.categorias = categorias;
        self.nomeAntigo = self.requisito.nome;
        self.categoriaSelect = self.requisito.categoria;

        self.estados = [
            {valor:'ESPECIFICADO', nome:'Especificado'},
            {valor:'APROVADO', nome:'Aprovado'},
            {valor:'IMPLEMENTADO', nome:'Implementado'},
            {valor:'VALIDADO', nome:'Validado'},
            {valor:'CANCELADO', nome:'Cancelado'},
        ];

        $scope.close = function () {
            $modalInstance.close();
        };

        $scope.confirmar = function () {
            $modalInstance.close();
        };

        $scope.cancel = function () {
            $modalInstance.dismiss();
        };

        $scope.uncheck = function (event) {
            if(self.paiSelecionado == null && event == null) {
                self.paiSelecionado = self.requisito.pai;
            } else {
                if(self.requisito.pai != null && self.requisito.pai.id == self.paiSelecionado.id) {
                    self.requisito.pai = null;
                    self.paiSelecionado = null;
                } else {
                    self.requisito.pai = {id:self.paiSelecionado.id};
                }
            }
            $scope.requisitoForm.$setDirty();
        };

        $scope.open = function(operacao, solicitacao) {
            var modalInstance;
            switch (operacao) {
                case 'solicitar-mudanca':
                    modalInstance = $modal.open({
                        templateUrl: 'views/solicitacao-mudanca.html',
                        controller: 'SolicitarMudancaController as solicitarMudancaCtrl',
                        resolve: {
                            requisito: self.requisito
                        }
                    });
                    break;
                case 'atender-solicitacao':
                    modalInstance = $modal.open({
                        templateUrl: 'views/atender-solicitacao.html',
                        controller: 'AtenderSolicitacaoController as atenderSolicitacaoCtrl',
                        resolve: {
                            solicitacao: solicitacao,
                            membro: self.membro
                        }
                    });
                    break;
                case 'visualizar-solicitacao':
                    modalInstance = $modal.open({
                        templateUrl: 'views/visualizar-solicitacao.html',
                        controller: 'VisualizarSolicitacaoController as visualizarSolicitacaoCtrl',
                        resolve: {
                            solicitacao: solicitacao
                        }
                    });
                    break;
                default:
                    break;
            }

            modalInstance.result.then(
                function (result) {
                    if(result != null) {
                        $modalInstance.close(result);
                    }
                    $scope.requisitoForm.$setDirty();
                }
            );
        };



        self.iniciarTabelaRelacionamentos = function() {
            self.iniciarPai();
            self.tamanhoTabelaRelacionamento = self.calcularTamanhoTabela(self.projeto.requisitos.length - 1);
            self.iniciarAssociacoes();
        };

        self.iniciarTabelaSolicitacao = function() {
            self.solicitacoesAbertas = self.obterSolicitacoesAbertas();
            self.tamanhoTabelaSolicitacao = self.calcularTamanhoTabela(self.solicitacoesAbertas.length);
        };

        self.obterSolicitacoesAbertas = function() {
            var solicitacoesAbertas = [];
            for(var s in self.requisito.solicitacoesMudanca) {
                if(self.requisito.solicitacoesMudanca[s].estado == 'SOLICITADO') {
                    solicitacoesAbertas.push(self.requisito.solicitacoesMudanca[s]);
                }
            }
            return solicitacoesAbertas;
        };

        self.iniciarTabelaHistorico = function() {
            self.tamanhoTabelaHistorico = self.calcularTamanhoTabela(self.requisito.mudancas.length);
        };

        self.iniciarPai = function() {
            var pai = self.requisito.pai;
            if(pai != null) {
                for(var r in self.projeto.requisitos) {
                    if(self.projeto.requisitos[r].id == pai.id) {
                        self.paiSelecionado = self.projeto.requisitos[r];
                        return;
                    }
                }
            }
        };

        self.iniciarAssociacoes = function () {
           self.associacoes = [];
            for(var r in self.projeto.requisitos) {
               var requisito = self.projeto.requisitos[r];
               if(requisito.estado != 'CANCELADO' && requisito.id != self.requisito.id)  {
                   var associacao = {};
                   associacao.requisito = requisito;
                   associacao.checked = self.contemAssociacao(requisito);
                   self.associacoes.push(associacao);
               }
           }
        };

        self.contemAssociacao = function(requisito) {
            for(var r in self.requisito.associados) {
                if(self.requisito.associados[r].id == requisito.id) {
                    return true;
                }
            }
            return false;
        };

        self.calcularTamanhoTabela = function(length) {
            if(length > 3) {
                return  '151px';
            } else {
                return (52 + length * 33) + 'px';
            }
        };

        self.alterarRequisito = function() {
            self.erro = {};
            self.requisitoInvalido = false;
            self.ajustarCategoria();
            self.ajustarAssociacoes();

            var promise = RequisitoService.alterarRequisito(self.requisito);
            promise.then(
                function(requisito) {
                    result = {tipo:'sucesso', msg:'Requisito alterado com sucesso!'};
                    $modalInstance.close(result);
                },
                function(response) {
                    self.requisitoInvalido = true;

                    if(response.status == "400") { //bad request
                        self.erro.nome = [response.data.nome];
                        self.erro.descricao = [response.data.descricao];
                        self.erro.categoria = [response.data.categoria];
                        self.erro.associados = [response.data.associados];
                    } else if(response.status == "403") {
                        self.erro.associados = [response.data.associados];
                    } else if(response.status == "401") {
                        $modalInstance.dismiss();
                        alert("Usu\u00e1rio n\u00e3o autorizado.")
                        $location.path('/login');
                        $location.replace();
                    } else if(response.status == "500") {
                        alert("Problema ao alterar requisito. Erro interno do servidor.");
                    } else if(response.status == "0") {
                        $modalInstance.dismiss();
                        alert("Servidor inacess\u00edvel. Tente mais tarde.");
                        $location.path('/');
                        $location.replace();
                    }
                }
            );

        };

        self.podeAlterarRequisito = function() {
            var papel = SERVER_CONFIG.PAPEL[self.membro.papel]
            return papel == SERVER_CONFIG.PAPEL.GERENTE_DE_PROJETO || papel == SERVER_CONFIG.PAPEL.GERENTE_DE_REQUISITOS;
        };

        self.podeSolicitarMudanca = function() {
            var papel = SERVER_CONFIG.PAPEL[self.membro.papel]
            return papel == SERVER_CONFIG.PAPEL.GERENTE_DE_PROJETO || papel == SERVER_CONFIG.PAPEL.GERENTE_DE_REQUISITOS || papel == SERVER_CONFIG.PAPEL.VERIFICADOR;
        }

        self.getEstadoRequisito = function(requisito) {
            return SERVER_CONFIG.ESTADO_REQUISITO[requisito.estado];
        };

        self.getTipoRequisito = function(requisito) {
            return SERVER_CONFIG.TIPO_REQUISITO[requisito.tipo];
        };

        self.getPrioridade = function(requisito) {
            return SERVER_CONFIG.PRIORIDADE[requisito.prioridade];
        };

        self.limpaAssociacoes = function() {
            for(var r in self.projeto.requisitos) {
                delete self.projeto.requisitos[r].checked;
            }
        };

        self.ajustarCategoria = function() {
            if(self.categoriaInput != null) {
                self.requisito.categoria = self.categoriaInput;
            } else {
                self.requisito.categoria = self.categoriaSelect;
            }
        };

        self.ajustarAssociacoes = function() {
            self.requisito.associados = [];
            for(var r in self.associacoes) {
                var associado = self.associacoes[r];
                if(associado.checked) {
                    self.requisito.associados.push({id:associado.requisito.id});
                }
            }
        };


        self.getDataFormatada = function(milis) {
            var data = new Date(milis);
            return ("0" + data.getHours()).slice(-2) + ":" + ("0" + data.getMinutes()).slice(-2) + " " +
                ("0" + data.getDate()).slice(-2) + "/" + ("0" + (data.getMonth() + 1)).slice(-2) + "/" + data.getFullYear();

        };

        self.cancelarAtendimento = function(solicitacao) {
            solicitacao.estado = 'SOLICITADO';
            solicitacao.observacoes = null;
        };


    }]);