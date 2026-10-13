package umariana.pacientes.modelo;

public class Paciente {

    private int codigo;
    private String nombre;
    private String clinica;
    private String informacionMedica;
    private int sexo;              
    private Paciente siguiente;    

    public Paciente(int codigo, String nombre, String clinica,
                    String informacionMedica, int sexo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.clinica = clinica;
        this.informacionMedica = informacionMedica;
        this.sexo = sexo;
        this.siguiente = null;
    }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getClinica() { return clinica; }
    public void setClinica(String clinica) { this.clinica = clinica; }

    public String getInformacionMedica() { return informacionMedica; }
    public void setInformacionMedica(String informacionMedica) { this.informacionMedica = informacionMedica; }

    public int getSexo() { return sexo; }
    public void setSexo(int sexo) { this.sexo = sexo; }

    public Paciente getSiguiente() { return siguiente; }
    public void setSiguiente(Paciente siguiente) { this.siguiente = siguiente; }

    
    public String getSexoTexto() {
        if (sexo == 1) return "Masculino";
        if (sexo == 2) return "Femenino";
        return "Sin dato";
    }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | " + clinica + " | "
                + informacionMedica + " | " + getSexoTexto();
    }
}
