package com.juan.restlogin.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.juan.restlogin.model.RefreshTokenRequest;
import com.juan.restlogin.model.Usuario;
import com.juan.restlogin.repository.UsuarioRepository;
import com.juan.restlogin.security.TokenUtil;
import com.juan.restlogin.service.UsuarioService;
import com.juan.restlogin.tools.PropertiesSingleton;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRestController {
	@Autowired
	private UsuarioService usuarioService;
	
	@PostMapping("/login")
	@ResponseBody
	public String login() {
		String token = TokenUtil.generateToken("hola");
		System.out.println("token: " + token);
		return token;
	}

	@PostMapping("/refresh")
	@ResponseBody
	public String refreshToken(@RequestBody RefreshTokenRequest rtr, HttpServletResponse response) throws IOException {
		String token = TokenUtil.generateRefreshToken(rtr.getToken(), response);
		System.out.println("token: " + token);
		return token;
	}
	
	@PostMapping("/save")
	@ResponseBody
	public Usuario save(@RequestBody Usuario usuario) {
		System.out.println("save!!");
		return usuarioService.save(usuario);
	}
	
	@GetMapping("/all")
	public List<Usuario> getAll() {
		System.out.println("getAll!!");
		return usuarioService.findAll();
	}

	@GetMapping("/{id}")
	public Usuario insert(@PathVariable("id") Long id) {
		System.out.println("insert!!");
		return usuarioService.findById(id);
	}
	
	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable("id") Long id) {
		System.out.println("delete!!");
		usuarioService.deleteById(id);
	}
	
	@PutMapping("/update")
	public void update(@RequestBody Usuario usuario) {
		System.out.println("update!!");
		usuarioService.update(usuario);
	}
}
