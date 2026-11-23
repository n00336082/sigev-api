package com.sigev.api.servicios;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sigev.api.modelos.Usuario;
import com.sigev.api.repositorio.UsuarioRepositorio;

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public List<Usuario> listar() {
        return usuarioRepositorio.findAll();
    }

    public Usuario obtener(Long id) {
        return usuarioRepositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe")
        );
    }

    public Usuario crear(Usuario datos) {
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        if (datos.getCorreo() == null || datos.getCorreo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo es obligatorio");
        }
        if (datos.getRol() == null || datos.getRol().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol es obligatorio");
        }
        String correo = datos.getCorreo().trim().toLowerCase();
        if (usuarioRepositorio.findByCorreoIgnoreCase(correo).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese correo");
        }
        Usuario nuevo = new Usuario(null, datos.getNombre().trim(), correo, datos.getRol().trim().toUpperCase(), true);
        return usuarioRepositorio.save(nuevo);
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario actual = obtener(id);
        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            actual.setNombre(datos.getNombre().trim());
        }
        if (datos.getRol() != null && !datos.getRol().isBlank()) {
            actual.setRol(datos.getRol().trim().toUpperCase());
        }
        actual.setActivo(datos.isActivo());
        return usuarioRepositorio.save(actual);
    }

    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario actual = obtener(id);
        actual.setActivo(activo);
        return usuarioRepositorio.save(actual);
    }
}
