package br.ufpr.ees.reqnrule.service;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import br.ufpr.ees.reqnrule.dao.MembroDao;
import br.ufpr.ees.reqnrule.exception.RuleException;
import br.ufpr.ees.reqnrule.model.Membro;
import br.ufpr.ees.reqnrule.model.Papel;
import br.ufpr.ees.reqnrule.model.Projeto;
import br.ufpr.ees.reqnrule.model.Usuario;

public class MembroService extends Service {

	@Inject
	private MembroDao membroDao;

	@Inject
	private ProjetoService projetoService;

	@Inject
	private UsuarioService usuarioService;

	public Membro adicionarMembro(Usuario usuario, Long projetoId, Long usuarioId, int valorPapel) throws Exception {
		Papel papel = Papel.getEnum(valorPapel); // argumento ilegal do papel
		Projeto projeto = projetoService.encontrarPorId(usuario, projetoId); // projeto não encontrado
		Membro membroUsuario = projetoService.encontrarMembroDoProjeto(usuario, projeto); //verifica se o usuario é membro do projeto
		if (membroUsuario.getPapel() == Papel.GERENTE_DE_PROJETO) { // nível de permissão inválido
			Usuario novoUsuario = usuarioService.encontrarPorId(usuarioId);
			Membro novoMembro = projeto.encontrarMembro(novoUsuario); // membro já adicionado.
			if (novoMembro == null) { 
				novoMembro = new Membro(novoUsuario, projeto, papel);
				membroDao.save(novoMembro);
				return novoMembro;
			} else if(novoMembro.getPapel() == Papel.EXCLUIDO) {				
					novoMembro.setPapel(papel);
					membroDao.update(novoMembro);
					return novoMembro;
			} else {
				throw new RuleException("permissao", "Membro já adicionado ao projeto.");
			}	
		} else {
			throw new RuleException("permissao", "Apenas membros com nível de gerente de projeto podem alterar membros do projeto.");
		}
	}
	
	public Membro alterarPapelMembro(Usuario usuario, Long membroId, int valorPapel) throws Exception {
		Papel novoPapel = Papel.getEnum(valorPapel); //argumento ilegal pro papel
		Membro membro = encontrarMembro(membroId); //membro não encontrado
		Projeto projeto = membro.getProjeto();
		Membro membroUsuario = projetoService.verificarPermissaoDeAcessoAoProjeto(usuario, projeto); //permissao de acesso ao projeto
		if(membroUsuario.getPapel() == Papel.GERENTE_DE_PROJETO || membroSaindoDoProjeto(membro, membroUsuario, novoPapel)) { //nível de permissão inválido
			if(projeto.getDono().getId().equals(membro.getUsuario().getId())) {
				//Não pode alterar o papel do dono. Se for o dono, não pode alterar seu papel para excluido.
				throw new RuleException("permissao", "Não é possível alterar o papel do dono do projeto.");				
			}
			membro.setPapel(novoPapel);
			membroDao.update(membro);
			return membro;
		} else {
			throw new RuleException("permissao", "Apenas membros com nível de gerente de projeto podem alterar membros do projeto.");
		}
	}
	
	private boolean membroSaindoDoProjeto(Membro membro, Membro membroUsuario, Papel novoPapel) {
		if(membroUsuario.getId().equals(membro.getId())) {
			if(novoPapel == Papel.EXCLUIDO) {
				return true;
			}
		}
		return false;
	}

	public Membro encontrarMembro(Long membroId) throws Exception {
		Membro membro = membroDao.findPorId(membroId);
		if (membro == null) {
			throw new NotFoundException("Membro não encontrado.");
		}
		return membro;
	}

	public Projeto alterarDonoProjeto(Usuario usuario, Long novoDonoMembroId) throws Exception {
		Membro membro = encontrarMembro(novoDonoMembroId); // membro inválido (not found)
		Projeto projeto = membro.getProjeto();
		projetoService.verificarPermissaoDeAcessoAoProjeto(usuario, projeto); //usuario pertence ao projeto
		if (projeto.getDono().getId().equals(usuario.getId())) { // tem que ser o dono
			if (membro.getPapel() == Papel.GERENTE_DE_PROJETO) { // novo dono deve ser gerente de projeto
				projeto.setDono(membro.getUsuario());
				projetoService.atualizarDono(projeto);
				projetoService.filtrarMembrosAtivos(projeto);
				return projeto;
			} else {
				throw new RuleException("permissao", "O novo dono deve ter nível de gerente de projeto.");
			}
		} else {
			throw new RuleException("permissao", "Apenas o dono do projeto pode designar um novo dono.");
		}
	}

}
