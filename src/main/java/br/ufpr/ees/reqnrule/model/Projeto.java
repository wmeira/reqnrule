package br.ufpr.ees.reqnrule.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Projeto implements Serializable  {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @NotEmpty(message="O nome do projeto não pode ser vazio")
    @Size(max = 255, message="O nome deve ter no máximo 255 caracteres.")
    private String nome;
    
    @Size(max = 2047, message="A descrição deve ter no máximo 2047 caracteres.")
    private String descricao;
    
    @OneToOne(optional = true)
    private Usuario dono;
    
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy="projeto")
    @JsonManagedReference(value="requisito-projeto")
    private Set<Requisito> requisitos = new HashSet<Requisito>();
    
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy="projeto", cascade = CascadeType.ALL)
    @JsonManagedReference(value="membro-projeto")
    private Set<Membro> membros = new HashSet<Membro>();   
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Usuario getDono() {
		return dono;
	}

	public void setDono(Usuario dono) {
		this.dono = dono;
	}

	public Set<Membro> getMembros() {
		return membros;
	}
	
	public Membro getMembro(Long id) {
		for(Membro membro : membros) {
			if(membro.getId() == id) {
				return membro;
			}
		}
		return null;
	}
	
	public void addMembro(Membro novoMembro) {
		membros.add(novoMembro);
	}

	public void setMembros(Set<Membro> membros) {
		this.membros = membros;
	}

	public Set<Requisito> getRequisitos() {
		return requisitos;
	}

	public void setRequisitos(Set<Requisito> requisitos) {
		this.requisitos = requisitos;
	}
	
	public Membro encontrarMembro(Usuario usuario) {
		for(Membro membro : membros) {
			if(membro.getUsuario().getId().equals(usuario.getId())) {
				return membro;
			}
		}
		return null;
	}
	
	public Requisito encontrarRequisito(Long requisitoId) {
		for(Requisito requisito : requisitos) {
			if(requisito.getId().equals(requisitoId)) {
				return requisito;
			}
		}
		return null;
	}
}
