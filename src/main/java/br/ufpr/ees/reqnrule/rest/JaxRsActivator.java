package br.ufpr.ees.reqnrule.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 7 "no XML" approach to activating
 * JAX-RS.
 */
@ApplicationPath("/api")
public class JaxRsActivator extends Application {
    /* class body intentionally left blank */
}
