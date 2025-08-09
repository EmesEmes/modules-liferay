package com.kruger.tramitesYDocumentos.portlet.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component(immediate = true, service = UserService.class)
public class UserService {

    @Reference
    private TokenService tokenService;

    public String getUserAuth() {
        String token = tokenService.getToken();
        //String apiUrl = "https://erp.krugercorp.com/msHumanTalent/v1/employees/me";
        String apiUrl = "https://kerpdev.krugercorp.com/msHumanTalent/v1/employees/me";
        try {
            // Crear conexión
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Configuración de la petición
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Accept", "application/json");

            // Leer la respuesta
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    return response.toString(); // JSON devuelto por la API
                }
            } else {
                throw new RuntimeException("Error en la solicitud: HTTP " + responseCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al llamar a la API", e);
        }
    }
}
