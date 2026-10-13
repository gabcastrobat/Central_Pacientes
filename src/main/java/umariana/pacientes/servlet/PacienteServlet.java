package umariana.pacientes.servlet;

import java.io.IOException;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import umariana.pacientes.modelo.CentralPacientes;
import umariana.pacientes.modelo.Paciente;
import umariana.pacientes.persistencia.ArchivoPacientes;

@WebServlet("/pacientes")
public class PacienteServlet extends HttpServlet {

    private CentralPacientes central;
    private ArchivoPacientes archivo;
    private final Object candado = new Object();

    @Override
    public void init() throws ServletException {
        
        String ruta = System.getProperty("pacientes.archivo");
        if (ruta != null && !ruta.trim().isEmpty()) {
            archivo = new ArchivoPacientes(Paths.get(ruta.trim()));
        } else {
            archivo = new ArchivoPacientes(); // data/pacientes.txt
        }
        try {
            central = archivo.cargar();
        } catch (IOException e) {
            central = new CentralPacientes();
            log("No se pudo leer el archivo: " + e.getMessage());
        }
        log("Archivo de datos: " + archivo.getRutaAbsoluta()
                + " | pacientes cargados: " + central.getNumPacientes());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        recuperarMensaje(request);
        String accion = request.getParameter("accion");

        synchronized (candado) {
            request.setAttribute("primero", central.getPrimero());
            request.setAttribute("numPacientes", central.getNumPacientes());
            request.setAttribute("rutaArchivo", archivo.getRutaAbsoluta().toString());

            if ("buscar".equals(accion)) {
                String texto = request.getParameter("codigo");
                request.setAttribute("codigoBuscado", texto);
                Integer codigo = aEntero(texto);
                if (codigo == null) {
                    request.setAttribute("mensaje", "Escribe un código numérico para buscar.");
                    request.setAttribute("mensajeError", Boolean.TRUE);
                } else {
                    request.setAttribute("busquedaHecha", Boolean.TRUE);
                    request.setAttribute("buscado", central.localizar(codigo));   // null si no existe
                }
            } else if ("editar".equals(accion)) {
                Integer codigo = aEntero(request.getParameter("codigo"));
                if (codigo != null) {
                    request.setAttribute("paraEditar", central.localizar(codigo));
                }
            }
        }
        request.getRequestDispatcher("/pacientes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "agregarFinal";
        }
        String error = null;
        String exito = null;

        synchronized (candado) {
            try {
                switch (accion) {
                    case "agregarFinal":
                    case "agregarComienzo":
                    case "agregarDespues": {
                        Paciente nuevo = leerPaciente(request);
                        if (central.localizar(nuevo.getCodigo()) != null) {
                            throw new IllegalArgumentException(
                                    "Ya existe un paciente con el código " + nuevo.getCodigo() + ".");
                        }
                        if ("agregarFinal".equals(accion)) {
                            central.agregarPacienteAlFinal(nuevo);
                            exito = "Paciente " + nuevo.getCodigo() + " agregado al final.";
                        } else if ("agregarComienzo".equals(accion)) {
                            central.agregarPacienteAlComienzo(nuevo);
                            exito = "Paciente " + nuevo.getCodigo() + " agregado al comienzo.";
                        } else {
                            Integer referencia = aEntero(request.getParameter("codigoReferencia"));
                            if (referencia == null) {
                                throw new IllegalArgumentException(
                                        "Escribe el código del paciente después del cual insertar.");
                            }
                            if (!central.agregarPacienteDespuesDe(referencia, nuevo)) {
                                throw new IllegalArgumentException(
                                        "No existe un paciente con el código " + referencia + ".");
                            }
                            exito = "Paciente " + nuevo.getCodigo()
                                    + " insertado después del " + referencia + ".";
                        }
                        break;
                    }
                    case "eliminar": {
                        Integer codigo = aEntero(request.getParameter("codigo"));
                        if (codigo == null || !central.eliminar(codigo)) {
                            throw new IllegalArgumentException("No existe un paciente con ese código.");
                        }
                        exito = "Paciente " + codigo + " eliminado.";
                        break;
                    }
                    case "editar": {
                        Paciente datos = leerPaciente(request);
                        boolean ok = central.editarPaciente(datos.getCodigo(), datos.getNombre(),
                                datos.getClinica(), datos.getInformacionMedica(), datos.getSexo());
                        if (!ok) {
                            throw new IllegalArgumentException("No existe un paciente con ese código.");
                        }
                        exito = "Paciente " + datos.getCodigo() + " actualizado.";
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Acción no reconocida.");
                }
                archivo.guardar(central);                      // sincroniza el archivo tras cada cambio
            } catch (IllegalArgumentException e) {
                error = e.getMessage();
            } catch (IOException e) {
                error = "La lista cambió, pero no se pudo guardar el archivo: " + e.getMessage();
            }
        }

        guardarMensaje(request, error != null ? error : exito, error != null);
        response.sendRedirect("pacientes");
    }
    
    private Paciente leerPaciente(HttpServletRequest request) {
        Integer codigo = aEntero(request.getParameter("codigo"));
        if (codigo == null || codigo <= 0) {
            throw new IllegalArgumentException("El código debe ser un número entero positivo.");
        }
        String nombre = limpiar(request.getParameter("nombre"));
        String clinica = limpiar(request.getParameter("clinica"));
        String info = limpiar(request.getParameter("informacionMedica"));
        if (nombre.isEmpty() || clinica.isEmpty() || info.isEmpty()) {
            throw new IllegalArgumentException("Nombre, clínica e información médica son obligatorios.");
        }
        Integer sexo = aEntero(request.getParameter("sexo"));
        if (sexo == null || (sexo != 1 && sexo != 2)) {
            throw new IllegalArgumentException("Selecciona el sexo (1 = Masculino, 2 = Femenino).");
        }
        return new Paciente(codigo, nombre, clinica, info, sexo);
    }
    private static Integer aEntero(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private static String limpiar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace('|', '/').replace('\r', ' ').replace('\n', ' ').trim();
    }    
    private void guardarMensaje(HttpServletRequest request, String texto, boolean esError) {
        HttpSession sesion = request.getSession();
        sesion.setAttribute("mensaje", texto);
        sesion.setAttribute("mensajeError", esError);
    }
    private void recuperarMensaje(HttpServletRequest request) {
        HttpSession sesion = request.getSession();
        Object mensaje = sesion.getAttribute("mensaje");
        if (mensaje != null) {
            request.setAttribute("mensaje", mensaje);
            request.setAttribute("mensajeError", sesion.getAttribute("mensajeError"));
            sesion.removeAttribute("mensaje");
            sesion.removeAttribute("mensajeError");
        }
    }
}
