package br.ufpr.ees.reqnrule.service;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import br.ufpr.ees.reqnrule.dao.UsuarioDao;
import br.ufpr.ees.reqnrule.exception.AuthException;
import br.ufpr.ees.reqnrule.model.Usuario;
import br.ufpr.ees.reqnrule.util.MD5;

@ApplicationScoped
public class AutenticadorService {

	private final Map<String, Usuario> authTokenStorage = new HashMap<String, Usuario>();
	
	@Inject
	private UsuarioDao usuarioDao;
	
	public String login(String email, String senha) throws LoginException {
		String authToken = null;
		try {
			Usuario usuario = usuarioDao.findPorEmail(email);
			String senhaCriptografada = MD5.getMD5(senha);
			if(usuario.getSenha().equals(senhaCriptografada)) {
				
				removerUsuarioDuplicado(usuario);
				/**
				 * Once all params are matched, the authToken will be generated
				 * and will be stored in the authorizationTokensStorage. The
				 * authToken will be needed for every REST API invocation and is
				 * only valid within the login session
				 */
				authToken = UUID.randomUUID().toString();
				authTokenStorage.put(authToken, usuario);
				return authToken;
			}
		} catch(Exception e) {
			//erro
		}
		throw new LoginException("E-mail ou senha inválida!");
	}
	
	public void removerUsuarioDuplicado(Usuario usuario) {
		for(String key : authTokenStorage.keySet()) {
			Usuario u = authTokenStorage.get(key);
			if(u.getId() == usuario.getId()) {
				authTokenStorage.remove(key);
			}
		}
	}
	
	public boolean isAuthTokenValido(String authToken) {
		return authTokenStorage.containsKey(authToken);		
	}
	
	public Usuario getUsuarioAutorizado(String authToken) throws AuthException{
		try {
			Usuario usuario = authTokenStorage.get(authToken);
			return usuario;
		} catch(Exception e) {
			throw new AuthException();
		}
	}
	
	public void atualizarUsuario(String authToken, Usuario novoUsuario) {
		authTokenStorage.replace(authToken, novoUsuario);
	}
	
	public void logout(String authToken) throws GeneralSecurityException {
		if(authTokenStorage.containsKey(authToken)) {
			authTokenStorage.remove(authToken);
			return;
		}
		throw new GeneralSecurityException("AuthToken inválida.");
	}
	
	
}
