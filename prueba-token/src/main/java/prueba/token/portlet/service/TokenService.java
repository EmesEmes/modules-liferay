package prueba.token.portlet.service;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component(immediate = true, service = TokenService.class)

public class TokenService {
  public String getToken() {
     Integer a = 5;
     Integer b = 4;
      ServiceContext serviceContext = ServiceContextThreadLocal.getServiceContext();
      HttpServletRequest request = serviceContext.getRequest();
      HttpSession session = request.getSession(false);
      String token = (String) session.getAttribute("KEYCLOAK_ACCESS_TOKEN");

      System.out.println(token);

     return token;
  }
}
