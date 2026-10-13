<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="umariana.pacientes.modelo.Paciente" %>
<%!
    /** Escapa texto para HTML: un nombre con < o " no rompe la página. */
    private static String h(Object o) {
        if (o == null) return "";
        return String.valueOf(o).replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
%>
<%
    // Si alguien abre pacientes.jsp directo (sin pasar por el Servlet), se le redirige.
    if (request.getAttribute("rutaArchivo") == null) {
        response.sendRedirect(request.getContextPath() + "/pacientes");
        return;
    }
    String ctx = request.getContextPath();
    Paciente primero = (Paciente) request.getAttribute("primero");
    Integer numPacientes = (Integer) request.getAttribute("numPacientes");
    int total = numPacientes == null ? 0 : numPacientes;
    String rutaArchivo = (String) request.getAttribute("rutaArchivo");
    String mensaje = (String) request.getAttribute("mensaje");
    boolean mensajeError = Boolean.TRUE.equals(request.getAttribute("mensajeError"));

    Paciente paraEditar = (Paciente) request.getAttribute("paraEditar");
    boolean editando = paraEditar != null;
    boolean busquedaHecha = Boolean.TRUE.equals(request.getAttribute("busquedaHecha"));
    Paciente buscado = (Paciente) request.getAttribute("buscado");
    String codigoBuscado = (String) request.getAttribute("codigoBuscado");

    String vCodigo = editando ? String.valueOf(paraEditar.getCodigo()) : "";
    String vNombre = editando ? paraEditar.getNombre() : "";
    String vClinica = editando ? paraEditar.getClinica() : "";
    String vInfo = editando ? paraEditar.getInformacionMedica() : "";
    int vSexo = editando ? paraEditar.getSexo() : 1;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Central de pacientes</title>
    <link rel="stylesheet" href="<%= ctx %>/css/estilos.css">
</head>
<body>
<header class="cabecera">
    <h1>Central de pacientes</h1>
    <p class="resumen"><%= total %> <%= total == 1 ? "paciente" : "pacientes" %> en la lista enlazada.</p>
</header>

<main>
    <% if (mensaje != null) { %>
    <p class="aviso <%= mensajeError ? "aviso-error" : "aviso-ok" %>" role="status"><%= h(mensaje) %></p>
    <% } %>

    <%-- Reto 1: la cadena de nodos, dibujada --%>
    <section aria-labelledby="t-cadena">
        <h2 id="t-cadena">Cadena de nodos</h2>
        <% if (primero == null) { %>
        <p class="vacio">La lista está vacía: <strong>primero</strong> apunta a null.</p>
        <% } else { %>
        <div class="cadena" role="img" aria-label="Lista enlazada de <%= total %> nodos terminada en null">
            <% Paciente nodo = primero;
               while (nodo != null) {
                   boolean esPrimero = (nodo == primero);
                   boolean esUltimo = (nodo.getSiguiente() == null);
                   String marca = esPrimero && esUltimo ? "primero y ultimo"
                                : esPrimero ? "primero" : esUltimo ? "ultimo" : ""; %>
            <div class="paso">
                <span class="marca"><%= marca %></span>
                <div class="nodo">
                    <span class="dato"><%= nodo.getCodigo() %>: <%= h(nodo.getNombre()) %></span>
                    <span class="ptr" aria-hidden="true"></span>
                </div>
            </div>
            <span class="flecha" aria-hidden="true">→</span>
            <%     nodo = nodo.getSiguiente();
               } %>
            <div class="paso"><span class="marca"></span><div class="nulo">null</div></div>
        </div>
        <% } %>
    </section>

    <div class="rejilla">
        <section class="panel" aria-labelledby="t-registro">
            <h2 id="t-registro"><%= editando ? "Editar paciente " + vCodigo : "Registrar paciente" %></h2>
            <form method="post" action="<%= ctx %>/pacientes">
                <% if (editando) { %><input type="hidden" name="accion" value="editar"><% } %>
                <label>Código
                    <input type="number" name="codigo" min="1" required value="<%= h(vCodigo) %>" <%= editando ? "readonly" : "" %>>
                </label>
                <label>Nombre
                    <input type="text" name="nombre" maxlength="80" required value="<%= h(vNombre) %>">
                </label>
                <label>Clínica
                    <input type="text" name="clinica" maxlength="80" required value="<%= h(vClinica) %>">
                </label>
                <label>Información médica
                    <textarea name="informacionMedica" rows="3" maxlength="300" required><%= h(vInfo) %></textarea>
                </label>
                <label>Sexo
                    <select name="sexo">
                        <option value="1" <%= vSexo == 1 ? "selected" : "" %>>Masculino</option>
                        <option value="2" <%= vSexo == 2 ? "selected" : "" %>>Femenino</option>
                    </select>
                </label>
                <% if (!editando) { %>
                <label>Dónde insertar
                    <select name="accion" id="accion">
                        <option value="agregarFinal">Al final de la lista</option>
                        <option value="agregarComienzo">Al comienzo de la lista</option>
                        <option value="agregarDespues">Después de un código</option>
                    </select>
                </label>
                <label id="fila-ref" hidden>Después del código
                    <input type="number" name="codigoReferencia" min="1">
                </label>
                <% } %>
                <div class="acciones">
                    <button type="submit" class="boton"><%= editando ? "Guardar cambios" : "Registrar paciente" %></button>
                    <% if (editando) { %><a class="enlace" href="<%= ctx %>/pacientes">Cancelar</a><% } %>
                </div>
            </form>
        </section>

        <section class="panel" aria-labelledby="t-buscar">
            <h2 id="t-buscar">Buscar por código</h2>
            <form method="get" action="<%= ctx %>/pacientes">
                <input type="hidden" name="accion" value="buscar">
                <label>Código
                    <input type="number" name="codigo" min="1" required value="<%= h(codigoBuscado) %>">
                </label>
                <div class="acciones">
                    <button type="submit" class="boton">Buscar</button>
                </div>
            </form>
            <% if (busquedaHecha && buscado != null) { %>
            <p class="resultado resultado-ok">
                Encontrado: <strong><%= h(buscado.getNombre()) %></strong> (código <%= buscado.getCodigo() %>),
                <%= h(buscado.getClinica()) %>, <%= h(buscado.getSexoTexto()) %>.
                Información médica: <%= h(buscado.getInformacionMedica()) %>
            </p>
            <% } else if (busquedaHecha) { %>
            <p class="resultado resultado-no">No existe un paciente con el código <%= h(codigoBuscado) %>.</p>
            <% } %>
        </section>
    </div>

    <section aria-labelledby="t-tabla">
        <h2 id="t-tabla">Pacientes</h2>
        <% if (primero == null) { %>
        <p class="vacio">Aún no hay pacientes. Registra el primero con el formulario.</p>
        <% } else { %>
        <div class="tabla-wrap">
            <table>
                <thead>
                <tr>
                    <th>Código</th><th>Nombre</th><th>Clínica</th>
                    <th>Información médica</th><th>Sexo</th><th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <% Paciente actual = primero;
                   while (actual != null) { %>
                <tr>
                    <td><%= actual.getCodigo() %></td>
                    <td><%= h(actual.getNombre()) %></td>
                    <td><%= h(actual.getClinica()) %></td>
                    <td><%= h(actual.getInformacionMedica()) %></td>
                    <td><%= h(actual.getSexoTexto()) %></td>
                    <td class="celda-acciones">
                        <a class="enlace" href="<%= ctx %>/pacientes?accion=editar&amp;codigo=<%= actual.getCodigo() %>">Editar</a>
                        <form method="post" action="<%= ctx %>/pacientes"
                              onsubmit="return confirm('¿Eliminar al paciente <%= actual.getCodigo() %>?');">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="codigo" value="<%= actual.getCodigo() %>">
                            <button type="submit" class="boton-peligro">Eliminar</button>
                        </form>
                    </td>
                </tr>
                <%     actual = actual.getSiguiente();
                   } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </section>
</main>

<footer class="pie">
    Made in Asus
</footer>

<script>
    // Muestra el campo "Después del código" solo cuando se elige esa opción.
    (function () {
        var sel = document.getElementById('accion');
        var fila = document.getElementById('fila-ref');
        if (!sel || !fila) return;
        var campo = fila.querySelector('input');
        sel.addEventListener('change', function () {
            var mostrar = sel.value === 'agregarDespues';
            fila.hidden = !mostrar;
            campo.required = mostrar;
        });
    })();
</script>
</body>
</html>
