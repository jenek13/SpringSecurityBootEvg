package com.ten.security.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import com.ten.model.Role;
import com.ten.service.RoleService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Service
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
										HttpServletResponse httpServletResponse,
										Authentication authentication) throws IOException, ServletException {
		handle(httpServletRequest, httpServletResponse, authentication);
		clearAuthenticationAttributes(httpServletRequest);
	}

	protected void handle(HttpServletRequest request,
						  HttpServletResponse response, Authentication authentication) throws IOException {
		String targetUrl = determineTargetUrl(authentication);

		redirectStrategy.sendRedirect(request, response, targetUrl);//тут /admin
	}


	private String determineTargetUrl(Authentication authentication) {
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		if (roles.contains("ROLE_ADMIN")) {
			return "/admin";
		} else if (roles.contains("ROLE_USER")) {
			return "/user";
		}   return "/error";

	}


	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		//удаление атрибутов-исключений из сессии
	}
}
