package gui;

import java.awt.*;
import javax.swing.*;
import main.Main;

public class DashboardPanel extends JPanel {
    private JLabel welcomeUser;
    private JLabel welcomeCourse;

    public DashboardPanel(String studentName, String courseYear) {
        setLayout(null);
        setBackground(Main.BG_COLOR);

        welcomeUser = new JLabel("Welcome back, " + studentName + "!");
        welcomeUser.setBounds(40, 60, 400, 30);
        welcomeUser.setFont(new Font("Helvetica", Font.PLAIN, 18));
        welcomeUser.setForeground(Main.TEXT_COLOR);
        add(welcomeUser);

        welcomeCourse = new JLabel(courseYear != null ? courseYear : "No Course Set");
        welcomeCourse.setBounds(40, 90, 400, 20);
        welcomeCourse.setFont(new Font("Helvetica", Font.ITALIC, 14));
        welcomeCourse.setForeground(Main.TEXT_COLOR);
        add(welcomeCourse);

        JLabel welcomeLabel = new JLabel("Dashboard Overview");
        welcomeLabel.setBounds(40, 100, 400, 50);
        welcomeLabel.setFont(new Font("Helvetica", Font.BOLD, 28));
        welcomeLabel.setForeground(Main.TEXT_COLOR);
        add(welcomeLabel);

        add(createStatCard("Total Projects", "12", 40, 160));
        add(createStatCard("Skills", "8", 260, 160));
        add(createProgressCard("Overall Progress", 75, 40, 280));
    }

    public void refreshDashboardInfo(String newName, String newCourse) {
        welcomeUser.setText("Welcome back, " + newName + "!");
        welcomeCourse.setText(newCourse);
    }

    public void updateWelcomeMessage(String newName) {
        welcomeUser.setText("Welcome back, " + newName + "!");
    }

    private JPanel createProgressCard(String title, int value, int x, int y) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(x, y, 420, 110); 
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 1));

        JLabel t = new JLabel(title.toUpperCase());
        t.setBounds(15, 15, 300, 20);
        t.setForeground(Main.TEXT_COLOR);
        t.setFont(new Font("Helvetica", Font.BOLD, 12));
        card.add(t);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(value);
        bar.setBounds(15, 40, 390, 30);


        bar.setForeground(new Color(0x575FCF)); // Your Indigo Accent
        bar.setBackground(new Color(0xF1F2F6)); // Light Grey track 

        bar.setBorderPainted(false);
        bar.setStringPainted(true); 
        bar.setFont(new Font("Helvetica", Font.BOLD, 12));
        card.add(bar);
        return card;
    }

    private JPanel createStatCard(String title, String value, int x, int y) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBounds(x, y, 200, 100);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 1));

        JLabel t = new JLabel(title);
        t.setBounds(10, 10, 180, 20);
        t.setForeground(new Color(0x636E72));
        t.setFont(new Font("Helvetica", Font.BOLD, 11));

        JLabel v = new JLabel(value);
        v.setBounds(10, 40, 180, 40);
        v.setForeground(Main.ACCENT_COLOR);
        v.setFont(new Font("Helvetica", Font.BOLD, 32));
        
        card.add(t);
        card.add(v);
        return card;
    }
}