package br.ufpr.ees.reqnrule.model;

public enum EstadoRequisito {
	ESPECIFICADO("Especificado", 1),
	APROVADO("Aprovado", 2),
	IMPLEMENTADO("Implementado", 3),
	VALIDADO("Validado", 4),
	CANCELADO("Cancelado", 5);
	
	private String nome;
	private int valor;
	
	EstadoRequisito(String nome, int valor) {
		this.nome = nome;
		this.valor = valor;		
	}
	
	public int getValor() {
		return valor;
	}
	
	public static EstadoRequisito getEnum(int valor) throws IllegalArgumentException {
		for (EstadoRequisito v : values()) {
			if (v.getValor() == valor) {
				return v;
			}
		}		
		throw new IllegalArgumentException("Estado do requisito inválido.");
	}
	
	@Override
	public String toString() {
		return nome;
	}
}
