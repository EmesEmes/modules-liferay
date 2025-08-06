package prueba.token.portlet.commons;

import com.liferay.portal.remote.cors.annotation.CORS;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@CORS(allowMethods = {"GET, POST"})
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public abstract class BaseController {
    protected BaseController() {}
}

