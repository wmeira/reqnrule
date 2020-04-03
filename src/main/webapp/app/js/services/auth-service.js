angular.module('reqnrule')
    .factory('AuthService', ['SERVER_CONFIG', '$http', '$cookies', '$location', '$q', function(SERVER_CONFIG, $http, $cookies, $location, $q) {

        var user = null;
        var service = {

            hasUser: function() {
                if($cookies.get("auth_token") == null) {
                    return false;
                }
                return true;
            },

            getUser: function() {
                return user;
            },

            session: function() {
                var deferred = $q.defer();
                $http({
                    method: 'GET',
                    url: SERVER_CONFIG.URI + '/api/auth/session',
                    headers: {'auth_token': $cookies.get("auth_token")}
                }).then(function(response) {
                    user = response.data;
                    deferred.resolve(user);
                }, function(response) {
                    $cookies.remove("auth_token");
                    user = null;
                    deferred.reject(response);
                });

                return deferred.promise;
            },

            login: function(email, senha) {
                var deferred = $q.defer();
                $http({
                    method: 'POST',
                    url: SERVER_CONFIG.URI + '/api/auth/login',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    transformRequest: function(obj) {
                        var str = [];
                        for(var p in obj)
                            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    },
                    data: {email:email, senha:senha}
                }).then(
                    function(response) {
                        user = null;
                        $cookies.put('auth_token', response.data.auth_token);
                        deferred.resolve(response);
                    }, function(response) {
                        user = null;
                        $cookies.remove("auth_token");
                        deferred.reject(response);
                    }
                );
                return deferred.promise;
            },

            logout: function() {
                return $http({
                    method: 'POST',
                    url: SERVER_CONFIG.URI + '/api/auth/logout',
                    headers: {'auth_token': $cookies.get("auth_token")},
                }).
                    then(function(response) {
                        user = null;
                        $cookies.remove("auth_token");
                        $location.path('/');
                        $location.replace();
                        return response;
                    }, function(response) {
                        user = null;
                        $cookies.remove("auth_token");
                        $location.path('/');
                        $location.replace();
                        return response;
                        //alert("Erro na comunicação com o servidor.");
                    });
            }
        };
        return service;
    }]);