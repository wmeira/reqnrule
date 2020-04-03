package br.ufpr.ees.reqnrule.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
public class Membro implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
	
    
	@ManyToOne(optional = false)
	@JsonBackReference(value="membro-projeto")
	private Projeto projeto;
	
	@ManyToOne(optional = false)
	private Usuario usuario;
	
	@NotNull(message="Um membro deve possuir um papel no projeto.")
	@Enumerated(EnumType.STRING)
	private Papel papel;

	public Membro() {
		
	}
	
	public Membro(Usuario usuario, Projeto projeto, Papel papel) {
		this.usuario = usuario;
		this.projeto = projeto;
		this.papel = papel;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public Papel getPapel() {
		return papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((papel == null) ? 0 : papel.hashCode());
		result = prime * result + ((projeto == null) ? 0 : projeto.hashCode());
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Membro other = (Membro) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (papel != other.papel)
			return false;
		if (projeto == null) {
			if (other.projeto != null)
				return false;
		} else if (!projeto.getId().equals(other.projeto.getId()))
			return false;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.getId().equals(other.usuario.getId()))
			return false;
		return true;
	}
	
	
	
}
