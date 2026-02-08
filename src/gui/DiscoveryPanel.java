package gui;

import db.Database;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import main.Main;

public class DiscoveryPanel extends JPanel {
    private JPanel feedContainer;
    private JScrollPane scrollPane;

    public DiscoveryPanel() {
        setLayout(null);
        setBackground(Main.BG_COLOR);

        JLabel title = new JLabel("Community Discovery");
        title.setBounds(50, 30, 400, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 28));
        title.setForeground(Main.TEXT_COLOR);
        add(title);

        // Feed container with Vertical FlowLayout
        feedContainer = new JPanel();
        feedContainer.setLayout(new BoxLayout(feedContainer, BoxLayout.Y_AXIS));
        feedContainer.setBackground(Main.BG_COLOR);

        scrollPane = new JScrollPane(feedContainer);
        scrollPane.setBounds(50, 100, 916, 580);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        loadCommunityFeed();
    }

    public void loadCommunityFeed() {
        feedContainer.removeAll();
        
        // SQL query to join portfolio with user details, ordered by date
        String sql = "SELECT p.project_name, p.file_data, p.image_path, p.upload_date, u.full_name " +
                    "FROM portfolios p JOIN users u ON p.user_id = u.id " +
                    "ORDER BY p.upload_date DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String projectName = rs.getString("project_name");
                String author = rs.getString("full_name");
                byte[] imgBytes = rs.getBytes("file_data");
                Timestamp date = rs.getTimestamp("upload_date");

                if (imgBytes == null || imgBytes.length == 0) {
                    imgBytes = loadPlaceholderBytes(); // We will create this helper below
                }

                feedContainer.add(createDiscoveryCard(projectName, author, imgBytes, date.toString()));
                feedContainer.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        feedContainer.revalidate();
        feedContainer.repaint();
    }

    private byte[] loadPlaceholderBytes() {
        try {
            // This is the ClassLoader fix that finally worked for you!
            java.net.URL imgURL = getClass().getResource("/assets/default_preview.png");
            
            if (imgURL != null) {
                try (java.io.InputStream is = imgURL.openStream()) {
                   return is.readAllBytes();
                }
           } else {
                System.out.println("❌ Discovery: Could not find /assets/default_preview.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // If it fails, the card will just show text
    }

    private JPanel createDiscoveryCard(String title, String author, byte[] imgData, String date) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(850, 500));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Header: Project Name and Author
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Helvetica", Font.BOLD, 20));
        JLabel lblAuthor = new JLabel("Shared by: " + author + " • " + date.substring(0, 16));
        lblAuthor.setForeground(Color.GRAY);
        header.add(lblTitle);
        header.add(lblAuthor);
        card.add(header, BorderLayout.NORTH);

        // Image Preview
        if (imgData != null) {
            ImageIcon icon = new ImageIcon(imgData);
            // Scale to fit card
            Image scaledImg = icon.getImage().getScaledInstance(800, 400, Image.SCALE_SMOOTH);
            JLabel imgLabel = new JLabel(new ImageIcon(scaledImg));
            imgLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // CLICK EVENT FOR CLOSER LOOK
            imgLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    showFullImage(imgData, title);
                }
            });
            card.add(imgLabel, BorderLayout.CENTER);
        }

        return card;
    }

    private void showFullImage(byte[] imgData, String title) {
        JDialog viewer = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        viewer.setUndecorated(true);
        viewer.getContentPane().setBackground(new Color(0, 0, 0, 200)); // Dark overlay

        ImageIcon icon = new ImageIcon(imgData);
        JLabel fullImg = new JLabel(icon);
        
        // Wrap in scrollpane in case image is larger than screen
        JScrollPane scroll = new JScrollPane(fullImg);
        scroll.setBorder(null);
        scroll.setBackground(Color.BLACK);
        
        JButton closeBtn = new JButton("CLOSE");
        closeBtn.addActionListener(e -> viewer.dispose());
        
        viewer.add(scroll, BorderLayout.CENTER);
        viewer.add(closeBtn, BorderLayout.SOUTH);
        
        viewer.setSize(1000, 700);
        viewer.setLocationRelativeTo(null);
        viewer.setVisible(true);
    }
}