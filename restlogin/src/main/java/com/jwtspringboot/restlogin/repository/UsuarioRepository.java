package com.jwtspringboot.restlogin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jwtspringboot.restlogin.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	@Query("SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)")
	public Usuario findByEmail(String email);
	
	@Query("SELECT u FROM Usuario u WHERE u.telefono = :telefono")
	public Usuario findByTelefono(String telefono);

	@Query("SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email) and u.password = :password")
	public Usuario findByEmailAndPassword(String email, String password);
}
