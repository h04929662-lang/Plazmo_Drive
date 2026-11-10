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

/**
 * Servicio REST encargado de autenticar a los usuarios de Plazmo Drive.
 */
@Path("/auth")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @PersistenceContext(unitName = "plazmoPU")
    private EntityManager em;

    /**
     * Datos recibidos desde el formulario o cliente de inicio de sesión.
     */
    public static class LoginDto {
        public String cedula;
        public String password;
    }

    /**
     * Verifica las credenciales y responde con el resultado de autenticación.
     *
     * @param dto datos de acceso del usuario.
     * @return respuesta HTTP con el resultado de la autenticación.
     */
    @POST
    @Path("/login")
    public Response login(LoginDto dto) {

        // Comprueba que se hayan enviado los datos obligatorios.
        if (dto == null || dto.cedula == null || dto.cedula.isBlank()
                || dto.password == null || dto.password.isBlank()) {
            return Response.status(400)
                    .entity(Map.of("error", "Usuario y contraseña son obligatorios"))
                    .build();
        }

        // Busca al usuario mediante su número de cédula.
        List<Usuario> usuarios = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.cedula = :c",
                Usuario.class
        ).setParameter("c", dto.cedula.trim()).getResultList();

        // Rechaza el acceso si el usuario no existe o la contraseña no coincide.
        if (usuarios.isEmpty()
                || !PasswordUtil.verify(dto.password, usuarios.get(0).getPasswordHash())) {
            return Response.status(401)
                    .entity(Map.of("error", "Error en la autenticación: usuario o contraseña incorrectos"))
                    .build();
        }

        Usuario usuario = usuarios.get(0);

        // Devuelve únicamente información pública, nunca el hash de la contraseña.
        Map<String, Object> datosUsuario = Map.of(
                "id", usuario.getId(),
                "nombres", usuario.getNombres(),
                "apellidos", usuario.getApellidos(),
                "cedula", usuario.getCedula(),
                "rol", usuario.getRol()
        );

        return Response.ok(Map.of(
                "mensaje", "Autenticación satisfactoria",
                "usuario", datosUsuario
        )).build();
    }
}