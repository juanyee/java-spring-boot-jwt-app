package com.jwtspringboot.restlogin.security;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.jwtspringboot.restlogin.tools.PropertiesSingleton;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

public class TokenUtil {
	
	
	public static String generateToken(String email) {
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		
		String token = Jwts
				.builder()
				.setId("softtekJWT")
				.setSubject(email)
				.claim("authorities",
						grantedAuthorities.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + PropertiesSingleton.getInstance().getJwtTokenExpiration()))
				.signWith(SignatureAlgorithm.HS512,
						PropertiesSingleton.getInstance().getJwtSecretKey().getBytes()).compact();
		return "Bearer "+token;
	}
	
	/**
	 * Método para generar un nuevo token.
	 * 
	 * @param currentToken, actual token
	 * @param response
	 * @return token actual o nuevo
	 * */
	public static String generateRefreshToken(String currentToken, HttpServletResponse response) throws IOException {
		/* Si currentToken es vigente, se devuelve el mismo.
		 * Si currentToken está vencido, se valida la tolerancia
		 * Si la diferencia entre la fecha de expiración y la actual es mayor a la tolerancia se devuelve 403
		 * de lo contrario se genera un nuevo token
		 * */
		try {
			Claims claims = Jwts.parser().setSigningKey(PropertiesSingleton.getInstance().getJwtSecretKey().getBytes()).parseClaimsJws(currentToken.replace("Bearer ", "")).getBody();
			if (claims.get("authorities") != null) {
				return currentToken;
			} else {
				SecurityContextHolder.clearContext();
			}
		} catch (ExpiredJwtException e) {
			System.out.println("e.getMessage():"+e.getMessage());
			e.printStackTrace();
			Date expDate = e.getClaims().getExpiration();
			long diff = (new Date()).getTime() - expDate.getTime();
			System.out.println("diff: "+diff);
			System.out.println("tole: "+PropertiesSingleton.getInstance().getJwtTolerance());
			// si la diferencia entre el tiempo de expiracion es menor a la hora actual entonces se devuelve 401 para que renueve el token
			if(diff < PropertiesSingleton.getInstance().getJwtTolerance()) {
				return generateToken(UUID.randomUUID().toString());
//				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//				((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());				
			} else {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			}
			return "";
		} catch (UnsupportedJwtException e) {
			System.out.println("e.getMessage():"+e.getMessage());
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return "";
		} catch (MalformedJwtException e) {
			System.out.println("e.getMessage():"+e.getMessage());
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return "";
		} catch (Exception e) {
			System.out.println("e.getMessage():"+e.getMessage());
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return "";
		}
		Claims claims = Jwts.parser().setSigningKey(PropertiesSingleton.getInstance().getJwtSecretKey().getBytes()).parseClaimsJws(currentToken).getBody();
		return "";
	}
}
