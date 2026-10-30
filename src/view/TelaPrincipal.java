package view;

import exception.DadosInvalidosException;
import repository.PacienteRepository;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;

public final class TelaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    public static final String TELA_CADASTRO = "CARD_CADASTRO";
    public static final String TELA_LISTAGEM = "CARD_LISTAGEM";

    private final transient PacienteRepository repository;
    private final CardLayout cardLayout;
    private final JPanel painelConteudo;

    private PainelCadastro painelCadastro;
    private PainelListagem painelListagem;

    private JButton btnMenuCadastro;
    private JButton btnMenuListagem;
    private JLabel lblBarraStatus;

    public TelaPrincipal() {
        this.repository = new PacienteRepository();
        this.cardLayout = new CardLayout();
        this.painelConteudo = new JPanel(cardLayout);

        configurarJanela();
        inicializarBarraNavegacao();
        inicializarTelas();
        inicializarBarraStatus();
        carregarPacientesAoIniciar();
    }

    private void configurarJanela() {
        setTitle("Sistema de Cadastro de Pacientes - POO (Coleções e Exceções)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 560);
        setMinimumSize(new Dimension(740, 500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarBarraNavegacao() {
        JPanel barraTopo = new JPanel(new BorderLayout());
        barraTopo.setBackground(new Color(15, 23, 42));
        barraTopo.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        JLabel lblMarca = new JLabel("Clínica Médica | Gestão de Pacientes");
        lblMarca.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblMarca.setForeground(Color.WHITE);

        JPanel painelBotoesMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoesMenu.setOpaque(false);

        btnMenuCadastro = new JButton("1. Cadastrar Paciente");
        btnMenuCadastro.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnMenuCadastro.setFocusPainted(false);
        btnMenuCadastro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMenuCadastro.addActionListener(e -> mostrarTelaCadastro());

        btnMenuListagem = new JButton("2. Listar Pacientes");
        btnMenuListagem.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnMenuListagem.setFocusPainted(false);
        btnMenuListagem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMenuListagem.addActionListener(e -> mostrarTelaListagem());

        painelBotoesMenu.add(btnMenuCadastro);
        painelBotoesMenu.add(btnMenuListagem);

        barraTopo.add(lblMarca, BorderLayout.WEST);
        barraTopo.add(painelBotoesMenu, BorderLayout.EAST);

        add(barraTopo, BorderLayout.NORTH);
    }

    private void inicializarTelas() {
        painelCadastro = new PainelCadastro(
                repository,
                this::mostrarTelaListagem,
                () -> {
                    painelListagem.atualizarTabela();
                    atualizarBarraStatus();
                }
        );

        painelListagem = new PainelListagem(
                repository,
                this::mostrarTelaCadastro,
                this::atualizarBarraStatus
        );

        painelConteudo.add(painelCadastro, TELA_CADASTRO);
        painelConteudo.add(painelListagem, TELA_LISTAGEM);

        add(painelConteudo, BorderLayout.CENTER);
    }

    private void inicializarBarraStatus() {
        JPanel painelStatus = new JPanel(new BorderLayout());
        painelStatus.setBackground(new Color(226, 232, 240));
        painelStatus.setBorder(BorderFactory.createEmptyBorder(6, 25, 6, 25));

        lblBarraStatus = new JLabel();
        lblBarraStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblBarraStatus.setForeground(new Color(51, 65, 85));

        painelStatus.add(lblBarraStatus, BorderLayout.WEST);
        add(painelStatus, BorderLayout.SOUTH);
    }

    private void carregarPacientesAoIniciar() {
        try {
            repository.carregarDoArquivo();
        } catch (DadosInvalidosException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Aviso ao Carregar Arquivo Inicial",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível ler o arquivo de pacientes salvo anteriormente:\n"
                    + ex.getMessage() + "\n\nO sistema iniciará com uma lista vazia.",
                    "Erro de Leitura na Inicialização",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro inesperado ao inicializar os dados:\n" + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            painelListagem.atualizarTabela();
            atualizarBarraStatus();
            mostrarTelaCadastro();
        }
    }

    public void mostrarTelaCadastro() {
        cardLayout.show(painelConteudo, TELA_CADASTRO);
        destacarBotaoAtivo(btnMenuCadastro, btnMenuListagem);
    }

    public void mostrarTelaListagem() {
        painelListagem.atualizarTabela();
        cardLayout.show(painelConteudo, TELA_LISTAGEM);
        destacarBotaoAtivo(btnMenuListagem, btnMenuCadastro);
    }

    private void destacarBotaoAtivo(JButton botaoAtivo, JButton botaoInativo) {
        botaoAtivo.setBackground(new Color(59, 130, 246));
        botaoAtivo.setForeground(Color.WHITE);

        botaoInativo.setBackground(new Color(226, 232, 240));
        botaoInativo.setForeground(new Color(30, 41, 59));

        btnMenuListagem.setText("2. Listar Pacientes (" + repository.getTotalPacientes() + ")");
    }

    private void atualizarBarraStatus() {
        int total = repository.getTotalPacientes();
        lblBarraStatus.setText(String.format(
                "Pacientes na coleção (ArrayList): %d  |  Arquivo local: %s",
                total, repository.getCaminhoArquivoAbsoluto()));
        if (btnMenuListagem != null) {
            btnMenuListagem.setText("2. Listar Pacientes (" + total + ")");
        }
    }
}
