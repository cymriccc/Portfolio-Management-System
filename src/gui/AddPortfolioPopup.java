package gui;

import db.Database;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import main.Main;

public class AddPortfolioPopup extends JDialog {
    private int currentUserId;
    private File selectedFile;
    private JLabel lblFileName;
    private JTextField txtProjectName;
    private PortfolioPanel parentPanel;

    public AddPortfolioPopup(Frame owner, PortfolioPanel parent, int userId) {
        super(owner, "Add New Project", true);
        this.parentPanel = parent;
        this.currentUserId = userId;
        
        setSize(500, 450);
        setLocationRelativeTo(owner);
        setLayout(null);
        setUndecorated(true);
        getContentPane().setBackground(Color.WHITE);

        JLabel title = new JLabel("Upload New Project");
        title.setBounds(30, 20, 300, 30);
        title.setFont(new Font("Helvetica", Font.BOLD, 22));
        add(title);

        // --- Reuse your existing input fields ---
        JLabel lblName = new JLabel("Project Name:");
        lblName.setBounds(30, 70, 150, 20);
        add(lblName);

        txtProjectName = new JTextField();
        txtProjectName.setBounds(30, 100, 420, 35);
        add(txtProjectName);

        JButton btnChoose = new JButton("CHOOSE FILE");
        btnChoose.setBounds(30, 160, 150, 40);
        btnChoose.setBackground(new Color(0x2D3436));
        btnChoose.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnChoose.setFocusPainted(false);
        btnChoose.setBorderPainted(false);
        btnChoose.setForeground(Color.WHITE);
        btnChoose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChoose.addActionListener(e -> chooseFile());
        add(btnChoose);

        lblFileName = new JLabel("No file selected");
        lblFileName.setBounds(30, 210, 400, 20);
        add(lblFileName);

        JButton btnUpload = new JButton("UPLOAD NOW");
        btnUpload.setBounds(30, 320, 420, 45);
        btnUpload.setBackground(Main.ACCENT_COLOR);
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpload.setFont(new Font("Helvetica", Font.BOLD, 16));
        btnUpload.setFocusPainted(false);
        btnUpload.setBorderPainted(false);
        btnUpload.addActionListener(e -> upload());
        add(btnUpload);

        // --- CUSTOM CLOSE BUTTON (Top Right) ---
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
                btnClose.setForeground(new Color(0xD63031)); // Red hover
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnClose.setForeground(new Color(0x636E72));
            }
        });

        btnClose.addActionListener(e -> dispose());
        add(btnClose);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images & PDFs", "jpg", "png", "pdf"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            lblFileName.setText("Selected: " + selectedFile.getName());
        }
    }

    private void upload() {
        if (selectedFile == null || txtProjectName.getText().isEmpty()) {
            CustomDialog.show(this, "Please fill all fields!", false);
            return;
        }

        String sql = "INSERT INTO portfolios (user_id, project_name, file_data, file_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             FileInputStream fis = new FileInputStream(selectedFile)) {
            
            pst.setInt(1, 1); // Replace with real user ID
            pst.setString(2, txtProjectName.getText());
            pst.setBinaryStream(3, fis, (int) selectedFile.length());
            pst.setString(4, selectedFile.getName());
            
            pst.executeUpdate();
            CustomDialog.show(this, "Uploaded!", true);
            parentPanel.loadProjects(currentUserId); // Refresh the gallery automatically!
            dispose(); // Close the popup
        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show(this, "Failed!", false);
        }
    }
}