package br.ufpr.ees.reqnrule.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.security.auth.login.LoginException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
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
import br.ufpr.ees.reqnrule.exception.SenhaException;
import br.ufpr.ees.reqnrule.model.Usuario;
import br.ufpr.ees.reqnrule.rest.filter.HttpHeaderNames;
import br.ufpr.ees.reqnrule.service.AutenticadorService;
import br.ufpr.ees.reqnrule.service.UsuarioService;

@Path("/usuario")
@RequestScoped
public class UsuarioRest extends Rest {

	@Inject
	private UsuarioService usuarioService;
	
	@Inject
	private AutenticadorService authService;

	@GET
	@Path("/encontrar-nome/{nome}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response encontrarPorNome(@Context HttpHeaders httpHeaders, @PathParam("nome") String nome) {
		Response.ResponseBuilder builder = null;
		try {
			List<Usuario> usuarios = usuarioService.encontrarPorNome(nome);
			builder = Response.ok().entity(usuarios);
		} catch (Exception e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}
	
	@GET
	@Path("/encontrar-email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response encontrarPorEmail(@Context HttpHeaders httpHeaders, @PathParam("email") String email) {
		Response.ResponseBuilder builder = null;
		try {
			Usuario usuario = usuarioService.encontrarPorEmail(email);
			builder = Response.ok().entity(usuario);
		} catch(BadRequestException | NoResultException e) {
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
	

	@POST
	@Path("/novo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response criarUsuario(Usuario usuario) {
		Response.ResponseBuilder builder = null;
		try {
			usuario = usuarioService.cadastrar(usuario);
			builder = Response.ok().entity(usuario);
		} catch (ConstraintViolationException e) {
			// Handle bean validation issues - FORBIDDEN
			builder = createViolationResponse(e.getConstraintViolations());
		} catch (ValidationException e) {
			// Handle the unique constraint violation
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("email", "E-mail já cadastrado.");
			builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
		} catch (SenhaException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("senha", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		} catch (Exception e) {
			// Handle internal server error
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}

	@PUT
	@Path("/atualizar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response alterarUsuario(@Context HttpHeaders httpHeaders, Usuario usuario) {
		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuarioAntigo = authService.getUsuarioAutorizado(authToken);
			usuarioAntigo = usuarioService.update(usuarioAntigo, usuario);
			authService.atualizarUsuario(authToken, usuarioAntigo);
			builder = Response.ok().entity(usuarioAntigo);			
		} catch (ConstraintViolationException e) {
			// Handle bean validation issues - Forbidden
			builder = createViolationResponse(e.getConstraintViolations());
		} catch (AuthException e) {
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
	@Path("/alterar-senha")
	@Produces(MediaType.APPLICATION_JSON)
	public Response alterarSenha(@Context HttpHeaders httpHeaders, @FormParam("senhaantiga") String senhaAntiga,
			@FormParam("senhanova") String senhaNova) {

		Response.ResponseBuilder builder = null;
		try {
			String authToken = httpHeaders.getHeaderString(HttpHeaderNames.AUTH_TOKEN);
			Usuario usuario = authService.getUsuarioAutorizado(authToken);
			usuario = usuarioService.alterarSenha(usuario, senhaAntiga, senhaNova);
			builder = Response.ok().entity(usuario);
		} catch (SenhaException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("senhanova", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		} catch (LoginException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("senhaantiga", "Senha atual incorreta.");
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		} catch (AuthException e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("auth", "Operação não autorizada.");
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(responseObj);
		} catch (Exception e) {
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("erro", e.getMessage());
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseObj);
		}
		return builder.build();
	}
}
