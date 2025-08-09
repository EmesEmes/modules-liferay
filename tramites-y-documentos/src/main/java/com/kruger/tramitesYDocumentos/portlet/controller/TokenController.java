package com.kruger.tramitesYDocumentos.portlet.controller;

import com.kruger.tramitesYDocumentos.portlet.commons.BaseController;
import com.kruger.tramitesYDocumentos.portlet.service.TokenService;
import lombok.Setter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsApplicationSelect;
import org.osgi.service.jaxrs.whiteboard.propertytypes.JaxrsResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component(service = TokenController.class, scope = ServiceScope.PROTOTYPE)
@JaxrsApplicationSelect("(osgi.jaxrs.name=LiferayService.Rest)")
@JaxrsResource
@Path("/token")
@Setter
public class TokenController extends BaseController {

    @Reference
    private TokenService tokenService;

    @GET
    @Path("/getToken")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken() {
        return Response.ok(tokenService.getToken()).build();
    }
}
