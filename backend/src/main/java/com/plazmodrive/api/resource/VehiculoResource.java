package com.plazmodrive.api.resource;

import com.plazmodrive.api.model.Vehiculo;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/vehiculos")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehiculoResource {
    @PersistenceContext(unitName = "plazmoPU")
    private EntityManager em;

    @GET
    public List<Vehiculo> listar() {
        return em.createQuery("SELECT v FROM Vehiculo v ORDER BY v.id", Vehiculo.class).getResultList();
    }

    @GET @Path("/{id}")
    public Response obtener(@PathParam("id") Long id) {
        Vehiculo v = em.find(Vehiculo.class, id);
        return v == null ? Response.status(404).build() : Response.ok(v).build();
    }

    @POST
    public Response crear(Vehiculo v) {
        if (v == null || vacio(v.getPlaca()) || vacio(v.getMarca()) || vacio(v.getModelo()))
            return Response.status(400).entity(Map.of("error", "Marca, modelo y placa son obligatorios")).build();
        v.setId(null);
        v.setPlaca(v.getPlaca().trim().toUpperCase());
        v.setEstado(vacio(v.getEstado()) ? "DISPONIBLE" : v.getEstado().trim().toUpperCase());
        long existe = em.createQuery("SELECT COUNT(v) FROM Vehiculo v WHERE v.placa = :p", Long.class)
                .setParameter("p", v.getPlaca()).getSingleResult();
        if (existe > 0)
            return Response.status(409).entity(Map.of("error", "Ya existe un vehículo con esa placa")).build();
        em.persist(v);
        em.flush();
        return Response.created(URI.create("/api/vehiculos/" + v.getId())).entity(v).build();
    }

    @PUT @Path("/{id}")
    public Response editar(@PathParam("id") Long id, Vehiculo d) {
        Vehiculo v = em.find(Vehiculo.class, id);
        if (v == null) return Response.status(404).build();
        if (!vacio(d.getPlaca())) v.setPlaca(d.getPlaca().trim().toUpperCase());
        if (!vacio(d.getMarca())) v.setMarca(d.getMarca());
        if (!vacio(d.getModelo())) v.setModelo(d.getModelo());
        if (d.getColor() != null) v.setColor(d.getColor());
        if (d.getTipo() != null) v.setTipo(d.getTipo());
        if (d.getAnio() != null) v.setAnio(d.getAnio());
        if (!vacio(d.getEstado())) v.setEstado(d.getEstado().trim().toUpperCase());
        return Response.ok(v).build();
    }

    @DELETE @Path("/{id}")
    public Response eliminar(@PathParam("id") Long id) {
        Vehiculo v = em.find(Vehiculo.class, id);
        if (v == null) return Response.status(404).build();
        em.remove(v);
        return Response.noContent().build();
    }

    private static boolean vacio(String s) { return s == null || s.isBlank(); }
}
