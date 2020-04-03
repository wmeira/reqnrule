package br.ufpr.ees.reqnrule.service;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import br.ufpr.ees.reqnrule.model.Usuario;

public class Service {

	@Inject
	protected Validator validator;
	
	protected <T> void validarBean(T t) throws Exception {
		Set<ConstraintViolation<T>> violations = validator.validate(t);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}
	}
}
