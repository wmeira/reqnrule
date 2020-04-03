package br.ufpr.ees.reqnrule.rest;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import br.ufpr.ees.reqnrule.exception.AuthException;
import br.ufpr.ees.reqnrule.model.Usuario;
import br.ufpr.ees.reqnrule.rest.filter.HttpHeaderNames;
import br.ufpr.ees.reqnrule.service.AutenticadorService;

@Path("/auth")
public class AutenticadorRest {

	@Inject
	AutenticadorService authService;

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(
			@Context HttpHeaders httpHeaders, 
			@FormParam("email") String email, 
			@FormParam("senha") String senha) {
		
		try {
			String authToken = authService.login(email, senha);
			JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
			jsonObjBuilder.add("auth_token", authToken);
			JsonObject jsonObj = jsonObjBuilder.build();
			return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();

		} catch (final LoginException ex) {
			JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
			jsonObjBuilder.add("erro", "Usuário ou senha inválidos");
			JsonObject jsonObj = jsonObjBuilder.build();
			return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
		} catch (Exception ex) {
			JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
			jsonObjBuilder.add("erro", "Erro inesperado");
			JsonObject jsonObj = jsonObjBuilder.build();
			return getNoCacheResponseBuilder(Response.Status.INTERNAL_SERVER_ERROR).entity(jsonObj.toString()).build();
		}
	}
	
	@GET
	@Path("/session")
	@Produces(MediaType.APPLICATION_JSON)
	public Response session(@Context HttpHeaders httpHeaders) {
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			return getNoCacheResponseBuilder(Response.Status.OK).entity(usuario).build();
		} catch (AuthException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", "Operação não autorizada.");
			return Response.status(Response.Status.FORBIDDEN).entity(responseObj).build();
		} catch (Exception e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj).build();
		}
	}

	@GET
	@Path("/demo-get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response demoGetMethod() {
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		jsonObjBuilder.add("message", "Demo get");
		JsonObject jsonObj = jsonObjBuilder.build();
		return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
	}

	@POST
	@Path("/demo-post")
	@Produces(MediaType.APPLICATION_JSON)
	public Response demoPostMethod() {
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		jsonObjBuilder.add("message", "Demo post");
		JsonObject jsonObj = jsonObjBuilder.build();
		return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
	}

	@POST
	@Path("/logout")
	public Response logout(@Context HttpHeaders httpHeaders) {
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			authService.logout(authToken);
			return getNoCacheResponseBuilder(Response.Status.NO_CONTENT).build();
		} catch (final GeneralSecurityException ex) {
		}
		return getNoCacheResponseBuilder(Response.Status.INTERNAL_SERVER_ERROR).build();
	}

	private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status) {
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		cc.setMaxAge(-1);
		cc.setMustRevalidate(true);

		return Response.status(status).cacheControl(cc);
	}
}
