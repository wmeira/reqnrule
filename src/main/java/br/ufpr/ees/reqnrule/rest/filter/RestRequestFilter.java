package br.ufpr.ees.reqnrule.rest.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import br.ufpr.ees.reqnrule.service.AutenticadorService;

@Provider
@PreMatching
public class RestRequestFilter implements ContainerRequestFilter {

	@Inject
	AutenticadorService authService;
	
	@Override
	public void filter(ContainerRequestContext requestCtx) throws IOException {
		String path = requestCtx.getUriInfo().getPath();
		
		// IMPORTANT!!! First, Acknowledge any pre-flight test from browsers for
		// this case before validating the headers (CORS stuff)
		if (requestCtx.getRequest().getMethod().equals("OPTIONS")) {
			requestCtx.abortWith(Response.status(Response.Status.OK).build());
			return;
		}

		// For any other methods besides login, the authToken must be verified
		if (!path.endsWith("/auth/login") && !path.endsWith("/usuario/novo") ) {
			String authToken = requestCtx.getHeaderString(HttpHeaderNames.AUTH_TOKEN);

			// if it isn't valid, just kick them out.
			if (!authService.isAuthTokenValido(authToken)) {
				requestCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}
		}
	}
}
