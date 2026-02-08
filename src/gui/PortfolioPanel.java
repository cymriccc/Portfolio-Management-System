package gui;

import db.Database;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import main.Main;

public class PortfolioPanel extends JPanel {
    private JPanel galleryContainer;
    private int currentUserId;

    public PortfolioPanel(int userId) {
        this.currentUserId = userId;

        // MATCHING DASHBOARD COLOR
        setLayout(null);
        setBackground(Main.BG_COLOR); 

        JLabel title = new JLabel("My Portfolio Projects");
        title.setBounds(50, 30, 400, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 28));
        title.setForeground(new Color(0x2D3436));
        add(title);

        // --- Add Button ---
        JButton btnOpenPopup = new JButton("+ ADD PROJECT");
        btnOpenPopup.setBounds(700, 30, 180, 40);
        btnOpenPopup.setBackground(Main.ACCENT_COLOR); // 0x575FCF
        btnOpenPopup.setForeground(Color.WHITE);
        btnOpenPopup.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnOpenPopup.setFocusPainted(false);
        btnOpenPopup.setBorderPainted(false);
        btnOpenPopup.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnOpenPopup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnOpenPopup.setBackground(Main.ACCENT_COLOR.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnOpenPopup.setBackground(Main.ACCENT_COLOR);
            }
        });

        btnOpenPopup.addActionListener(e -> showAddPortfolioPopup());
        add(btnOpenPopup);

        // --- Gallery Scroll Area ---
        galleryContainer = new JPanel();
        galleryContainer.setLayout(new GridLayout(0,3,25,25));
        galleryContainer.setBackground(Main.BG_COLOR);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setBackground(Main.BG_COLOR);
        wrapper.add(galleryContainer);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        // Since your main panel uses null layout, you MUST use setBounds
        scrollPane.setBounds(50, 100, 850, 500); 
        scrollPane.setBorder(null); 
        scrollPane.setBackground(Main.BG_COLOR);
        scrollPane.getViewport().setBackground(Main.BG_COLOR); // Ensures "Ice" theme consistency

        // FORCE scrollbar behavior so it only goes vertical
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        loadProjects(currentUserId); // Initial load from MySQL
    }

    private void showAddPortfolioPopup() {
        // We pass 'this' so the popup can call loadProjects() when done
        AddPortfolioPopup popup = new AddPortfolioPopup(
            (Frame) SwingUtilities.getWindowAncestor(this),
            this,
            this.currentUserId
        );
        popup.setVisible(true);
    }

    public void loadProjects(int userId) {
        this.currentUserId = userId;
        galleryContainer.removeAll();
        // Fetching ID, Name, and the actual Image data
        System.out.println("DEBUG: Fetching projects for User ID: " + currentUserId);
        String sql = "SELECT id, project_name, description, upload_date, file_data, file_name, image_path FROM portfolios WHERE user_id = ?";

        try (Connection conn = Database.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, currentUserId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("project_name");
                String fileName = rs.getString("file_name");
                byte[] imgBytes = rs.getBytes("file_data");
                String imagePath = rs.getString("image_path");
            
                // 1. PRIORITY: If there is actual uploaded data (BLOB), use it
                if (imgBytes != null && imgBytes.length > 0) {
                    System.out.println("DEBUG: Real upload found for: " + name);
                    boolean isPdf = (fileName != null && fileName.toLowerCase().endsWith(".pdf"));
                    galleryContainer.add(createProjectCard(id, name, imgBytes, isPdf)); 
                } 
                // 2. FALLBACK: Use the placeholder path if no BLOB exists
                else if (imagePath != null && !imagePath.isEmpty()) {
                    System.out.println("DEBUG: Using placeholder path for: " + name);
                    // We pass the path to a modified card creator or handle conversion
                    galleryContainer.add(createProjectCardFromPath(id, name, imagePath));
                } 
                // 3. EMERGENCY: Default if everything is missing
                else {
                    System.out.println("DEBUG: No data or path for: " + name);
                    galleryContainer.add(createProjectCardFromPath(id, name, "src/assets/image_preview.png"));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ SQL ERROR IN PortfolioPanel:");
            e.printStackTrace();
        }
        galleryContainer.revalidate();
        galleryContainer.repaint();
    }

    private JPanel createProjectCardFromPath(int id, String name, String path) {
        try {
            java.net.URL imgURL = getClass().getResource("/assets/default_preview.png");
        
            if (imgURL != null) {
                System.out.println("✅ SUCCESS: Found image via ClassLoader!");
            
                // Read raw bytes directly from the URL to avoid scaling errors
                java.io.InputStream is = imgURL.openStream();
                byte[] data = is.readAllBytes();
                is.close();
            
                return createProjectCard(id, name, data, false);
            } else {
                System.out.println("❌ STILL NOT FOUND: Check the filename one more time.");
                return createProjectCard(id, name, null, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return createProjectCard(id, name, null, false);
        }
    }

    // Quick helper to keep your original byte[] method working
    private byte[] toByteArray(Image img) {
        java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(
            img.getWidth(null), img.getHeight(null), java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
       java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        try {
            javax.imageio.ImageIO.write(bi, "png", baos);
        } catch (Exception e) {}
        return baos.toByteArray();
}

    private JPanel createProjectCard(int id, String name, byte[] imgBytes, boolean isPdf) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(240, 220));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 1));

        // --- 1. UNIFIED PREVIEW (Image or PDF) ---
        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(240, 140));

        if (isPdf) {
            // 1. If it's a PDF, we use the icon and STOP here
            try {
                ImageIcon pdfIcon = new ImageIcon("pdf_icon.png");
                Image scaled = pdfIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                imgLabel.setText("PDF"); // Fallback if icon file is missing
            }
        } 
        else if (imgBytes != null && imgBytes.length > 0) {
            // 2. If it's NOT a PDF, try to render the actual image bytes
            try {
                ImageIcon icon = new ImageIcon(imgBytes);
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image img = icon.getImage();
                    Image scaled = img.getScaledInstance(240, 140, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(scaled));
               } else {
                    // This is what was showing up for your PDF before!
                    imgLabel.setText("Corrupted Image"); 
                }
            } catch (Exception e) {
               imgLabel.setText("Error");
            }
        } 
        else {
            // 3. No data at all
            imgLabel.setText("No Preview");
            imgLabel.setForeground(Color.GRAY);
        }

        card.add(imgLabel, BorderLayout.CENTER);

        // --- 2. Bottom Info Panel ---
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
    
        JLabel lblName = new JLabel(" " + name);
        lblName.setFont(new Font("Helvetica", Font.BOLD, 14));
    
        // --- 3. Delete Button ---
        JButton btnDelete = new JButton("🗑");
        btnDelete.setForeground(Color.RED);
        btnDelete.setBorderPainted(false);
        btnDelete.setFocusPainted(false);
        btnDelete.setContentAreaFilled(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnDelete.setForeground(new Color(0xC0392B)); // Darker Red
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnDelete.setForeground(Color.RED); // Original Red
            }
        });

        btnDelete.addActionListener(e -> deleteProject(id));

        infoPanel.add(lblName, BorderLayout.CENTER);
        infoPanel.add(btnDelete, BorderLayout.EAST);
        card.add(infoPanel, BorderLayout.SOUTH);

        return card;
    }

    private void deleteProject(int id) {
        // Using your custom Styled Dialog for confirmation
        boolean confirm = CustomDialog.showConfirm(this, "Are you sure you want to delete this project?");

        if (confirm) {
            String sql = "DELETE FROM portfolios WHERE id = ?";
            try (Connection conn = Database.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
            
                pst.setInt(1, id);
                int rowsDeleted = pst.executeUpdate();
            
                if (rowsDeleted > 0) {
                    // Success feedback using CustomDialog
                    CustomDialog.show(this, "Project deleted successfully!", true);
                    loadProjects(currentUserId); // Refresh the gallery cards
                }
            
            } catch (Exception e) {
                e.printStackTrace();
                CustomDialog.show(this, "Error deleting project.", false);
            }
        }
    }
}