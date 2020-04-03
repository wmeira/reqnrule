package br.ufpr.ees.reqnrule.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.ufpr.ees.reqnrule.model.EstadoSolicitacao;
import br.ufpr.ees.reqnrule.model.Membro;
import br.ufpr.ees.reqnrule.model.Mudanca;
import br.ufpr.ees.reqnrule.model.SolicitacaoMudanca;

@Stateless
public class MudancaDao {

	@PersistenceContext(unitName = "primary")
	private EntityManager em;

	public MudancaDao() {

	}

	public MudancaDao(EntityManager em) {
		this.em = em;
	}
	
	public void save(Mudanca mudanca) {
		em.persist(mudanca);
		for(SolicitacaoMudanca solicitacao : mudanca.getSolicitacoes()) {
			solicitacao.setEstado(EstadoSolicitacao.ATENDIDO);
			em.merge(solicitacao);
		}
	}
}
