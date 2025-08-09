package com.kruger.tramitesYDocumentos.portlet.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;

@Component(immediate = true, service = TokenService.class)
public class TokenService {

    public String getToken() {
        ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();
        HttpServletRequest request = serviceContext.getRequest();
        HttpSession session = request.getSession(false);
        String token = (String) session.getAttribute("KEYCLOAK_ACCESS_TOKEN");
        return token;
    }
}
