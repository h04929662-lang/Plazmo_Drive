package com.plazmodrive.api.resource;

import com.plazmodrive.api.model.Reserva;
import com.plazmodrive.api.model.Usuario;
import com.plazmodrive.api.model.Vehiculo;
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

@Path("/reservas")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservaResource {
    @PersistenceContext(unitName = "plazmoPU")
    private EntityManager em;

    public static class ReservaDto {
        public Long usuarioId; public Long vehiculoId;
        public LocalDate fechaInicio; public LocalDate fechaFin;
    }

    /** Historial: todas, o filtradas con ?usuarioId=1 */
    @GET
    public List<Reserva> listar(@QueryParam("usuarioId") Long usuarioId) {
        if (usuarioId != null)
            return em.createQuery("SELECT r FROM Reserva r WHERE r.usuario.id = :u ORDER BY r.fechaInicio DESC", Reserva.class)
                    .setParameter("u", usuarioId).getResultList();
        return em.createQuery("SELECT r FROM Reserva r ORDER BY r.fechaInicio DESC", Reserva.class).getResultList();
    }

    @GET @Path("/{id}")
    public Response obtener(@PathParam("id") Long id) {
        Reserva r = em.find(Reserva.class, id);
        return r == null ? Response.status(404).build() : Response.ok(r).build();
    }

    @POST
    public Response crear(ReservaDto dto) {
        if (dto == null || dto.usuarioId == null || dto.vehiculoId == null
                || dto.fechaInicio == null || dto.fechaFin == null)
            return Response.status(400).entity(Map.of("error", "usuarioId, vehiculoId, fechaInicio y fechaFin son obligatorios")).build();
        if (dto.fechaFin.isBefore(dto.fechaInicio))
            return Response.status(400).entity(Map.of("error", "La fecha final no puede ser anterior a la inicial")).build();
        Usuario u = em.find(Usuario.class, dto.usuarioId);
        Vehiculo v = em.find(Vehiculo.class, dto.vehiculoId);
        if (u == null || v == null)
            return Response.status(404).entity(Map.of("error", "Usuario o vehículo no existe")).build();

        long cruces = em.createQuery("SELECT COUNT(r) FROM Reserva r WHERE r.vehiculo.id = :v AND r.estado = 'ACTIVA' "
                        + "AND r.fechaInicio <= :fin AND r.fechaFin >= :ini", Long.class)
                .setParameter("v", v.getId()).setParameter("ini", dto.fechaInicio).setParameter("fin", dto.fechaFin)
                .getSingleResult();
        if (cruces > 0)
            return Response.status(409).entity(Map.of("error", "El vehículo ya está reservado en esas fechas")).build();

        Reserva r = new Reserva();
        r.setUsuario(u);
        r.setVehiculo(v);
        r.setFechaInicio(dto.fechaInicio);
        r.setFechaFin(dto.fechaFin);
        em.persist(r);
        em.flush();
        return Response.created(URI.create("/api/reservas/" + r.getId())).entity(r).build();
    }

    /** Cancelar una reserva (cambia el estado, no la borra del historial). */
    @PUT @Path("/{id}/cancelar")
    public Response cancelar(@PathParam("id") Long id) {
        Reserva r = em.find(Reserva.class, id);
        if (r == null) return Response.status(404).build();
        r.setEstado("CANCELADA");
        return Response.ok(r).build();
    }
}
