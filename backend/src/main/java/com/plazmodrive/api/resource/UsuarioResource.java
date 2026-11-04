package com.plazmodrive.api.resource;

import com.plazmodrive.api.model.Usuario;
import com.plazmodrive.api.util.PasswordUtil;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Path("/usuarios")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    @PersistenceContext(unitName = "plazmoPU")
    private EntityManager em;

    public static class UsuarioDto {
        public String nombres; public String apellidos; public String cedula;
        public LocalDate fechaNacimiento; public String password; public String rol;
    }

    @GET
    public List<Usuario> listar() {
        return em.createQuery("SELECT u FROM Usuario u ORDER BY u.id", Usuario.class).getResultList();
    }

    @GET @Path("/{id}")
    public Response obtener(@PathParam("id") Long id) {
        Usuario u = em.find(Usuario.class, id);
        return u == null ? Response.status(404).build() : Response.ok(u).build();
    }

    @POST
    public Response crear(UsuarioDto dto) {
        if (dto == null || vacio(dto.nombres) || vacio(dto.apellidos) || vacio(dto.cedula)
                || dto.fechaNacimiento == null || vacio(dto.password))
            return Response.status(400).entity(Map.of("error", "Complete todos los campos")).build();
        String cedula = dto.cedula.trim();
        long existe = em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.cedula = :c", Long.class)
                .setParameter("c", cedula).getSingleResult();
        if (existe > 0)
            return Response.status(409).entity(Map.of("error", "Ya existe un usuario con esa cédula")).build();
        Usuario u = new Usuario();
        u.setNombres(dto.nombres.trim());
        u.setApellidos(dto.apellidos.trim());
        u.setCedula(cedula);
        u.setFechaNacimiento(dto.fechaNacimiento);
        u.setPasswordHash(PasswordUtil.hash(dto.password));
        em.persist(u); // rol USUARIO por defecto; un admin se cambia luego con PUT
        em.flush();
        return Response.created(URI.create("/api/usuarios/" + u.getId())).entity(u).build();
    }

    @PUT @Path("/{id}")
    public Response editar(@PathParam("id") Long id, UsuarioDto dto) {
        Usuario u = em.find(Usuario.class, id);
        if (u == null) return Response.status(404).build();
        if (!vacio(dto.nombres)) u.setNombres(dto.nombres.trim());
        if (!vacio(dto.apellidos)) u.setApellidos(dto.apellidos.trim());
        if (!vacio(dto.cedula)) u.setCedula(dto.cedula.trim());
        if (dto.fechaNacimiento != null) u.setFechaNacimiento(dto.fechaNacimiento);
        if (!vacio(dto.rol)) u.setRol(dto.rol);
        if (!vacio(dto.password)) u.setPasswordHash(PasswordUtil.hash(dto.password));
        return Response.ok(u).build();
    }

    @DELETE @Path("/{id}")
    public Response eliminar(@PathParam("id") Long id) {
        Usuario u = em.find(Usuario.class, id);
        if (u == null) return Response.status(404).build();
        em.remove(u);
        return Response.noContent().build();
    }

    private static boolean vacio(String s) { return s == null || s.isBlank(); }
}
