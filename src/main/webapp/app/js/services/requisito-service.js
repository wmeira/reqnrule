angular.module('reqnrule')
    .factory('RequisitoService', ['SERVER_CONFIG', 'ProjetoService', '$http', '$cookies', '$q', function(SERVER_CONFIG, ProjetoService, $http, $cookies, $q) {

        var service = {

            adicionarRequisito: function(projeto, requisito) {
                var deferred = $q.defer();
                $http({
                    method: 'POST',
                    url: SERVER_CONFIG.URI + '/api/projeto/' + projeto.id + '/requisito/novo',
                    headers: {'auth_token': $cookies.get("auth_token")},
                    data: angular.toJson(requisito)
                }).then(function(response) {
                    deferred.resolve(response.data);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            encontrarRequisitoPorId: function(requisitoId) {
                var deferred = $q.defer();
                $http({
                    method: 'GET',
                    url: SERVER_CONFIG.URI + '/api/projeto/requisito/' + requisitoId,
                    headers: {'auth_token': $cookies.get("auth_token")}
                }).then(function(response) {
                    deferred.resolve(response.data);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            solicitarMudanca: function(requisitoId, solicitacao) {
                var deferred = $q.defer();
                $http({
                    method: 'POST',
                    url: SERVER_CONFIG.URI + '/api/projeto/requisito/' + requisitoId + '/solicitar-mudanca',
                    headers: {'auth_token': $cookies.get("auth_token"), 'Content-Type': 'application/x-www-form-urlencoded'},
                    transformRequest: function(obj) {
                        var str = [];
                        for(var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    },
                    data: {solicitacao:solicitacao}
                }).then(function(response) {
                    deferred.resolve(response.data);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            alterarRequisito: function(requisito) {
                var deferred = $q.defer();
                $http({
                    method: 'PUT',
                    url: SERVER_CONFIG.URI + '/api/projeto/requisito/alterar',
                    headers: {'auth_token': $cookies.get("auth_token")},
                    data: angular.toJson(requisito)
                }).then(function(response) {
                    deferred.resolve(response.data);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            deletarRequisito: function(requisito) {
                var deferred = $q.defer();
                $http({
                    method: 'DELETE',
                    url: SERVER_CONFIG.URI + '/api/projeto/requisito/deletar/' + requisito.id,
                    headers: {'auth_token': $cookies.get("auth_token")}
                }).then(function(response) {
                    deferred.resolve();
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            }
        };

        return service;
    }]);

