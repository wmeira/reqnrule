package br.ufpr.ees.reqnrule.model;

public enum TipoRequisito {
	REQUISITO_FUNCIONAL("Requisito funcional", 1),
	REQUISITO_NAO_FUNCIONAL("Requisito não-funcional" , 2),
	REQUISITO_DE_PROJETO("Requisito de projeto", 3);	
	
	private String nome;
	private int valor;
	
	TipoRequisito(String nome, int valor) {
		this.nome = nome;
		this.valor = valor;		
	}
	
	public int getValor() {
		return valor;
	}
	
	public static TipoRequisito getEnum(int valor) throws IllegalArgumentException {
		for (TipoRequisito v : values()) {
			if (v.getValor() == valor) {
				return v;
			}
		}		
		throw new IllegalArgumentException("Tipo de requisito inválido.");
	}
	
	@Override
	public String toString() {
		return nome;
	}
}
