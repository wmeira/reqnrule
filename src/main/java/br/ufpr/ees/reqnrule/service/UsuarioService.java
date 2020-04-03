package br.ufpr.ees.reqnrule.service;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.security.auth.login.LoginException;
import javax.validation.ValidationException;
import javax.ws.rs.NotFoundException;

import br.ufpr.ees.reqnrule.dao.UsuarioDao;
import br.ufpr.ees.reqnrule.exception.AuthException;
import br.ufpr.ees.reqnrule.exception.SenhaException;
import br.ufpr.ees.reqnrule.model.Usuario;
import br.ufpr.ees.reqnrule.util.MD5;

public class UsuarioService extends Service {

	@Inject
	private UsuarioDao usuarioDao;

	public Usuario cadastrar(Usuario usuario) throws Exception {
		ajustarValores(usuario);
		validarUnicidadeEmail(usuario.getEmail());
		validaSenha(usuario.getSenha());
		criptografarSenha(usuario);
		validarBean(usuario);
		usuarioDao.save(usuario);
		return usuario;
	}

	private void ajustarValores(Usuario usuario) {
		if(usuario.getNome() != null) usuario.setNome(usuario.getNome().trim());
		if(usuario.getEmail() != null) usuario.setEmail(usuario.getEmail().toLowerCase().trim());
		
		if(usuario.getCompanhia() != null) {
			usuario.setCompanhia(usuario.getCompanhia().trim());
		} else {
			usuario.setCompanhia("");
		}
	}

	/**
	 * A senha deve ter no mínimo 8 caracteres, conter pelo menos um número e
	 * uma letra minúscula.
	 * 
	 * @param usuario
	 * @throws SenhaException
	 */
	private void validaSenha(String senha) throws SenhaException {
		String pattern = "(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}";
		if (!senha.matches(pattern)) {
			throw new SenhaException("Senha deve ter ao menos uma letra minúscula e um número.");
		}
	}

	private void criptografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
		usuario.setSenha(MD5.getMD5(senha));
	}

	private void validarUnicidadeEmail(String email) {
		Usuario usuario = null;
		try {
			usuario = usuarioDao.findPorEmail(email);
		} catch (Exception e) {
			// No result exception
		}
		if (usuario != null) {
			throw new ValidationException("E-mail já cadastrado.");
		}
	}

	public Usuario update(Usuario usuarioAntigo, Usuario usuario) throws Exception {
		
		if (usuarioAntigo != null) {
			usuario.setId(usuarioAntigo.getId());
			usuario.setEmail(usuarioAntigo.getEmail());
			ajustarValores(usuario);
			usuario.setSenha(usuarioAntigo.getSenha());
//			criptografarSenha(usuario);
//			confirmarSenha(usuarioAntigo.getSenha(), usuario.getSenha());
			validarBean(usuario);
			usuarioDao.update(usuario);
			usuarioAntigo = usuario;
			return usuarioAntigo;
		} else {
			throw new AuthException();
		}
	}

	private void confirmarSenha(String senhaAtual, String senhaVerificacao) throws LoginException {
		if (!senhaAtual.equals(senhaVerificacao)) {
			throw new LoginException();
		}
	}

	public Usuario alterarSenha(Usuario usuario, String senhaAntiga, String senhaNova) throws Exception {
		senhaAntiga = MD5.getMD5(senhaAntiga);
		confirmarSenha(usuario.getSenha(), senhaAntiga);
		validaSenha(senhaNova);
		senhaNova = MD5.getMD5(senhaNova);
		try {
			usuario.setSenha(senhaNova);
			usuarioDao.update(usuario);
			return usuario;
		} catch (Exception e) {
			throw e;
		}
	}

	public Usuario encontrarPorId(Long id) throws Exception {
		Usuario usuario = usuarioDao.findPorId(id);
		if(usuario == null) {
			throw new NotFoundException("Usuário não encontrado.");
		}
		return usuario;
	}
	
	public List<Usuario> encontrarPorNome(String nome) {
		List<Usuario> usuarios = usuarioDao.findPorNome(nome);
		return usuarios;
	}
	
	public Usuario encontrarPorEmail(String email) throws NoResultException {
		try {
			Usuario usuario = usuarioDao.findPorEmail(email);
			if(usuario == null) {
				throw new NoResultException("Usuário não encontrado.");
			}
			return usuario;
		} catch(Exception e) {
			throw new NoResultException("Usuário não encontrado.");
		}
	}

}
