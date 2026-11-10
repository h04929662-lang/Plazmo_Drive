package com.plazmodrive.web.service;

import com.plazmodrive.api.model.Vehiculo;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 * Servicio de negocio para las operaciones de gestión de vehículos.
 * Utiliza JPA para consultar y almacenar información en PostgreSQL.
 */
@Stateless
public class VehiculoWebService {

    @PersistenceContext(unitName = "plazmoPU")
    private EntityManager em;

    /**
     * Obtiene todos los vehículos registrados, ordenados por identificador.
     *
     * @return lista de vehículos almacenados.
     */
    public List<Vehiculo> listar() {
        return em.createQuery(
                "SELECT v FROM Vehiculo v ORDER BY v.id",
                Vehiculo.class
        ).getResultList();
    }

    /**
     * Normaliza los datos principales y registra un vehículo nuevo.
     *
     * @param vehiculo información del vehículo que se desea guardar.
     */
    public void registrar(Vehiculo vehiculo) {
        vehiculo.setId(null);
        vehiculo.setPlaca(vehiculo.getPlaca().trim().toUpperCase());
        vehiculo.setMarca(vehiculo.getMarca().trim());
        vehiculo.setModelo(vehiculo.getModelo().trim());

        // Asigna el estado inicial cuando no se ha especificado.
        if (vehiculo.getEstado() == null || vehiculo.getEstado().isBlank()) {
            vehiculo.setEstado("DISPONIBLE");
        }

        em.persist(vehiculo);
    }
}