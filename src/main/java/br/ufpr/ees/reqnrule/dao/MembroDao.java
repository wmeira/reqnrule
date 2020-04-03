package br.ufpr.ees.reqnrule.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.NotFoundException;

import br.ufpr.ees.reqnrule.model.Membro;

@Stateless
public class MembroDao {

	@PersistenceContext(unitName = "primary")
    private EntityManager em;
	
	public MembroDao() {
		
	}
	
	public MembroDao(EntityManager em) {
		this.em = em;
	}
	
	public void save(Membro membro) {
		em.persist(membro);
	}
	
	public void update(Membro membro) {
		em.merge(membro);
	}
	
	public void delete(Long id) {
		Membro membro = em.find(Membro.class, id);
		em.remove(membro);
	}
	
	public Membro findPorId(Long id) {
		return em.find(Membro.class, id);
	}
}
