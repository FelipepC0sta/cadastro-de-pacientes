package model;

import exception.DadosInvalidosException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;

public final class Paciente implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter
            .ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String telefone;

    public Paciente(String nome, String dataNascimentoStr, String cpf, String telefone)
            throws DadosInvalidosException, NumberFormatException {
        setNome(nome);
        setDataNascimentoPorString(dataNascimentoStr);
        setCpf(cpf);
        setTelefone(telefone);
    }

    public Paciente(String nome, LocalDate dataNascimento, String cpf, String telefone)
            throws DadosInvalidosException, NumberFormatException {
        setNome(nome);
        setDataNascimento(dataNascimento);
        setCpf(cpf);
        setTelefone(telefone);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws DadosInvalidosException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new DadosInvalidosException("O campo 'Nome' é obrigatório e não pode estar vazio.");
        }

        String nomeLimpo = nome.trim().replaceAll("\\s+", " ");

        if (nomeLimpo.length() < 3) {
            throw new DadosInvalidosException("O nome do paciente deve possuir pelo menos 3 caracteres.");
        }

        if (nomeLimpo.length() > 100) {
            throw new DadosInvalidosException("O nome informado é longo demais (máximo de 100 caracteres).");
        }

        if (!nomeLimpo.matches("^[\\p{L} '\\-]+$")) {
            throw new DadosInvalidosException(
                    "O campo 'Nome' contém caracteres inválidos (números ou símbolos não são permitidos).");
        }

        this.nome = nomeLimpo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getDataNascimentoFormatada() {
        return dataNascimento != null ? dataNascimento.format(FORMATADOR_DATA) : "";
    }

    public int getIdade() {
        if (dataNascimento == null) {
            return 0;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public void setDataNascimentoPorString(String dataStr) throws DadosInvalidosException {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            throw new DadosInvalidosException("O campo 'Data de Nascimento' é obrigatório.");
        }

        try {
            LocalDate dataConvertida = LocalDate.parse(dataStr.trim(), FORMATADOR_DATA);
            setDataNascimento(dataConvertida);
        } catch (DateTimeParseException e) {
            throw new DadosInvalidosException(
                    "Data de nascimento inválida ('" + dataStr.trim() + "'). "
                    + "Use o formato dd/MM/aaaa (ex.: 25/08/1995) e informe uma data existente no calendário.",
                    e);
        }
    }

    public void setDataNascimento(LocalDate dataNascimento) throws DadosInvalidosException {
        if (dataNascimento == null) {
            throw new DadosInvalidosException("A data de nascimento não pode ser nula.");
        }

        LocalDate hoje = LocalDate.now();

        if (dataNascimento.isAfter(hoje)) {
            throw new DadosInvalidosException(
                    "Data de nascimento inválida: a data informada ("
                    + dataNascimento.format(FORMATADOR_DATA) + ") está no futuro.");
        }

        LocalDate limitePassado = hoje.minusYears(130);
        if (dataNascimento.isBefore(limitePassado)) {
            throw new DadosInvalidosException(
                    "Data de nascimento absurda: a data informada indica mais de 130 anos de idade.");
        }

        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) throws DadosInvalidosException, NumberFormatException {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new DadosInvalidosException("O campo 'CPF' é obrigatório e não pode estar vazio.");
        }

        String cpfSemPontuacao = cpf.trim().replace(".", "").replace("-", "").replace(" ", "");

        try {
            Long.parseLong(cpfSemPontuacao);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(
                    "O CPF informado ('" + cpf + "') contém caracteres não numéricos. Digite apenas números.");
        }

        if (cpfSemPontuacao.length() != 11) {
            throw new DadosInvalidosException(
                    "CPF em formato inválido: o CPF deve conter exatamente 11 dígitos numéricos (foram informados "
                    + cpfSemPontuacao.length() + " dígitos).");
        }

        if (cpfSemPontuacao.chars().distinct().count() == 1) {
            throw new DadosInvalidosException(
                    "CPF inválido: não são permitidos CPFs com todos os dígitos repetidos (" + cpf + ").");
        }

        this.cpf = String.format("%s.%s.%s-%s",
                cpfSemPontuacao.substring(0, 3),
                cpfSemPontuacao.substring(3, 6),
                cpfSemPontuacao.substring(6, 9),
                cpfSemPontuacao.substring(9, 11));
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) throws DadosInvalidosException, NumberFormatException {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new DadosInvalidosException("O campo 'Telefone' é obrigatório e não pode estar vazio.");
        }

        String telSemMascara = telefone.trim()
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "");

        try {
            Long.parseLong(telSemMascara);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(
                    "O telefone informado ('" + telefone + "') contém letras ou símbolos inválidos. "
                    + "Digite apenas números (com DDD).");
        }

        if (telSemMascara.length() < 10 || telSemMascara.length() > 11) {
            throw new DadosInvalidosException(
                    "Telefone inválido: informe o DDD seguido do número, totalizando 10 dígitos (fixo) "
                    + "ou 11 dígitos (celular). Exemplo: (11) 98765-4321.");
        }

        int ddd = Integer.parseInt(telSemMascara.substring(0, 2));
        if (ddd < 11 || ddd > 99) {
            throw new DadosInvalidosException(
                    "DDD de telefone inválido (" + ddd + "). Informe um DDD válido entre 11 e 99.");
        }

        if (telSemMascara.chars().distinct().count() == 1) {
            throw new DadosInvalidosException(
                    "Telefone inválido: número com todos os dígitos repetidos não é permitido.");
        }

        if (telSemMascara.length() == 11) {
            this.telefone = String.format("(%s) %s-%s",
                    telSemMascara.substring(0, 2),
                    telSemMascara.substring(2, 7),
                    telSemMascara.substring(7, 11));
        } else {
            this.telefone = String.format("(%s) %s-%s",
                    telSemMascara.substring(0, 2),
                    telSemMascara.substring(2, 6),
                    telSemMascara.substring(6, 10));
        }
    }

    @Override
    public String toString() {
        return String.format("Paciente { nome='%s', dataNascimento='%s' (%d anos), cpf='%s', telefone='%s' }",
                nome, getDataNascimentoFormatada(), getIdade(), cpf, telefone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paciente paciente = (Paciente) o;
        return Objects.equals(cpf, paciente.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}
