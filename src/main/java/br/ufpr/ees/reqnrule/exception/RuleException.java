package br.ufpr.ees.reqnrule.exception;

public class RuleException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private String key;
	
	public RuleException(String key, String message) {
		super(message);
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

}
