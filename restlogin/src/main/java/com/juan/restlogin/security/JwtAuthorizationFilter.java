package com.juan.restlogin.security;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.juan.restlogin.tools.PropertiesSingleton;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
	
	private final String HEADER = "Authorization";
	private final String PREFIX = "Bearer ";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {
			if (existeJWTToken(request, response)) {
				Claims claims = validateToken(request);
				if (claims.get("authorities") != null) {
					setUpSpringAuthentication(claims);
				} else {
					SecurityContextHolder.clearContext();
				}
			} else {
					SecurityContextHolder.clearContext();
			}
			chain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			System.out.println("e.getMessage():"+e.getMessage());
			e.printStackTrace();
			Date expDate = e.getClaims().getExpiration();
			long diff = (new Date()).getTime() - expDate.getTime();
			System.out.println("diff: "+diff);
			System.out.println("tole: "+PropertiesSingleton.getInstance().getJwtTolerance());
			// si la diferencia entre el tiempo de expiracion es menor a la hora actual entonces se devuelve 401 para que renueve el token
			if(diff < PropertiesSingleton.getInstance().getJwtTolerance()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());				
			} else {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			}
			return;
		} catch (UnsupportedJwtException e) {
			System.out.println("e.getMessage():"+e.getMessage());
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		} catch (MalformedJwtException e) {
			System.out.println("e.getMessage():"+e.getMessage());
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("e.getMessage():"+e.getMessage());
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		}
	}	

	private Claims validateToken(HttpServletRequest request) {
		String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
		return Jwts.parser().setSigningKey(PropertiesSingleton.getInstance().getJwtSecretKey().getBytes()).parseClaimsJws(jwtToken).getBody();
	}

	/**
	 * Metodo para autenticarnos dentro del flujo de Spring
	 * 
	 * @param claims
	 */
	private void setUpSpringAuthentication(Claims claims) {
		@SuppressWarnings("unchecked")
		List<String> authorities = (List) claims.get("authorities");

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
				authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		SecurityContextHolder.getContext().setAuthentication(auth);

	}

	private boolean existeJWTToken(HttpServletRequest request, HttpServletResponse res) {
		String authenticationHeader = request.getHeader(HEADER);
		if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
			return false;
		return true;
	}

}
