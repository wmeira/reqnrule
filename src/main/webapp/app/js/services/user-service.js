/**
 * Created by User on 09/09/2015.
 */

angular.module('reqnrule')
    .factory('UserService', ['SERVER_CONFIG', '$http', '$cookies', '$q', function(SERVER_CONFIG, $http, $cookies, $q) {

        var service = {

            signup: function(usuario) {
                var deferred = $q.defer();
                $http({
                    method: 'POST',
                    url: SERVER_CONFIG.URI + '/api/usuario/novo',
                    data: JSON.stringify(usuario)
                }).then(
                    function(response) {
                        deferred.resolve(response);
                    }, function(response) {
                        deferred.reject(response);
                    }
                );
                return deferred.promise;
            },

            alterar: function(usuario) {
                var deferred = $q.defer();
                $http({
                    method: 'PUT',
                    url: SERVER_CONFIG.URI + '/api/usuario/atualizar',
                    headers: {'auth_token': $cookies.get("auth_token")},
                    data: JSON.stringify(usuario)
                }).then(function(response) {
                    deferred.resolve(response);
                }, function(response) {
                    deferred.reject(response);
                });
                return deferred.promise;
            },

            alterarSenha: function(senhaantiga, senhanova) {
                var deferred = $q.defer();
                $http({
                    method: 'PUT',
                    url: SERVER_CONFIG.URI + '/api/usuario/alterar-senha',
                    headers: {'auth_token': $cookies.get("auth_token"), 'Content-Type': 'application/x-www-form-urlencoded'},
                    transformRequest: function(obj) {
                        var str = [];
                        for(var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    },
                    data: {senhaantiga:senhaantiga, senhanova:senhanova}
                }).then(
                    function(success) {
                        deferred.resolve(success);
                    },
                    function(error) {
                        deferred.reject(error);
                    }
                );
                return deferred.promise;
            },

            encontrarPorEmail: function(email) {
                var deferred = $q.defer();
                $http({
                    method: 'GET',
                    url: SERVER_CONFIG.URI + '/api/usuario/encontrar-email/' + email,
                    headers: {'auth_token': $cookies.get("auth_token")}
                }).then(
                    function(response) {
                        deferred.resolve(response.data);
                    },
                    function(response) {
                        deferred.reject(response);
                    }
                );
                return deferred.promise;
            }
        };
        return service;
    }]);