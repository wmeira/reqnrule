package br.ufpr.ees.reqnrule.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * Entity implementation class for Entity: Mudanca
 *
 */
@Entity

public class Mudanca implements Serializable {

	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
        
    @ManyToOne
    private Membro autor;    
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy="mudanca")
    @JsonManagedReference(value="mudanca-solicitacao")
    private Set<SolicitacaoMudanca> solicitacoes = new HashSet<SolicitacaoMudanca>();
    
    @NotNull
    @ManyToOne(optional = false)
    @JsonBackReference(value="mudanca-requisito")
    private Requisito requisito;
    
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date data = new Date(); 
    
    @NotNull
    private int versao;
    
    @PrePersist
    public void inserirDataMudanca() {
    	data = new Date();
    }
   
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Membro getAutor() {
		return autor;
	}

	public void setAutor(Membro autor) {
		this.autor = autor;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Set<SolicitacaoMudanca> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Set<SolicitacaoMudanca> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public int getVersao() {
		return versao;
	}

	public void setVersao(int versao) {
		this.versao = versao;
	}

	public Requisito getRequisito() {
		return requisito;
	}

	public void setRequisito(Requisito requisito) {
		this.requisito = requisito;
	}
}
