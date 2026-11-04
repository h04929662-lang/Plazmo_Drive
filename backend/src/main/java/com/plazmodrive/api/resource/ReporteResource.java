package com.plazmodrive.api.resource;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/reportes")
@Stateless
@Produces(MediaType.APPLICATION_JSON)
public class ReporteResource {
    @PersistenceContext(unitName = "plazmoPU")
    private EntityManager em;

    @GET
    public Map<String, Object> resumen() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("usuarios", count("SELECT COUNT(u) FROM Usuario u"));
        m.put("vehiculos", count("SELECT COUNT(v) FROM Vehiculo v"));
        m.put("vehiculosDisponibles", count("SELECT COUNT(v) FROM Vehiculo v WHERE v.estado = 'DISPONIBLE'"));
        m.put("reservasActivas", count("SELECT COUNT(r) FROM Reserva r WHERE r.estado = 'ACTIVA'"));
        m.put("reservasCanceladas", count("SELECT COUNT(r) FROM Reserva r WHERE r.estado = 'CANCELADA'"));
        return m;
    }

    private long count(String jpql) {
        return em.createQuery(jpql, Long.class).getSingleResult();
    }
}
