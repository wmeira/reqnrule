package br.ufpr.ees.reqnrule.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import br.ufpr.ees.reqnrule.model.Membro;
import br.ufpr.ees.reqnrule.model.Projeto;
import br.ufpr.ees.reqnrule.model.Usuario;

@Stateless
public class ProjetoDao {

	@PersistenceContext(unitName = "primary")
    private EntityManager em;
	
	public ProjetoDao() {
	}
	
	public ProjetoDao(EntityManager em) {
		this.em = em;
	}
	
	public void save(Projeto projeto) {
		em.persist(projeto);
	}
	
	public void update(Projeto projeto){
		em.merge(projeto);
	}
		
	public Projeto findPorId(Long id) {
        return em.find(Projeto.class, id);
    }
	
	public void delete(Long id) {
		Projeto projeto = findPorId(id);
		em.remove(projeto);
	}
	
	public List<Projeto> listaProjetoDoUsuario(Usuario usuario) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Projeto> query = cb.createQuery(Projeto.class);
        Root<Membro> membro = query.from(Membro.class);
        Join<Membro, Projeto> projetos = membro.join("projeto");
        query.select(projetos);
        query.where(cb.equal(membro.get("usuario"), usuario));       
        return em.createQuery(query).getResultList();
    }
}
