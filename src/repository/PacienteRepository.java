package repository;

import exception.DadosInvalidosException;
import model.Paciente;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PacienteRepository {

    private static final String SEPARADOR_CSV = ";";
    private static final String CABECALHO_CSV = "NOME;DATA_NASCIMENTO;CPF;TELEFONE";

    private final ArrayList<Paciente> pacientes;
    private final Path caminhoArquivo;

    public PacienteRepository() {
        this("pacientes.csv");
    }

    public PacienteRepository(String nomeArquivo) {
        this.pacientes = new ArrayList<>();
        this.caminhoArquivo = Paths.get(nomeArquivo);
    }

    public void adicionar(Paciente novoPaciente) throws DadosInvalidosException, IOException {
        if (novoPaciente == null) {
            throw new DadosInvalidosException("Não é possível cadastrar um paciente nulo.");
        }

        for (Paciente existente : pacientes) {
            if (existente.getCpf().equals(novoPaciente.getCpf())) {
                throw new DadosInvalidosException(
                        "Já existe um paciente cadastrado com o CPF " + novoPaciente.getCpf()
                        + " (" + existente.getNome() + ").");
            }
        }

        pacientes.add(novoPaciente);

        try {
            salvarEmArquivo();
        } catch (IOException e) {
            pacientes.remove(novoPaciente);
            throw e;
        }
    }

    public Paciente removerPorIndice(int indice) throws DadosInvalidosException, IOException {
        if (indice < 0 || indice >= pacientes.size()) {
            throw new DadosInvalidosException(
                    "Selecione um paciente válido na tabela antes de clicar em Remover.");
        }

        Paciente removido = pacientes.remove(indice);

        try {
            salvarEmArquivo();
        } catch (IOException e) {
            pacientes.add(indice, removido);
            throw e;
        }

        return removido;
    }

    public List<Paciente> listarTodos() {
        return new ArrayList<>(pacientes);
    }

    public int getTotalPacientes() {
        return pacientes.size();
    }

    public String getCaminhoArquivoAbsoluto() {
        return caminhoArquivo.toAbsolutePath().toString();
    }

    public void salvarEmArquivo() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(caminhoArquivo, StandardCharsets.UTF_8)) {
            writer.write(CABECALHO_CSV);
            writer.newLine();

            for (Paciente p : pacientes) {
                String nomeSeguro = p.getNome().replace(SEPARADOR_CSV, ",");
                String linha = String.join(SEPARADOR_CSV,
                        nomeSeguro,
                        p.getDataNascimentoFormatada(),
                        p.getCpf(),
                        p.getTelefone());
                writer.write(linha);
                writer.newLine();
            }
        }
    }

    public void carregarDoArquivo() throws IOException, DadosInvalidosException {
        pacientes.clear();

        if (!Files.exists(caminhoArquivo)) {
            return;
        }

        StringBuilder errosDeLeitura = new StringBuilder();
        int numeroLinha = 0;

        try (BufferedReader reader = Files.newBufferedReader(caminhoArquivo, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                String linhaTrim = linha.trim();

                if (linhaTrim.isEmpty() || (numeroLinha == 1 && linhaTrim.equalsIgnoreCase(CABECALHO_CSV))) {
                    continue;
                }

                String[] campos = linhaTrim.split(SEPARADOR_CSV, -1);
                if (campos.length < 4) {
                    errosDeLeitura.append(String.format(
                            "- Linha %d: formato incompleto (esperados 4 campos, encontrados %d).%n",
                            numeroLinha, campos.length));
                    continue;
                }

                try {
                    Paciente paciente = new Paciente(
                            campos[0].trim(),
                            campos[1].trim(),
                            campos[2].trim(),
                            campos[3].trim()
                    );

                    boolean cpfDuplicado = false;
                    for (Paciente existente : pacientes) {
                        if (existente.getCpf().equals(paciente.getCpf())) {
                            cpfDuplicado = true;
                            break;
                        }
                    }

                    if (cpfDuplicado) {
                        errosDeLeitura.append(String.format(
                                "- Linha %d: CPF duplicado (%s) ignorado.%n",
                                numeroLinha, paciente.getCpf()));
                    } else {
                        pacientes.add(paciente);
                    }
                } catch (DadosInvalidosException | NumberFormatException ex) {
                    errosDeLeitura.append(String.format(
                            "- Linha %d ignorada (%s)%n", numeroLinha, ex.getMessage()));
                }
            }
        }

        if (errosDeLeitura.length() > 0) {
            throw new DadosInvalidosException(
                    "Alguns registros do arquivo continham dados inválidos e foram ignorados:\n\n"
                    + errosDeLeitura);
        }
    }
}
