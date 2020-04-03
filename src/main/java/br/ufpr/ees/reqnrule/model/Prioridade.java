package br.ufpr.ees.reqnrule.model;

public enum Prioridade {
	ESSENCIAL("Essencial", 1),
	DESEJADO("Desejado", 2),
	OPCIONAL("Opcional", 3);
	
	private String nome;
	private int valor;
	
	Prioridade(String nome, int valor) {
		this.nome = nome;
		this.valor = valor;		
	}
	
	public int getValor() {
		return valor;
	}
	
	public static Prioridade getEnum(int valor) throws IllegalArgumentException {
		for (Prioridade v : values()) {
			if (v.getValor() == valor) {
				return v;
			}
		}		
		throw new IllegalArgumentException("Prioridade inválida.");
	}
	
	@Override
	public String toString() {
		return nome;
	}

}
