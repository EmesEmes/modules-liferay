package com.token.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.osgi.service.component.annotations.Component;

@Component(
        property = "key=login.events.post",
        service = LifecycleAction.class
)
public class SimpleTokenGrabber implements LifecycleAction {

    private static final Log _log = LogFactoryUtil.getLog(SimpleTokenGrabber.class);

    public static final String KEYCLOAK_ACCESS_TOKEN = "KEYCLOAK_ACCESS_TOKEN";

    @Override
    public void processLifecycleEvent(LifecycleEvent event) throws ActionException {
        HttpServletRequest request = event.getRequest();

        _log.info("=== Capturando token de OpenID y guardando en sesión ===");

        try {
            Object openIdSessionObj = request.getSession().getAttribute("OPEN_ID_CONNECT_SESSION");

            if (openIdSessionObj != null) {
                _log.info("SESION OPENID ENCONTRADA!");

                try {
                    java.lang.reflect.Method getAccessTokenMethod = openIdSessionObj.getClass().getMethod("getAccessTokenValue");
                    String accessToken = (String) getAccessTokenMethod.invoke(openIdSessionObj);

                    if (accessToken != null && !accessToken.isEmpty()) {
                        request.getSession().setAttribute(KEYCLOAK_ACCESS_TOKEN, accessToken);

                        _log.info("ACCESS TOKEN GUARDADO EN SESION PARA EL USUARIO ACTUAL.");
                        // _log.info(request.getSession().getAttribute(KEYCLOAK_ACCESS_TOKEN));
                        System.out.println(request.getSession().getId());
                    }
                } catch (Exception reflectionError) {
                    _log.error("Error con reflexión: " + reflectionError.getMessage());
                }
            } else {
                _log.info("No hay sesión OpenID Connect");
            }
        } catch (Exception e) {
            _log.error("Error obteniendo token: " + e.getMessage(), e);
        }
    }
}
