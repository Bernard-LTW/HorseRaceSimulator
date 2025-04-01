package ui;

import models.Horse;
import models.HorseItem;
import utils.FileIO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class HorseCustomizerPanel extends JPanel {
    private JTable horseTable;
    private DefaultTableModel tableModel;
    private List<Horse> horses;
    private JButton createHorseBtn;
    private JButton customizeEquipmentBtn;
    private JButton backButton;

    public HorseCustomizerPanel() {
        setLayout(new BorderLayout(10, 20));
        setBackground(new Color(70, 130, 180));
        
        // Load horses
        horses = new ArrayList<>(List.of(FileIO.ingestHorses()));
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        headerPanel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("Horse Customizer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Create table model with all columns
        String[] columns = {"Name", "Symbol", "Breed", "Coat Color", "Confidence", "Equipment", "Accessories"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        horseTable = new JTable(tableModel);
        horseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        horseTable.setFont(new Font("Arial", Font.PLAIN, 14));
        horseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        horseTable.getTableHeader().setBackground(new Color(70, 130, 180));
        horseTable.getTableHeader().setForeground(Color.WHITE);
        horseTable.setRowHeight(30);
        horseTable.setShowGrid(true);
        horseTable.setGridColor(new Color(200, 200, 200));
        horseTable.setBackground(Color.WHITE);
        horseTable.setSelectionBackground(new Color(230, 240, 250));
        horseTable.setSelectionForeground(Color.BLACK);

        // Set column widths
        horseTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Name
        horseTable.getColumnModel().getColumn(1).setPreferredWidth(50);  // Symbol
        horseTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Breed
        horseTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Coat Color
        horseTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Confidence
        horseTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Equipment
        horseTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Accessories

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(horseTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Populate table
        updateHorseTable();

        return contentPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        createHorseBtn = createStyledButton("Create New Horse");
        // editHorseBtn = createStyledButton("Edit Selected Horse");
        customizeEquipmentBtn = createStyledButton("Customize Equipment");
        backButton = createStyledButton("Back");

        buttonPanel.add(createHorseBtn);
        // buttonPanel.add(editHorseBtn);
        buttonPanel.add(customizeEquipmentBtn);
        buttonPanel.add(backButton);

        // Add action listeners
        createHorseBtn.addActionListener(e -> showCreateHorseDialog());
        // editHorseBtn.addActionListener(e -> showEditHorseDialog());
        customizeEquipmentBtn.addActionListener(e -> showEquipmentCustomizer());
        backButton.addActionListener(e -> goBack());

        return buttonPanel;
    }

    private void showCreateHorseDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create New Horse", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(Color.WHITE);
        dialog.setSize(400, 300);

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JComboBox<String> breedSelector = new JComboBox<>(FileIO.loadBreeds().toArray(new String[0]));
        JComboBox<String> coatColorSelector = new JComboBox<>(FileIO.loadCoatColors().toArray(new String[0]));
        JTextField symbolField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Breed:"));
        inputPanel.add(breedSelector);
        inputPanel.add(new JLabel("Coat Color:"));
        inputPanel.add(coatColorSelector);
        inputPanel.add(new JLabel("Symbol:"));
        inputPanel.add(symbolField);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        JButton createBtn = createStyledButton("Create");
        JButton cancelBtn = createStyledButton("Cancel");

        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String breed = (String) breedSelector.getSelectedItem();
            String coatColor = (String) coatColorSelector.getSelectedItem();
            String symbol = symbolField.getText().trim();

            if (name.isEmpty() || symbol.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all fields",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Horse newHorse = new Horse(
                symbol.charAt(0),
                name,
                1.0, // default confidence
                breed,
                coatColor
            );

            horses.add(newHorse);
            FileIO.saveHorses(horses.toArray(new Horse[0]));
            updateHorseTable();
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void showEquipmentCustomizer() {
        int selectedRow = horseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a horse to customize",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Horse selectedHorse = horses.get(selectedRow);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                "Customize " + selectedHorse.getName() + "'s Equipment", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(Color.WHITE);
        dialog.setSize(600, 400);

        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create equipment panel
        JPanel equipmentPanel = new JPanel(new BorderLayout(5, 5));
        equipmentPanel.setBackground(Color.WHITE);
        equipmentPanel.setBorder(BorderFactory.createTitledBorder("Equipment"));

        DefaultListModel<String> equipmentModel = new DefaultListModel<>();
        JList<String> equipmentList = new JList<>(equipmentModel);
        equipmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        equipmentList.setFont(new Font("Arial", Font.PLAIN, 14));
        equipmentList.setBackground(Color.WHITE);
        equipmentList.setSelectionBackground(new Color(230, 240, 250));
        equipmentList.setSelectionForeground(Color.BLACK);

        // Populate equipment list
        for (HorseItem item : selectedHorse.getEquipment()) {
            equipmentModel.addElement(item.getName());
        }

        JScrollPane equipmentScroll = new JScrollPane(equipmentList);
        equipmentScroll.setBorder(BorderFactory.createEmptyBorder());
        equipmentScroll.getViewport().setBackground(Color.WHITE);

        // Create equipment buttons
        JPanel equipmentButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        equipmentButtons.setBackground(Color.WHITE);
        JButton addEquipmentBtn = createStyledButton("Add Equipment");
        JButton removeEquipmentBtn = createStyledButton("Remove Equipment");
        equipmentButtons.add(addEquipmentBtn);
        equipmentButtons.add(removeEquipmentBtn);

        // Add action listeners for equipment buttons
        addEquipmentBtn.addActionListener(e -> {
            List<HorseItem> availableEquipment = FileIO.loadEquipment();
            List<HorseItem> currentEquipment = selectedHorse.getEquipment();
            
            // Filter out already equipped items
            List<HorseItem> newEquipment = availableEquipment.stream()
                .filter(equip -> !currentEquipment.stream()
                    .anyMatch(current -> current.getName().equals(equip.getName())))
                .toList();

            if (newEquipment.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "No new equipment available to add",
                    "No Equipment Available",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog addDialog = new JDialog(dialog, "Add Equipment", true);
            addDialog.setLayout(new BorderLayout(10, 10));
            addDialog.setBackground(Color.WHITE);
            addDialog.setSize(400, 300);

            DefaultListModel<String> model = new DefaultListModel<>();
            for (HorseItem equip : newEquipment) {
                model.addElement(equip.getName() + " - " + equip.getDescription());
            }

            JList<String> availableEquipmentList = new JList<>(model);
            availableEquipmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            availableEquipmentList.setFont(new Font("Arial", Font.PLAIN, 14));
            availableEquipmentList.setBackground(Color.WHITE);
            availableEquipmentList.setSelectionBackground(new Color(230, 240, 250));
            availableEquipmentList.setSelectionForeground(Color.BLACK);

            JScrollPane scrollPane = new JScrollPane(availableEquipmentList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            JButton confirmBtn = createStyledButton("Add");
            JButton cancelBtn = createStyledButton("Cancel");

            confirmBtn.addActionListener(ev -> {
                int selectedIndex = availableEquipmentList.getSelectedIndex();
                if (selectedIndex != -1) {
                    HorseItem selectedEquipment = newEquipment.get(selectedIndex);
                    selectedHorse.addEquipment(selectedEquipment);
                    equipmentModel.addElement(selectedEquipment.getName());
                    FileIO.saveHorses(horses.toArray(new Horse[0]));
                    updateHorseTable();
                }
                addDialog.dispose();
            });

            cancelBtn.addActionListener(ev -> addDialog.dispose());

            buttonPanel.add(confirmBtn);
            buttonPanel.add(cancelBtn);

            addDialog.add(scrollPane, BorderLayout.CENTER);
            addDialog.add(buttonPanel, BorderLayout.SOUTH);

            addDialog.setLocationRelativeTo(dialog);
            addDialog.setVisible(true);
        });

        removeEquipmentBtn.addActionListener(e -> {
            int selectedIndex = equipmentList.getSelectedIndex();
            if (selectedIndex != -1) {
                String itemName = equipmentModel.getElementAt(selectedIndex);
                selectedHorse.removeEquipment(itemName);
                equipmentModel.remove(selectedIndex);
                FileIO.saveHorses(horses.toArray(new Horse[0]));
                updateHorseTable();
            }
        });

        equipmentPanel.add(equipmentScroll, BorderLayout.CENTER);
        equipmentPanel.add(equipmentButtons, BorderLayout.SOUTH);

        // Create accessories panel
        JPanel accessoriesPanel = new JPanel(new BorderLayout(5, 5));
        accessoriesPanel.setBackground(Color.WHITE);
        accessoriesPanel.setBorder(BorderFactory.createTitledBorder("Accessories"));

        DefaultListModel<String> accessoriesModel = new DefaultListModel<>();
        JList<String> accessoriesList = new JList<>(accessoriesModel);
        accessoriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accessoriesList.setFont(new Font("Arial", Font.PLAIN, 14));
        accessoriesList.setBackground(Color.WHITE);
        accessoriesList.setSelectionBackground(new Color(230, 240, 250));
        accessoriesList.setSelectionForeground(Color.BLACK);

        // Populate accessories list
        for (HorseItem item : selectedHorse.getAccessories()) {
            accessoriesModel.addElement(item.getName());
        }

        JScrollPane accessoriesScroll = new JScrollPane(accessoriesList);
        accessoriesScroll.setBorder(BorderFactory.createEmptyBorder());
        accessoriesScroll.getViewport().setBackground(Color.WHITE);

        // Create accessories buttons
        JPanel accessoriesButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        accessoriesButtons.setBackground(Color.WHITE);
        JButton addAccessoryBtn = createStyledButton("Add Accessory");
        JButton removeAccessoryBtn = createStyledButton("Remove Accessory");
        accessoriesButtons.add(addAccessoryBtn);
        accessoriesButtons.add(removeAccessoryBtn);

        // Add action listeners for accessory buttons
        addAccessoryBtn.addActionListener(e -> {
            List<HorseItem> availableAccessories = FileIO.loadAccessories();
            List<HorseItem> currentAccessories = selectedHorse.getAccessories();
            
            // Filter out already equipped accessories
            List<HorseItem> newAccessories = availableAccessories.stream()
                .filter(acc -> !currentAccessories.stream()
                    .anyMatch(current -> current.getName().equals(acc.getName())))
                .toList();

            if (newAccessories.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "No new accessories available to add",
                    "No Accessories Available",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JDialog addDialog = new JDialog(dialog, "Add Accessory", true);
            addDialog.setLayout(new BorderLayout(10, 10));
            addDialog.setBackground(Color.WHITE);
            addDialog.setSize(400, 300);

            DefaultListModel<String> model = new DefaultListModel<>();
            for (HorseItem acc : newAccessories) {
                model.addElement(acc.getName() + " - " + acc.getDescription());
            }

            JList<String> accessoryList = new JList<>(model);
            accessoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            accessoryList.setFont(new Font("Arial", Font.PLAIN, 14));
            accessoryList.setBackground(Color.WHITE);
            accessoryList.setSelectionBackground(new Color(230, 240, 250));
            accessoryList.setSelectionForeground(Color.BLACK);

            JScrollPane scrollPane = new JScrollPane(accessoryList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(Color.WHITE);
            JButton confirmBtn = createStyledButton("Add");
            JButton cancelBtn = createStyledButton("Cancel");

            confirmBtn.addActionListener(ev -> {
                int selectedIndex = accessoryList.getSelectedIndex();
                if (selectedIndex != -1) {
                    HorseItem selectedAccessory = newAccessories.get(selectedIndex);
                    selectedHorse.addAccessory(selectedAccessory);
                    accessoriesModel.addElement(selectedAccessory.getName());
                    FileIO.saveHorses(horses.toArray(new Horse[0]));
                }
                addDialog.dispose();
            });

            cancelBtn.addActionListener(ev -> addDialog.dispose());

            buttonPanel.add(confirmBtn);
            buttonPanel.add(cancelBtn);

            addDialog.add(scrollPane, BorderLayout.CENTER);
            addDialog.add(buttonPanel, BorderLayout.SOUTH);

            addDialog.setLocationRelativeTo(dialog);
            addDialog.setVisible(true);
        });

        removeAccessoryBtn.addActionListener(e -> {
            int selectedIndex = accessoriesList.getSelectedIndex();
            if (selectedIndex != -1) {
                String accessoryName = accessoriesModel.getElementAt(selectedIndex);
                selectedHorse.removeAccessory(accessoryName);
                accessoriesModel.remove(selectedIndex);
                FileIO.saveHorses(horses.toArray(new Horse[0]));
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Please select an accessory to remove",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        accessoriesPanel.add(accessoriesScroll, BorderLayout.CENTER);
        accessoriesPanel.add(accessoriesButtons, BorderLayout.SOUTH);

        // Add panels to content
        contentPanel.add(equipmentPanel, BorderLayout.NORTH);
        contentPanel.add(accessoriesPanel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        JButton closeBtn = createStyledButton("Close");
        buttonPanel.add(closeBtn);

        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateHorseTable() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add each horse to the table
        for (Horse horse : horses) {
            // Format equipment and accessories as comma-separated lists
            String equipmentList = String.join(", ", horse.getEquipment().stream()
                .map(HorseItem::getName)
                .toArray(String[]::new));
            String accessoriesList = String.join(", ", horse.getAccessories().stream()
                .map(HorseItem::getName)
                .toArray(String[]::new));

            // Add row to table
            tableModel.addRow(new Object[]{
                horse.getName(),
                horse.getSymbol(),
                horse.getBreed(),
                horse.getCoatColor(),
                String.format("%.2f", horse.getConfidence()),
                equipmentList,
                accessoriesList
            });
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    private void goBack() {
        CardLayout cardLayout = (CardLayout) getParent().getLayout();
        cardLayout.show(getParent(), "MAIN");
    }
} 