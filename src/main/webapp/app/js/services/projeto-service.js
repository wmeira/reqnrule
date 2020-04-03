angular.module('reqnrule')
    .factory('ProjetoService', ['SERVER_CONFIG', '$http', '$cookies', '$q', function(SERVER_CONFIG, $http, $cookies, $q) {

        var projetos = [];
        var projetoSelecionado = {};

        var service = {

            getProjetoSelecionado : function() {
                return projetoSelecionado;
            },

            encontrarProjetoPorId : function(projetoId) {
                var deferred = $q.defer();
                $http({
                    method: 'GET',
                    url: SERVER_CONFIG.URI + '/api/projeto/encontrar/' + projetoId,
                    headers: {'auth_token': $cookies.get("auth_token")}
                }).then(function(response) {
                    projetoSelecionado = response.data;
                    deferred.resolve(projetoSelecionado);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            listaProjetos: function() {
                return projetos;
            },

            getProjetos: function() {
                var deferred = $q.defer();
                $http({
                    method: 'GET',
                    url: SERVER_CONFIG.URI + '/api/projeto/lista-projetos',
                    headers: {'auth_token': $cookies.get("auth_token")}
                }).then(function(response) {
                    projetos = response.data;
                    deferred.resolve(projetos);
                }, function(response) {
                    projetos = [];
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            novoProjeto: function(projeto) {
                var deferred = $q.defer();
                $http({
                    method: 'POST',
                    url: SERVER_CONFIG.URI + '/api/projeto/novo',
                    headers: {'auth_token': $cookies.get("auth_token")},
                    data: JSON.stringify(projeto)
                }).then(function(response) {
                    deferred.resolve(response.data);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            alterarProjeto: function(projeto) {
                var deferred = $q.defer();
                $http({
                    method: 'PUT',
                    url: SERVER_CONFIG.URI + '/api/projeto/alterar/' + projeto.id,
                    headers: {'auth_token': $cookies.get("auth_token"), 'Content-Type': 'application/x-www-form-urlencoded'},
                    transformRequest: function(obj) {
                        var str = [];
                        for(var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    },
                    data: {nome:projeto.nome, descricao:projeto.descricao}
                }).then(function(response) {
                    deferred.resolve(response.data);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            deletarProjeto: function(projetoId) {
                var deferred = $q.defer();
                $http({
                    method: 'DELETE',
                    url: SERVER_CONFIG.URI + '/api/projeto/deletar/' + projetoId,
                    headers: {'auth_token': $cookies.get("auth_token")}
                }).then(function(response) {
                    deferred.resolve(response.data);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            getPapel: function(projeto, userId) {
                for(i = 0; i < projeto.membros.length; i++) {
                    membro = projeto.membros[i];
                    if(membro.usuario.id == userId ) {
                        return SERVER_CONFIG.PAPEL[membro.papel];
                    }
                }
            },

            alterarMembro: function(membro, novoPapel) {
                var deferred = $q.defer();
                $http({
                    method: 'PUT',
                    url: SERVER_CONFIG.URI + '/api/projeto/membro/alterar/' + membro.id,
                    headers: {'auth_token': $cookies.get("auth_token"), 'Content-Type': 'application/x-www-form-urlencoded'},
                    transformRequest: function(obj) {
                        var str = [];
                        for(var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    },
                    data: {papel:novoPapel}
                }).then(function(response) {
                    var membro = response.data;
                    service.atualizarMembros(membro);
                    deferred.resolve(membro);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            adicionarMembro: function(projeto, usuario, papel) {
                var deferred = $q.defer();
                $http({
                    method: 'POST',
                    url: SERVER_CONFIG.URI + '/api/projeto/' + projeto.id + '/membro/novo',
                    headers: {'auth_token': $cookies.get("auth_token"), 'Content-Type': 'application/x-www-form-urlencoded'},
                    transformRequest: function(obj) {
                        var str = [];
                        for(var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    },
                    data: {usuario:usuario.id, papel:papel}
                }).then(function(response) {
                    var membro = response.data;
                    service.atualizarMembros(membro);
                    deferred.resolve(membro);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            alterarDonoProjeto: function(novoMembro) {
                var deferred = $q.defer();
                $http({
                    method: 'PUT',
                    url: SERVER_CONFIG.URI + '/api/projeto/alterar-dono/' + novoMembro.id,
                    headers: {'auth_token': $cookies.get("auth_token"), 'Content-Type': 'application/x-www-form-urlencoded'},
                    transformRequest: function(obj) {
                        var str = [];
                        for(var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    }
                }).then(function(response) {
                    service.getProjetoSelecionado().dono = novoMembro.usuario;
                    deferred.resolve(response.data);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            }
        };

        service.atualizarMembros = function(membro) {
            for(var m in projetoSelecionado.membros) {
                if(projetoSelecionado.membros[m].id == membro.id) {
                    if(membro.papel == 'EXCLUIDO') {
                        projetoSelecionado.membros.splice(m,1);
                    } else{
                        projetoSelecionado.membros[m] = membro;
                    }
                    return;
                }
            }
            projetoSelecionado.membros.push(membro);
        };


        return service;
    }]);