package gui;

import db.Database;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import main.Main;

public class DashboardPanel extends JPanel {
    private int userId;
    private JLabel lblWelcome, lblCourse, lblTotalProjects;
    private JPanel recentProjectsContainer;

    public DashboardPanel(int userId, String studentName, String courseYear) {
        this.userId = userId;
        setLayout(null);
        setBackground(Main.BG_COLOR);

        // Header 
        lblWelcome = new JLabel("Welcome back, " + studentName + "!");
        lblWelcome.setBounds(40, 40, 500, 30);
        lblWelcome.setFont(new Font("Helvetica", Font.PLAIN, 18));
        lblWelcome.setForeground(Main.TEXT_COLOR);
        add(lblWelcome);

        // Course Year
        lblCourse = new JLabel(courseYear != null ? courseYear : "No Course Set");
        lblCourse.setBounds(40, 65, 500, 20);
        lblCourse.setFont(new Font("Helvetica", Font.ITALIC, 14));
        lblCourse.setForeground(Main.TEXT_COLOR);
        add(lblCourse);

        JLabel title = new JLabel("Dashboard Overview");
        title.setBounds(40, 90, 500, 50);
        title.setFont(new Font("Helvetica", Font.BOLD, 28));
        title.setForeground(Main.TEXT_COLOR);
        add(title);

        // --- STAT CARDS ---
        add(createStatCard("Total Projects", 40, 160));

        JPanel activityGraph = createActivityGraph();
        activityGraph.setBounds(260, 160, 690, 200);
        add(activityGraph);

        // --- RECENT PROJECTS (MAXIMIZED SPACE) ---
        JLabel recentTitle = new JLabel("RECENT PROJECTS");
        recentTitle.setBounds(40, 380, 300, 20);
        recentTitle.setFont(new Font("Helvetica", Font.BOLD, 14));
        recentTitle.setForeground(Main.TEXT_COLOR);
        add(recentTitle);

        recentProjectsContainer = new JPanel();
        recentProjectsContainer.setLayout(null);
        recentProjectsContainer.setBackground(Main.BG_COLOR);
        recentProjectsContainer.setBounds(40, 410, 930, 300); // Maximized width
        add(recentProjectsContainer);

        refreshData();
    }

    public void refreshData() {
        // 1. Sync User Name/Course from DB
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT full_name, course_year FROM users WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                lblWelcome.setText("Welcome back, " + rs.getString("full_name") + "!");
                lblCourse.setText(rs.getString("course_year"));
            }

            // 2. Sync Project Count
            String countSql = "SELECT COUNT(*) FROM portfolios WHERE user_id = ?";
            PreparedStatement pst2 = conn.prepareStatement(countSql);
            pst2.setInt(1, userId);
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                int count = rs2.getInt(1);
                lblTotalProjects.setText(String.valueOf(count));
            }
        } catch (Exception e) { e.printStackTrace(); }

        loadRecentProjects();
    }

    private void loadRecentProjects() {
        recentProjectsContainer.removeAll();
        String sql = "SELECT project_name, upload_date FROM portfolios WHERE user_id = ? ORDER BY upload_date DESC LIMIT 3";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            int y = 0;
            while (rs.next()) {
                JPanel item = new JPanel(null);
                item.setBounds(0, y, 930, 60);
                item.setBackground(Color.WHITE);
                item.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0)));

                JLabel name = new JLabel(rs.getString("project_name"));
                name.setBounds(20, 15, 500, 30);
                name.setFont(new Font("Helvetica", Font.BOLD, 15));
                item.add(name);

                JLabel date = new JLabel("Added: " + rs.getString("upload_date").substring(0, 10));
                date.setBounds(750, 15, 150, 30);
                date.setHorizontalAlignment(SwingConstants.RIGHT);
                item.add(date);

                recentProjectsContainer.add(item);
                y += 70;
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        recentProjectsContainer.revalidate();
        recentProjectsContainer.repaint();
    }

    private int[] getPersonalMonthlyData(int limit) {
        int[] data = new int[limit];
        String sql = "SELECT MONTH(upload_date) as m, COUNT(*) as c FROM portfolios " +
                 "WHERE user_id = ? GROUP BY MONTH(upload_date) ORDER BY m DESC LIMIT " + limit;

        try (Connection conn = Database.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
        
            while (rs.next()) {
                int monthValue = rs.getInt("m"); // 1 for Jan, 2 for Feb, etc.
                int count = rs.getInt("c");
                int index = -1;

                // Map the month to your OCT-JUL array index
                if (monthValue >= 10) index = monthValue - 10; // Oct(0), Nov(1), Dec(2)
                else if (monthValue <= 7) index = monthValue + 2; // Jan(3), Feb(4)... Jul(9)

               if (index >= 0 && index < limit) {
                    data[index] = count;
               }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    private JPanel createActivityGraph() {
        JPanel graphBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
               super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
                int[] activity = getPersonalMonthlyData(10); 
                String[] months = {"OCT", "NOV", "DEC", "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL"};

                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(40, 160, 660, 160);

                for (int i = 0; i < activity.length; i++) {
                    int barHeight = Math.min(activity[i] * 20, 130); 
                    g2.setColor(Main.ACCENT_COLOR);
                    // Spacing: 62px between bars to fill the 700px panel width
                    g2.fillRoundRect(60 + (i * 62), 160 - barHeight, 30, barHeight, 10, 10);
    
                    // --- UX FIX: Number above the bar ---
                                    if (activity[i] > 0) {
                        g2.setColor(Main.TEXT_COLOR);
                        g2.setFont(new Font("Helvetica", Font.PLAIN, 11));
                        g2.drawString(String.valueOf(activity[i]), 68 + (i * 62), 155 - barHeight);
                    }

                    // Month Labels
                    g2.setColor(Main.TEXT_COLOR);
                    g2.setFont(new Font("Helvetica", Font.BOLD, 10));
                    g2.drawString(months[i], 63 + (i * 62), 175);
                }
            }
        };
        graphBox.setBounds(260, 160, 700, 200);
        graphBox.setBackground(Color.WHITE);
        graphBox.setBorder(BorderFactory.createTitledBorder("Academic Year Upload Activity"));
        return graphBox;
    }

    private JPanel createStatCard(String title, int x, int y) {
        // Override paintComponent to draw the rounded shape and border
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint the white background with rounded corners
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                
                // Paint the light gray border with rounded corners
                g2.setColor(new Color(0xD1D8E0));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };

        card.setBounds(x, y, 200, 100);
        card.setBackground(Color.WHITE);
        card.setOpaque(false); // Required to see the rounded effect
        
        // Remove the old square border
        card.setBorder(null);

        JLabel t = new JLabel(title);
        t.setBounds(15, 15, 170, 20);
        t.setFont(new Font("Helvetica", Font.BOLD, 12));
        card.add(t);

        lblTotalProjects = new JLabel("0");
        lblTotalProjects.setBounds(15, 40, 170, 45);
        lblTotalProjects.setFont(new Font("Helvetica", Font.BOLD, 36));
        lblTotalProjects.setForeground(Main.ACCENT_COLOR);
        card.add(lblTotalProjects);
        
        return card;
    }
}