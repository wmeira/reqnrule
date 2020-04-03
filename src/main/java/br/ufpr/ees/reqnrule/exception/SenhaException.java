package br.ufpr.ees.reqnrule.exception;

public class SenhaException extends Exception {

	private static final long serialVersionUID = 1L;

	public SenhaException() {
		super();
	}
	
	public SenhaException(String mensagem) {
		super(mensagem);
	}
}
