package br.ufpr.ees.reqnrule.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Requisito implements Serializable  {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @ManyToOne(optional=false)
    @JsonBackReference(value="requisito-projeto")
    private Projeto projeto;
    
    @NotNull(message = "O código não pode ser nulo.")
    private int codigo;
    
    @NotNull(message = "O nome não pode ser nulo.")
    @Size(max = 255, message="O nome do requisito deve ter no máximo 255 caracteres.")
    private String nome;
    
    @Size(max = 2047, message="A descrição do requisito deve ter no máximo 2047 caracteres.")
    private String descricao;
    
    @NotNull(message = "O tipo não pode ser nulo.")
    @Enumerated(EnumType.STRING)
    private TipoRequisito tipo;   
    
    @NotNull(message = "A prioridade não pode ser nula.")
    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;
    
    @Size(max = 255, message="A categoria deve ter no máximo 255 caracteres.")
    private String categoria = "NENHUMA";
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoRequisito estado = EstadoRequisito.ESPECIFICADO;
    
    @ManyToOne
    @JsonIgnoreProperties({"descricao", "tipo", "prioridade", "categoria", "estado", "pai", "filhos", "solicitacoesMudanca", "mudancas", "associados", "versao"})
    private Requisito pai;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy="pai")
    @JsonIgnoreProperties({"descricao", "tipo", "prioridade", "categoria", "estado", "pai", "filhos", "solicitacoesMudanca", "mudancas", "associados", "versao"})
    private Set<Requisito> filhos = new HashSet<Requisito>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
    	      name="associados",
    	      joinColumns={@JoinColumn(name="requisito_id")},
    	      inverseJoinColumns={@JoinColumn(name="associado_id")})
    @JsonIgnoreProperties({"descricao", "tipo", "prioridade", "categoria", "estado", "pai", "filhos", "solicitacoesMudanca", "mudancas", "associados", "versao"})
    private Set<Requisito> associados = new HashSet<Requisito>();

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy="requisito", cascade = CascadeType.ALL)
    @JsonManagedReference(value="solicitacao-requisito")
    private Set<SolicitacaoMudanca> solicitacoesMudanca = new HashSet<SolicitacaoMudanca>();
    
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, mappedBy="requisito", cascade = CascadeType.ALL)
    @JsonManagedReference(value="mudanca-requisito")
    private Set<Mudanca> mudancas = new HashSet<Mudanca>();
    
    @Transient
    private int versao;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public TipoRequisito getTipo() {
		return tipo;
	}

	public void setTipo(TipoRequisito tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public EstadoRequisito getEstado() {
		return estado;
	}

	public void setEstado(EstadoRequisito estado) {
		this.estado = estado;
	}

	public Requisito getPai() {
		return pai;
	}

	public void setPai(Requisito pai) {
		this.pai = pai;
	}

	public Set<Requisito> getAssociados() {
		return associados;
	}
	
	public void addAssociacao(Requisito requisito) {
		this.associados.add(requisito);
	}
	
	public void removeAssociacao(Requisito requisito) {
		this.associados.remove(requisito);
	}

	public void setAssociados(Set<Requisito> associados) {
		this.associados = associados;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}
	
	public void addSolicitacaoMudanca(SolicitacaoMudanca solicitacao) {
		this.solicitacoesMudanca.add(solicitacao);
	}

	public Set<SolicitacaoMudanca> getSolicitacoesMudanca() {
		return solicitacoesMudanca;
	}

	public void setSolicitacoesMudanca(Set<SolicitacaoMudanca> solicitacoesMudanca) {
		this.solicitacoesMudanca = solicitacoesMudanca;
	}

	public void addMudanca(Mudanca mudanca) { 
		this.mudancas.add(mudanca);
	}
	
	public Set<Mudanca> getMudancas() {
		return mudancas;
	}

	public void setMudancas(Set<Mudanca> mudancas) {
		this.mudancas = mudancas;
	}

	public Prioridade getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Prioridade prioridade) {
		this.prioridade = prioridade;
	}

//	@Basic(fetch=FetchType.EAGER)
//	public List<Requisito> getFilhos() {
//		List<Requisito> filhos = new ArrayList<Requisito>();
//		List<Requisito> requisitosDoProjeto = projeto.getRequisitos();
//		for(Requisito requisito : requisitosDoProjeto) {
//			if(requisito.getPai().equals(this)) {
//				filhos.add(requisito);
//			}
//		}
//		return filhos;
//	}
	
	public Set<Requisito> getFilhos() {
		return filhos;
	}
	
	public void setFilhos(Set<Requisito> filhos) {
		this.filhos = filhos;
	}
	
	@Basic(fetch=FetchType.EAGER)
	public int getVersao() {
		return mudancas.size();
	}


	
	
}
