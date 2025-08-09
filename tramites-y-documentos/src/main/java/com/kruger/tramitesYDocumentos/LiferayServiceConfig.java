package com.kruger.tramitesYDocumentos;


import lombok.Getter;
import lombok.Setter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

import javax.ws.rs.core.Application;

@Component(property = {JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/liferayService",
        JaxrsWhiteboardConstants.JAX_RS_NAME + "=LiferayService.Rest",
        "liferay.cors.annotation=true"}, service = Application.class)
@Getter
@Setter
public class LiferayServiceConfig extends Application{
}
