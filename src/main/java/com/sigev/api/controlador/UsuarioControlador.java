package com.sigev.api.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sigev.api.modelos.Usuario;
import com.sigev.api.servicios.UsuarioServicio;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioServicio.listar();
    }

    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable("id") Long id) {
        return usuarioServicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioServicio.crear(usuario);
    }

    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable("id") Long id, @RequestBody Usuario usuario) {
        return usuarioServicio.actualizar(id, usuario);
    }

    @PatchMapping("/{id}/estado")
    public Usuario cambiarEstado(@PathVariable("id") Long id, @RequestParam("activo") boolean activo) {
        return usuarioServicio.cambiarEstado(id, activo);
    }
}
