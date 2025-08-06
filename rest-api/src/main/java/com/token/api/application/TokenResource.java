package com.token.api.application;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

@Path("/user-token")
public class TokenResource {

    private static final Log _log = LogFactoryUtil.getLog(TokenResource.class);

    // Cache estático para el token (similar al interceptor)
    private static String cachedAccessToken = null;
    private static String cachedRefreshToken = null;
    private static long tokenCacheTime = 0;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutos

    @Context
    private HttpServletRequest request;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken() {
        _log.info("🔍 Obteniendo token de acceso...");

        try {
            // 1. Verificar si tenemos token en cache y aún es válido
            if (cachedAccessToken != null && !cachedAccessToken.isEmpty() &&
                    (System.currentTimeMillis() - tokenCacheTime) < CACHE_DURATION) {

                _log.info("✅ Token encontrado en cache");
                return buildSuccessResponse(cachedAccessToken, "cache");
            }

            // 2. Buscar token en la sesión OpenID Connect
            String freshToken = extractTokenFromSession();

            if (freshToken != null && !freshToken.isEmpty()) {
                // Actualizar cache
                cachedAccessToken = freshToken;
                tokenCacheTime = System.currentTimeMillis();

                _log.info("✅ Token fresco obtenido de sesión OpenID");
                return buildSuccessResponse(freshToken, "openid_session");
            }

            // 3. Si no hay token, devolver error
            return buildErrorResponse("No se encontró token de acceso", Response.Status.UNAUTHORIZED);

        } catch (Exception e) {
            _log.error("💥 Error obteniendo token: " + e.getMessage(), e);
            return buildErrorResponse("Error interno: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshToken() {
        _log.info("🔄 Forzando actualización de token...");

        try {
            // Limpiar cache
            clearTokenCache();

            // Obtener token fresco
            String freshToken = extractTokenFromSession();

            if (freshToken != null && !freshToken.isEmpty()) {
                // Actualizar cache
                cachedAccessToken = freshToken;
                tokenCacheTime = System.currentTimeMillis();

                _log.info("✅ Token actualizado exitosamente");
                return buildSuccessResponse(freshToken, "refreshed_from_session");
            }

            return buildErrorResponse("No se pudo obtener token fresco", Response.Status.NOT_FOUND);

        } catch (Exception e) {
            _log.error("💥 Error refrescando token: " + e.getMessage(), e);
            return buildErrorResponse("Error refrescando: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("/debug")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDebugInfo() {
        _log.info("🐛 Información de debug solicitada");

        Map<String, Object> debugInfo = new HashMap<>();

        try {
            // Info del cache
            debugInfo.put("cache_token_present", cachedAccessToken != null);
            debugInfo.put("cache_token_length", cachedAccessToken != null ? cachedAccessToken.length() : 0);
            debugInfo.put("cache_age_ms", System.currentTimeMillis() - tokenCacheTime);
            debugInfo.put("cache_valid", (System.currentTimeMillis() - tokenCacheTime) < CACHE_DURATION);

            // Info de la sesión HTTP
            debugInfo.put("http_session_id", request.getSession().getId());
            debugInfo.put("user_principal", request.getUserPrincipal() != null ?
                    request.getUserPrincipal().getName() : "null");

            // Info de la sesión OpenID
            Object openIdSession = request.getSession().getAttribute("OPEN_ID_CONNECT_SESSION");
            debugInfo.put("openid_session_present", openIdSession != null);
            debugInfo.put("openid_session_type", openIdSession != null ?
                    openIdSession.getClass().getName() : "null");

            // Intentar extraer token para debug
            if (openIdSession != null) {
                try {
                    String sessionToken = extractTokenFromSessionObject(openIdSession);
                    debugInfo.put("session_token_present", sessionToken != null && !sessionToken.isEmpty());
                    debugInfo.put("session_token_length", sessionToken != null ? sessionToken.length() : 0);
                    debugInfo.put("tokens_match", sessionToken != null && sessionToken.equals(cachedAccessToken));
                } catch (Exception e) {
                    debugInfo.put("session_extraction_error", e.getMessage());
                }
            }

            debugInfo.put("timestamp", System.currentTimeMillis());

            return Response.ok(debugInfo).build();

        } catch (Exception e) {
            debugInfo.put("debug_error", e.getMessage());
            return Response.ok(debugInfo).build();
        }
    }

    @GET
    @Path("/clear-cache")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCache() {
        _log.info("🗑️ Limpiando cache de tokens");

        clearTokenCache();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Cache limpiado");
        response.put("timestamp", System.currentTimeMillis());

        return Response.ok(response).build();
    }

    // === MÉTODOS PRIVADOS ===

    /**
     * Extrae el token de la sesión OpenID Connect
     */
    private String extractTokenFromSession() {
        try {
            Object openIdSessionObj = request.getSession().getAttribute("OPEN_ID_CONNECT_SESSION");

            if (openIdSessionObj != null) {
                _log.info("📋 Sesión OpenID encontrada: " + openIdSessionObj.getClass().getName());
                return extractTokenFromSessionObject(openIdSessionObj);
            } else {
                _log.info("😞 No hay sesión OpenID Connect");
                return null;
            }

        } catch (Exception e) {
            _log.error("💥 Error extrayendo token de sesión: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extrae el token del objeto de sesión OpenID usando reflexión
     */
    private String extractTokenFromSessionObject(Object openIdSessionObj) throws Exception {
        // Intentar obtener el access token usando reflexión
        Method getAccessTokenMethod = openIdSessionObj.getClass().getMethod("getAccessTokenValue");
        String accessToken = (String) getAccessTokenMethod.invoke(openIdSessionObj);

        if (accessToken != null && !accessToken.isEmpty()) {
            _log.info("🎉 Access token extraído exitosamente");

            // También intentar obtener refresh token para cache
            try {
                Method getRefreshTokenMethod = openIdSessionObj.getClass().getMethod("getRefreshTokenValue");
                String refreshToken = (String) getRefreshTokenMethod.invoke(openIdSessionObj);
                if (refreshToken != null && !refreshToken.isEmpty()) {
                    cachedRefreshToken = refreshToken;
                    _log.info("🔄 Refresh token también extraído");
                }
            } catch (Exception refreshError) {
                _log.warn("⚠️ No se pudo obtener refresh token: " + refreshError.getMessage());
            }

            return accessToken;
        }

        return null;
    }

    /**
     * Construye respuesta de éxito
     */
    private Response buildSuccessResponse(String token, String source) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("accessToken", token);
        response.put("source", source);
        response.put("timestamp", System.currentTimeMillis());
        response.put("token_length", token.length());
        response.put("has_refresh_token", cachedRefreshToken != null);

        return Response.ok(response).build();
    }

    /**
     * Construye respuesta de error
     */
    private Response buildErrorResponse(String message, Response.Status status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());

        return Response.status(status).entity(errorResponse).build();
    }

    /**
     * Limpia el cache de tokens
     */
    private static void clearTokenCache() {
        cachedAccessToken = null;
        cachedRefreshToken = null;
        tokenCacheTime = 0;
    }
}