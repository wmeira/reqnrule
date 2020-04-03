package br.ufpr.ees.reqnrule.model;

public enum Papel {
	GERENTE_DE_PROJETO("Gerente de projeto", 1),
	GERENTE_DE_REQUISITOS("Gerente de requisitos", 2),
	VERIFICADOR("Verificador", 3),
	OBSERVADOR("Observador", 4),
	EXCLUIDO("Excluído", 5);
	
	private String nome;
	private int valor;
	
	Papel(String nome, int valor) {
		this.nome = nome;
		this.valor = valor;		
	}
	
	public int getValor() {
		return valor;
	}
	
	public static Papel getEnum(int valor) throws IllegalArgumentException {
		for (Papel v : values()) {
			if (v.getValor() == valor) {
				return v;
			}
		}		
		throw new IllegalArgumentException("Papel inválido.");
	}
	
	@Override
	public String toString() {
		return nome;
	}
	
}
