package com.plazmodrive.web.service;

import com.plazmodrive.api.model.Vehiculo;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class VehiculoWebService {

    @PersistenceContext(unitName = "plazmoPU")
    private EntityManager em;

    public List<Vehiculo> listar() {
        return em.createQuery(
                "SELECT v FROM Vehiculo v ORDER BY v.id",
                Vehiculo.class
        ).getResultList();
    }

    public void registrar(Vehiculo vehiculo) {
        vehiculo.setId(null);
        vehiculo.setPlaca(vehiculo.getPlaca().trim().toUpperCase());
        vehiculo.setMarca(vehiculo.getMarca().trim());
        vehiculo.setModelo(vehiculo.getModelo().trim());

        if (vehiculo.getEstado() == null || vehiculo.getEstado().isBlank()) {
            vehiculo.setEstado("DISPONIBLE");
        }

        em.persist(vehiculo);
    }
}
