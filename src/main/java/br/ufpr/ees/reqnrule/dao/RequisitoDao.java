package br.ufpr.ees.reqnrule.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.ufpr.ees.reqnrule.model.Requisito;

@Stateless
public class RequisitoDao {
	
	@PersistenceContext(unitName = "primary")
    private EntityManager em;
	
	public RequisitoDao() {
		
	}
	
	public RequisitoDao(EntityManager em){
		this.em = em;
	}
	
	public void save(Requisito requisito) {
		em.persist(requisito);
		for(Requisito associado : requisito.getAssociados()) {
			associado.addAssociacao(requisito);
			em.merge(associado);
		}
	}
	
	public void update(Requisito requisito) {
		for(Requisito associado : requisito.getAssociados()) {
			if(!associado.getAssociados().contains(requisito)) {
				associado.addAssociacao(requisito);
				em.merge(associado);
			}			
		}
		em.merge(requisito);
	}
		
	public Requisito findPorId(Long id) {
        return em.find(Requisito.class, id);
    }
	
	public void delete(Long id) {
		Requisito requisito = em.find(Requisito.class, id);
		for(Requisito associado : requisito.getAssociados()) {
			associado.removeAssociacao(requisito);
			em.merge(associado);
		}
		for(Requisito filho : requisito.getFilhos()) {
			filho.setPai(null);
			em.merge(filho);
		}
		
		em.remove(requisito);
	}
}
