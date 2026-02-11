package gui;

import db.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import main.Main;

public class AdminDashboard extends JFrame {
    private JPanel contentArea;
    private JPanel dashPanel, userPanel, postPanel;
    private JPanel activeIndicator;
    private JTable userTable, postTable; 
    private DefaultTableModel userModel, postModel;
    private JLabel lblTotalUsers;
    private JLabel lblTotalPortfolios;

    public AdminDashboard() {
        setTitle("Vantage Admin Console");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        getContentPane().setBackground(Main.BG_COLOR);
        setLayout(null);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBounds(0, 0, 250, 800);
        sidebar.setBackground(Main.DARK_PANEL);
        sidebar.setLayout(null);
        add(sidebar);

        // Sidebar Title
        JLabel lblAdmin = new JLabel("ADMIN PORTAL");
        lblAdmin.setForeground(Color.WHITE);
        lblAdmin.setFont(new Font("Helvetica", Font.BOLD, 20));
        lblAdmin.setBounds(40, 50, 200, 30);
        sidebar.add(lblAdmin);

        // Inside Sidebar panel setup
        activeIndicator = new JPanel();
        activeIndicator.setBounds(0, 120, 5, 40); // Matches the first button's Y
        activeIndicator.setBackground(Main.ACCENT_COLOR); 
        sidebar.add(activeIndicator);

        // Sidebar Navigation Buttons
        JButton btnDash = createSidebarBtn("Dashboard Management", 120);
        btnDash.setForeground(Main.ACCENT_COLOR);
        JButton btnUsers = createSidebarBtn("Manage Users", 170);
        JButton btnPosts = createSidebarBtn("Manage Posts", 220);
        JButton btnLogout = createSidebarBtn("Logout", 700);

        sidebar.add(btnDash);
        sidebar.add(btnUsers);
        sidebar.add(btnPosts);
        sidebar.add(btnLogout);

        // Content Area Setup
        contentArea = new JPanel(null);
        contentArea.setBounds(250, 0, 950, 800);
        contentArea.setBackground(Main.BG_COLOR);
        add(contentArea);

        // Initialize Panels
        dashPanel = createDashboardManagement();
        userPanel = createUserManager();
        postPanel = createPostManager();

        // Default View
        showPanel(dashPanel);

        // Switch Logic for the buttons
        btnDash.addActionListener(e -> {
            updateSummaryCounts();
            showPanel(dashPanel);
            activeIndicator.setLocation(0, 125); 
    
            btnDash.setForeground(Main.ACCENT_COLOR);
            btnUsers.setForeground(new Color(0xD1D8E0));
            btnPosts.setForeground(new Color(0xD1D8E0));
        });

        btnUsers.addActionListener(e -> {
            loadUserData();
            showPanel(userPanel);
            activeIndicator.setLocation(0, 175);
    
            btnUsers.setForeground(Main.ACCENT_COLOR);
            btnDash.setForeground(new Color(0xD1D8E0));
            btnPosts.setForeground(new Color(0xD1D8E0));
        });

        btnPosts.addActionListener(e -> {
            refreshPostTable();
            showPanel(postPanel);
            activeIndicator.setLocation(0, 225);
    
            btnPosts.setForeground(Main.ACCENT_COLOR);
            btnDash.setForeground(new Color(0xD1D8E0));
            btnUsers.setForeground(new Color(0xD1D8E0));
        });

        // Hover Effects for Logout
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
            // Lighten the background slightly or change text color
            btnLogout.setContentAreaFilled(true); 
            btnLogout.setBackground(new Color(0x3E444A)); // A slightly lighter dark shade
            btnLogout.setForeground(new Color(0xE74C3C)); // Highlight text in your blue accent
        }

        public void mouseExited(java.awt.event.MouseEvent e) {
            // Return to original "Slate" look
            btnLogout.setContentAreaFilled(false);
            btnLogout.setForeground(new Color(0xD1D8E0));
        }
    });

        btnLogout.addActionListener(e -> logout());

        addWindowControls();
    }

    // Helper to switch the main content area to show the selected panel
    private void showPanel(JPanel panel) {
        contentArea.removeAll();
        panel.setBounds(0, 0, 950, 800);
        contentArea.add(panel);
    
        // Safety check to ensure the UI refreshes
        contentArea.repaint();
        contentArea.revalidate();
    }

    // DASHBOARD MANAGEMENT
    private JPanel createDashboardManagement() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Main.BG_COLOR);

        JLabel lblTitle = new JLabel("System Analytics");
        lblTitle.setFont(new Font("Helvetica", Font.BOLD, 28));
        lblTitle.setBounds(30, 30, 500, 40);
        panel.add(lblTitle);

        int totalUsers = getTotalCount("users");
        int totalPosts = getTotalCount("portfolios");

        JPanel cardUsers = createStatCard("Total Registered Users", String.valueOf(totalUsers), 30, 90, new Color(0x3498DB), true);
        JPanel cardPosts = createStatCard("Total Portfolios", String.valueOf(totalPosts), 330, 90, new Color(0x2ECC71), false);
    
        panel.add(cardUsers);
        panel.add(cardPosts);

        JPanel graphBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 1. Fetch REAL data from DB
                int[] activity = getMonthlyActivityData();
                String[] monthNames = new String[6];
                
                // Get current month to label backwards
                java.time.LocalDate now = java.time.LocalDate.now();
                for (int i = 0; i < 6; i++) {
                    // Subtract months to get the name for each bar
                    java.time.Month m = now.minusMonths(5 - i).getMonth();
                    // Convert to a 3-letter string (e.g., "JAN")
                    monthNames[i] = m.name().substring(0, 3);
                }

                // 2. Draw Axis
                g2.setColor(Color.GRAY);
                g2.drawLine(50, 230, 450, 230); // X-Axis
                g2.drawLine(50, 40, 50, 230);   // Y-Axis

                // 3. Draw Bars based on real counts
                for (int i = 0; i < activity.length; i++) {
                    // Scale bar height (9 * 15 = 135 pixels tall)
                    int barHeight = Math.min(activity[i] * 15, 170); 
                
                    g2.setColor(Main.ACCENT_COLOR); // Use a consistent color for bars
                    g2.fillRect(70 + (i * 60), 230 - barHeight, 40, barHeight);
                
                    // Labels - Adjusted Y to 250 to keep them clear of the axis
                    g2.setColor(Main.TEXT_COLOR);
                    g2.setFont(new Font("Helvetica", Font.BOLD, 12));
                    g2.drawString(monthNames[i], 75 + (i * 60), 250);
                
                    // Count above bar
                    g2.setFont(new Font("Helvetica", Font.PLAIN, 11));
                    g2.drawString(String.valueOf(activity[i]), 82 + (i * 60), 220 - barHeight);
                }
            }
        };
        graphBox.setBounds(30, 220, 500, 280);
        graphBox.setBackground(Color.WHITE);
        graphBox.setBorder(BorderFactory.createTitledBorder("Monthly Portfolio Activity"));
        panel.add(graphBox);

        return panel;
    }

    // Helper to initialize the labels correctly
    private JPanel createStatCard(String title, String value, int x, int y, Color accent, boolean isUserCard) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 230, 100);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, accent));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Helvetica", Font.PLAIN, 12));
        lblT.setForeground(Color.GRAY);
        lblT.setBounds(15, 15, 200, 20);
        card.add(lblT);

        JLabel lblV = new JLabel(value);
        lblV.setFont(new Font("Helvetica", Font.BOLD, 32));
        lblV.setBounds(15, 40, 200, 40);
        card.add(lblV);

        // Link the labels to our class variables so updateSummaryCounts can find them
        if (isUserCard) lblTotalUsers = lblV;
        else lblTotalPortfolios = lblV;

        return card;
    }

    // Helper to refresh the counts on the dashboard cards by fetching the latest totals from the database
    public void updateSummaryCounts() {
        // Fetch direct from DB and update the UI labels immediately
        lblTotalPortfolios.setText(String.valueOf(getTotalCount("portfolios")));
        lblTotalUsers.setText(String.valueOf(getTotalCount("users")));
        dashPanel.repaint(); // Force graph to redraw too
    }

    // Generic helper to get total count of records from any specified table, used for both users and portfolios
    private int getTotalCount(String tableName) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT COUNT(*) FROM " + tableName;
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    //  MANAGE POSTS 
    private JPanel createPostManager() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Main.BG_COLOR);

        String[] cols = {"ID", "Project Title", "Owner", "Status", "Date Added"};
        postModel = new DefaultTableModel(cols, 0); 
        postTable = new JTable(postModel);          // Use the global variable

        JLabel lblTitle = new JLabel("Portfolio Moderation");
        lblTitle.setFont(new Font("Helvetica", Font.BOLD, 28));
        lblTitle.setBounds(30, 40, 500, 40);
        panel.add(lblTitle);

        // --- View/Preview Button ---
        JButton btnPreview = new JButton("VIEW PREVIEW");
        btnPreview.setBounds(350, 530, 150, 40);
        btnPreview.setBackground(new Color(0x34495E));
        btnPreview.setFocusPainted(false);
        btnPreview.setBorderPainted(false);
        btnPreview.setForeground(Color.WHITE);
        btnPreview.addActionListener(e -> showPostPreview());
        panel.add(btnPreview);

        JLabel lblSearch = new JLabel("Search Projects:");
        lblSearch.setBounds(30, 85, 200, 20);
        panel.add(lblSearch);

        // Inside createPostManager()
        JButton btnUpdatePost = new JButton("UPDATE POST");
        btnUpdatePost.setBounds(190, 530, 150, 40);
        btnUpdatePost.setBackground(new Color(0x2ECC71));
        btnUpdatePost.setForeground(Color.WHITE);
        btnUpdatePost.setFocusPainted(false);
        btnUpdatePost.setBorderPainted(false);
        btnUpdatePost.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnUpdatePost.addActionListener(e -> updateSelectedPost());
        panel.add(btnUpdatePost);

        JTextField txtSearch = new JTextField();
        txtSearch.setBounds(30, 110, 400, 35);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(txtSearch);

        postTable.setRowHeight(30);
        
        // Live Search Sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(postModel);
        postTable.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2)); // Search Title and Owner
            }
        });

        refreshPostTable();

        postTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        postTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Project Title (Wide)
        postTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Owner
        postTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Status (Small)
        postTable.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane sp = new JScrollPane(postTable);
        sp.setBounds(30, 160, 880, 350);
        panel.add(sp);

        // --- 3. Delete Action ---
        JButton btnDeletePost = new JButton("DELETE POST");
        btnDeletePost.setBounds(30, 530, 150, 40);
        btnDeletePost.setBackground(new Color(0xE74C3C));
        btnDeletePost.setForeground(Color.WHITE);
        btnDeletePost.setFocusPainted(false);
        btnDeletePost.setBorderPainted(false);
        btnDeletePost.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDeletePost.addActionListener(e -> {
            int selectedRow = postTable.getSelectedRow();
            if (selectedRow == -1) {
                CustomDialog.show(this, "Please select a project to delete.", false);
               return;
            }

           // Convert view index to model index (important when searching!)
            int modelRow = postTable.convertRowIndexToModel(selectedRow);
            int postId = Integer.parseInt(postModel.getValueAt(modelRow, 0).toString());

            boolean confirm = CustomDialog.showConfirm(this, "Permanently delete this project?");
            if (confirm) {
                try (Connection conn = Database.getConnection()) {
                    String sql = "DELETE FROM projects WHERE id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, postId);
                    pst.executeUpdate();
                    
                    postModel.removeRow(modelRow);
                    CustomDialog.show(this, "Project removed.", true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    CustomDialog.show(this, "Database error!", false);
                }
            }
        });
        panel.add(btnDeletePost);

        return panel;
    }

    // MANAGE USERS
    private JPanel createUserManager() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Main.BG_COLOR);

        JLabel lblTitle = new JLabel("User Management");
        lblTitle.setFont(new Font("Helvetica", Font.BOLD, 28));
        lblTitle.setBounds(30, 40, 500, 40);
        panel.add(lblTitle);

        // --- Search Field ---
        JLabel lblSearch = new JLabel("Search Users:");
        lblSearch.setBounds(30, 85, 200, 20);
        panel.add(lblSearch);

        // Inside createUserManager()
        JButton btnUpdate = new JButton("UPDATE USER");
        btnUpdate.setBounds(190, 530, 150, 40); // Placed next to delete button
        btnUpdate.setBackground(new Color(0x3498DB));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setBorderPainted(false);
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnUpdate.addActionListener(e -> updateSelectedUser()); // We will create this method
        panel.add(btnUpdate);

        JTextField txtSearch = new JTextField();
        txtSearch.setBounds(30, 110, 400, 35);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(txtSearch);

        // --- Table Setup ---
        String[] columns = {"ID", "Name", "Student ID", "Role", "Email"};
        userModel = new DefaultTableModel(columns, 0); // Use global userModel
        userTable = new JTable(userModel);            // Use global userTable
        userTable.setRowHeight(30);

        // Add Live Filter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(userModel);
        userTable.setRowSorter(sorter);
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2));
            }
        });

        // Fetch initial data
        loadUserData(); 

        JScrollPane sp = new JScrollPane(userTable);
        sp.setBounds(30, 160, 880, 350); 
        panel.add(sp);

        // --- Delete Button ---
        JButton btnDelete = new JButton("DELETE USER");
        btnDelete.setBounds(30, 530, 150, 40);
        btnDelete.setBackground(new Color(0xE74C3C));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setBorderPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(e -> deleteSelectedUser());
        panel.add(btnDelete);

        return panel;
    }

    // Helper to update the selected user's information in the database
    private void updateSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            CustomDialog.show(this, "Select a user to update.", false);
            return;
        }

        int modelRow = userTable.convertRowIndexToModel(selectedRow);
        String userId = userModel.getValueAt(modelRow, 0).toString();
        
        // FETCH DATA DIRECT FROM DB
        String currentUsername = "", currentFullName = "", currentStudentId = "", currentEmail = "";
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT username, full_name, student_id, email FROM users WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                currentUsername = rs.getString("username");
                currentFullName = rs.getString("full_name");
                currentStudentId = rs.getString("student_id");
                currentEmail = rs.getString("email");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // UNDECORATED POPUP WINDOW
        // Matches your minimalist theme and hides the default Windows title bar
        JDialog editUserDialog = new JDialog(this, true);
        editUserDialog.setUndecorated(true);
        editUserDialog.setSize(450, 520);
        editUserDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(0x636E72), 1)); // Slate border

        // CUSTOM CLOSE BUTTON
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(400, 5, 45, 30);
        closeBtn.setFont(new Font("Helvetica", Font.BOLD, 16));
        closeBtn.setFocusable(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        closeBtn.setForeground(Main.TEXT_COLOR);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> editUserDialog.dispose());
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeBtn.setContentAreaFilled(true);
                closeBtn.setBackground(new Color(0xE74C3C));
                closeBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeBtn.setContentAreaFilled(false);
                closeBtn.setForeground(Main.TEXT_COLOR);
            }
        });
        mainPanel.add(closeBtn);

        // REFINED FORM LAYOUT
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBounds(50, 50, 350, 420);

        JTextField txtUser = new JTextField(currentUsername);
        JTextField txtName = new JTextField(currentFullName);
        JTextField txtId = new JTextField(currentStudentId);
        JTextField txtEmail = new JTextField(currentEmail);

        for (JTextField f : new JTextField[]{txtUser, txtName, txtId, txtEmail}) {
            f.setMaximumSize(new Dimension(350, 45));
            f.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xCED4DA)), 
                f == txtUser ? "Username" : (f == txtName ? "Full Name" : (f == txtId ? "Student ID" : "Email")),
                0, 0, new Font("Helvetica", Font.BOLD, 10), new Color(0xADBCCE)));
            form.add(f);
            form.add(Box.createVerticalStrut(10));
        }

        JButton btnSave = new JButton("UPDATE USER INFO");
        btnSave.setMaximumSize(new Dimension(350, 45));
        btnSave.setBackground(Main.ACCENT_COLOR);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Helvetica", Font.BOLD, 12));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // UPDATED SQL QUERY
        btnSave.addActionListener(e -> {
            try (Connection conn = Database.getConnection()) {
                String updateSql = "UPDATE users SET username=?, full_name=?, student_id=?, email=? WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(updateSql);
                pst.setString(1, txtUser.getText());
                pst.setString(2, txtName.getText());
                pst.setString(3, txtId.getText());
                pst.setString(4, txtEmail.getText());
                pst.setString(5, userId);
                pst.executeUpdate();
            
                CustomDialog.show(this, "User information updated!", true);
                loadUserData(); // Refresh the table
                editUserDialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                CustomDialog.show(this, "Error: Check if username is already taken.", false);
            }
        });

        form.add(Box.createVerticalStrut(10));
        form.add(btnSave);
        mainPanel.add(form);
        editUserDialog.add(mainPanel);
        editUserDialog.setVisible(true);
    }   

    // Helper to update the selected post's information in the database
    private void updateSelectedPost() { 
        Color idleColor = Main.BG_COLOR; 
        Color closeHover = new Color(0xE74C3C);

        int selectedRow = postTable.getSelectedRow();
        if (selectedRow == -1) {
            CustomDialog.show(this, "Select a post to update.", false);
            return;
        }

        int modelRow = postTable.convertRowIndexToModel(selectedRow);
        int projectId = Integer.parseInt(postModel.getValueAt(modelRow, 0).toString());

        String currentName = "", currentTags = "", currentDesc = "";
        try (Connection conn = Database.getConnection()) {
            String fetchSql = "SELECT project_name, description, tags FROM projects WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(fetchSql);
            pst.setInt(1, projectId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                currentName = rs.getString("project_name");
                currentDesc = rs.getString("description");
                currentTags = rs.getString("tags");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Create Styled Undecorated Edit Dialog
        JDialog editDialog = new JDialog(this, true);
        editDialog.setUndecorated(true);
        editDialog.setSize(450, 550);
        editDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x636E72), 1),
            BorderFactory.createLineBorder(Color.WHITE, 10)
        ));

        JPanel titleBar = new JPanel(null);
        titleBar.setBounds(0, 0, 450, 40);
        titleBar.setBackground(Color.WHITE);
        mainPanel.add(titleBar);

        // Custom Close Button
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(400, 5, 45, 30);
        closeBtn.setFont(new Font("Helvetica", Font.BOLD, 16));
        closeBtn.setBackground(idleColor);
        closeBtn.setForeground(Main.TEXT_COLOR);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusable(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeBtn.addActionListener(e -> editDialog.dispose());

        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeBtn.setContentAreaFilled(true);
                closeBtn.setBackground(closeHover);
                closeBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeBtn.setContentAreaFilled(false);
                closeBtn.setBackground(idleColor);
                closeBtn.setForeground(Main.TEXT_COLOR);
            }
        });
        titleBar.add(closeBtn);

        // Form Content (Edit Inputs)
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBounds(25, 50, 400, 500);

        JLabel lblHeader = new JLabel("PROJECT SETTINGS");
        lblHeader.setFont(new Font("Helvetica", Font.BOLD, 14));
        lblHeader.setForeground(new Color(0x636E72));
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtName = new JTextField(currentName);
        JTextField txtTags = new JTextField(currentTags != null ? currentTags : "");
        txtName.setMaximumSize(new Dimension(350, 45));
        txtTags.setMaximumSize(new Dimension(350, 45));

        JTextArea txtDesc = new JTextArea(currentDesc != null ? currentDesc : "");
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setFont(new Font("Helvetica", Font.PLAIN, 13));

        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setMaximumSize(new Dimension(350, 150));
        scrollDesc.setBorder(BorderFactory.createLineBorder(Main.BG_COLOR));

        // Styling the inputs using your theme
        for (JComponent c : new JComponent[]{txtName, txtTags, scrollDesc}) {
            c.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0xCED4DA)),
                c == txtName ? "Title" : (c == txtTags ? "Tags" : "Description"),
                0, 0, new Font("Helvetica", Font.BOLD, 10), new Color(0xADBCCE)
            ));
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        // SAVE BUTTON
        JButton btnSave = new JButton("UPDATE PROJECT");
        btnSave.setMaximumSize(new Dimension(350, 45));
        btnSave.setBackground(Main.ACCENT_COLOR);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Helvetica", Font.BOLD, 12));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // The Save Action (Updates both tables)
        btnSave.addActionListener(e -> {
            try (Connection conn = Database.getConnection()) {
                conn.setAutoCommit(false); 
            
                // Update the 'projects' table (Name, Tags, Description)
                String sqlProj = "UPDATE projects SET project_name=?, description=?, tags=? WHERE id=?";
                PreparedStatement pst1 = conn.prepareStatement(sqlProj);
                pst1.setString(1, txtName.getText());
                pst1.setString(2, txtDesc.getText());
                pst1.setString(3, txtTags.getText());
                pst1.setInt(4, projectId);
                pst1.executeUpdate();

               // Update the 'portfolios' table to keep the name in sync
                String sqlPort = "UPDATE portfolios SET project_name=? WHERE project_id=?";
                PreparedStatement pst2 = conn.prepareStatement(sqlPort);
                pst2.setString(1, txtName.getText());
                pst2.setInt(2, projectId);
                pst2.executeUpdate();

                conn.commit();
                CustomDialog.show(this, "Project updated successfully!", true);
                refreshPostTable();
                editDialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                CustomDialog.show(this, "Error updating database.", false);
            }
        });

        form.add(lblHeader); form.add(Box.createVerticalStrut(25));
        form.add(txtName); form.add(Box.createVerticalStrut(15));
        form.add(txtTags); form.add(Box.createVerticalStrut(15));
        form.add(scrollDesc); form.add(Box.createVerticalStrut(25));
        form.add(btnSave);

        mainPanel.add(form);
        editDialog.add(mainPanel);
        editDialog.setVisible(true);
    }
    
    // Helper to show a preview of the selected post's description and image in a dialog
    private void showPostPreview() {
        int selectedRow = postTable.getSelectedRow();
        if (selectedRow == -1) {
            CustomDialog.show(this, "Select a post to preview.", false);
            return;
        }

        int modelRow = postTable.convertRowIndexToModel(selectedRow);
        int postId = Integer.parseInt(postModel.getValueAt(modelRow, 0).toString());

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT p.project_name, pr.description, p.file_data, pr.tags " +
                        "FROM portfolios p " +
                        "JOIN projects pr ON p.project_id = pr.id " +
                        "WHERE p.id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, postId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JDialog previewDialog = new JDialog(this, true);
                previewDialog.setUndecorated(true);
                previewDialog.setSize(500, 600);
                previewDialog.setLocationRelativeTo(this);

                // Main Container 
                JPanel mainPanel = new JPanel(null);
                mainPanel.setBackground(Color.WHITE);
                mainPanel.setBorder(BorderFactory.createLineBorder(Main.BG_COLOR, 2));

                // Custom Close Button
                Color idleColor = Color.WHITE;
                Color closeHover = new Color(0xE74C3C);
                
                JButton closeBtn = new JButton("X");
                closeBtn.setBounds(445, 10, 45, 30);
                closeBtn.setBackground(idleColor);
                closeBtn.setForeground(Main.TEXT_COLOR);
                closeBtn.setBorderPainted(false);
                closeBtn.setFocusable(false);
                closeBtn.setFont(new Font("Helvetica", Font.BOLD, 14));
                closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                // Logic: Dispose dialog instead of exiting system
                closeBtn.addActionListener(e -> previewDialog.dispose());
                
                closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        closeBtn.setBackground(closeHover);
                        closeBtn.setForeground(Color.WHITE);
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        closeBtn.setBackground(idleColor);
                        closeBtn.setForeground(Main.TEXT_COLOR);
                    }
                });
                mainPanel.add(closeBtn);

                // Content Area
                JPanel content = new JPanel();
                content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
                content.setBackground(Color.WHITE);
                content.setBounds(25, 50, 450, 520);

                // Project Image
                byte[] imgBytes = rs.getBytes("file_data");
                JLabel imgLabel = new JLabel("No Image Provided", SwingConstants.CENTER);
                imgLabel.setPreferredSize(new Dimension(400, 240));
                imgLabel.setMaximumSize(new Dimension(400, 240));
                imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                imgLabel.setOpaque(true);
                imgLabel.setBackground(Main.BG_COLOR);
                imgLabel.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 1));

                if (imgBytes != null && imgBytes.length > 0) {
                    ImageIcon icon = new ImageIcon(imgBytes);
                    Image img = icon.getImage().getScaledInstance(400, 240, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(img));
                    imgLabel.setText(""); 
                }

                // Text Info
                JLabel lblName = new JLabel(rs.getString("project_name").toUpperCase());
                lblName.setFont(new Font("Helvetica", Font.BOLD, 18));
                lblName.setForeground(Main.TEXT_COLOR);
                lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

                String tags = rs.getString("tags");
                JLabel lblTags = new JLabel(tags != null ? tags.replace(",", "  •  ") : "NO TAGS");
                lblTags.setFont(new Font("Helvetica", Font.BOLD, 11));
                lblTags.setForeground(Main.ACCENT_COLOR);
                lblTags.setAlignmentX(Component.CENTER_ALIGNMENT);

                JTextArea txtDesc = new JTextArea(rs.getString("description"));
                txtDesc.setFont(new Font("Helvetica", Font.PLAIN, 14));
                txtDesc.setForeground(new Color(0x636E72));
                txtDesc.setLineWrap(true);
                txtDesc.setWrapStyleWord(true);
                txtDesc.setEditable(false);
                txtDesc.setOpaque(false);

                JScrollPane scroll = new JScrollPane(txtDesc);
                scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Main.BG_COLOR));
                scroll.setPreferredSize(new Dimension(400, 100));
                scroll.setAlignmentX(Component.CENTER_ALIGNMENT);

               // Assemble
                content.add(imgLabel);
                content.add(Box.createVerticalStrut(20));
                content.add(lblName);
                content.add(Box.createVerticalStrut(5));
                content.add(lblTags);
                content.add(Box.createVerticalStrut(15));
                content.add(scroll);

                mainPanel.add(content);
                previewDialog.add(mainPanel);
                previewDialog.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog.show(this, "Error loading preview data.", false);
        }
    }

    // Helper for Sidebar Buttons
    private JButton createSidebarBtn(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(0, y, 250, 50);
        btn.setForeground(new Color(0xD1D8E0));
        btn.setFont(new Font("Helvetica", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setVerticalAlignment(SwingConstants.CENTER);
    
        btn.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
    
        return btn;
    }

    // LOGOUT FUNCTION
    private void logout() {
        boolean confirm = CustomDialog.showConfirm(this, "Are you sure you want to logout?");

        if (confirm) {
            // Show success message
            CustomDialog.show(this, "Logged out successfully!", true);

            // Open Login and close Admin Dashboard
            new LoginForm().setVisible(true);
            this.dispose();
        }
    }

    // Helper to refresh the posts table with the latest data from the database
    private void refreshPostTable() {
       postModel.setRowCount(0);

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT p.id, p.project_name, u.username, pr.upload_date " +
                     "FROM projects p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "JOIN portfolios pr ON p.id = pr.project_id " +
                     "ORDER BY pr.upload_date DESC";
            
            ResultSet rs = conn.createStatement().executeQuery(sql);
        
            while(rs.next()){
                postModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("project_name"),
                    rs.getString("username"),
                    "Active",
                    rs.getString("upload_date")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
                CustomDialog.show(this, "Failed to load projects.", false);
        }
    }

    // Helper to load user data into the table
    private void loadUserData() {
        // 1. Clear the table first so we don't duplicate rows
        if (userModel == null) return;
        userModel.setRowCount(0); 

        try (Connection conn = Database.getConnection()) {
            // 2. The SQL query to get your registered students
            String sql = "SELECT id, full_name, student_id, role, email FROM users";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            // 3. Loop through the results and add them to the table
            while (rs.next()) {
                Object[] row = {
                    rs.getString("id"),
                    rs.getString("full_name"),
                    rs.getString("student_id"),
                    rs.getString("role"),
                    rs.getString("email")
                };
               userModel.addRow(row);
            }
        } catch (Exception e) {
           e.printStackTrace();
            // Just in case there's an error, show a message
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    // Helper to delete the selected user from the database and refresh the table
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
           CustomDialog.show(this, "Please select a user to delete!", false);
            return;
        }

        // Convert view index to model index (important for sorted/filtered tables)
        int modelRow = userTable.convertRowIndexToModel(selectedRow);
       String userId = userModel.getValueAt(modelRow, 0).toString();

        if (CustomDialog.showConfirm(this, "Are you sure you want to delete this user?")) {
           try (Connection conn = Database.getConnection()) {
                String sql = "DELETE FROM users WHERE id = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, userId);
                pst.executeUpdate();

                userModel.removeRow(modelRow);
                CustomDialog.show(this, "User deleted successfully.", true);
            } catch (Exception e) {
                e.printStackTrace();
                CustomDialog.show(this, "Error deleting user.", false);
           }
        }
    }

    // Helper to get monthly activity data for the graph
    private int[] getMonthlyActivityData() {
        int[] monthlyCounts = new int[6]; // To store counts for the last 6 months
        try (Connection conn = Database.getConnection()) {
            // SQL query to count uploads per month for the current year
            String sql = "SELECT MONTH(upload_date) as m, COUNT(*) as c FROM portfolios " +
                         "WHERE YEAR(upload_date) = YEAR(CURDATE()) " +
                         "GROUP BY MONTH(upload_date) ORDER BY m DESC LIMIT 6";
        
            ResultSet rs = conn.createStatement().executeQuery(sql);
            int i = 5; // Start from the right-most bar
            while (rs.next() && i >= 0) {
                monthlyCounts[i] = rs.getInt("c");
                i--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monthlyCounts;
    }

    // Helper to get total portfolios count for the dashboard card
    public static int getTotalPortfolios() {
        String sql = "SELECT COUNT(*) FROM portfolios";
        // Add 'Database.' before getConnection()
        try (Connection conn = Database.getConnection(); 
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper to get total users count for the dashboard card
    private void addWindowControls() {
        JLayeredPane lp = this.getLayeredPane();

        Color idleColor = Main.BG_COLOR; 
        Color minHover = new Color(0xD1D8E0); 
        Color closeHover = new Color(0xE74C3C);

        // -- CLOSE BUTTON --
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(1150, 5, 45, 30);
        closeBtn.setBackground(idleColor);
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusable(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));
    
        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeBtn.setBackground(closeHover);
                closeBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeBtn.setBackground(idleColor);
                closeBtn.setForeground(Color.BLACK);
            }
        });

        // -- MINIMIZE BUTTON --
        JButton minBtn = new JButton("-");
        minBtn.setBounds(1105, 5, 45, 30);
        minBtn.setBackground(idleColor);
        minBtn.setForeground(Color.BLACK);
        minBtn.setBorderPainted(false);
        minBtn.setFocusable(false);
        minBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        minBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        
        minBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                minBtn.setBackground(minHover);
                minBtn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                minBtn.setBackground(idleColor);
                minBtn.setForeground(Color.BLACK);
            }
        });

        lp.add(closeBtn, JLayeredPane.PALETTE_LAYER);
        lp.add(minBtn, JLayeredPane.PALETTE_LAYER);
    }
}