package umariana.pacientes.modelo;

public class CentralPacientes {

    private Paciente primero;
    private Paciente ultimo;
    private int numPacientes;

    public void agregarPacienteAlFinal(Paciente nuevo) {
        nuevo.setSiguiente(null);
        if (primero == null) {
            primero = nuevo;
            ultimo = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
            ultimo = nuevo;
        }
        numPacientes++;
    }

    public void agregarPacienteAlComienzo(Paciente nuevo) {
        nuevo.setSiguiente(primero);
        primero = nuevo;
        if (ultimo == null) {
            ultimo = nuevo;
        }
        numPacientes++;
    }

    public boolean agregarPacienteDespuesDe(int codigo, Paciente nuevo) {
        Paciente referencia = localizar(codigo);
        if (referencia == null) {
            return false;
        }
        nuevo.setSiguiente(referencia.getSiguiente());   
        referencia.setSiguiente(nuevo);                  
        if (referencia == ultimo) {                      
            ultimo = nuevo;
        }
        numPacientes++;
        return true;
    }

    public Paciente localizar(int codigo) {
        Paciente actual = primero;
        while (actual != null) {
            if (actual.getCodigo() == codigo) {
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }
    public boolean eliminar(int codigo) {
        Paciente anterior = null;
        Paciente actual = primero;

        while (actual != null && actual.getCodigo() != codigo) {
            anterior = actual;
            actual = actual.getSiguiente();
        }
        if (actual == null) {
            return false;                               
        }
        if (anterior == null) {
            primero = actual.getSiguiente();            
        } else {
            anterior.setSiguiente(actual.getSiguiente());
        }
        if (actual == ultimo) {
            ultimo = anterior;                           
        }
        actual.setSiguiente(null);
        numPacientes--;
        return true;
    }

    public boolean editarPaciente(int codigo, String nombre, String clinica,
                                  String informacionMedica, int sexo) {
        Paciente p = localizar(codigo);
        if (p == null) {
            return false;
        }
        p.setNombre(nombre);
        p.setClinica(clinica);
        p.setInformacionMedica(informacionMedica);
        p.setSexo(sexo);
        return true;
    }

    public String cadenaDeNodos() {
        StringBuilder sb = new StringBuilder("Primero");
        Paciente actual = primero;
        while (actual != null) {
            sb.append(" → [").append(actual.getCodigo()).append(": ")
              .append(actual.getNombre()).append("]");
            actual = actual.getSiguiente();
        }
        return sb.append(" → null").toString();
    }
    public void listar() {
        Paciente actual = primero;
        if (actual == null) {
            System.out.println("(lista vacía)");
            return;
        }
        while (actual != null) {
            System.out.println(actual);
            actual = actual.getSiguiente();
        }
    }

    public Paciente getPrimero() { return primero; }
    public Paciente getUltimo() { return ultimo; }
    public int getNumPacientes() { return numPacientes; }
    public boolean estaVacia() { return primero == null; }
}
