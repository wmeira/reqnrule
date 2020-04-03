package br.ufpr.ees.reqnrule.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
public class SolicitacaoMudanca implements Serializable {

	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @NotNull(message = "O código não pode ser nulo.")
    private int codigo;
    
    @ManyToOne(optional = false)
    private Membro autor;
    
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataSolicitacao;
    
    @NotNull
    @Size(max = 2047, message="A solicitação deve ter no máximo 2047 caracteres.")
    private String solicitacao;
    
    @NotNull
    @ManyToOne(optional = false)
    @JsonBackReference(value="solicitacao-requisito")
    private Requisito requisito;
    
    @Enumerated(EnumType.STRING)
    private EstadoSolicitacao estado;
    
    @ManyToOne
    private Membro atendente;
    
    @ManyToOne
    @JsonBackReference(value="mudanca-solicitacao")
    private Mudanca mudanca;
    
    @Size(max = 2047, message="As observações devem ter no máximo 2047 caracteres.")
    private String observacoes;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAtendimento;
    
    @PrePersist
    public void inserirDataMudanca() {
    	dataSolicitacao = new Date();
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

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public String getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}

	public EstadoSolicitacao getEstado() {
		return estado;
	}

	public void setEstado(EstadoSolicitacao estado) {
		this.estado = estado;
	}

	public Membro getAtendente() {
		return atendente;
	}

	public void setAtendente(Membro atendente) {
		this.atendente = atendente;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Date getDataAtendimento() {
		return dataAtendimento;
	}

	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}

	public Mudanca getMudanca() {
		return mudanca;
	}

	public void setMudanca(Mudanca mudanca) {
		this.mudanca = mudanca;
	}

	public Requisito getRequisito() {
		return requisito;
	}

	public void setRequisito(Requisito requisito) {
		this.requisito = requisito;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
}
