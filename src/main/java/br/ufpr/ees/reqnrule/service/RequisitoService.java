package br.ufpr.ees.reqnrule.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import br.ufpr.ees.reqnrule.dao.RequisitoDao;
import br.ufpr.ees.reqnrule.exception.RuleException;
import br.ufpr.ees.reqnrule.model.EstadoSolicitacao;
import br.ufpr.ees.reqnrule.model.Membro;
import br.ufpr.ees.reqnrule.model.Mudanca;
import br.ufpr.ees.reqnrule.model.Papel;
import br.ufpr.ees.reqnrule.model.Projeto;
import br.ufpr.ees.reqnrule.model.Requisito;
import br.ufpr.ees.reqnrule.model.SolicitacaoMudanca;
import br.ufpr.ees.reqnrule.model.Usuario;

public class RequisitoService extends Service {

	@Inject
	private RequisitoDao requisitoDao;

	@Inject
	private ProjetoService projetoService;

	public Requisito adicionarRequisito(Usuario usuario, Long projetoId, Requisito requisito) throws Exception {
		Projeto projeto = projetoService.encontrarPorId(usuario, projetoId); // not found exception, usuario não tem acesso ao projeto
		projetoService.filtrarMembrosAtivos(projeto);
		Membro membro = projeto.encontrarMembro(usuario); // not found exception, nao tem acesso ao projeto
		if (membro.getPapel() == Papel.GERENTE_DE_PROJETO || membro.getPapel() == Papel.GERENTE_DE_REQUISITOS) {
			requisito.setProjeto(projeto);
			adicionarCodigo(requisito);
			ajustaValores(requisito); // enums são verificados se são validos quando o json é transformado em objeto
			limpaConjuntos(requisito);
			validarAssociacoes(requisito);
			validarExistenciaCiclos(requisito);
			adicionarMudanca(membro, requisito, new HashSet<SolicitacaoMudanca>());
			requisitoDao.save(requisito);
			return requisito;
		} else {
			throw new RuleException("permissao", "O usuário não tem nível de gerente de projeto ou de requisitos para adicionar requisitos.");
		}
	}
	
	private void adicionarCodigo(Requisito requisito) {
		Projeto projeto = requisito.getProjeto();
		int maiorValor = 0;
		for(Requisito r : projeto.getRequisitos()) {
			if(r.getCodigo() > maiorValor) {
				maiorValor = r.getCodigo();
			}
		}
		requisito.setCodigo(maiorValor + 1);		
	}

	private void ajustaValores(Requisito requisito) {
		if(requisito.getNome() != null) {
			requisito.setNome(requisito.getNome().trim());
		}
		
		if(requisito.getDescricao() != null) {
			requisito.setDescricao(requisito.getDescricao().trim());
		} else {
			requisito.setDescricao("");
		}
		
		if(requisito.getCategoria() != null) {
			requisito.setCategoria(requisito.getCategoria().trim().toUpperCase());
		}
	}

	private void limpaConjuntos(Requisito requisito) {
		requisito.getSolicitacoesMudanca().clear();
		requisito.getMudancas().clear();
	}

	private void validarAssociacoes(Requisito requisito) throws Exception {
		validarPai(requisito);
		validarAssociacaoBidirecional(requisito);
	}

	private void validarPai(Requisito requisito) throws Exception {		
		Requisito pai = requisito.getPai();
		if (pai == null)
			return;
		Projeto projeto = requisito.getProjeto();
		for (Requisito r : projeto.getRequisitos()) {
			if (r.getId().equals(pai.getId())) {
				return;				
			}
		}
		throw new RuleException("associados", "Requisito pai não está presente no projeto.");
	}
	
	private void validarExistenciaCiclos(Requisito requisito) throws Exception {
		if(requisito.getPai() == null) {
			return;
		}
		Projeto projeto = requisito.getProjeto();
		Requisito pai = projeto.encontrarRequisito(requisito.getPai().getId());
		
		while(pai != null) {
			if(pai.getId().equals(requisito.getId())) {
				throw new RuleException("associados", "Requisito pai proibido, pois insere um ciclo no nível dos requisitos.");
			}
			pai = pai.getPai();			
		}
	}

	private void validarAssociacaoBidirecional(Requisito requisito) throws Exception {
		Set<Requisito> associacoes = requisito.getAssociados();
		Set<Requisito> requisitosAssociados = new HashSet<Requisito>();
		Projeto projeto = requisito.getProjeto();
		for (Requisito associacao : associacoes) {
			Requisito invalido = null;
			for (Requisito r : projeto.getRequisitos()) {
				if (r.getId().equals(associacao.getId())) {
					requisitosAssociados.add(r);
					invalido = r;
					break;
				}
			}
			if (invalido == null) {
				throw new RuleException("associados", "Associação com " + invalido.getCodigo() + " inválida.");
			}
		}
		requisito.getAssociados().clear();
		requisito.setAssociados(requisitosAssociados);
	}

	private void adicionarMudanca(Membro membro, Requisito requisito, Set<SolicitacaoMudanca> solicitacoes) throws Exception {
		Mudanca mudanca = new Mudanca();
		mudanca.setAutor(membro);
		mudanca.setRequisito(requisito);
		mudanca.setVersao(requisito.getVersao() + 1);
		for(SolicitacaoMudanca solicitacao : solicitacoes) {
			solicitacao.setMudanca(mudanca);
		}
		mudanca.setSolicitacoes(solicitacoes);
		requisito.addMudanca(mudanca);
	}

	public Requisito encontrarRequisitoPorId(Usuario usuario, Long requisitoId) throws Exception {
		Requisito requisito = requisitoDao.findPorId(requisitoId);
		if (requisito == null) {
			throw new NotFoundException("Requisito não encontrado.");
		}
		Projeto projeto = requisito.getProjeto();
		projetoService.verificarPermissaoDeAcessoAoProjeto(usuario, projeto);
		return requisito;
	}

	public Requisito alterarRequisito(Usuario usuario, Requisito requisito) throws Exception {
		Requisito requisitoAntigo = requisitoDao.findPorId(requisito.getId());
		Projeto projeto = requisitoAntigo.getProjeto(); // not-found-exception
		Membro membro = projetoService.encontrarMembroDoProjeto(usuario, projeto); // not found exception, permissao
		if (membro.getPapel() == Papel.GERENTE_DE_PROJETO || membro.getPapel() == Papel.GERENTE_DE_REQUISITOS) { // ruleexception
			requisito.setProjeto(projeto);
			ajustaValores(requisito);
			validarAssociacoes(requisito);
			validarExistenciaCiclos(requisito);
			setValoresInalteraveis(requisitoAntigo, requisito);
			Mudanca mudanca = obterMudanca(membro, requisito);
			atualizarSolicitacoesAlteradas(requisitoAntigo, requisito, membro, mudanca);
			requisito.addMudanca(mudanca);
			requisitoDao.update(requisito);
			
			return requisito;
		} else {
			throw new RuleException("permissao", "O usuário não tem nível de gerente de projeto ou de requisitos para alterar o requisito.");
		}
	}
	
	private Mudanca obterMudanca(Membro membro, Requisito requisito) {
		Mudanca mudanca = new Mudanca();
		mudanca.setAutor(membro);
		mudanca.setRequisito(requisito);
		mudanca.setVersao(requisito.getVersao() + 1);
		return mudanca;
	}
	
	private void atualizarSolicitacoesAlteradas(Requisito requisitoAntigo, Requisito requisito, Membro membroAtendente, Mudanca mudanca) {
		for (SolicitacaoMudanca sAntiga : requisitoAntigo.getSolicitacoesMudanca()) {
			for (SolicitacaoMudanca s : requisito.getSolicitacoesMudanca()) {
				if (s.getId().equals(sAntiga.getId())) {
					if (sAntiga.getEstado() == EstadoSolicitacao.SOLICITADO && sAntiga.getEstado() != s.getEstado()) {
						s.setDataAtendimento(new Date());
						s.setAtendente(membroAtendente);
						s.setMudanca(mudanca);
						mudanca.getSolicitacoes().add(s);						
					} else {
						s.setMudanca(sAntiga.getMudanca());
					}
					break;
				}
			}
		}		
	}

	private void setValoresInalteraveis(Requisito requisitoAntigo, Requisito requisito) {
		requisito.setCodigo(requisitoAntigo.getCodigo());
		requisito.setMudancas(requisitoAntigo.getMudancas());
		requisito.setProjeto(requisitoAntigo.getProjeto());
	}

	public SolicitacaoMudanca solicitarMudancaRequisito(Usuario usuario, Long requisitoId, String solicitacaoTexto) throws Exception {
		Requisito requisito = encontrarRequisitoPorId(usuario, requisitoId); //not-found, permissão de acesso ao projeto
		Membro autor = projetoService.encontrarMembroDoProjeto(usuario, requisito.getProjeto());
		
		if(autor.getPapel() != Papel.EXCLUIDO && autor.getPapel() != Papel.OBSERVADOR) {
			SolicitacaoMudanca solicitacao = new SolicitacaoMudanca();
			solicitacao.setAutor(autor);
			solicitacao.setCodigo(obterCodigoSolicitacao(requisito.getProjeto()));
			solicitacao.setSolicitacao(solicitacaoTexto.trim());
			solicitacao.setEstado(EstadoSolicitacao.SOLICITADO);
			solicitacao.setRequisito(requisito);
			solicitacao.setDataSolicitacao(new Date());
			requisito.addSolicitacaoMudanca(solicitacao);
			requisitoDao.update(requisito);
			return solicitacao;
		} else {
			throw new RuleException("permissao", "O usuário não tem nível de permissão para solicitar mudanças no projeto.");
		}
	}
	
	private int obterCodigoSolicitacao(Projeto projeto) {
		int codigo = 1;
		for(Requisito requisito : projeto.getRequisitos()) {
			codigo += requisito.getSolicitacoesMudanca().size();
		}		
		return codigo;
	}

	public void deletarRequisito(Usuario usuario, Long requisitoId) throws Exception {
		Requisito requisito = encontrarRequisitoPorId(usuario, requisitoId); //not-found, permissão de acesso ao projeto
		Membro membro = projetoService.encontrarMembroDoProjeto(usuario, requisito.getProjeto());
		if(membro.getPapel() == Papel.GERENTE_DE_PROJETO || membro.getPapel() == Papel.GERENTE_DE_REQUISITOS) {
			requisitoDao.delete(requisito.getId());
		} else {
			throw new RuleException("permissao", "O usuário não tem nível de permissão para deletar requisitos do projeto.");
		}
	}

}
