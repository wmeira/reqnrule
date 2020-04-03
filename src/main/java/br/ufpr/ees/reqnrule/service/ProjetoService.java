package br.ufpr.ees.reqnrule.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import br.ufpr.ees.reqnrule.dao.ProjetoDao;
import br.ufpr.ees.reqnrule.exception.RuleException;
import br.ufpr.ees.reqnrule.model.Membro;
import br.ufpr.ees.reqnrule.model.Papel;
import br.ufpr.ees.reqnrule.model.Projeto;
import br.ufpr.ees.reqnrule.model.Usuario;

public class ProjetoService extends Service {
	
	@Inject
	private ProjetoDao projetoDao;
	
	public Projeto criarProjeto(Usuario usuario, Projeto projeto) throws Exception {
		adicionarUsuarioAoProjeto(projeto, usuario);
		ajustarValores(projeto);
		validarBean(projeto);
		projetoDao.save(projeto);
		return projeto;
	}
	
	private void ajustarValores(Projeto projeto) {
		if(projeto.getDescricao() != null) {
			projeto.setDescricao(projeto.getDescricao().trim());
		} else {
			projeto.setDescricao("");
		}
		
		if(projeto.getNome() != null) {
			projeto.setNome(projeto.getNome().trim());
		}		
	}
	
	private void adicionarUsuarioAoProjeto(Projeto projeto, Usuario usuario) {
		projeto.setDono(usuario);
		Membro membro = new Membro(usuario, projeto, Papel.GERENTE_DE_PROJETO);
		projeto.getMembros().add(membro);
	}
	
	public Membro adicionarMembro(Projeto projeto, Usuario usuario, Papel papel) throws Exception {
		Membro novoMembro = new Membro(usuario, projeto, papel);
		projeto.addMembro(novoMembro);
		projetoDao.update(projeto);
		return novoMembro;
	}
	 
	public Projeto alterarProjeto(Usuario usuario, Long projetoId, String nome, String descricao) throws Exception {
		Projeto projeto = encontrarPorId(usuario, projetoId);
		filtrarMembrosAtivos(projeto);
		Membro membro = encontrarMembroDoProjeto(usuario, projeto);
		if(membro.getPapel() == Papel.GERENTE_DE_PROJETO) {
			ajustarValores(projeto);
			projeto.setNome(nome);
			projeto.setDescricao(descricao);
			validarBean(projeto);
			projetoDao.update(projeto);
			filtrarMembrosAtivos(projeto);
			return projeto;
		} else {
			throw new RuleException("permissao", "Apenas membros com nível de gerente de projeto podem alterar o projeto.");
		}
	}

	public void deletarProjeto(Usuario usuario, Long projetoId) throws Exception {
		Projeto projeto = projetoDao.findPorId(projetoId);
		if(projeto.getDono().getId().equals(usuario.getId())) {
			projetoDao.delete(projeto.getId());
		} else {
			throw new RuleException("permissao", "Apenas o dono pode deletar o projeto.");
		}
	}
	
	public Projeto encontrarPorId(Usuario usuario, Long projetoId) throws Exception {
		try {
			Projeto projeto = projetoDao.findPorId(projetoId);
			if(projeto == null) {
				throw new NoResultException("Projeto não encontrado.");
			} else{
				encontrarMembroDoProjeto(usuario, projeto); //throws rule exception
				return projeto;
			}			
		} catch(Exception e) {
			throw new NoResultException("Projeto não encontrado.");
		}
	}
	
	public void filtrarMembrosAtivos(Projeto projeto) {
		Set<Membro> membrosAtivos = new HashSet<Membro>();
		for(Membro membro : projeto.getMembros()) {
			if(membro.getPapel() != Papel.EXCLUIDO) {
				membrosAtivos.add(membro);
			}
		}
		projeto.setMembros(membrosAtivos);
	}
	
	public Membro encontrarMembroDoProjeto(Usuario usuario, Projeto projeto) throws Exception {
		Membro membro = projeto.encontrarMembro(usuario);
		if(membro == null || membro.getPapel() == Papel.EXCLUIDO) {
			throw new RuleException("permissao", "O usuário não tem acesso ao projeto."); 
		}
		return membro;
	}
	
	public Membro verificarPermissaoDeAcessoAoProjeto(Usuario usuario, Projeto projeto) throws Exception {
		Membro membro = projeto.encontrarMembro(usuario);
		if(membro == null || membro.getPapel() == Papel.EXCLUIDO) {
			throw new RuleException("permissao", "O usuário não tem acesso ao projeto."); 
		}
		return membro;
	}
	
	public Set<Projeto> listaProjetosUsuario(Usuario usuario) throws Exception {
		List<Projeto> projetos = projetoDao.listaProjetoDoUsuario(usuario);
		Set<Projeto> projetosFiltrado = new HashSet<Projeto>();
		for(Projeto projeto : projetos) {
			Membro membro = projeto.encontrarMembro(usuario);
			if(membro != null && membro.getPapel() != Papel.EXCLUIDO) {
				filtrarMembrosAtivos(projeto);
				projetosFiltrado.add(projeto);
			}
		}
		return projetosFiltrado;
	}
	
	public Projeto atualizarDono(Projeto projeto) throws Exception {
		projetoDao.update(projeto);
		filtrarMembrosAtivos(projeto);
		return projeto;
	}
}
