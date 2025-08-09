package com.kruger.tramitesYDocumentos.portlet.controller;

import com.kruger.tramitesYDocumentos.portlet.commons.BaseController;
import com.kruger.tramitesYDocumentos.portlet.service.UserService;
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

@Component(service = UserController.class, scope = ServiceScope.PROTOTYPE)
@JaxrsApplicationSelect("(osgi.jaxrs.name=LiferayService.Rest)")
@JaxrsResource
@Path("/user")
@Setter
public class UserController extends BaseController {

    @Reference
    private UserService userService;

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAuth() {
        return Response.ok(userService.getUserAuth()).build();
    }
}
