angular.module('reqnrule')
    .controller('ListaProjetoController', ['SERVER_CONFIG', 'ProjetoService', 'AuthService', '$location', '$scope', function(SERVER_CONFIG, ProjetoService, AuthService, $location, $scope) {
        var self = this;
        self.erro = {};

        self.listaProjetos = function() {
            return ProjetoService.listaProjetos();
        };

        self.getPapel = function(projeto) {
            userId = AuthService.getUser().id;
            return ProjetoService.getPapel(projeto, userId);
        };

        self.isDono = function(projeto) {
            userId = AuthService.getUser().id;
            return projeto.dono.id == userId;
        };

        self.deletarMensagem = function() {
            ProjetoService.deletarMensagem();
        };
    }]);