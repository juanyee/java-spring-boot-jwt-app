package com.jwtspringboot.restlogin.service;

import java.util.List;

import com.jwtspringboot.restlogin.model.Usuario;

public interface UsuarioService {
	public Usuario save(Usuario usuario);
	public List<Usuario> findAll();
	public Usuario findById(Long id);
	public Boolean deleteById(Long id);
	public Boolean update(Usuario usuario);
	public Usuario findByEmail(String email);
	public Usuario findByTelefono(String telefono);
	public Boolean login(String email, String password);
}
