package com.juan.restlogin.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juan.restlogin.model.Usuario;
import com.juan.restlogin.repository.UsuarioRepository;
import com.juan.restlogin.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public Usuario save(Usuario usuario) {
		try {
			return usuarioRepository.save(usuario);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Usuario> findAll() {
		List<Usuario> list = new ArrayList<>();
		try {
			return usuarioRepository.findAll();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	@Override
	public Usuario findById(Long id) {
		try {
			return usuarioRepository.findById(id).orElseThrow();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Boolean deleteById(Long id) {
		try {
			usuarioRepository.deleteById(id);
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	@Override
	public Boolean update(Usuario usuario) {
		try {
			usuarioRepository.save(usuario);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
		
	}

	@Override
	public Usuario findByEmail(String email) {
		try {
			return usuarioRepository.findByEmail(email);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Usuario findByTelefono(String telefono) {
		try {
			return usuarioRepository.findByTelefono(telefono);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	public Boolean login(String email, String password) {
		try {
			Usuario usuario = usuarioRepository.findByEmailAndPassword(email, password);
			if(usuario!=null) {
				return true;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
