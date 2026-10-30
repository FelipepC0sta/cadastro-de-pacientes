package view;

import exception.DadosInvalidosException;
import model.Paciente;
import repository.PacienteRepository;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.List;

public final class PainelListagem extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient PacienteRepository repository;
    private final transient Runnable acaoIrParaCadastro;
    private final transient Runnable acaoAtualizarStatus;

    private JTable tabelaPacientes;
    private DefaultTableModel modeloTabela;
    private JLabel lblResumoLista;

    public PainelListagem(PacienteRepository repository,
                          Runnable acaoIrParaCadastro,
                          Runnable acaoAtualizarStatus) {
        this.repository = repository;
        this.acaoIrParaCadastro = acaoIrParaCadastro;
        this.acaoAtualizarStatus = acaoAtualizarStatus;

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        setBackground(new Color(248, 250, 252));

        JPanel painelCabecalho = new JPanel(new BorderLayout(5, 5));
        painelCabecalho.setOpaque(false);

        JLabel lblTitulo = new JLabel("Lista de Pacientes Cadastrados");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(30, 41, 59));

        lblResumoLista = new JLabel("Pacientes carregados da coleção em memória (ArrayList) e do arquivo local.");
        lblResumoLista.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblResumoLista.setForeground(new Color(100, 116, 139));

        painelCabecalho.add(lblTitulo, BorderLayout.NORTH);
        painelCabecalho.add(lblResumoLista, BorderLayout.SOUTH);
        add(painelCabecalho, BorderLayout.NORTH);

        String[] colunas = {"#", "Nome do Paciente", "Data de Nascimento", "Idade", "CPF", "Telefone"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaPacientes = new JTable(modeloTabela);
        tabelaPacientes.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabelaPacientes.setRowHeight(28);
        tabelaPacientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPacientes.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabelaPacientes.getTableHeader().setBackground(new Color(226, 232, 240));
        tabelaPacientes.getTableHeader().setForeground(new Color(30, 41, 59));
        tabelaPacientes.setGridColor(new Color(226, 232, 240));
        tabelaPacientes.setSelectionBackground(new Color(219, 234, 254));
        tabelaPacientes.setSelectionForeground(new Color(30, 58, 138));

        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        centralizado.setHorizontalAlignment(JLabel.CENTER);

        tabelaPacientes.getColumnModel().getColumn(0).setMaxWidth(45);
        tabelaPacientes.getColumnModel().getColumn(0).setCellRenderer(centralizado);
        tabelaPacientes.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabelaPacientes.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabelaPacientes.getColumnModel().getColumn(2).setCellRenderer(centralizado);
        tabelaPacientes.getColumnModel().getColumn(3).setMaxWidth(75);
        tabelaPacientes.getColumnModel().getColumn(3).setCellRenderer(centralizado);
        tabelaPacientes.getColumnModel().getColumn(4).setPreferredWidth(130);
        tabelaPacientes.getColumnModel().getColumn(4).setCellRenderer(centralizado);
        tabelaPacientes.getColumnModel().getColumn(5).setPreferredWidth(130);
        tabelaPacientes.getColumnModel().getColumn(5).setCellRenderer(centralizado);

        JScrollPane scrollPane = new JScrollPane(tabelaPacientes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1));
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        painelBotoes.setOpaque(false);

        JButton btnVoltarCadastro = new JButton("Novo Paciente (Cadastrar)");
        btnVoltarCadastro.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnVoltarCadastro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarCadastro.addActionListener(e -> {
            if (acaoIrParaCadastro != null) {
                acaoIrParaCadastro.run();
            }
        });

        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAtualizar.setBackground(new Color(37, 99, 235));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtualizar.addActionListener(e -> recarregarDoArquivoComMensagem());

        JButton btnRemover = new JButton("Remover Selecionado");
        btnRemover.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnRemover.setBackground(new Color(220, 38, 38));
        btnRemover.setForeground(Color.WHITE);
        btnRemover.setFocusPainted(false);
        btnRemover.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRemover.addActionListener(e -> removerPacienteSelecionado());

        painelBotoes.add(btnVoltarCadastro);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnRemover);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    public void atualizarTabela() {
        modeloTabela.setRowCount(0);
        List<Paciente> lista = repository.listarTodos();

        for (int i = 0; i < lista.size(); i++) {
            Paciente p = lista.get(i);
            Object[] linha = {
                    i + 1,
                    p.getNome(),
                    p.getDataNascimentoFormatada(),
                    p.getIdade() + " anos",
                    p.getCpf(),
                    p.getTelefone()
            };
            modeloTabela.addRow(linha);
        }

        lblResumoLista.setText(String.format(
                "Total de pacientes na coleção: %d registro(s) | Selecione uma linha para remover.",
                lista.size()));

        if (acaoAtualizarStatus != null) {
            acaoAtualizarStatus.run();
        }
    }

    private void recarregarDoArquivoComMensagem() {
        try {
            repository.carregarDoArquivo();
            atualizarTabela();
            JOptionPane.showMessageDialog(
                    this,
                    "Lista de pacientes sincronizada com sucesso!\nTotal carregado: "
                    + repository.getTotalPacientes() + " paciente(s).",
                    "Lista Atualizada",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (DadosInvalidosException ex) {
            atualizarTabela();
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Aviso na Leitura do Arquivo",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao ler o arquivo local de pacientes:\n" + ex.getMessage(),
                    "Erro de Leitura (IOException)",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro inesperado ao atualizar a lista:\n" + ex.getMessage(),
                    "Erro no Sistema",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void removerPacienteSelecionado() {
        try {
            int linhaSelecionada = tabelaPacientes.getSelectedRow();
            if (linhaSelecionada < 0) {
                throw new DadosInvalidosException(
                        "Nenhum paciente foi selecionado.\n"
                        + "Clique sobre um paciente na tabela antes de clicar em 'Remover Selecionado'.");
            }

            String nomePaciente = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
            String cpfPaciente = (String) modeloTabela.getValueAt(linhaSelecionada, 4);

            int confirmacao = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza que deseja remover o paciente abaixo?\n\n"
                    + "Nome: " + nomePaciente + "\n"
                    + "CPF: " + cpfPaciente + "\n\n"
                    + "Esta alteração também será gravada imediatamente no arquivo local.",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirmacao == JOptionPane.YES_OPTION) {
                Paciente removido = repository.removerPorIndice(linhaSelecionada);
                atualizarTabela();

                JOptionPane.showMessageDialog(
                        this,
                        "Paciente '" + removido.getNome() + "' removido com sucesso!",
                        "Paciente Removido",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (DadosInvalidosException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao atualizar o arquivo após a remoção:\n" + ex.getMessage(),
                    "Erro de Persistência (IOException)",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ocorreu um erro inesperado ao remover o paciente:\n" + ex.getMessage(),
                    "Erro no Sistema",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
