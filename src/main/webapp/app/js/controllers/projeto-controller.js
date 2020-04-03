angular.module('reqnrule')
    .controller('ProjetoController', ['SERVER_CONFIG', 'ProjetoService', 'UserService', 'AuthService', '$location', '$scope', '$modal',
        function(SERVER_CONFIG, ProjetoService, UserService, AuthService, $location, $scope, $modal) {

        var self = this;
        self.erro = {};
        self.alterarProjetoInvalido = false;
        self.projeto = ProjetoService.getProjetoSelecionado();
        self.podeAlterarProjeto = false;
        self.nomeAtual = self.projeto.nome;

        $scope.open = function (operacao, projeto, membro, requisito) {
            var modalInstance;
            switch(operacao) {
                case 'deletar-projeto':
                    modalInstance = $modal.open({
                        templateUrl: 'views/deletar-projeto.html',
                        controller: 'DeletarProjetoController as deletarProjetoCtrl',
                        resolve: {
                            projeto: projeto
                        }
                    });
                    break;
                case 'sair-projeto':
                    modalInstance = $modal.open({
                        templateUrl: 'views/sair-projeto.html',
                        controller: 'SairProjetoController as sairProjetoCtrl',
                        resolve: {
                            projeto: projeto,
                            membro: membro
                        }
                    });
                    break;
                case 'novo-membro':
                    modalInstance = $modal.open({
                        templateUrl: 'views/novo-membro.html',
                        controller: 'NovoMembroController as novoMembroCtrl',
                        resolve: {
                            projeto: projeto
                        }
                    });
                    break;
                case 'editar-membro':
                    modalInstance = $modal.open({
                        templateUrl: 'views/update-membro.html',
                        controller: 'UpdateMembroController as updateMembroCtrl',
                        resolve: {
                            membro: membro
                        }
                    });
                    break;
                case 'alterar-dono':
                    modalInstance = $modal.open({
                        templateUrl: 'views/alterar-dono.html',
                        controller: 'AlterarDonoController as alterarDonoCtrl',
                        resolve: {
                            projeto: projeto
                        }
                    });
                    break;
                case 'adicionar-requisito':
                    var cat = self.getCategorias();
                    modalInstance = $modal.open({
                        templateUrl: 'views/novo-requisito.html',
                        controller: 'NovoRequisitoController as novoRequisitoCtrl',
                        size: 'lg',
                        //backdrop: 'static',
                        resolve: {
                            projeto: projeto,
                            categorias: function() {
                                var categorias = ['NENHUMA'];
                                for(var r in projeto.requisitos) {
                                    var requisito = projeto.requisitos[r];
                                    var contem = false;
                                    for(var c in categorias) {
                                        if(categorias[c] == requisito.categoria) {
                                            contem = true;
                                            break;
                                        }
                                    }
                                    if(!contem) {
                                        categorias.push(requisito.categoria);
                                    }
                                }
                                return categorias;
                            }
                        }
                    });
                    break;
                case 'deletar-requisito':
                    modalInstance = $modal.open({
                        templateUrl: 'views/deletar-requisito.html',
                        controller: 'DeletarRequisitoController as deletarRequisitoCtrl',
                        resolve: {
                            projeto:projeto,
                            requisito:requisito
                        }
                    });
                    break;
                case 'requisito':
                    modalInstance = $modal.open({
                        templateUrl: 'views/requisito.html',
                        controller: 'RequisitoController as requisitoCtrl',
                        size: 'lg',
                        resolve: {
                            projeto:projeto,
                            membro: self.getMembro(),
                            requisito:requisito,
                            categorias: function() {
                                var categorias = ['NENHUMA'];
                                for(var r in projeto.requisitos) {
                                    var requisito = projeto.requisitos[r];
                                    var contem = false;
                                    for(var c in categorias) {
                                        if(categorias[c] == requisito.categoria) {
                                            contem = true;
                                            break;
                                        }
                                    }
                                    if(!contem) {
                                        categorias.push(requisito.categoria);
                                    }
                                }
                                return categorias;
                            }
                        }
                    });
                    break;
                default:
                    break;
            };

            modalInstance.result.then(function (result) {
                self.mensagemErro = null;
                self.mensagemSucesso = null;

                if(result != null) {
                    if(result.tipo == 'erro') {
                        self.mensagemErro = result.msg;
                        self.mensagemSucesso = null;
                    } else {
                        self.mensagemSucesso = result.msg;
                        self.mensagemErro = null;
                    }
                }
            }, function() {
                self.mensagemErro = null;
                self.mensagemSucesso = null;
            }).finally( function() {
                if(operacao != 'sair-projeto') {
                    return ProjetoService.encontrarProjetoPorId(self.projeto.id).then(
                        function() {
                            self.projeto = ProjetoService.getProjetoSelecionado();
                            self.getRequisitosOrganizados();
                        },
                        function(response) {
                            if(response.status == "403" ) {
                                alert("Erro: usu\u00e1rio n\u00e3o \u00e9 membro do projeto.");
                                $location.path("/projetos");
                                $location.replace();
                            }
                            else if(response.status == "400") {
                                alert("Projeto n\u00e3o encontrado.");
                                $location.path("/projetos");
                                $location.replace();
                            } else if(response.status == "401") {
                                $location.path('/login');
                                $location.replace();
                            } else if(response.status == "500") { //internal server
                                alert("Problema ao obter projeto. Erro interno do servidor.");
                            } else if(response.status == "0") { //server não encontrado
                                alert("Servidor inacess\u00edvel. Tente mais tarde.");
                                $location.path('/');
                                $location.replace();
                            }
                        })
                }
            });
        };

        self.tab = 1;
        self.changeTab = function(n) {
            self.tab = n;
        };

         self.tooltipSairProjeto = function() {
             if(self.isDono()) {
                 return "Para sair do projeto, transfira a posi\u00e7\u00e3o de dono do projeto para outro Gerente de Projeto"
             } else {
                 return "Ao sair do projeto n\u00e3o ser\u00e1 mais poss\u00edvel visualiz\u00e1-lo";
             }
         };

        self.podeAlterarProjeto = function() {
            var papelUsuario = ProjetoService.getPapel(self.projeto, AuthService.getUser().id);
            return papelUsuario == SERVER_CONFIG.PAPEL.GERENTE_DE_PROJETO;
        };

        self.podeAlterarRequisito = function() {
            var papelUsuario = ProjetoService.getPapel(self.projeto, AuthService.getUser().id);
            return papelUsuario == SERVER_CONFIG.PAPEL.GERENTE_DE_PROJETO || papelUsuario == SERVER_CONFIG.PAPEL.GERENTE_DE_REQUISITOS;
        };

        self.alterarProjeto = function() {
            var promise = ProjetoService.alterarProjeto(self.projeto);
            promise.then(
                function(projeto) {
                    self.alterarProjetoInvalido = false;
                    self.alterarProjetoValido = true;
                    self.nomeAtual = projeto.nome;
                    $scope.projetoForm.$setPristine();
                },
                function(response) {
                    self.erro = {};
                    self.alterarProjetoInvalido = true;
                    self.alterarProjetoValido = false;

                    if(response.status == "403") { //forbidden - violation
                        self.erro.nome = [response.data.nome];
                        self.erro.descricao = [response.data.descricao];
                    } else if(response.status == "401") { //unauthorized
                        alert("Usu\u00e1rio n\u00e3o autorizado.")
                        $location.path('/login');
                        $location.replace();
                    } else if(response.status == "500") {
                        alert("Problema ao alterar projeto. Erro interno do servidor.");
                    } else if(response.status == "0") {
                        alert("Servidor inacess\u00edvel. Tente mais tarde.");
                        $location.path('/');
                        $location.replace();
                    }
                }
            );
        };

        self.isDono = function() {
            var userId = AuthService.getUser().id;
            return self.projeto.dono.id == userId;
        };

        self.getMembro = function() {
            var userId = AuthService.getUser().id;
            for(i = 0; i < self.projeto.membros.length; i++) {
                var membro = self.projeto.membros[i];
                if(membro.usuario.id == userId ) {
                    return membro;
                }
            }
        };

        self.getCategorias = function() {
            var categorias = ['NENHUMA'];
            for(var r in self.projeto.requisitos) {
                var requisito = self.projeto.requisitos[r];
                var contem = false;
                for(var c in categorias) {
                    if(categorias[c] == requisito.categoria) {
                        contem = true;
                        break;
                    }
                }
                if(!contem) {
                    categorias.push(requisito.categoria);
                }
            }
            return categorias;
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

        self.organizacao = [
            'TIPO', 'CATEGORIA', 'PRIORIDADE', 'ESTADO'
        ];

        self.orgSelecionada = 'TIPO';

        self.getRequisitosOrganizados = function() {
            switch(self.orgSelecionada) {
                case 'TIPO':
                    self.getRequisitosPorTipo();
                    break;
                case 'CATEGORIA':
                    self.getRequisitosPorCategoria();
                    break;
                case 'PRIORIDADE':
                    self.getRequisitosPorPrioridade();
                    break;
                case 'ESTADO':
                    self.getRequisitosPorEstado();
                    break;
                default:
                    break;
            }
        };

        self.getRequisitosPorTipo = function() {
            var requisitosPorTipo = [ {titulo: 'REQUISITOS FUNCIONAIS', req: []}, {titulo: 'REQUISITOS N\u00c1O-FUNCIONAIS', req: []}, {titulo: 'REQUISITOS DE PROJETO', req: []} ];
            for (var r in self.projeto.requisitos) {
                var requisito = self.projeto.requisitos[r];
                if(requisito.tipo == 'REQUISITO_FUNCIONAL') requisitosPorTipo[0].req.push(requisito);
                else if(requisito.tipo == 'REQUISITO_NAO_FUNCIONAL') requisitosPorTipo[1].req.push(requisito);
                else if(requisito.tipo == 'REQUISITO_DE_PROJETO') requisitosPorTipo[2].req.push(requisito);
            }
            self.requisitosOrganizados = requisitosPorTipo;
        };

        self.getRequisitosPorCategoria = function() {
            var categorias = self.getCategorias();
            var requisitosPorCategoria = [];
            for(var c in categorias) {
                requisitosPorCategoria.push({titulo: categorias[c], req:[]});
            }
            for (var r in self.projeto.requisitos) {
                var requisito = self.projeto.requisitos[r];
                for(var rCategoria in requisitosPorCategoria) {
                    if(requisitosPorCategoria[rCategoria].titulo == requisito.categoria) {
                        requisitosPorCategoria[rCategoria].req.push(requisito);
                    }
                }
            }

            self.requisitosOrganizados = requisitosPorCategoria;
        };

        self.getRequisitosPorPrioridade = function() {
            var requisitosPorPrioridade = [ {titulo: 'ESSENCIAIS', req: []}, {titulo: 'DESEJADOS', req: []}, {titulo: 'OPCIONAIS', req: []} ];
            for (var r in self.projeto.requisitos) {
                var requisito = self.projeto.requisitos[r];
                if(requisito.prioridade == 'ESSENCIAL') requisitosPorPrioridade[0].req.push(requisito);
                else if(requisito.prioridade == 'DESEJADO') requisitosPorPrioridade[1].req.push(requisito);
                else if(requisito.prioridade == 'OPCIONAL') requisitosPorPrioridade[2].req.push(requisito);

            }
            self.requisitosOrganizados = requisitosPorPrioridade;
        };

        self.getRequisitosPorEstado = function() {
            var requisitosPorEstado = [ {titulo: 'ESPECIFICADO', req: []}, {titulo: 'APROVADO', req: []}, {titulo: 'IMPLEMENTADO', req: []},  {titulo: 'VALIDADO', req: []}, {titulo: 'CANCELADO', req: []}];
            for (var r in self.projeto.requisitos) {
                var requisito = self.projeto.requisitos[r];
                if(requisito.estado == 'ESPECIFICADO') requisitosPorEstado[0].req.push(requisito);
                else if(requisito.estado == 'APROVADO') requisitosPorEstado[1].req.push(requisito);
                else if(requisito.estado == 'IMPLEMENTADO') requisitosPorEstado[2].req.push(requisito);
                else if(requisito.estado == 'VALIDADO') requisitosPorEstado[3].req.push(requisito);
                else if(requisito.estado == 'CANCELADO') requisitosPorEstado[4].req.push(requisito);
            }
            self.requisitosOrganizados = requisitosPorEstado;
        };

        self.gruposAbertos = false;

        $scope.expandirColapsarGrupos = function(grupoAberto){
            $scope.accordion.groups.map(function(item){
                item.isOpen = grupoAberto;
            });
        };

    }]);