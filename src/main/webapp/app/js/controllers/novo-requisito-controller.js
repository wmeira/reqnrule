angular.module('reqnrule')
    .controller('NovoRequisitoController', ['SERVER_CONFIG', 'RequisitoService', 'ProjetoService', '$scope', '$modalInstance', '$location', 'projeto', 'categorias',
        function(SERVER_CONFIG, RequisitoService, ProjetoService, $scope, $modalInstance, $location, projeto, categorias) {

            var self = this;
            self.projeto = projeto;
            self.categorias = categorias;


            self.prioridade = [
                {valor:'ESSENCIAL', nome:'Essencial'},
                {valor:'DESEJADO', nome:'Desejado'},
                {valor:'OPCIONAL', nome:'Opcional'}
            ];

            self.estados = [
                {valor:'ESPECIFICADO', nome:'Especificado'},
                {valor:'APROVADO', nome:'Aprovado'},
                {valor:'IMPLEMENTADO', nome:'Implementado'},
                {valor:'VALIDADO', nome:'Validado'},
                {valor:'CANCELADO', nome:'Cancelado'},
            ];

            self.requisito = {};
            self.requisito.tipo = 'REQUISITO_FUNCIONAL';
            self.requisito.prioridade = 'ESSENCIAL';
            self.requisito.estado = 'ESPECIFICADO';
            self.categoriaSelect = 'NENHUMA';

            self.requisito.pai = null;
            self.paiSelecionado = {};

            $scope.cancel = function () {
                self.limpaAssociacoes();
                $modalInstance.dismiss();
            };

            $scope.close = function () {
                self.limpaAssociacoes();
                $modalInstance.close();
            };

            $scope.confirmar = function () {
                self.limpaAssociacoes();
                $modalInstance.close();
            };

            $scope.uncheck = function (event) {
                if(self.requisito.pai != null && self.requisito.pai.id == self.paiSelecionado.id) {
                    self.requisito.pai = null;
                    self.paiSelecionado = null;
                } else {
                    self.requisito.pai = {id:self.paiSelecionado.id};
                }
            };

            self.calcularTamanhoTabela = function() {
                if(self.projeto.requisitos.length > 3) {
                    self.tamanhoTabela =  '151px';
                } else {
                    self.tamanhoTabela = (52 + self.projeto.requisitos.length * 33) + 'px';
                }
            };

            self.iniciarTabelaRelacionamento = function() {
                self.iniciarAssociacoes();
                self.calcularTamanhoTabela();
            };

            self.iniciarAssociacoes = function () {
                self.associacoes = [];
                for(var r in self.projeto.requisitos) {
                    var requisito = self.projeto.requisitos[r];
                    if(requisito.estado != 'CANCELADO' && requisito.id != self.requisito.id)  {
                        var associacao = {};
                        associacao.requisito = requisito;
                        associacao.checked = false;
                        self.associacoes.push(associacao);
                    }
                }
            };

            self.adicionarRequisito = function() {
                self.erro = {};
                self.novoRequisitoInvalido = false;
                self.ajustarCategoria();
                self.setAssociacoes();
                self.ajustarAssociacoes();

                var promise = RequisitoService.adicionarRequisito(self.projeto, self.requisito);
                promise.then(
                    function(requisito) {
                        result = {tipo:'sucesso', msg:'Requisito adicionado com sucesso!'};
                        $modalInstance.close(result);
                    },
                    function(response) {
                        self.novoRequisitoInvalido = true;

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
                            alert("Problema ao adicionar novo requisito. Erro interno do servidor.");
                        } else if(response.status == "0") {
                            $modalInstance.dismiss();
                            alert("Servidor inacess\u00edvel. Tente mais tarde.");
                            $location.path('/');
                            $location.replace();
                        }
                    }
                );
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

            self.setAssociacoes = function() {
                self.requisito.associados = [];
                for(var r in self.projeto.requisitos) {
                    var requisito = self.projeto.requisitos[r];
                    if(requisito.checked) {
                        self.requisito.associados.push({id:requisito.id});
                    }
                }
            };

            self.limpaAssociacoes = function() {
                for(var r in self.projeto.requisitos) {
                    delete self.projeto.requisitos[r].checked;
                }
            };

            self.getEstadoRequisito = function(requisito) {
                return SERVER_CONFIG.ESTADO_REQUISITO[requisito.estado];
            };

            self.getTipoRequisito = function(requisito) {
                return SERVER_CONFIG.TIPO_REQUISITO[requisito.tipo];
            };

            self.getPrioridade = function(requisito) {
                return SERVER_CONFIG.PRIORIDADE[requisito.prioridade];
            };

            self.ajustarCategoria = function() {
                if(self.categoriaInput != null) {
                    self.requisito.categoria = self.categoriaInput;
                } else {
                    self.requisito.categoria = self.categoriaSelect;
                }
            };

        }]);