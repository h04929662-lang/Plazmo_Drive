package com.plazmodrive.web.servlet;

import com.plazmodrive.api.model.Vehiculo;
import com.plazmodrive.web.service.VehiculoWebService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/web/vehiculos")
public class VehiculoServlet extends HttpServlet {

    @Inject
    private VehiculoWebService vehiculoService;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        List<Vehiculo> vehiculos = vehiculoService.listar();
        request.setAttribute("vehiculos", vehiculos);

        request.getRequestDispatcher("/WEB-INF/views/vehiculos.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String placa = request.getParameter("placa");
        String marca = request.getParameter("marca");
        String modelo = request.getParameter("modelo");

        if (placa == null || placa.isBlank()
                || marca == null || marca.isBlank()
                || modelo == null || modelo.isBlank()) {

            request.setAttribute("error",
                    "La placa, marca y modelo son obligatorios.");

            doGet(request, response);
            return;
        }

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(placa);
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setColor(request.getParameter("color"));
        vehiculo.setTipo(request.getParameter("tipo"));

        String anio = request.getParameter("anio");

        if (anio != null && !anio.isBlank()) {
            try {
                vehiculo.setAnio(Integer.valueOf(anio));
            } catch (NumberFormatException e) {
                request.setAttribute("error",
                        "El año debe ser un número válido.");
                doGet(request, response);
                return;
            }
        }

        try {
            vehiculoService.registrar(vehiculo);
            response.sendRedirect(
                    request.getContextPath() + "/web/vehiculos?registrado=1"
            );
        } catch (Exception e) {
            getServletContext().log("Error al registrar vehículo", e);
            request.setAttribute("error",
                    "No fue posible registrar el vehículo. Verifica que la placa no esté repetida.");
            doGet(request, response);
        }
    }
}
