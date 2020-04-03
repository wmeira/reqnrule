package br.ufpr.ees.reqnrule.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.ufpr.ees.reqnrule.model.Usuario;

@Stateless
public class UsuarioDao {

	@PersistenceContext(unitName = "primary")
    private EntityManager em;
	
	public UsuarioDao(){
		
	}
	
	public UsuarioDao(EntityManager em){
		this.em = em;
	}
	
	public void save(Usuario usuario) {
		em.persist(usuario);
	}
	
	public void update(Usuario usuario){
		em.merge(usuario);
	}
		
	public Usuario findPorId(Long id) {
        return em.find(Usuario.class, id);
    }
	
	public List<Usuario> findPorNome(String nome) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
        Root<Usuario> usuario = criteria.from(Usuario.class);
        criteria.select(usuario).where(cb.like(cb.lower(usuario.<String>get("nome")), (nome + "%").toLowerCase())).orderBy(cb.asc(usuario.get("nome")));
        return em.createQuery(criteria).getResultList();
	}
	
	public Usuario findPorEmail(String email) throws Exception {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<Usuario> criteria = cb.createQuery(Usuario.class);
      Root<Usuario> usuario = criteria.from(Usuario.class);
      criteria.select(usuario).where(cb.equal(cb.lower(usuario.<String>get("email")), email.toLowerCase()));      
      return em.createQuery(criteria).getSingleResult();
	}
}
