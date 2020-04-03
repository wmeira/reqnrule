angular.module('reqnrule', ['ngRoute', 'ngCookies', 'ui.bootstrap'])
	.constant("SERVER_CONFIG", {
		"URI":"http://localhost:8080/reqnrule",
		"PAPEL": {
			"GERENTE_DE_PROJETO":"Gerente de projeto",
			"GERENTE_DE_REQUISITOS":"Gerente de requisitos",
			"VERIFICADOR":"Verificador",
			"OBSERVADOR":"Observador",
			"EXCLUIDO":"Exclu\u00eddo"
		},
		"TIPO_REQUISITO": {
			"REQUISITO_FUNCIONAL":"Requisito Funcional",
			"REQUISITO_NAO_FUNCIONAL":"Requisito N\u00e3o-Funcional",
			"REQUISITO_DE_PROJETO":"Requisito de Projeto"
		},
		"PRIORIDADE": {
			"ESSENCIAL":"Essencial",
			"DESEJADO":"Desejado",
			"OPCIONAL":"Opcional"
		},
		"ESTADO_REQUISITO": {
			"ESPECIFICADO":"Especificado",
			"APROVADO":"Aprovado",
			"IMPLEMENTADO":"Implementado",
			"VALIDADO":"Validado",
			"CANCELADO":"Cancelado"
		},
		"ESTADO_SOLICITACA": {
			"SOLICITADO":"Solicitado",
			"ATENDIDO":"Atendido",
			"REJEITADO":"Rejeitado"
		}

	})
	.config(['$locationProvider','$routeProvider',function($locationProvider, $routeProvider) {

		//$locationProvider.html5Mode(true);

		$routeProvider
			.when('/', {
				templateUrl: 'views/main.html'
			})
			.when('/login', {
				templateUrl: 'views/login.html',
				controller: 'LoginController as loginCtrl',
				resolve: {
					loggedOn: ['AuthService', '$location', function(AuthService, $location) {
						return AuthService.session()
							.then(function() {
								$location.path('/usuario');
								$location.replace();
							}, function(response){})
					}]
				}
			})
			.when('/signup', {
				templateUrl: 'views/signup.html',
				controller: 'SignupController as signupCtrl',
				resolve: {
					loggedOn: ['AuthService', '$location', function(AuthService, $location) {
						return AuthService.session()
							.then(function() {
								$location.path('/usuario');
								$location.replace();
							}, function(response){})
					}]
				}
			})
			.when('/usuario', {
				templateUrl: 'views/usuario.html',
				controller: 'UsuarioController',
				controllerAs: 'usuarioCtrl',
				resolve: {
					loggedOn: ['AuthService', '$location', function(AuthService, $location) {
							return AuthService.session()
								.then(function() {},
									function(response){
										$location.path('/login');
										$location.replace();
									})
					}]
				}
			})
			.when('/projetos', {
				templateUrl: 'views/lista-projetos.html',
				controller: 'ListaProjetoController as listaProjetoCtrl',
				resolve: {
					loggedOn: ['AuthService','ProjetoService', '$location', function(AuthService, ProjetoService, $location) {
						return AuthService.session()
							.then(function() {
								ProjetoService.getProjetos().then(
									function() {
									},
									function(response) {
										if(response.status == "401") { //auth_token inválido
											alert("Usu\u00e1rio n\u00e3o autorizado.")
											$location.path('/login');
											$location.replace();
										}else if(response.status == "500") { //internal server
											alert("Problema ao obter lista de projetos. Erro interno do servidor.");
										} else if(response.status == "0") { //server não encontrado
											alert("Servidor inacess\u00edvel. Tente mais tarde.");
											$location.path('/');
											$location.replace();
										}
									}
								);
							},
							function(response){
								$location.path('/login');
								$location.replace();
							})
					}]
				}
			})
			.when('/novo-projeto', {
				templateUrl: 'views/novo-projeto.html',
				controller:'NovoProjetoController as projetoCtrl',
				resolve: {
					loggedOn: ['AuthService', '$location', function(AuthService, $location) {
						return AuthService.session()
							.then(function() {},
							function(response){
								$location.path('/login');
								$location.replace();
							})
					}]
				}
			})
			.when('/projeto/:pid', {
				templateUrl: 'views/projeto.html',
				controller: 'ProjetoController as projetoCtrl',
				resolve: {
					loggedOn: ['AuthService', 'ProjetoService', '$route', '$location', function(AuthService, ProjetoService, $route, $location) {
						return AuthService.session()
							.then(function() {
								return ProjetoService.encontrarProjetoPorId($route.current.params.pid).then(
									function(projeto) {},
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
									}
								);
							},
							function(response){
								$location.path('/login');
								$location.replace();
							})
					}]
				}
			})
			.when('/projeto/:pid/requisito/:rid/solicitacao-mudanca', {
				templateUrl: 'views/soliciatacao-mudanca.html',
				resolve: {
					loggedOn: ['AuthService', '$location', function(AuthService, $location) {
						return AuthService.session()
							.then(function() {},
							function(response){
								$location.path('/login');
								$location.replace();
							})
					}]
				}
			})
			.when('/sobre', {
				templateUrl: 'views/sobre.html'
			})
			.otherwise({
				redirectTo:'/'
			});
	}])