package gui;

import db.Database;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import main.Main;

public class AddPortfolioPopup extends JDialog {
    private int currentUserId;
    private File selectedFile;
    private JLabel lblFileName;
    private JTextField txtProjectName;
    private PortfolioPanel parentPanel;

    private JTextArea txtDescription;
    private DefaultListModel<String> selectedFilesModel = new DefaultListModel<>();
    private JPanel tagContainer;
    private JTextField tagInput;
    private java.util.List<String> currentTags = new java.util.ArrayList<>();
    private java.util.List<File> selectedFiles = new java.util.ArrayList<>();

    public AddPortfolioPopup(Frame owner, PortfolioPanel parent, int userId) {
        super(owner, "Add New Project", true);
        this.parentPanel = parent;
        this.currentUserId = userId;
        
        setSize(500, 620);
        setLocationRelativeTo(owner);
        setUndecorated(true);
        
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(null);
        rootPanel.setBackground(Color.WHITE);
        rootPanel.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 2)); 
        this.setContentPane(rootPanel);

        // TITLE
        JLabel title = new JLabel("Upload New Project");
        title.setBounds(30, 20, 300, 30);
        title.setFont(new Font("Helvetica", Font.BOLD, 22));
        add(title);

        // INPUT FIELDS
        JLabel lblName = new JLabel("Project Name:");
        lblName.setBounds(30, 70, 150, 20);
        add(lblName);

        txtProjectName = new JTextField();
        txtProjectName.setBounds(30, 95, 420, 35);
        add(txtProjectName);

        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setBounds(30, 140, 150, 20);
        add(lblDesc);

        // Custom JTextArea with scroll for description input
        txtDescription = new JTextArea();
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0)));
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setBounds(30, 165, 420, 80);
        add(descScroll);

        // TAGS INPUT
        JLabel lblTags = new JLabel("Tags (e.g., 3D Art, Illustration):");
        lblTags.setBounds(30, 255, 300, 20);
        add(lblTags);

        // Custom input field with add button for tags
        tagInput = new JTextField();
        tagInput.setBounds(30, 280, 320, 35);
        add(tagInput);

        JButton btnAddTag = new JButton("ADD");
        btnAddTag.setBounds(360, 280, 90, 35);
        btnAddTag.setBackground(Main.ACCENT_COLOR);
        btnAddTag.setForeground(Color.WHITE);
        btnAddTag.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnAddTag.setFocusPainted(false);
        btnAddTag.setBorderPainted(false);
        btnAddTag.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddTag.addActionListener(e -> addTag(tagInput.getText().trim()));
        add(btnAddTag);

        // Container for displaying added tags with remove option
        tagContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        tagContainer.setBackground(Color.WHITE);
        JScrollPane tagScroll = new JScrollPane(tagContainer);
        tagScroll.setBounds(30, 325, 420, 60);
        tagScroll.setBorder(null);
        add(tagScroll);

        // FILE CHOOSER
        JButton btnChoose = new JButton("CHOOSE FILE");
        btnChoose.setBounds(30, 405, 150, 40);
        btnChoose.setBackground(new Color(0x2D3436));
        btnChoose.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnChoose.setFocusPainted(false);
        btnChoose.setBorderPainted(false);
        btnChoose.setForeground(Color.WHITE);
        btnChoose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChoose.addActionListener(e -> chooseFile());
        add(btnChoose);

        // Label to show selected file name(s)
        lblFileName = new JLabel("No file selected");
        lblFileName.setBounds(30, 450, 420, 20);
        add(lblFileName);

        // UPLOAD BUTTON
        JButton btnUpload = new JButton("UPLOAD NOW");
        btnUpload.setBounds(30, 530, 420, 45);
        btnUpload.setBackground(Main.ACCENT_COLOR);
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpload.setFont(new Font("Helvetica", Font.BOLD, 16));
        btnUpload.setFocusPainted(false);
        btnUpload.setBorderPainted(false);
        btnUpload.addActionListener(e -> upload());
        add(btnUpload);
        
        // CUSTOM CLOSE BUTTON
        JButton btnClose = new JButton("X");
        btnClose.setBounds(460, 5, 40, 40);
        btnClose.setFont(new Font("Arial", Font.PLAIN, 24));
        btnClose.setForeground(new Color(0x636E72));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setMargin(new Insets(0, 0, 0, 0));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnClose.setForeground(new Color(0xD63031)); 
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnClose.setForeground(new Color(0x636E72));
            }
        });

        btnClose.addActionListener(e -> dispose());
        add(btnClose);
    }

    // UI Handler - Opens a file chooser dialog allowing multiple image selection and updates the label with the count of selected files
    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true); // Allow selecting multiple files
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
    
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            for (File f : files) {
                selectedFiles.add(f);
            }
            lblFileName.setText(selectedFiles.size() + " files selected");
        }
    }

    // UI Handler - Adds a tag to the current list and displays it as a button with an option to remove it from the selection
    private void addTag(String tag) {
        if (!tag.isEmpty() && !currentTags.contains(tag)) {
            currentTags.add(tag);
            JButton tagBtn = new JButton(tag + "  ✕");
            tagBtn.setFont(new Font("Helvetica", Font.PLAIN, 11));
            tagBtn.setBackground(new Color(0xF1F2F6));
            tagBtn.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR));
            tagBtn.addActionListener(e -> {
                currentTags.remove(tag);
                tagContainer.remove(tagBtn);
                tagContainer.revalidate();
                tagContainer.repaint();
            });
            tagContainer.add(tagBtn);
            tagInput.setText("");
            tagContainer.revalidate();
        }
    }

    // Data handler - Validates input and uploads project info along with associated files to the database, then refreshes the portfolio panel to show the new project
    private void upload() {
    if (selectedFiles.isEmpty() || txtProjectName.getText().isEmpty()) {
        CustomDialog.show(this, "Please select files and enter a name!", false);
        return;
    }

    try (Connection conn = Database.getConnection()) {
        conn.setAutoCommit(false);

        // Insert Project Info
        String projectSql = "INSERT INTO projects (user_id, project_name, description, tags) VALUES (?, ?, ?, ?)";
            try (PreparedStatement projectPst = conn.prepareStatement(projectSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                projectPst.setInt(1, currentUserId);
                projectPst.setString(2, txtProjectName.getText());
                projectPst.setString(3, txtDescription.getText());
                projectPst.setString(4, String.join(",", currentTags));
                projectPst.executeUpdate();

                try (ResultSet rs = projectPst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int projectId = rs.getInt(1);

                        String fileSql = "INSERT INTO portfolios (user_id, project_id, project_name, file_data, file_name) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement filePst = conn.prepareStatement(fileSql)) {
                            for (File file : selectedFiles) {
                                filePst.setInt(1, currentUserId);
                                filePst.setInt(2, projectId);
                                filePst.setString(3, txtProjectName.getText());
                                try (FileInputStream fis = new FileInputStream(file)) {
                                    filePst.setBinaryStream(4, fis, (int) file.length());
                                    filePst.setString(5, file.getName());
                                    filePst.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
            conn.commit();
            CustomDialog.show(this, "Project Uploaded!", true);
            if(parentPanel != null) parentPanel.loadProjects(currentUserId);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show(this, "Upload failed!", false);
        }
    }
}