package unaerp.br.view;

import unaerp.br.controller.FinancialSummaryController;
import unaerp.br.model.dto.FinancialSummary;
import unaerp.br.model.entity.User;
// If using JDateChooser, uncomment:
// import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

public class SummaryPanel extends JPanel {
    private User currentUser;
    private JFrame ownerFrame;

    // If using JDateChooser:
    // private JDateChooser startDateChooser;
    // private JDateChooser endDateChooser;
    private JTextField startDateField;
    private JTextField endDateField;

    private JButton generateButton;
    private JLabel totalIncomeLabel;
    private JLabel totalExpenseLabel;
    private JLabel balanceLabel;

    private FinancialSummaryController summaryController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));


    public SummaryPanel(User user, JFrame owner) {
        this.currentUser = user;
        this.ownerFrame = owner;
        this.summaryController = new FinancialSummaryController();

        setLayout(new GridBagLayout());
        initComponents();
        layoutComponents();
        attachListeners();

        setDefaultDates();
    }

    private void initComponents() {
        // If using JDateChooser:
        // startDateChooser = new JDateChooser();
        // endDateChooser = new JDateChooser();
        // startDateChooser.setDateFormatString("yyyy-MM-dd");
        // endDateChooser.setDateFormatString("yyyy-MM-dd");
        startDateField = new JTextField(10);
        endDateField = new JTextField(10);
        startDateField.setToolTipText("AAAA-MM-DD");
        endDateField.setToolTipText("AAAA-MM-DD");


        generateButton = new JButton("Gerar Resumo");

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        totalIncomeLabel = new JLabel("Total de Receitas: R$ 0,00");
        totalIncomeLabel.setFont(labelFont);
        totalExpenseLabel = new JLabel("Total de Despesas: R$ 0,00");
        totalExpenseLabel.setFont(labelFont);
        balanceLabel = new JLabel("Saldo: R$ 0,00");
        balanceLabel.setFont(labelFont);
    }

    private void setDefaultDates() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        // If using JDateChooser:
        // startDateChooser.setDate(Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        // endDateChooser.setDate(Date.from(lastDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        startDateField.setText(firstDayOfMonth.format(dateFormatter));
        endDateField.setText(lastDayOfMonth.format(dateFormatter));
    }


    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; panelAdd(new JLabel("Data de Início:"), gbc);
        // If using JDateChooser:
        // gbc.gridx = 1; gbc.gridy = y; panelAdd(startDateChooser, gbc, GridBagConstraints.HORIZONTAL);
        gbc.gridx = 1; gbc.gridy = y; panelAdd(startDateField, gbc, GridBagConstraints.HORIZONTAL);

        gbc.gridx = 2; gbc.gridy = y; panelAdd(new JLabel("Data de Fim:"), gbc);
        // If using JDateChooser:
        // gbc.gridx = 3; gbc.gridy = y; panelAdd(endDateChooser, gbc, GridBagConstraints.HORIZONTAL);
        gbc.gridx = 3; gbc.gridy = y++; panelAdd(endDateField, gbc, GridBagConstraints.HORIZONTAL);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER; panelAdd(generateButton, gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        y++;

        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 4; panelAdd(totalIncomeLabel, gbc);
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 4; panelAdd(totalExpenseLabel, gbc);
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 4; panelAdd(balanceLabel, gbc);

        gbc.gridx = 0; gbc.gridy = y; gbc.weighty = 1.0; panelAdd(new JLabel(), gbc);


        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void panelAdd(Component comp, GridBagConstraints gbc, int fill) {
        gbc.fill = fill;
        add(comp, gbc);
    }
    private void panelAdd(Component comp, GridBagConstraints gbc) {
        panelAdd(comp, gbc, GridBagConstraints.NONE);
    }


    private void attachListeners() {
        generateButton.addActionListener(e -> generateSummary());
    }

    private void generateSummary() {
        LocalDate startDate;
        LocalDate endDate;

        try {
            // If using JDateChooser:
            // Date sDate = startDateChooser.getDate();
            // Date eDate = endDateChooser.getDate();
            // if (sDate == null || eDate == null) {
            // JOptionPane.showMessageDialog(ownerFrame, "Por favor, selecione as datas de início e fim.", "Erro de Data", JOptionPane.ERROR_MESSAGE);
            // return;
            // }
            // startDate = sDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            // endDate = eDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            startDate = LocalDate.parse(startDateField.getText(), dateFormatter);
            endDate = LocalDate.parse(endDateField.getText(), dateFormatter);

            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(ownerFrame, "A data de fim não pode ser anterior à data de início.", "Erro de Data", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(ownerFrame, "Formato de data inválido. Por favor, use AAAA-MM-DD.", "Erro de Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            FinancialSummary summary = summaryController.getFinancialSummary(currentUser, startDate, endDate);
            totalIncomeLabel.setText("Total de Receitas: " + currencyFormatter.format(summary.getTotalIncome()));
            totalExpenseLabel.setText("Total de Despesas: " + currencyFormatter.format(summary.getTotalExpense()));
            balanceLabel.setText("Saldo: " + currencyFormatter.format(summary.getBalance()));

            if (summary.getBalance().compareTo(java.math.BigDecimal.ZERO) < 0) {
                balanceLabel.setForeground(Color.RED);
            } else {
                balanceLabel.setForeground(new Color(0,128,0));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(ownerFrame, "Erro ao gerar resumo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            totalIncomeLabel.setText("Total de Receitas: Erro");
            totalExpenseLabel.setText("Total de Despesas: Erro");
            balanceLabel.setText("Saldo: Erro");
        }
    }

    public void closeResources() {
        if (summaryController != null) {
            summaryController.close();
            System.out.println("SummaryPanel fechado, recursos do FinancialSummaryController liberados.");
        }
    }
}