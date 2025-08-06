
package com.ejemplo.token.consumer.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.PortalUtil;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.token.interceptor.SimpleTokenGrabber;

public class TokenConsumerPortlet extends MVCPortlet {

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		String apiResponse = "No se pudo obtener el token de acceso.";
		int statusCode = 0;

		try {
			HttpServletRequest httpRequest = PortalUtil.getHttpServletRequest(renderRequest);
			HttpSession session = httpRequest.getSession();
			// Intentamos obtener el token usando la clave estática de tu interceptor
			String accessToken = (String) session.getAttribute(SimpleTokenGrabber.KEYCLOAK_ACCESS_TOKEN); // <-- ¡Corregido aquí también!

			if (accessToken != null && !accessToken.isEmpty()) {
				// Si el token existe, intentamos usarlo
				HttpClient client = HttpClient.newHttpClient();
				String apiUrl = "https://tu-keycloak-instance/auth/realms/mi-realm/protocol/openid-connect/userinfo";

				HttpRequest apiRequest = HttpRequest.newBuilder()
						.uri(URI.create(apiUrl))
						.header("Authorization", "Bearer " + accessToken)
						.GET()
						.build();

				HttpResponse<String> response = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

				apiResponse = response.body();
				statusCode = response.statusCode();
			}
		} catch (Exception e) {
			apiResponse = "Error al llamar a la API: " + e.getMessage();
			e.printStackTrace();
		}

		renderRequest.setAttribute("apiResponse", apiResponse);
		renderRequest.setAttribute("statusCode", statusCode);

		super.doView(renderRequest, renderResponse);
	}
}