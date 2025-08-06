package com.token.api.application;

import javax.ws.rs.core.Application;
import org.osgi.service.component.annotations.Component;
import java.util.Collections;
import java.util.Set;

@Component(
		property = {
				"osgi.jaxrs.application.base=/token",
				"osgi.jaxrs.name=Token.Rest"
		},
		service = Application.class
)
public class TokenRestApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		return Collections.singleton(new TokenResource());
	}
}