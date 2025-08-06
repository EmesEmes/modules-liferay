<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects />

<%
    String apiResponse = (String) renderRequest.getAttribute("apiResponse");
    Integer statusCode = (Integer) renderRequest.getAttribute("statusCode");
%>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <h3>Resultado de la Llamada a la API Externa</h3>
            <hr>

            <% if (statusCode != null && statusCode != 0) { %>
                <div class="alert alert-info">
                    <strong>Código de estado HTTP:</strong> <%= statusCode %>
                </div>
            <% } %>

            <p><strong>Cuerpo de la respuesta de la API:</strong></p>
            <pre style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; white-space: pre-wrap; word-wrap: break-word;">
<%= apiResponse %>
            </pre>
        </div>
    </div>
</div>