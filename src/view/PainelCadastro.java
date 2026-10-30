package view;

import exception.DadosInvalidosException;
import model.Paciente;
import repository.PacienteRepository;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

public final class PainelCadastro extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient PacienteRepository repository;
    private final transient Runnable acaoIrParaListagem;
    private final transient Runnable acaoAoSalvarComSucesso;

    private JTextField txtNome;
    private JTextField txtDataNascimento;
    private JTextField txtCpf;
    private JTextField txtTelefone;

    public PainelCadastro(PacienteRepository repository,
                          Runnable acaoIrParaListagem,
                          Runnable acaoAoSalvarComSucesso) {
        this.repository = repository;
        this.acaoIrParaListagem = acaoIrParaListagem;
        this.acaoAoSalvarComSucesso = acaoAoSalvarComSucesso;

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setBackground(new Color(248, 250, 252));

        JPanel painelCabecalho = new JPanel(new BorderLayout(5, 5));
        painelCabecalho.setOpaque(false);

        JLabel lblTitulo = new JLabel("Cadastrar Novo Paciente");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(30, 41, 59));

        JLabel lblSubtitulo = new JLabel(
                "Preencha todos os campos abaixo. Os dados serão validados e salvos automaticamente no arquivo local.");
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(100, 116, 139));

        painelCabecalho.add(lblTitulo, BorderLayout.NORTH);
        painelCabecalho.add(lblSubtitulo, BorderLayout.SOUTH);
        add(painelCabecalho, BorderLayout.NORTH);

        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBackground(Color.WHITE);
        painelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 2, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Font fonteLabel = new Font("SansSerif", Font.BOLD, 13);
        Font fonteCampo = new Font("SansSerif", Font.PLAIN, 14);
        Font fonteDica = new Font("SansSerif", Font.ITALIC, 11);
        Color corDica = new Color(100, 116, 139);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel lblNome = new JLabel("Nome Completo: *");
        lblNome.setFont(fonteLabel);
        painelFormulario.add(lblNome, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtNome = new JTextField(30);
        txtNome.setFont(fonteCampo);
        painelFormulario.add(txtNome, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 8, 12, 8);
        JLabel dicaNome = new JLabel("Ex.: Maria Helena da Silva (mínimo 3 letras, sem números)");
        dicaNome.setFont(fonteDica);
        dicaNome.setForeground(corDica);
        painelFormulario.add(dicaNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(8, 8, 2, 8);
        JLabel lblData = new JLabel("Data de Nascimento: *");
        lblData.setFont(fonteLabel);
        painelFormulario.add(lblData, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtDataNascimento = new JTextField(15);
        txtDataNascimento.setFont(fonteCampo);
        painelFormulario.add(txtDataNascimento, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 8, 12, 8);
        JLabel dicaData = new JLabel("Formato obrigatório: dd/MM/aaaa (Ex.: 15/08/1992)");
        dicaData.setFont(fonteDica);
        dicaData.setForeground(corDica);
        painelFormulario.add(dicaData, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(8, 8, 2, 8);
        JLabel lblCpf = new JLabel("CPF: *");
        lblCpf.setFont(fonteLabel);
        painelFormulario.add(lblCpf, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtCpf = new JTextField(18);
        txtCpf.setFont(fonteCampo);
        painelFormulario.add(txtCpf, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 8, 12, 8);
        JLabel dicaCpf = new JLabel("11 dígitos numéricos (Ex.: 123.456.789-01 ou 12345678901)");
        dicaCpf.setFont(fonteDica);
        dicaCpf.setForeground(corDica);
        painelFormulario.add(dicaCpf, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(8, 8, 2, 8);
        JLabel lblTelefone = new JLabel("Telefone (com DDD): *");
        lblTelefone.setFont(fonteLabel);
        painelFormulario.add(lblTelefone, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtTelefone = new JTextField(18);
        txtTelefone.setFont(fonteCampo);
        painelFormulario.add(txtTelefone, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 8, 8, 8);
        JLabel dicaTelefone = new JLabel("10 ou 11 dígitos numéricos (Ex.: (11) 98765-4321 ou 11987654321)");
        dicaTelefone.setFont(fonteDica);
        dicaTelefone.setForeground(corDica);
        painelFormulario.add(dicaTelefone, gbc);

        add(painelFormulario, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        painelBotoes.setOpaque(false);

        JButton btnLimpar = new JButton("Limpar Campos");
        btnLimpar.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnLimpar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpar.addActionListener(e -> limparCampos());

        JButton btnIrLista = new JButton("Ver Lista de Pacientes");
        btnIrLista.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnIrLista.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIrLista.addActionListener(e -> {
            if (acaoIrParaListagem != null) {
                acaoIrParaListagem.run();
            }
        });

        JButton btnSalvar = new JButton("Salvar Paciente");
        btnSalvar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSalvar.setBackground(new Color(22, 163, 74));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.addActionListener(e -> salvarPaciente());

        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnIrLista);
        painelBotoes.add(btnSalvar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void salvarPaciente() {
        try {
            String nome = txtNome.getText();
            String dataNasc = txtDataNascimento.getText();
            String cpf = txtCpf.getText();
            String telefone = txtTelefone.getText();

            Paciente novoPaciente = new Paciente(nome, dataNasc, cpf, telefone);
            repository.adicionar(novoPaciente);

            if (acaoAoSalvarComSucesso != null) {
                acaoAoSalvarComSucesso.run();
            }

            limparCampos();

            int opcao = JOptionPane.showConfirmDialog(
                    this,
                    "Paciente cadastrado e salvo no arquivo com sucesso!\n\n"
                    + "Nome: " + novoPaciente.getNome() + "\n"
                    + "CPF: " + novoPaciente.getCpf() + "\n"
                    + "Data de Nascimento: " + novoPaciente.getDataNascimentoFormatada()
                    + " (" + novoPaciente.getIdade() + " anos)\n\n"
                    + "Deseja ir para a tela de listagem de pacientes agora?",
                    "Cadastro Realizado com Sucesso",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
            );

            if (opcao == JOptionPane.YES_OPTION && acaoIrParaListagem != null) {
                acaoIrParaListagem.run();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro de Formato Numérico:\n" + ex.getMessage(),
                    "Caracteres Não Numéricos",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (DadosInvalidosException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível salvar o paciente:\n\n" + ex.getMessage(),
                    "Dados Inválidos",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ocorreu um erro ao gravar os dados no arquivo local:\n" + ex.getMessage(),
                    "Erro de Persistência em Arquivo",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ocorreu um erro inesperado: " + ex.getMessage(),
                    "Erro no Sistema",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void limparCampos() {
        txtNome.setText("");
        txtDataNascimento.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtNome.requestFocusInWindow();
    }
}
