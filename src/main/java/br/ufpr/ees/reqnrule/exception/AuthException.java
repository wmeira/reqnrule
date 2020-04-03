package br.ufpr.ees.reqnrule.exception;


public class AuthException extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthException() {
		super("Operação não autorizada");
	}
	
	public AuthException(String mensagem) {
		super(mensagem);
	}
}
