package gui;

import db.Database;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;
import main.Main;

public class PortfolioPanel extends JPanel {
    private JPanel galleryContainer;
    private int currentUserId; 
    private Set<String> selectedTags = new HashSet<>();
    private final int MAX_VISIBLE_TAGS = 8;
    private JTextField searchField;
    private JPanel rightSidebar;

    public PortfolioPanel(int userId) {
        this.currentUserId = userId;
        setLayout(null);
        setBackground(Main.BG_COLOR); 

        // Title
        JLabel title = new JLabel("My Portfolio Projects");
        title.setBounds(50, 20, 300, 40);
        title.setFont(new Font("Helvetica", Font.BOLD, 28));
        title.setForeground(new Color(0x2D3436));
        add(title);

        // Search Bar
        searchField = new JTextField(" Search projects...");
        searchField.setBounds(20, 75, 550, 45);
        searchField.setFont(new Font("Helvetica", Font.PLAIN, 15));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 2, true), 
            BorderFactory.createEmptyBorder(10, 15, 10, 15) 
        ));

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                loadProjects(currentUserId, "SEARCH"); 
            }
        });
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(" Search projects...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(" Search projects...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        add(searchField);

        // Add Project Button
        JButton btnOpenPopup = new JButton("+ ADD PROJECT");
        btnOpenPopup.setBounds(720, 25, 180, 45);
        btnOpenPopup.setBackground(Main.ACCENT_COLOR); 
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

        // Right sidebar for filtering of tags
        rightSidebar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        rightSidebar.setBackground(Color.WHITE); 
        rightSidebar.setPreferredSize(new Dimension(220, 450)); 
        rightSidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(0xEEEEEE)));

        // Main Content Wrapper
        JPanel mainContentWrapper = new JPanel(new BorderLayout(20, 0)); 
        mainContentWrapper.setBackground(Main.BG_COLOR);
        mainContentWrapper.setBounds(20, 140, 920, 560);
        add(mainContentWrapper);

        galleryContainer = new JPanel(new GridLayout(0, 3, 10, 20));
        galleryContainer.setBackground(Main.BG_COLOR);
        galleryContainer.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setBackground(Main.BG_COLOR);
        wrapper.add(galleryContainer);

        JScrollPane scrollPane = new JScrollPane(wrapper);
        scrollPane.setBorder(null); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Modern slim scrollbar UI
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); 
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0xCED4DA); 
                this.trackColor = Main.BG_COLOR;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) { 
                return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { 
                return createZeroButton(); }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

            mainContentWrapper.add(scrollPane, BorderLayout.CENTER);
            mainContentWrapper.add(rightSidebar, BorderLayout.EAST);
            refreshTagUI();
            loadProjects(currentUserId);
        }
    // Refreshes the right sidebar UI to show tag filters based on current selection and search state
    private void refreshTagUI() {
        rightSidebar.removeAll();

        // Header with "Filter by Tags" and "Clear All" button
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setBackground(Color.WHITE);
        headerContainer.setPreferredSize(new Dimension(200, 40));
        headerContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel tagHeader = new JLabel("FILTER BY TAGS");
        tagHeader.setFont(new Font("Helvetica", Font.BOLD, 11));
        tagHeader.setForeground(new Color(0x636E72));
        headerContainer.add(tagHeader, BorderLayout.WEST);

        boolean hasSearch = !searchField.getText().trim().isEmpty() && 
                            !searchField.getText().equals(" Search projects...");

        if (!selectedTags.isEmpty() || hasSearch) {
            JButton clearBtn = new JButton("Clear All");
            clearBtn.setFont(new Font("Helvetica", Font.BOLD, 11));
            clearBtn.setForeground(Main.ACCENT_COLOR); 
            clearBtn.setBorder(null);
            clearBtn.setContentAreaFilled(false);
            clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            clearBtn.addActionListener(e -> {
                selectedTags.clear();
                searchField.setText(" Search projects...");
                searchField.setForeground(Color.GRAY);
                refreshTagUI();
                loadProjects(currentUserId);
            });
            headerContainer.add(clearBtn, BorderLayout.EAST);
        }
        rightSidebar.add(headerContainer);

        // Fetch unique tags from the database and create pills
        Set<String> allTags = fetchUniqueTags();
        int count = 0;
        
        for (String tag : allTags) {
            if (count < MAX_VISIBLE_TAGS) {
                JToggleButton pill = createTagPill(tag);
                pill.setPreferredSize(new Dimension(190, 38));
                rightSidebar.add(pill);
                count++;
            } else {
                break; 
            }
        }

        if (allTags.size() > MAX_VISIBLE_TAGS) {
            int remaining = allTags.size() - MAX_VISIBLE_TAGS;
            JButton moreBtn = new JButton("+ " + remaining + " More");
            styleGhostButton(moreBtn); 
            moreBtn.setPreferredSize(new Dimension(190, 38));
            moreBtn.addActionListener(e -> showAllTagsPopup(allTags));
            rightSidebar.add(moreBtn);
        }

        rightSidebar.revalidate();
        rightSidebar.repaint();
    }

    // Creates a toggle button styled as a pill for a given tag
    private JToggleButton createTagPill(String tag) {
        JToggleButton tagBtn = new JToggleButton(tag);
        tagBtn.setFocusPainted(false);
        tagBtn.setSelected(selectedTags.contains(tag));
        tagBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tagBtn.setFont(new Font("Helvetica", Font.PLAIN, 13));

        tagBtn.setBackground(tagBtn.isSelected() ? Main.ACCENT_COLOR : Color.WHITE);
        tagBtn.setForeground(tagBtn.isSelected() ? Color.WHITE : new Color(0x2D3436));
        tagBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1, true),
            BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));

        // Toggle selection and update filters on click
        tagBtn.addActionListener(e -> {
            if (tagBtn.isSelected()) selectedTags.add(tag);
            else selectedTags.remove(tag);
            refreshTagUI();
            loadProjects(currentUserId, "FILTER");
        });
        return tagBtn;
    }

    // Styles the "More" button in the tag filter sidebar
    private void styleGhostButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("Helvetica", Font.BOLD, 13));
        btn.setForeground(Main.ACCENT_COLOR);
        btn.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Main.ACCENT_COLOR, 1, true),
        BorderFactory.createEmptyBorder(8, 15, 8, 15)
    ));
    }

    // Displays a popup with all tags when "More" is clicked
    private void showAllTagsPopup(Set<String> allTags) {
        TagSelectionDialog dialog = new TagSelectionDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            allTags, 
            selectedTags
        );
        dialog.setVisible(true);
        refreshTagUI();
        loadProjects(currentUserId, "FILTER");
    }

    // Custom dialog to show all tags with checkboxes for selection
    private class TagSelectionDialog extends JDialog {
        public TagSelectionDialog(Frame owner, Set<String> allTags, Set<String> selected) {
            super(owner, true);
            setSize(500, 450); 
            setLocationRelativeTo(owner);
            setUndecorated(true);
            
            JPanel content = new JPanel(new BorderLayout());
            content.setBackground(Color.WHITE);
            content.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 2));

            // Header
            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 20));
            header.setBackground(Color.WHITE);
            JLabel title = new JLabel("All Categories");
            title.setFont(new Font("Helvetica", Font.BOLD, 22));
            header.add(title);
            content.add(header, BorderLayout.NORTH);

            JPanel listPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
            listPanel.setBackground(Color.WHITE);
            
            for (String tag : allTags) {
                listPanel.add(createTagPill(tag));
            }

            int totalTags = allTags.size();
            int estimatedRows = (totalTags / 3) + 1;
            int calculatedHeight = estimatedRows * 60; 
            listPanel.setPreferredSize(new Dimension(460, Math.max(300, calculatedHeight)));

            JScrollPane scroll = new JScrollPane(listPanel);
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
            scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            
            // Modern Slim Scrollbar UI
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

            // Close Button
            JButton cancelBtn = new JButton("CLOSE");
            cancelBtn.setPreferredSize(new Dimension(100, 40));
            cancelBtn.setFont(new Font("Helvetica", Font.BOLD, 12));
            cancelBtn.setForeground(new Color(0x636E72));
            cancelBtn.setContentAreaFilled(false);
            cancelBtn.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0)));
            cancelBtn.setFocusPainted(false);
            cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelBtn.addActionListener(e -> dispose());

            // Apply Button
            JButton doneBtn = new JButton("APPLY FILTERS");
            doneBtn.setPreferredSize(new Dimension(150, 40));
            doneBtn.setBackground(Main.ACCENT_COLOR);
            doneBtn.setForeground(Color.WHITE);
            doneBtn.setFont(new Font("Helvetica", Font.BOLD, 12));
            doneBtn.setFocusPainted(false);
            doneBtn.setBorderPainted(false);
            doneBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            doneBtn.addActionListener(e -> dispose());
            
            footer.add(cancelBtn);
            footer.add(doneBtn);
            content.add(footer, BorderLayout.SOUTH);

            add(content);
        }
    }

    private Set<String> fetchUniqueTags() {
        Set<String> uniqueTags = new java.util.TreeSet<>();
        String sql = "SELECT DISTINCT tags FROM projects WHERE user_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, currentUserId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String t = rs.getString("tags");
                if (t != null) for (String p : t.split(",")) uniqueTags.add(p.trim());
            }
        } catch (Exception e) { e.printStackTrace(); }
        return uniqueTags;
    }

    // Data fetcher from database with dynamic SQL construction based on search text and selected tags for filtering
    public void loadProjects(int userId, String filterTrigger) {
        galleryContainer.removeAll();

        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.equals("search projects...") || searchText.isEmpty()) {
            searchText = ""; 
        }
            
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, pr.tags FROM portfolios p " +
            "JOIN projects pr ON p.project_id = pr.id " +
            "WHERE p.user_id = ?"
        );

        if (!searchText.isEmpty()) {
            sql.append(" AND (LOWER(p.project_name) LIKE ? OR LOWER(pr.description) LIKE ?)");
        }

        if (!selectedTags.isEmpty()) {
            sql.append(" AND (");
            String tagPlaceholders = selectedTags.stream()
                .map(t -> "pr.tags LIKE ?")
                .collect(Collectors.joining(" OR "));
            sql.append(tagPlaceholders).append(")");
        }

        try (Connection conn = Database.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            pst.setInt(paramIndex++, userId);

            if (!searchText.isEmpty()) {
                pst.setString(paramIndex++, "%" + searchText + "%");
                pst.setString(paramIndex++, "%" + searchText + "%");
            }

            if (!selectedTags.isEmpty()) {
                for (String tag : selectedTags) {
                    pst.setString(paramIndex++, "%" + tag + "%");
                }
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("project_name");
                byte[] imgBytes = rs.getBytes("file_data");
                String fileName = rs.getString("file_name");
            
                boolean isPdf = (fileName != null && fileName.toLowerCase().endsWith(".pdf"));
                galleryContainer.add(createProjectCard(id, name, imgBytes, isPdf));
            }
        
        galleryContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0)); 
        galleryContainer.revalidate();
        galleryContainer.repaint();

        } catch (Exception e) { 
            e.printStackTrace(); 
        }

        galleryContainer.revalidate();
        galleryContainer.repaint();
    }

    // Overloaded method for initial load without filter trigger
    public void loadProjects(int userId) { 
        loadProjects(userId, null);
    }

    // UI Handler - Opens the Add Portfolio popup dialog and refreshes the tag filters after a new project is added
    private void showAddPortfolioPopup() {
        AddPortfolioPopup popup = new AddPortfolioPopup(
            (Frame) SwingUtilities.getWindowAncestor(this),
            this,
            this.currentUserId
        );
        popup.setVisible(true);
        refreshTagUI();
    }

    // Creates a card UI component for a project, displaying either an image preview or a PDF icon
    private JPanel createProjectCard(int id, String name, byte[] imgBytes, boolean isPdf) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setPreferredSize(new Dimension(215, 280));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 1));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 1));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 1));
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showProjectDetails(id, isPdf);
            }
        });

        // Unified Preview(Image or PDF) 
        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(215, 140));

        if (isPdf) {
            try {
                ImageIcon pdfIcon = new ImageIcon("pdf_icon.png");
                Image scaled = pdfIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                imgLabel.setText("PDF"); 
            }
        } 
        else if (imgBytes != null && imgBytes.length > 0) {
            try {
                ImageIcon icon = new ImageIcon(imgBytes);
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image img = icon.getImage().getScaledInstance(200, 130, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(img));
               } else {
                    imgLabel.setText("Corrupted Image"); 
                }
            } catch (Exception e) {
               imgLabel.setText("Error");
            }
        } 
        else {
            imgLabel.setText("No Preview");
            imgLabel.setForeground(Color.GRAY);
        }

        card.add(imgLabel, BorderLayout.CENTER);

        // Bottom Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
    
        JLabel lblName = new JLabel(" " + name);
        lblName.setFont(new Font("Helvetica", Font.BOLD, 14));
    
        // Delete Button
        JButton btnDelete = new JButton("🗑");
        btnDelete.setForeground(Color.RED);
        btnDelete.setBorderPainted(false);
        btnDelete.setFocusPainted(false);
        btnDelete.setContentAreaFilled(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnDelete.setForeground(new Color(0xC0392B)); 
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnDelete.setForeground(Color.RED); 
            }
        });

        btnDelete.addActionListener(e -> deleteProject(id));

        infoPanel.add(lblName, BorderLayout.CENTER);
        infoPanel.add(btnDelete, BorderLayout.EAST);
        card.add(infoPanel, BorderLayout.SOUTH);

        return card;
    }

    // Data fetcher from database
    private void showProjectDetails(int portfolioId, boolean isPdf) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT p.project_name, p.file_data, p.file_name, " +
                "pr.description AS real_desc, pr.tags " + 
                "FROM portfolios p " +
                "LEFT JOIN projects pr ON p.project_id = pr.id " +
                "WHERE p.id = ?";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, portfolioId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
            String name = rs.getString("project_name");
            byte[] imgData = rs.getBytes("file_data");
            String tags = rs.getString("tags");
            String desc = rs.getString("real_desc");
            
            
            String fileName = rs.getString("file_name");
            isPdf = (fileName != null && fileName.toLowerCase().endsWith(".pdf"));

            if (desc == null || desc.trim().isEmpty()) {
                desc = "No description provided.";
            }

            ProjectDetailDialog dialog = new ProjectDetailDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                name, desc, imgData, tags, isPdf
            );
            dialog.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }

    // User Interface - Custom dialog to show project details with a larger image preview, description, and tags
    private class ProjectDetailDialog extends JDialog { 
    public ProjectDetailDialog(Frame owner, String name, String desc, byte[] imgData, String tags, boolean isPdf) {
        super(owner, "Project Details", true);
        setSize(600, 700);
        setLocationRelativeTo(owner);
        setUndecorated(true);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 2));
        mainContainer.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(600, 50));
        
        mainContainer.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(40, 30, 30, 30));

        // LARGE IMAGE
        JLabel previewLabel = new JLabel();
        previewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isPdf) {
            try {
                ImageIcon pdfIcon = new ImageIcon("pdf_icon.png"); 
                Image img = pdfIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                previewLabel.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                previewLabel.setText("PDF FILE");
                previewLabel.setFont(new Font("Helvetica", Font.BOLD, 20));
            }
        } else if (imgData != null && imgData.length > 0) {
            ImageIcon original = new ImageIcon(imgData);
            int targetW = 500;
            int targetH = 350; 
            Image img = original.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
            previewLabel.setIcon(new ImageIcon(img));
        }

        content.add(previewLabel);

        // TEXT ELEMENTS
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
        txtDesc.setFocusable(false);
        txtDesc.setOpaque(false);
        txtDesc.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        content.add(Box.createVerticalStrut(20));
        content.add(lblTitle);
        content.add(lblTags);
        content.add(txtDesc);

        // MODERN SCROLLBAR
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = new Color(0xCED4DA);
                this.trackColor = Color.WHITE;
            }
            @Override protected JButton createDecreaseButton(int orientation) { return createZero(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZero(); }
            private JButton createZero() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
        });

        mainContainer.add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("CLOSE");
        btnClose.setPreferredSize(new Dimension(0, 50));
        btnClose.setBackground(Main.ACCENT_COLOR);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.addActionListener(e -> dispose());
        add(btnClose, BorderLayout.SOUTH);
        
        add(mainContainer);

        
    }
}

    // Data handler - Deletes a project and all its associated data (including tags) from the database with confirmation and error handling
    private void deleteProject(int projectId) {
        boolean confirm = CustomDialog.showConfirm(this, "Are you sure you want to delete this project and all its data?");

        if (confirm) {
            try (Connection conn = Database.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    // STEP A: Fetch the real Project ID first so we can delete the tags in 'projects'
                    int realProjectId = -1;
                    String findIdSql = "SELECT project_id FROM portfolios WHERE id = ?";
                    try (PreparedStatement pstId = conn.prepareStatement(findIdSql)) {
                        pstId.setInt(1, projectId);
                        ResultSet rs = pstId.executeQuery();
                        if (rs.next()) {
                            realProjectId = rs.getInt("project_id");
                        }
                    }

                    // STEP B: Delete from 'portfolios' first
                    String sqlPortfolios = "DELETE FROM portfolios WHERE id = ?";
                    try (PreparedStatement pst1 = conn.prepareStatement(sqlPortfolios)) {
                        pst1.setInt(1, projectId);
                        pst1.executeUpdate();
                    }

                    // STEP C: Delete from 'projects' using the REAL project ID
                    if (realProjectId != -1) {
                        String sqlProjects = "DELETE FROM projects WHERE id = ?";
                        try (PreparedStatement pst2 = conn.prepareStatement(sqlProjects)) {
                            pst2.setInt(1, realProjectId);
                            pst2.executeUpdate();
                        }
                    }

                    conn.commit();
                    CustomDialog.show(this, "Project and its tags removed!", true);
                
                    loadProjects(currentUserId);
                    refreshTagUI();

                } catch (SQLException ex) {
                    conn.rollback();
                    ex.printStackTrace();
                    CustomDialog.show(this, "Delete failed: Data was rolled back.", false);
                } finally {
                    conn.setAutoCommit(true);
                }
            
            } catch (Exception e) {
                e.printStackTrace();
                CustomDialog.show(this, "Connection Error.", false);
            }
        }
    }
}