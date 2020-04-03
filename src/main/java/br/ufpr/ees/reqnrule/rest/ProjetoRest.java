package br.ufpr.ees.reqnrule.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.ufpr.ees.reqnrule.exception.AuthException;
import br.ufpr.ees.reqnrule.exception.RuleException;
import br.ufpr.ees.reqnrule.model.Projeto;
import br.ufpr.ees.reqnrule.model.Usuario;
import br.ufpr.ees.reqnrule.rest.filter.HttpHeaderNames;
import br.ufpr.ees.reqnrule.service.AutenticadorService;
import br.ufpr.ees.reqnrule.service.ProjetoService;

@Path("/projeto")
@RequestScoped
public class ProjetoRest extends Rest {

	@Inject
	private ProjetoService projetoService;
	
	@Inject
	private AutenticadorService authService;

	@POST
	@Path("/novo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response criarProjeto(@Context HttpHeaders httpHeaders, Projeto projeto) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			Projeto projetoCriado = projetoService.criarProjeto(usuario, projeto);
			builder = Response.ok().entity(projetoCriado);
		} catch (ConstraintViolationException e) {
			// Handle bean validation issues (Forbidden)
			builder = createViolationResponse(e.getConstraintViolations());
		} catch(AuthException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", e.getMessage());
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}

	@PUT
	@Path("/alterar/{id:[0-9][0-9]*}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response alterarConfigProjeto(@Context HttpHeaders httpHeaders, @PathParam("id") long projetoId, @FormParam("nome") String nome, @FormParam("descricao") String descricao) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			Projeto projetoAlterado = projetoService.alterarProjeto(usuario, projetoId, nome, descricao);
			builder = Response.ok().entity(projetoAlterado);
		} catch (ConstraintViolationException e) {
			// Handle bean validation issues (Forbidden)
			builder = createViolationResponse(e.getConstraintViolations());
		} catch(AuthException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", e.getMessage());
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}

	@DELETE
	@Path("/deletar/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletarProjeto(@Context HttpHeaders httpHeaders, @PathParam("id") long projetoId) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			projetoService.deletarProjeto(usuario, projetoId);
			builder = Response.ok();
		} catch (RuleException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put(e.getKey(), e.getMessage());
			builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
		} catch(AuthException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", e.getMessage());
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
		} catch (Exception e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}

	@GET
	@Path("/encontrar/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response encontrarPorId(@Context HttpHeaders httpHeaders, @PathParam("id") Long id) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			Projeto projetoEncontrado = projetoService.encontrarPorId(usuario, id);
			projetoService.filtrarMembrosAtivos(projetoEncontrado);
			builder = Response.ok().entity(projetoEncontrado);
		} catch(RuleException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put(e.getKey(), e.getMessage());
			builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
		} catch(AuthException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", e.getMessage());
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
		} catch(NoResultException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		} catch (Exception e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}

	@GET
	@Path("/lista-projetos")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listaProjetos(@Context HttpHeaders httpHeaders) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			Set<Projeto> projetos = projetoService.listaProjetosUsuario(usuario);			
			builder = Response.ok().entity(projetos);
		} catch (AuthException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", e.getMessage());
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
		} catch (Exception e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}

}
