package br.ufpr.ees.reqnrule.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;

public class Rest {

	 protected Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
       Map<String, String> responseObj = new HashMap<>();
       for (ConstraintViolation<?> violation : violations) {
           responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
       }  
       return Response.status(Response.Status.FORBIDDEN).entity(responseObj);
   }
}
