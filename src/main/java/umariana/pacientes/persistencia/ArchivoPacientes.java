package umariana.pacientes.persistencia;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import umariana.pacientes.modelo.CentralPacientes;
import umariana.pacientes.modelo.Paciente;

public class ArchivoPacientes {

    private static final String SEPARADOR = "|";
    private final Path ruta;

    public ArchivoPacientes() {
        this(Paths.get("data", "pacientes.txt"));
    }

    public ArchivoPacientes(Path ruta) {
        this.ruta = ruta;
    }

    public Path getRutaAbsoluta() {
        return ruta.toAbsolutePath();
    }

    public void guardar(CentralPacientes central) throws IOException {
        Path padre = ruta.toAbsolutePath().getParent();
        if (padre != null) {
            Files.createDirectories(padre);
        }
        try (BufferedWriter bw = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            Paciente actual = central.getPrimero();
            while (actual != null) {
                bw.write(actual.getCodigo() + SEPARADOR
                        + actual.getNombre() + SEPARADOR
                        + actual.getClinica() + SEPARADOR
                        + actual.getInformacionMedica() + SEPARADOR
                        + actual.getSexo());
                bw.newLine();
                actual = actual.getSiguiente();
            }
        }
    }
    public CentralPacientes cargar() throws IOException {
        CentralPacientes central = new CentralPacientes();
        if (!Files.exists(ruta)) {
            return central;
        }
        for (String linea : Files.readAllLines(ruta, StandardCharsets.UTF_8)) {
            if (linea.trim().isEmpty()) {
                continue;
            }
            String[] d = linea.split("\\|", -1); 
            if (d.length != 5) {
                continue; 
            }
            try {
                int codigo = Integer.parseInt(d[0].trim());
                if (central.localizar(codigo) != null) {
                    continue; 
                }
                Paciente p = new Paciente(codigo, d[1], d[2], d[3],
                                          Integer.parseInt(d[4].trim()));
                central.agregarPacienteAlFinal(p);
            } catch (NumberFormatException e) {
            }
        }
        return central;
    }
}
