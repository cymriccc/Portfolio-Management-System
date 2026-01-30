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

public class PortfolioPanel extends JPanel {
    private File selectedFile;
    private JLabel lblFileName;
    private JTextField txtProjectName;
    private JButton btnChoose, btnUpload;

    public PortfolioPanel() {
        // MATCHING DASHBOARD COLOR
        setLayout(null);
        setBackground(Main.BG_COLOR); 

        JLabel title = new JLabel("My Portfolio Projects");
        title.setBounds(50, 30, 400, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 28));
        title.setForeground(new Color(0x2D3436));
        add(title);

        JLabel lblName = new JLabel("Project Name:");
        lblName.setBounds(50, 100, 150, 20);
        lblName.setFont(new Font("Helvetica", Font.BOLD, 14));
        add(lblName);

        txtProjectName = new JTextField();
        txtProjectName.setBounds(50, 130, 400, 35);
       txtProjectName.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0)));
        add(txtProjectName);

        // 2. File Selection Area
        btnChoose = new JButton("CHOOSE FILE");
        btnChoose.setBounds(50, 190, 150, 40);
        btnChoose.setBackground(new Color(0x2D3436)); // Slate
        btnChoose.setForeground(Color.WHITE);
        btnChoose.setFocusPainted(false);
        btnChoose.addActionListener(e -> chooseFile());
        add(btnChoose);

        // This is the label that had the red line error
        lblFileName = new JLabel("No file selected");
        lblFileName.setBounds(220, 200, 300, 20);
        lblFileName.setForeground(new Color(0x636E72));
        add(lblFileName);

        // 3. Upload Button (Accent Color)
        btnUpload = new JButton("UPLOAD TO PORTFOLIO");
        btnUpload.setBounds(50, 260, 400, 45);
        btnUpload.setBackground(Main.ACCENT_COLOR); // Your Indigo 0x575FCF
        btnUpload.setForeground(Color.WHITE);
        btnUpload.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnUpload.addActionListener(e -> {
            // Add logic to get current user ID here
            uploadToDatabase(1, txtProjectName.getText()); 
        });
        add(btnUpload);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
    
        // Filter for images/PDFs
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Images & PDFs", "jpg", "png", "pdf");
       fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
           selectedFile = fileChooser.getSelectedFile();
            lblFileName.setText("Selected: " + selectedFile.getName());
        }
    }

    private void uploadToDatabase(int currentUserId, String projectName) {
        String sql = "INSERT INTO portfolios (user_id, project_name, file_data, file_name) VALUES (?, ?, ?, ?)";
    
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             FileInputStream fis = new FileInputStream(selectedFile)) {
        
            pst.setInt(1, currentUserId);
            pst.setString(2, projectName);
            pst.setBinaryStream(3, fis, (int) selectedFile.length()); // The binary data
            pst.setString(4, selectedFile.getName());
                
            pst.executeUpdate();
            CustomDialog.show(this, "Project Uploaded Successfully!", true);
                
       } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show(this, "Upload Failed!", false);
        }
    }
}