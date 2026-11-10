<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.plazmodrive.api.model.Vehiculo" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de vehículos - Plazmo Drive</title>
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f2f5f8;
            color: #263238;
        }
        header {
            background: #172b4d;
            color: white;
            padding: 22px 8%;
        }
        header h1 { margin: 0; }
        main {
            max-width: 1100px;
            margin: 30px auto;
            padding: 0 20px;
        }
        section {
            background: white;
            padding: 25px;
            margin-bottom: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 8px #00000012;
        }
        .formulario {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 15px;
        }
        label { font-weight: bold; }
        input, select {
            width: 100%;
            padding: 11px;
            margin-top: 7px;
            border: 1px solid #ccd4dc;
            border-radius: 6px;
        }
        button {
            background: #1687d9;
            color: white;
            border: 0;
            padding: 12px 22px;
            border-radius: 6px;
            cursor: pointer;
            font-weight: bold;
        }
        button:hover { background: #0e68aa; }
        .accion { grid-column: 1 / -1; }
        .mensaje {
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 15px;
        }
        .error { background: #fde8e8; color: #a12626; }
        .exito { background: #e4f6e9; color: #176b35; }
        .tabla { overflow-x: auto; }
        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 650px;
        }
        th, td {
            text-align: left;
            padding: 12px;
            border-bottom: 1px solid #e4e8ed;
        }
        th { background: #edf3f8; }
        footer {
            text-align: center;
            padding: 20px;
            color: #65717d;
        }
    </style>
</head>
<body>

<header>
    <h1>PLAZMO DRIVE</h1>
    <p>Administración y consulta de vehículos</p>
</header>

<main>

    <section>
        <h2>Registrar vehículo</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="mensaje error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <% if ("1".equals(request.getParameter("registrado"))) { %>
            <div class="mensaje exito">
                Vehículo registrado correctamente.
            </div>
        <% } %>

        <form action="<%= request.getContextPath() %>/web/vehiculos"
              method="post">

            <div class="formulario">
                <label>Placa
                    <input type="text" name="placa" required maxlength="10">
                </label>

                <label>Marca
                    <input type="text" name="marca" required maxlength="60">
                </label>

                <label>Modelo
                    <input type="text" name="modelo" required maxlength="60">
                </label>

                <label>Color
                    <input type="text" name="color" maxlength="40">
                </label>

                <label>Tipo
                    <select name="tipo">
                        <option value="">Seleccione</option>
                        <option value="Automóvil">Automóvil</option>
                        <option value="Camioneta">Camioneta</option>
                        <option value="Motocicleta">Motocicleta</option>
                        <option value="Campero">Campero</option>
                    </select>
                </label>

                <label>Año
                    <input type="number" name="anio" min="1900" max="2100">
                </label>

                <div class="accion">
                    <button type="submit">Registrar vehículo</button>
                </div>
            </div>
        </form>
    </section>

    <section>
        <h2>Vehículos registrados</h2>

        <div class="tabla">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Placa</th>
                        <th>Marca</th>
                        <th>Modelo</th>
                        <th>Color</th>
                        <th>Tipo</th>
                        <th>Año</th>
                        <th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Vehiculo> vehiculos =
                            (List<Vehiculo>) request.getAttribute("vehiculos");

                        if (vehiculos != null && !vehiculos.isEmpty()) {
                            for (Vehiculo v : vehiculos) {
                    %>
                    <tr>
                        <td><%= v.getId() %></td>
                        <td><%= v.getPlaca() %></td>
                        <td><%= v.getMarca() %></td>
                        <td><%= v.getModelo() %></td>
                        <td><%= v.getColor() == null ? "" : v.getColor() %></td>
                        <td><%= v.getTipo() == null ? "" : v.getTipo() %></td>
                        <td><%= v.getAnio() == null ? "" : v.getAnio() %></td>
                        <td><%= v.getEstado() %></td>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="8">No hay vehículos registrados.</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </section>

</main>

<footer>
    PLAZMO DRIVE - Sistema de gestión de vehículos
</footer>

</body>
</html>
