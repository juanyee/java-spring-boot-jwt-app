package com.jwtspringboot.restlogin.tools;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class PropertiesSingleton {

	private static PropertiesSingleton instance;
	private Properties prop = new Properties();
	private String propFileName = "application.properties";
	
	// propiedades
	private String jwtSecretKey;
	private int jwtTokenExpiration;
	private int jwtTolerance;

	private PropertiesSingleton() {
		loadProperties();
	}
	
	private void loadProperties() {
		try(InputStream is = getClass().getClassLoader().getResourceAsStream(propFileName)) {
			prop.load(is);
			
			jwtSecretKey = prop.getProperty("jwt.secretKey", "12345");
			jwtTokenExpiration = Integer.parseInt(prop.getProperty("jwt.tokenExpirationSeconds", "600"))*1000;
			jwtTolerance = Integer.parseInt(prop.getProperty("jwt.toleranceSeconds", "120"))*1000;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getJwtTokenExpiration() {
		return jwtTokenExpiration;
	}

	public static PropertiesSingleton getInstance() {
		if(instance==null) {
			instance = new PropertiesSingleton();
		}
		return instance;
	}

	public void reloadProperties() {
		loadProperties();
	}

	public String getJwtSecretKey() {
		return jwtSecretKey;
	}
	
	public int getJwtTolerance() {
		return jwtTolerance;
	}
}
