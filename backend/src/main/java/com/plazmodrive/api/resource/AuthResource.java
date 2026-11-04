package com.plazmodrive.api.resource;

import com.plazmodrive.api.model.Usuario;
import com.plazmodrive.api.util.PasswordUtil;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/auth")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    @PersistenceContext(unitName = "plazmoPU")
    private EntityManager em;

    public static class LoginDto { public String cedula; public String password; }

    @POST @Path("/login")
    public Response login(LoginDto dto) {
        if (dto == null || dto.cedula == null || dto.password == null)
            return Response.status(400).entity(Map.of("error", "Usuario y contraseña son obligatorios")).build();
        List<Usuario> r = em.createQuery("SELECT u FROM Usuario u WHERE u.cedula = :c", Usuario.class)
                .setParameter("c", dto.cedula.trim()).getResultList();
        if (r.isEmpty() || !PasswordUtil.verify(dto.password, r.get(0).getPasswordHash()))
            return Response.status(401).entity(Map.of("error", "Usuario o contraseña incorrectos")).build();
        return Response.ok(r.get(0)).build();
    }
}
