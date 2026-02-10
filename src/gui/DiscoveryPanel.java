package gui;

import db.Database;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashSet; // For managing selected tags in the sidebar
import java.util.Set; // To store unique tags fetched from the database
import javax.swing.*;
import main.Main;

public class DiscoveryPanel extends JPanel {
    private JPanel feedContainer; 
    private JTextField searchField; 
    private JPanel rightSidebar;
    private Set<String> selectedTags = new HashSet<>();
    private final int MAX_VISIBLE_TAGS = 8; // Limit for how many tags to show in the sidebar before showing "See More"

    public DiscoveryPanel() {
        setLayout(null);
        setBackground(Main.BG_COLOR);

        // Title
        JLabel title = new JLabel("Community Discovery");
        title.setBounds(50, 30, 400, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 28));
        title.setForeground(Main.TEXT_COLOR);
        add(title);

        // Search Bar (Mirrored from Portfolio)
        searchField = new JTextField(" Search community projects...");
        searchField.setForeground(Color.GRAY);
        searchField.setBounds(60, 95, 550, 45);
        searchField.setFont(new Font("Helvetica", Font.PLAIN, 15));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 2, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // this allows us to have the "Search..." placeholder text that disappears when the user clicks on the field
        searchField.addFocusListener(new java.awt.event.FocusAdapter() { 
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(" Search community projects...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK); 
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(" Search community projects...");
                    searchField.setForeground(Color.GRAY); 
                }
            }
        });

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { loadCommunityFeed(); }
        });
        add(searchField);

        // Main Content Wrapper (Feeds and Sidebar)
        JPanel mainContentWrapper = new JPanel(new BorderLayout(20, 0));
        mainContentWrapper.setBackground(Main.BG_COLOR);
        mainContentWrapper.setBounds(20, 150, 900, 550);

        feedContainer = new JPanel(new GridLayout(0, 1, 25, 30)); // Dynamic rows, 1 column, with spacing
        feedContainer.setBackground(Main.BG_COLOR);
        feedContainer.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // Wrap feedContainer in a JPanel to center it and add padding
        JPanel feedWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); 
        feedWrapper.setBackground(Main.BG_COLOR);
        feedWrapper.add(feedContainer);

        // Custom ScrollPane for feedContainer to remove the standard scrollbar 
        JScrollPane scrollPane = new JScrollPane(feedContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // We want to avoid horizontal scrolling at all costs for a feed layout
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);  
        scrollPane.getViewport().setBackground(Main.BG_COLOR); 

        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0xCED4DA);
                this.trackColor = Main.BG_COLOR;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) { 
                return createZeroButton(); 
            }
            @Override
            protected JButton createIncreaseButton(int orientation) { 
                return createZeroButton(); 
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });

        // Right Sidebar for Tag Filters
        rightSidebar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        rightSidebar.setBackground(Color.WHITE);
        rightSidebar.setPreferredSize(new Dimension(220, 500)); 
        rightSidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(0xEEEEEE)));

        mainContentWrapper.add(scrollPane, BorderLayout.CENTER);
        mainContentWrapper.add(rightSidebar, BorderLayout.EAST);
        add(mainContentWrapper);

        refreshGlobalTags();
        loadCommunityFeed();
    }

    public void loadCommunityFeed() {
        feedContainer.removeAll();

        String searchKeyword = "";
        if (searchField != null) {  
            String text = searchField.getText().trim();
            if (!text.isEmpty() && !text.equals("Search community projects...")) { 
                searchKeyword = text.toLowerCase();
            }
        }
        
        StringBuilder sql = new StringBuilder(
            "SELECT p.id, p.project_name, p.file_data, p.file_name, p.upload_date, u.full_name " +
            "FROM portfolios p " +
            "JOIN users u ON p.user_id = u.id " +
            "LEFT JOIN projects pr ON p.project_id = pr.id WHERE 1=1 "
        );

        if (!searchKeyword.isEmpty()) { // Only apply search filter if user has entered something meaningful
            sql.append("AND LOWER(p.project_name) LIKE ? ");
        }

        if (!selectedTags.isEmpty()) { // Only apply tag filters if there are selected tags
        sql.append("AND (");
        for (int i = 0; i < selectedTags.size(); i++) {
            sql.append("pr.tags LIKE ?");
            if (i < selectedTags.size() - 1) { 
                sql.append(" OR ");
            }
        }
        sql.append(") "); // Wrap tag conditions in parentheses to ensure correct SQL logic when combined with search filter
    }

        sql.append("ORDER BY p.upload_date DESC"); 

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (!searchKeyword.isEmpty()) {
                pst.setString(paramIndex++, "%" + searchKeyword + "%");
            }

            for (String tag : selectedTags) {
                pst.setString(paramIndex++, "%" + tag + "%");
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String projectName = rs.getString("project_name");
                String author = rs.getString("full_name");
                byte[] imgBytes = rs.getBytes("file_data");
                String fileName = rs.getString("file_name");
                Timestamp date = rs.getTimestamp("upload_date");

                boolean isPdf = (fileName != null && fileName.toLowerCase().endsWith(".pdf"));
                JPanel card = createDiscoveryCard(projectName, author, imgBytes, date.toString(), isPdf);
                
                card.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) { 
                        showProjectDetails(id); 
                    }
                });

                JPanel footer = (JPanel) card.getComponent(2);
                JButton btnView = (JButton) footer.getComponent(0);
                btnView.addActionListener(e -> showProjectDetails(id));
                feedContainer.add(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        feedContainer.revalidate();
        feedContainer.repaint();
    }

    public void refresh() {
        refreshGlobalTags(); // Clears and re-fetches the sidebar tags
        loadCommunityFeed(); // Re-runs the SQL query to get the newest posts
    }

    private void refreshGlobalTags() {
        rightSidebar.removeAll();

        // Header Panel with "FILTER BY TAGS" and conditional "Clear All" button
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setBackground(Color.WHITE);
        headerContainer.setPreferredSize(new Dimension(180, 40));
        headerContainer.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 10));

        JLabel tagHeader = new JLabel("FILTER BY TAGS");
        tagHeader.setFont(new Font("Helvetica", Font.BOLD, 12));
        tagHeader.setForeground(new Color(0x636E72));
        headerContainer.add(tagHeader, BorderLayout.WEST);

        // Show "Clear All" ONLY if tags are selected or search is active
        boolean hasSearch = !searchField.getText().equals(" Search community projects...") && !searchField.getText().trim().isEmpty();
        
        if (!selectedTags.isEmpty() || hasSearch) {
            JButton resetBtn = new JButton("Clear All");
            resetBtn.setFont(new Font("Helvetica", Font.BOLD, 11));
            resetBtn.setForeground(new Color(0x6C5CE7)); 
            resetBtn.setBorder(null);
            resetBtn.setContentAreaFilled(false);
            resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            resetBtn.addActionListener(e -> {
                selectedTags.clear();
                searchField.setText(" Search community projects...");
                searchField.setForeground(Color.GRAY);
                refreshGlobalTags(); 
                loadCommunityFeed();
            });
            headerContainer.add(resetBtn, BorderLayout.EAST);
        }
        
        rightSidebar.add(headerContainer);

        // Render Tag Buttons (Limit to MAX_VISIBLE_TAGS, then show "See More" if there are more)
        Set<String> allTags = fetchAllGlobalTags();
        int count = 0;
        for (String tag : allTags) {

            if (count >= MAX_VISIBLE_TAGS) 
                break;
            
            JButton tagBtn = new JButton(tag);
            tagBtn.setPreferredSize(new Dimension(180, 35));
            tagBtn.setFocusPainted(false);
            tagBtn.setFont(new Font("Helvetica", Font.PLAIN, 12));

            if (selectedTags.contains(tag)) {
                tagBtn.setBackground(Main.ACCENT_COLOR);
                tagBtn.setForeground(Color.WHITE);
            } else {
                tagBtn.setBackground(new Color(0xF8F9FA));
                tagBtn.setForeground(new Color(0x2D3436));
                tagBtn.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
            }

            tagBtn.addActionListener(e -> {
                if (selectedTags.contains(tag)) selectedTags.remove(tag);
                else selectedTags.add(tag);
                refreshGlobalTags();
                loadCommunityFeed();
            });

            rightSidebar.add(tagBtn);
            count++;
        }

        // "SEE MORE" BUTTON
        if (allTags.size() > MAX_VISIBLE_TAGS) {
            JButton seeMoreBtn = new JButton("+ " + (allTags.size() - MAX_VISIBLE_TAGS) + " More");
            seeMoreBtn.setPreferredSize(new Dimension(180, 35));
            seeMoreBtn.setFont(new Font("Helvetica", Font.BOLD, 12));
            seeMoreBtn.setForeground(Main.ACCENT_COLOR);
            seeMoreBtn.setBackground(Color.WHITE);
            seeMoreBtn.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR));
            seeMoreBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seeMoreBtn.addActionListener(e -> showAllTagsDialog(allTags));
            rightSidebar.add(seeMoreBtn);
        }

        rightSidebar.revalidate();
        rightSidebar.repaint();
    }

    private Set<String> fetchAllGlobalTags() {
        Set<String> uniqueTags = new java.util.TreeSet<>();
        String sql = "SELECT DISTINCT tags FROM projects";
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String t = rs.getString("tags");
                if (t != null && !t.trim().isEmpty()) {
                    for (String p : t.split(",")) {
                        if (!p.trim().isEmpty()) uniqueTags.add(p.trim());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uniqueTags;
    }

    private void showAllTagsDialog(Set<String> allTags) {
        TagSelectionDialog dialog = new TagSelectionDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            allTags,
            selectedTags
        );
        dialog.setVisible(true);

        refreshGlobalTags();
        loadCommunityFeed();
    }

    private class TagSelectionDialog extends JDialog {
        public TagSelectionDialog(Frame owner, Set<String> allTags, Set<String> selected) {
            super(owner, true);
            setSize(500, 450);
            setLocationRelativeTo(owner);
            setUndecorated(true);

            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBackground(Color.WHITE);
            content.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));


            // Header
            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 20));
            header.setBackground(Color.WHITE);
            JLabel title = new JLabel("All Categories");
            title.setFont(new Font("Helvetica", Font.BOLD, 22));
            header.add(title);
            content.add(header, BorderLayout.NORTH);

            // Wrapped Tag Grid
            JPanel listPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
            listPanel.setBackground(Color.WHITE);

            Set<String> tempSelected = new HashSet<>(selected);

            for (String tag : allTags) {
                JToggleButton pill = new JToggleButton(tag);
                pill.setFocusPainted(false);
                pill.setSelected(tempSelected.contains(tag));
                pill.setCursor(new Cursor(Cursor.HAND_CURSOR));
                pill.setFont(new Font("Helvetica", Font.PLAIN, 13));
                pill.setPreferredSize(new Dimension(140, 40));

                updatePillStyle(pill);

                pill.addActionListener(e -> {
                    if (pill.isSelected()) tempSelected.add(tag);
                    else tempSelected.remove(tag);
                    updatePillStyle(pill);
                });
                listPanel.add(pill);
            }

            int totalTags = allTags.size();
            int estimatedRows = (totalTags / 3) + 1;
            int calculatedHeight = estimatedRows * 55;
            listPanel.setPreferredSize(new Dimension(460, Math.max(300, calculatedHeight)));

            JScrollPane scroll = new JScrollPane(listPanel);
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
            scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
                @Override protected void configureScrollBarColors() {
                    this.thumbColor = new Color(0xCED4DA);
                    this.trackColor = Color.WHITE;
                }
                @Override protected JButton createDecreaseButton(int orientation) { return createZero(); }
                @Override protected JButton createIncreaseButton(int orientation) { return createZero(); }
                private JButton createZero() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            });

            content.add(scroll, BorderLayout.CENTER);

            // Footer
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
            footer.setBackground(new Color(0xF8F9FA));
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xEEEEEE)));

            JButton cancelBtn = new JButton("CLOSE");
            cancelBtn.setPreferredSize(new Dimension(100, 40));
            cancelBtn.setFont(new Font("Helvetica", Font.BOLD, 12));
            cancelBtn.setForeground(new Color(0x636E72));
            cancelBtn.setContentAreaFilled(false);
            cancelBtn.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0)));
            cancelBtn.setFocusPainted(false);
            cancelBtn.addActionListener(e -> dispose());

            JButton applyBtn = new JButton("APPLY FILTERS");
            applyBtn.setPreferredSize(new Dimension(150, 40));
            applyBtn.setBackground(Main.ACCENT_COLOR);
            applyBtn.setForeground(Color.WHITE);
            applyBtn.setFont(new Font("Helvetica", Font.BOLD, 12));
            applyBtn.setFocusPainted(false);
            applyBtn.setBorderPainted(false);
            applyBtn.addActionListener(e -> {
                selected.clear();
                selected.addAll(tempSelected);
                dispose();
            });

            footer.add(cancelBtn);
            footer.add(applyBtn);
            content.add(footer, BorderLayout.SOUTH);

            add(content);
        }

        private void updatePillStyle(JToggleButton btn) {
            btn.setBackground(btn.isSelected() ? Main.ACCENT_COLOR : Color.WHITE);
            btn.setForeground(btn.isSelected() ? Color.WHITE : new Color(0x2D3436));
            btn.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 1, true));
        }
    }

    private byte[] loadPlaceholderBytes() {
        try {
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
        return null;
    }

    // This method creates a card for each project in the discovery feed, handling both image and PDF previews
    private JPanel createDiscoveryCard(String title, String author, byte[] imgData, String date, boolean isPdf) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(600, 480));
        card.setMaximumSize(new Dimension(600, 480));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Helvetica", Font.BOLD, 20));

        JLabel lblAuthor = new JLabel("Shared by: " + author + " • " + date.substring(0, 16));
        lblAuthor.setForeground(Color.GRAY);
        header.add(lblTitle);
        header.add(lblAuthor);
        card.add(header, BorderLayout.NORTH);

        JLabel previewLabel = new JLabel("", SwingConstants.CENTER);
        previewLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        byte[] finalImgData = imgData;
        if (!isPdf && (finalImgData == null || finalImgData.length < 100)) {
            finalImgData = loadPlaceholderBytes();
        }

        // PDF files will show a generic PDF icon, while images will attempt to load the actual image data (or a placeholder if missing)
        if (isPdf) {
            try {
                ImageIcon pdfIcon = new ImageIcon("pdf_icon.png");
                if (pdfIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                    pdfIcon = new ImageIcon(getClass().getResource("/assets/pdf_icon.png"));
                }
                Image scaled = pdfIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                previewLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                previewLabel.setText("PDF DOCUMENT");
                previewLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
            }
        } else if (finalImgData != null) {
            ImageIcon icon = new ImageIcon(finalImgData);
            Image img = icon.getImage();

            int maxWidth = 560; 
            int maxHeight = 300;
            int imgWidth = img.getWidth(null);
            int imgHeight = img.getHeight(null);

            if (imgWidth > 0 && imgHeight > 0) {
                double widthRatio = (double) maxWidth / imgWidth;
                double heightRatio = (double) maxHeight / imgHeight;
                double scale = Math.min(widthRatio, heightRatio);

                int scaledWidth = (int) (imgWidth * scale);
                int scaledHeight = (int) (imgHeight * scale);

                Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                previewLabel.setIcon(new ImageIcon(scaledImg));
                
                final byte[] clickableData = finalImgData;
                previewLabel.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        showFullImage(clickableData, title);
                    }
                });
            } else {
                previewLabel.setText("Image unavailable");
            }
        }

        card.add(previewLabel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton btnView = new JButton("View Project");
        btnView.setPreferredSize(new Dimension(560, 45)); 
        btnView.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnView.setForeground(new Color(0x575FCF)); 
        btnView.setBackground(Color.WHITE);
        btnView.setFocusPainted(false);
        btnView.setContentAreaFilled(false);
        btnView.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnView.setBorder(BorderFactory.createLineBorder(new Color(0x575FCF), 1));

        btnView.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnView.setBackground(new Color(0xF8F9FA));
                btnView.setContentAreaFilled(true);
            }
            public void mouseExited(MouseEvent e) {
                btnView.setBackground(Color.WHITE);
                btnView.setContentAreaFilled(false);
            }
        });

        footer.add(btnView);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private void showProjectDetails(int projectId) {
        String sql = "SELECT p.*, pr.tags, pr.description, u.full_name " + 
                     "FROM portfolios p " +
                     "LEFT JOIN projects pr ON p.project_id = pr.id " +
                     "JOIN users u ON p.user_id = u.id " +
                     "WHERE p.id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, projectId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String name = rs.getString("project_name");
                byte[] imgData = rs.getBytes("file_data");
                String tags = rs.getString("tags");
                String author = rs.getString("full_name");
                String desc = rs.getString("description");
                String displayDesc = "Shared by: " + author + "\n\n" + (desc != null ? desc : "No description provided.");

                String fileName = rs.getString("file_name"); 
                boolean isPdf = (fileName != null && fileName.toLowerCase().endsWith(".pdf"));

                ProjectDetailDialog dialog = new ProjectDetailDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    name, displayDesc, imgData, tags, isPdf, fileName
                );
                dialog.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This dialog shows the project details when a user clicks on a discovery card, including a larger preview and description
    private class ProjectDetailDialog extends JDialog {
        public ProjectDetailDialog(Frame owner, String name, String desc, byte[] imgData, String tags, boolean isPdf, String originalFileName) {
            super(owner, "Project Details", true);
            setSize(600, 700);
            setLocationRelativeTo(owner);
            setUndecorated(true);

            JPanel mainContainer = new JPanel(new BorderLayout());
            mainContainer.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 2));
            mainContainer.setBackground(Color.WHITE);

            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBackground(Color.WHITE);
            content.setBorder(BorderFactory.createEmptyBorder(40, 30, 30, 30));

            JLabel previewLabel = new JLabel();
            previewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            if (isPdf) {
                try {
                    ImageIcon pdfIcon = new ImageIcon("pdf_icon.png");
                    Image img = pdfIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    previewLabel.setIcon(new ImageIcon(img));
                } catch (Exception e) {
                    previewLabel.setText("PDF FILE");
                }
            } else if (imgData != null && imgData.length > 0) {
                ImageIcon original = new ImageIcon(imgData);
                Image img = original.getImage().getScaledInstance(500, 350, Image.SCALE_SMOOTH);
                previewLabel.setIcon(new ImageIcon(img));
            }
            content.add(previewLabel);

            if (isPdf && imgData != null) {
                content.add(Box.createVerticalStrut(15));
                JButton downloadBtn = new JButton("DOWNLOAD PDF DOCUMENT");
                downloadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                downloadBtn.setPreferredSize(new Dimension(300, 45));
                downloadBtn.setMaximumSize(new Dimension(300, 45));
                downloadBtn.setBackground(new Color(0xEB4D4B)); // Red theme for PDF
                downloadBtn.setForeground(Color.WHITE);
                downloadBtn.setFont(new Font("Helvetica", Font.BOLD, 14));
                downloadBtn.setFocusPainted(false);
                downloadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                downloadBtn.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new java.io.File(originalFileName != null ? originalFileName : name + ".pdf"));
                    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileChooser.getSelectedFile())) {
                            fos.write(imgData);
                            JOptionPane.showMessageDialog(this, "File saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(this, "Error saving file.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                content.add(downloadBtn);
            }

            JLabel lblTitle = new JLabel(name);
            lblTitle.setFont(new Font("Helvetica", Font.BOLD, 26));
            lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblTags = new JLabel("Tags: " + (tags != null ? tags : "None"));
            lblTags.setFont(new Font("Helvetica", Font.ITALIC, 14));
            lblTags.setForeground(new Color(0x6c5ce7));
            lblTags.setAlignmentX(Component.CENTER_ALIGNMENT);

            JTextArea txtDesc = new JTextArea(desc);
            txtDesc.setFont(new Font("Helvetica", Font.PLAIN, 16));
            txtDesc.setLineWrap(true);
            txtDesc.setWrapStyleWord(true);
            txtDesc.setEditable(false);
            txtDesc.setOpaque(false);
            txtDesc.setFocusable(false);
            txtDesc.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

            content.add(Box.createVerticalStrut(20));
            content.add(lblTitle);
            content.add(lblTags);
            content.add(txtDesc);

            JScrollPane scroll = new JScrollPane(content);
            scroll.setBorder(null);
            mainContainer.add(scroll, BorderLayout.CENTER);

            JButton btnClose = new JButton("CLOSE");
            btnClose.setPreferredSize(new Dimension(0, 50));
            btnClose.setBackground(Main.ACCENT_COLOR);
            btnClose.setForeground(Color.WHITE);
            btnClose.setFocusPainted(false);
            btnClose.addActionListener(e -> dispose());
            mainContainer.add(btnClose, BorderLayout.SOUTH);

            add(mainContainer);
        }
    }

    private void showFullImage(byte[] imgData, String title) {
        JDialog viewer = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        viewer.setUndecorated(true);
        viewer.setBackground(new Color(0, 0, 0, 230)); 

        ImageIcon icon = new ImageIcon(imgData);
        JLabel imageLabel = new JLabel(icon);
    
        // SET ZOOM/PAN CURSOR
        imageLabel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        JScrollPane scroll = new JScrollPane(imageLabel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        
        // HIDE ALL SCROLLBARS
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // ENABLE MOUSE PANNING (Click and Drag to move image)
        MouseAdapter ma = new MouseAdapter() {
            private Point origin;
            @Override
            public void mousePressed(MouseEvent e) {
                origin = new Point(e.getPoint());
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (origin != null) {
                    JViewport viewPort = scroll.getViewport();
                    Point cp = viewPort.getViewPosition();
                    Point vp = e.getPoint();
                    cp.x += origin.x - vp.x;
                    cp.y += origin.y - vp.y;
                    
                    // Keep panning within bounds
                    Dimension viewSize = imageLabel.getSize();
                    Dimension portSize = viewPort.getSize();
                    if (cp.x < 0) cp.x = 0;
                    if (cp.y < 0) cp.y = 0;
                    if (cp.x > viewSize.width - portSize.width) cp.x = Math.max(0, viewSize.width - portSize.width);
                    if (cp.y > viewSize.height - portSize.height) cp.y = Math.max(0, viewSize.height - portSize.height);

                    viewPort.setViewPosition(cp);
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) || e.getClickCount() == 2) {
                    viewer.dispose();
                }
            }
        };
        imageLabel.addMouseListener(ma);
        imageLabel.addMouseMotionListener(ma);

        // ADD AN "ESC" KEY TO CLOSE
        viewer.getRootPane().registerKeyboardAction(e -> viewer.dispose(), 
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        viewer.setLayout(new BorderLayout());
        viewer.add(scroll, BorderLayout.CENTER);

        // Helper text at the bottom 
        JLabel hint = new JLabel("Drag to move • Press ESC or Right-click to close", SwingConstants.CENTER);
        hint.setForeground(new Color(255, 255, 255, 150));
        hint.setFont(new Font("Helvetica", Font.PLAIN, 12));
        hint.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        viewer.add(hint, BorderLayout.SOUTH);

        viewer.setSize(Toolkit.getDefaultToolkit().getScreenSize()); // Fullscreen
        viewer.setLocationRelativeTo(null);
        viewer.setVisible(true);
    }
}