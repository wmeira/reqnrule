package br.ufpr.ees.reqnrule.rest;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
import br.ufpr.ees.reqnrule.model.Requisito;
import br.ufpr.ees.reqnrule.model.SolicitacaoMudanca;
import br.ufpr.ees.reqnrule.model.Usuario;
import br.ufpr.ees.reqnrule.rest.filter.HttpHeaderNames;
import br.ufpr.ees.reqnrule.service.AutenticadorService;
import br.ufpr.ees.reqnrule.service.RequisitoService;


@Path("/projeto")
@RequestScoped
public class RequisitoRest extends Rest{

	@Inject
	private RequisitoService requisitoService;
	
	@Inject 
	private AutenticadorService authService;
	
	@POST
	@Path("/{id:[0-9][0-9]*}/requisito/novo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response adicionarRequisito(@Context HttpHeaders httpHeaders, @PathParam("id") Long projetoId, Requisito requisito) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			Requisito requisitoCriado = requisitoService.adicionarRequisito(usuario, projetoId, requisito);
			builder = Response.ok().entity(requisitoCriado);
		} catch (ConstraintViolationException e) {
			// Handle bean validation issues (Forbidden)
			builder = createViolationResponse(e.getConstraintViolations());
		} catch(RuleException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put(e.getKey(), e.getMessage());
			builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
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
	@Path("/requisito/alterar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response alterarRequisito(@Context HttpHeaders httpHeaders, Requisito requisito) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			Requisito requisitoAlterado = requisitoService.alterarRequisito(usuario, requisito);
			builder = Response.ok().entity(requisitoAlterado);
		} catch (ConstraintViolationException e) {
			builder = createViolationResponse(e.getConstraintViolations());
		} catch(RuleException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put(e.getKey(), e.getMessage());
			builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
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
	
	@POST
	@Path("/requisito/{id:[0-9][0-9]*}/solicitar-mudanca")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response solicitacaoMudanca(@Context HttpHeaders httpHeaders, @PathParam("id") Long requisitoId, @FormParam("solicitacao") String solicitacaoTexto) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			SolicitacaoMudanca solicitacao = requisitoService.solicitarMudancaRequisito(usuario, requisitoId, solicitacaoTexto);
			builder = Response.ok().entity(solicitacao);
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
	@Path("/requisito/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response encontrarRequisitoPorId(@Context HttpHeaders httpHeaders, @PathParam("id") Long requisitoId) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			Requisito requisitoEncontrado = requisitoService.encontrarRequisitoPorId(usuario, requisitoId);
			builder = Response.ok().entity(requisitoEncontrado);
		} catch(AuthException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", e.getMessage());
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
		} catch (Exception e) {			
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("error", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}
	
	@DELETE
	@Path("/requisito/deletar/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletarRequisito(@Context HttpHeaders httpHeaders, @PathParam("id") Long requisitoId) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			requisitoService.deletarRequisito(usuario, requisitoId);
			builder = Response.ok();
		} catch(RuleException e) {
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
	
}
