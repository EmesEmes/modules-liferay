package com.kruger.tramitesYDocumentos.portlet.commons;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.liferay.portal.remote.cors.annotation.CORS;

@CORS(allowMethods = {"GET, POST"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public abstract class BaseController {

    protected BaseController() {
    }
}
