package com.token.interceptor;

import javax.servlet.http.HttpServletRequest;
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
public class TokenSessionManager implements LifecycleAction {

    private static final Log _log = LogFactoryUtil.getLog(TokenSessionManager.class);
    // Definimos una constante para la clave del atributo de sesión
    public static final String KEYCLOAK_ACCESS_TOKEN = "KEYCLOAK_ACCESS_TOKEN";

    @Override
    public void processLifecycleEvent(LifecycleEvent event) throws ActionException {
        HttpServletRequest request = event.getRequest();
        _log.info("=== Capturando token de OpenID y guardando en sesión ===");

        try {
            Object openIdSessionObj = request.getSession().getAttribute("OPEN_ID_CONNECT_SESSION");

            if (openIdSessionObj != null) {
                // ... (tu lógica para obtener el accessToken es correcta) ...
                java.lang.reflect.Method getAccessTokenMethod =
                        openIdSessionObj.getClass().getMethod("getAccessTokenValue");
                String accessToken = (String) getAccessTokenMethod.invoke(openIdSessionObj);

                if (accessToken != null && !accessToken.isEmpty()) {
                    // 🎉 La clave: guardar el token en la sesión HTTP del usuario
                    request.getSession().setAttribute(KEYCLOAK_ACCESS_TOKEN, accessToken);
                    _log.info("🎉 ACCESS TOKEN GUARDADO EN SESIÓN PARA EL USUARIO ACTUAL.");
                }

                // Puedes hacer lo mismo con el refresh token si lo necesitas
                // String refreshToken = (String) openIdSessionObj.getClass().getMethod("getRefreshTokenValue").invoke(openIdSessionObj);
                // request.getSession().setAttribute("KEYCLOAK_REFRESH_TOKEN", refreshToken);

            } else {
                _log.info("😞 No hay sesión OpenID Connect");
            }
        } catch (Exception e) {
            _log.error("💥 Error al procesar el evento de login: " + e.getMessage(), e);
        }
    }
}