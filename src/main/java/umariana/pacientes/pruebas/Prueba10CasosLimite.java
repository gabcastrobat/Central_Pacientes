package umariana.pacientes.pruebas;

import umariana.pacientes.modelo.CentralPacientes;
import umariana.pacientes.modelo.Paciente;

/**
 * PASO 10: casos límite de la lista. NO toca el archivo data/pacientes.txt.
 * Imprime OK / FALLO por cada verificación.
 */
public class Prueba10CasosLimite {

    private static int fallos = 0;

    public static void main(String[] args) {
        CentralPacientes c = new CentralPacientes();

        titulo("1) Lista vacía");
        c.listar();
        verificar("primero == null y ultimo == null", c.getPrimero() == null && c.getUltimo() == null);
        verificar("localizar(1) devuelve null", c.localizar(1) == null);
        verificar("eliminar(1) devuelve false", !c.eliminar(1));
        verificar("numPacientes == 0", c.getNumPacientes() == 0);
        System.out.println(c.cadenaDeNodos());

        titulo("2) Un solo nodo");
        c.agregarPacienteAlFinal(p(1001, "Ana Torres"));
        verificar("primero == ultimo", c.getPrimero() == c.getUltimo());
        verificar("primero.siguiente == null", c.getPrimero().getSiguiente() == null);
        System.out.println(c.cadenaDeNodos());
        verificar("eliminar(1001) deja la lista vacía", c.eliminar(1001)
                && c.getPrimero() == null && c.getUltimo() == null && c.getNumPacientes() == 0);
        c.agregarPacienteAlComienzo(p(1001, "Ana Torres"));
        verificar("agregar al COMIENZO en lista vacía fija también ultimo", c.getUltimo() == c.getPrimero());

        titulo("3) Armar 6 pacientes (final, comienzo e intermedia)");
        c.agregarPacienteAlFinal(p(1002, "Carlos Ruiz"));
        c.agregarPacienteAlFinal(p(1003, "Luisa Mora"));
        c.agregarPacienteAlComienzo(p(1000, "Pedro Gómez"));
        c.agregarPacienteDespuesDe(1001, p(1004, "Marta Díaz"));   // intermedia
        c.agregarPacienteDespuesDe(1003, p(1005, "Juan Pérez"));   // tras el último
        System.out.println(c.cadenaDeNodos());
        verificar("orden esperado 1000,1001,1004,1002,1003,1005",
                c.cadenaDeNodos().equals("Primero → [1000: Pedro Gómez] → [1001: Ana Torres] → [1004: Marta Díaz]"
                        + " → [1002: Carlos Ruiz] → [1003: Luisa Mora] → [1005: Juan Pérez] → null"));
        verificar("ultimo es 1005 (insertar tras el último actualiza ultimo)", c.getUltimo().getCodigo() == 1005);
        verificar("numPacientes == 6", c.getNumPacientes() == 6);

        titulo("4) Eliminaciones");
        verificar("eliminar el PRIMERO (1000)", c.eliminar(1000) && c.getPrimero().getCodigo() == 1001);
        verificar("eliminar el ÚLTIMO (1005)", c.eliminar(1005) && c.getUltimo().getCodigo() == 1003
                && c.getUltimo().getSiguiente() == null);
        verificar("eliminar uno INTERMEDIO (1004)", c.eliminar(1004) && c.localizar(1004) == null);
        verificar("eliminar un código INEXISTENTE (9999) devuelve false", !c.eliminar(9999));
        verificar("localizar un código inexistente devuelve null", c.localizar(9999) == null);
        System.out.println(c.cadenaDeNodos());
        verificar("numPacientes == 3", c.getNumPacientes() == 3);

        titulo("5) Editar y referencia inexistente");
        verificar("editarPaciente(1002) mantiene el código",
                c.editarPaciente(1002, "Carlos R.", "Clínica Centro", "Control", 1)
                        && c.localizar(1002).getNombre().equals("Carlos R."));
        verificar("editarPaciente(9999) devuelve false", !c.editarPaciente(9999, "x", "x", "x", 1));
        verificar("agregarPacienteDespuesDe(9999) devuelve false", !c.agregarPacienteDespuesDe(9999, p(2000, "Nadie")));
        System.out.println(c.cadenaDeNodos());

        System.out.println("\n" + (fallos == 0 ? "TODAS LAS PRUEBAS OK" : "HAY " + fallos + " FALLO(S)"));
    }

    private static Paciente p(int codigo, String nombre) {
        return new Paciente(codigo, nombre, "Clínica Norte", "Control", 1);
    }

    private static void titulo(String t) {
        System.out.println("\n=== " + t + " ===");
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (!condicion) fallos++;
        System.out.println((condicion ? "  OK     " : "  FALLO  ") + descripcion);
    }
}
