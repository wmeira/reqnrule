package br.ufpr.ees.reqnrule.model;

public enum EstadoSolicitacao {
	SOLICITADO("Solicitado", 1),
	ATENDIDO("Atendido", 2),
	REJEITADO("Rejeitado", 3);
	
	private String nome;
	private int valor;
	
	EstadoSolicitacao(String nome, int valor) {
		this.nome = nome;
		this.valor = valor;		
	}
	
	public int getValor() {
		return valor;
	}
	
	public static EstadoSolicitacao getEnum(int valor) throws IllegalArgumentException {
		for (EstadoSolicitacao v : values()) {
			if (v.getValor() == valor) {
				return v;
			}
		}		
		throw new IllegalArgumentException("Estado da solicitação de mudança inválido.");
	}
	
	@Override
	public String toString() {
		return nome;
	}
}
