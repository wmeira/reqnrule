package br.ufpr.ees.reqnrule.rest;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
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
import br.ufpr.ees.reqnrule.model.Membro;
import br.ufpr.ees.reqnrule.model.Projeto;
import br.ufpr.ees.reqnrule.model.Usuario;
import br.ufpr.ees.reqnrule.rest.filter.HttpHeaderNames;
import br.ufpr.ees.reqnrule.service.AutenticadorService;
import br.ufpr.ees.reqnrule.service.MembroService;


@Path("/projeto")
@RequestScoped
public class MembroRest extends Rest {

	@Inject
	private MembroService membroService;
		
	@Inject
	private AutenticadorService authService;
	
	@POST
	@Path("{id:[0-9][0-9]*}/membro/novo")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response adicionarNovoMembro(@Context HttpHeaders httpHeaders, 
			@PathParam("id") Long projetoId, 
			@FormParam("usuario") Long usuarioId,
			@FormParam("papel") Integer valorPapel) {
		
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken); //authtoken inválida
			Membro membroAdicionado = membroService.adicionarMembro(usuario, projetoId, usuarioId, valorPapel);
			builder = Response.ok().entity(membroAdicionado);
		} catch(AuthException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", e.getMessage());
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
		} catch(RuleException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put(e.getKey(), e.getMessage()); //permissao, duplicado
			builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
		} catch (Exception e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}
	
	@PUT
	@Path("/membro/alterar/{id:[0-9][0-9]*}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response alterarMembro(@Context HttpHeaders httpHeaders, @PathParam("id") Long membroId, @FormParam("papel") int valorPapel) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken); //authtoken inválida
			Membro membroAlterado = membroService.alterarPapelMembro(usuario, membroId, valorPapel);
			builder = Response.ok().entity(membroAlterado);
		} catch(AuthException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", e.getMessage());
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
		} catch(RuleException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put(e.getKey(), e.getMessage()); //permissao
			builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
		} catch (Exception e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}
	
	@PUT
	@Path("/alterar-dono/{id:[0-9][0-9]*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response alterarDonoDoProjeto(@Context HttpHeaders httpHeaders, @PathParam("id") Long novoDonoMembroId) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken); //authtoken inválida
			Projeto projeto = membroService.alterarDonoProjeto(usuario, novoDonoMembroId);			
			builder = Response.ok().entity(projeto);
		} catch (AuthException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", e.getMessage());
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
		} catch(RuleException e) { 
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put(e.getKey(), e.getMessage());
			builder = Response.status(Response.Status.FORBIDDEN).entity(responseObj);
		} catch (Exception e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}
	
}
