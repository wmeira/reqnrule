package br.ufpr.ees.reqnrule.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@NotEmpty(message = "O nome do usuário não pode ser vazio.")
	@Size(max = 255)
	private String nome;

	@NotNull(message = "O e-mail deve ser informado.")
	@Email(message = "O formato do e-mail está incorreto.")
	@Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
	private String email;
	
	@NotEmpty(message = "A senha não pode ser vazia.")
	@Size(max = 127)
	private String senha;

	@Size(max = 255, message = "A companhia deve ter no máximo 255 caracteres.")
	private String companhia;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@JsonIgnore
	public String getSenha() {
		return senha;
	}
	
	@JsonProperty("senha")
	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCompanhia() {
		return companhia;
	}

	public void setCompanhia(String companhia) {
		this.companhia = companhia;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
//		return "{\"id\":" + id + ", \"nome\":\"" + nome + "\", \"email\":\"" + email + "\", \"companhia\":\"" + companhia + "\" }";
		return "{id:" + id + ", nome:" + nome + ", email:" + email + ", companhia:" + companhia + "}";
	}

}
