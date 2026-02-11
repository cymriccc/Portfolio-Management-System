package gui;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import main.Main;

public class Register extends JFrame {
    private JTextField txtFirst, txtMiddle, txtLast, txtUsername, txtStudentID, txtEmail;
    private JComboBox<String> cbCourse, cbYear;
    private JPasswordField txtPassword;
    private JButton btnRegister, btnLogin;
    private JComboBox<String> cbSec1, cbSec2, cbSec3;
    private JTextField txtAns1, txtAns2, txtAns3;
    private String[] securityQuestions = {
        "Select a question...",
        "What was the name of your first pet?",
        "What is your mother's maiden name?",
        "What was the name of your elementary school?",
        "In what city were you born?",
        "What is your favorite book?"
    };

    public Register() {
        setTitle("Vantage - Sign Up");
        setSize(600, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setBackground(Main.BG_COLOR);

        int centerX = 100;
        int fieldWidth = 400;

        // Title
        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setBounds(centerX, 30, fieldWidth, 40); 
        title.setFont(new Font("Helvetica", Font.BOLD, 32));
        title.setForeground(Main.TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // First and Last Name
        addLabelAndField("First Name", 100, txtFirst = new JTextField(), "Letters & Dash only", 100, 190);
        addLabelAndField("Last Name", 100, txtLast = new JTextField(), "Letters & Dash only", 310, 190);

        // Row 2: Middle Name & ID
        addLabelAndField("Middle Name", 190, txtMiddle = new JTextField(), "Optional", 100, 190);
        addLabelAndField("Student ID", 190, txtStudentID = new JTextField(), "0000-0000", 310, 190);
        
        // Row 3: Course & Year
        JLabel lblCourse = new JLabel("Course & Year");
        lblCourse.setBounds(100, 275, 200, 20);
        lblCourse.setForeground(Main.TEXT_COLOR);
        add(lblCourse);

        String[] courses = {"BS Accountancy", "BS Business Administration", "BS Tourism Management", "BS Information Technology", "BS Psychology", "BS Nursing", "BS Medical Technology", "Doctor of Optometry", "Dentaly Hygiene IV", "Dental Technology NCIV"};
        String[] years = {"1", "2", "3", "4"};
        
        cbCourse = createStyledComboBox(courses);
        cbYear = createStyledComboBox(years);

        cbCourse.setBounds(100, 305, 290, 45); 
        cbYear.setBounds(400, 305, 100, 45);
        add(cbCourse);
        add(cbYear);

        // Email, Username, and Password 
        addLabelAndField("Email Address", 360, txtEmail = new JTextField(), "user@email.com", 100, 400);
        addLabelAndField("Username", 450, txtUsername = new JTextField(), "3+ chars; alphanumeric, _ or - only", 100, 400);

        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(100, 540, 100, 20);
        lblPass.setForeground(Main.TEXT_COLOR);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(100, 565, 400, 45);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(txtPassword);

        JLabel passGuide = new JLabel("Min 8 chars, 1 Upper, 1 Lower, 1 Number");
        passGuide.setFont(new Font("Helvetica", Font.PLAIN, 10));
        passGuide.setForeground(new Color(0x551212));
        passGuide.setBounds(100, 610, 400, 15);
        add(passGuide);

        // Buttons
        btnRegister = new JButton("SIGN UP");
        btnRegister.setBounds(centerX, 645, fieldWidth, 50);
        btnRegister.setBackground(Main.ACCENT_COLOR);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
            btnRegister.setBackground(Main.ACCENT_COLOR.darker());
        }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
            btnRegister.setBackground(Main.ACCENT_COLOR);
        }
        });
        btnRegister.addActionListener(e -> handleRegistration());
        add(btnRegister);

        btnLogin = new JButton("Already have an account? Login");
        btnLogin.setBounds(centerX, 695, fieldWidth, 30);
        btnLogin.setForeground(Main.TEXT_COLOR);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });
        add(btnLogin);

        addWindowControls();
        revalidate();
        repaint();
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(Color.WHITE);
        combo.setForeground(Main.TEXT_COLOR);
        combo.setFont(new Font("Helvetica", Font.PLAIN, 14));
        combo.setBorder(BorderFactory.createLineBorder(new Color(0xD1D8E0), 1));
        
        // Add Padding to text
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setBorder(new EmptyBorder(5, 10, 5, 10));
                if (isSelected) {
                    l.setBackground(Main.ACCENT_COLOR);
                    l.setForeground(Color.WHITE);
                } else {
                    l.setBackground(Color.WHITE);
                    l.setForeground(Main.TEXT_COLOR);
                }
                return l;
            }
        });

        // Customize Arrow Button
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = super.createArrowButton();
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createEmptyBorder());
                return btn;
            }
        });
        return combo;
    }
    
    private void addLabelAndField(String labelText, int yPos, JTextField field, String guide, int xPos, int width) {
        JLabel label = new JLabel(labelText);
        label.setBounds(xPos, yPos, width, 20);
        label.setForeground(Main.TEXT_COLOR);
        add(label);

        field.setBounds(xPos, yPos + 25, width, 40);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(field);

        JLabel guideLbl = new JLabel(guide);
        guideLbl.setFont(new Font("Helvetica", Font.PLAIN, 10));
        guideLbl.setForeground(new Color(0x551212));
        guideLbl.setBounds(xPos, yPos + 65, width, 15);
        add(guideLbl);
    }

    private void handleRegistration() {
        String first = txtFirst.getText().trim();
        String middle = txtMiddle.getText().trim();
        String last = txtLast.getText().trim();
        String studentID = txtStudentID.getText().trim();
        String email = txtEmail.getText().trim();
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());
        String courseYear = cbCourse.getSelectedItem() + " - " + cbYear.getSelectedItem();
        StringBuilder errors = new StringBuilder();

        try (Connection conn = db.Database.getConnection()) {
            // Check Username
            String checkUser = "SELECT username FROM users WHERE username = ?";
            PreparedStatement stUser = conn.prepareStatement(checkUser);
            stUser.setString(1, user);
            if (stUser.executeQuery().next()) {
                CustomDialog.show(this, "Username is already taken.", false);
                return;
            }

            // Check Email
            String checkEmail = "SELECT email FROM users WHERE email = ?";
            PreparedStatement stEmail = conn.prepareStatement(checkEmail);
            stEmail.setString(1, email);
            if (stEmail.executeQuery().next()) {
                CustomDialog.show(this, "Email is already registered.", false);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        String nameRegex = "^[a-zA-Z-]{2,30}$";
        if (!first.matches(nameRegex)) {
            errors.append("• First name must be 2-30 letters and '-' only.\n");
        }
        if (!middle.isEmpty() && !middle.matches(nameRegex)) {
            errors.append("• Middle name must be 2-30 letters and '-' only if provided.\n");
        }
        if (!last.matches(nameRegex)) {
            errors.append("• Last name must be 2-30 letters and '-' only.\n");
        }
        if (errors.length() > 0) {
        CustomDialog.show(this, errors.toString(), false);
        return;
        }
        if (!studentID.matches("\\d{4}-\\d{4}")) {
            CustomDialog.show(this, "Student ID must be in the format 0000-0000 (e.g., 2025-0000)", false);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            CustomDialog.show(this, "Please enter a valid professional email address!", false);
            return;
        }
        if (!user.matches("^[a-zA-Z0-9_-]{3,}$")) {
            CustomDialog.show(this, "Username must be 3+ characters (no special chars except _ or -).", false);
            return;
        }
        if (!pass.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            CustomDialog.show(this, "Password must have 8 chars, 1 Upper, 1 Lower, and 1 Number.", false);
            return;
        }

        JPanel securityPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        securityPanel.setPreferredSize(new Dimension(400, 300));
        cbSec1 = createStyledComboBox(securityQuestions);
        cbSec2 = createStyledComboBox(securityQuestions);
        cbSec3 = createStyledComboBox(securityQuestions);
        txtAns1 = new JTextField(); txtAns2 = new JTextField(); txtAns3 = new JTextField();

        securityPanel.add(new JLabel("Security Question 1:")); securityPanel.add(cbSec1);
        securityPanel.add(new JLabel("Answer 1:")); securityPanel.add(txtAns1);
        securityPanel.add(new JLabel("Security Question 2:")); securityPanel.add(cbSec2);
        securityPanel.add(new JLabel("Answer 2:")); securityPanel.add(txtAns2);
        securityPanel.add(new JLabel("Security Question 3:")); securityPanel.add(cbSec3);
        securityPanel.add(new JLabel("Answer 3:")); securityPanel.add(txtAns3);

        showSecurityDialog(first, middle, last, studentID, courseYear, email, user, pass);
    }
    private void showSecurityDialog(String f, String m, String l, String id, String cy, String em, String u, String p) {
        JDialog dialog = new JDialog(this, true);
        dialog.setUndecorated(true); 
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);
    
        JPanel panel = new JPanel(null);
        panel.setBackground(Main.BG_COLOR);
        panel.setBorder(BorderFactory.createLineBorder(Main.ACCENT_COLOR, 2));
        dialog.add(panel);

        // Title
        JLabel title = new JLabel("SECURITY QUESTIONS");
        title.setBounds(0, 30, 450, 30);
        title.setFont(new Font("Helvetica", Font.BOLD, 22));
        title.setForeground(Main.TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title);

        // Styling the inputs (Reusing your styled combo logic)
        cbSec1 = createStyledComboBox(securityQuestions);
        cbSec2 = createStyledComboBox(securityQuestions);
        cbSec3 = createStyledComboBox(securityQuestions);

        cbSec1.addActionListener(e -> updateQuestionLists());
        cbSec2.addActionListener(e -> updateQuestionLists());
        cbSec3.addActionListener(e -> updateQuestionLists());

        txtAns1 = new JTextField(); txtAns2 = new JTextField(); txtAns3 = new JTextField();

        // Positioning - Row 1
        addDialogLabel(panel, "Question 1:", 80);
        cbSec1.setBounds(50, 105, 350, 40); panel.add(cbSec1);
        addDialogField(panel, txtAns1, 150);

        // Row 2
        addDialogLabel(panel, "Question 2:", 210);
        cbSec2.setBounds(50, 235, 350, 40); panel.add(cbSec2);
        addDialogField(panel, txtAns2, 280);

        // Row 3
        addDialogLabel(panel, "Question 3:", 340);
        cbSec3.setBounds(50, 365, 350, 40); panel.add(cbSec3);
        addDialogField(panel, txtAns3, 410);

        // Action Button
        JButton btnComplete = new JButton("COMPLETE REGISTRATION");
        btnComplete.setBounds(50, 480, 350, 45);
        btnComplete.setBackground(Main.ACCENT_COLOR);
        btnComplete.setForeground(Color.WHITE);
        btnComplete.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnComplete.setFocusPainted(false);
        btnComplete.setBorderPainted(false);
        btnComplete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnComplete.addActionListener(e -> {
            if (validateSecurity()) { // Uses your existing validation method
                String[] qs = {(String)cbSec1.getSelectedItem(), (String)cbSec2.getSelectedItem(), (String)cbSec3.getSelectedItem()};
                String[] as = {txtAns1.getText().trim(), txtAns2.getText().trim(), txtAns3.getText().trim()};
                
                dialog.dispose();
                saveUserToDatabase(f, m, l, id, cy, em, u, p, qs, as);
            }
        });
        panel.add(btnComplete);

        dialog.setVisible(true);
    }
    private void updateQuestionLists() {
        Object s1 = cbSec1.getSelectedItem();
        Object s2 = cbSec2.getSelectedItem();
        Object s3 = cbSec3.getSelectedItem();

        refreshCombo(cbSec1, s1, s2, s3);
        refreshCombo(cbSec2, s2, s1, s3);
        refreshCombo(cbSec3, s3, s1, s2);
    }

    private void refreshCombo(JComboBox<String> combo, Object current, Object other1, Object other2) {
        java.awt.event.ActionListener[] listeners = combo.getActionListeners();
        for (java.awt.event.ActionListener l : listeners) combo.removeActionListener(l);

        combo.removeAllItems();
        for (String q : securityQuestions) {
            if (q.equals(securityQuestions[0]) || q.equals(current) || (!q.equals(other1) && !q.equals(other2))) {
                combo.addItem(q);
            }
        }
        combo.setSelectedItem(current);

        for (java.awt.event.ActionListener l : listeners) combo.addActionListener(l);
    }

    private void addDialogLabel(JPanel p, String text, int y) {
        JLabel l = new JLabel(text);
        l.setBounds(50, y, 350, 20);
        l.setForeground(Main.TEXT_COLOR);
        p.add(l);
    }

    private void addDialogField(JPanel p, JTextField f, int y) {
        f.setBounds(50, y, 350, 40);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xD1D8E0), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        p.add(f);
    }

    private boolean validateSecurity() {
        String q1 = (String) cbSec1.getSelectedItem();
        String q2 = (String) cbSec2.getSelectedItem();
        String q3 = (String) cbSec3.getSelectedItem();
        
        if (q1.equals(securityQuestions[0]) || q2.equals(securityQuestions[0]) || q3.equals(securityQuestions[0])) {
            CustomDialog.show(this, "Please select all 3 security questions.", false);
            return false;
        }
        if (q1.equals(q2) || q1.equals(q3) || q2.equals(q3)) {
            CustomDialog.show(this, "Each security question must be unique!", false);
            return false;
        }
        if (txtAns1.getText().trim().isEmpty() || txtAns2.getText().trim().isEmpty() || txtAns3.getText().trim().isEmpty()) {
            CustomDialog.show(this, "Please provide answers for all questions.", false);
            return false;
        }
        return true;
    }

    // Final Database Logic
    private void saveUserToDatabase(String f, String m, String l, String id, String cy, String em, String u, String p, String[] qs, String[] as) {
    String fullName = f + (m.isEmpty() ? "" : " " + m) + " " + l;
    
    try (Connection conn = db.Database.getConnection()) {
        String sql = "INSERT INTO users (full_name, student_id, course_year, email, username, password, role, q1, a1, q2, a2, q3, a3) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        
        pst.setString(1, fullName);
        pst.setString(2, id);
        pst.setString(3, cy);
        pst.setString(4, em);
        pst.setString(5, u);
        pst.setString(6, p);
        pst.setString(7, "student");
        pst.setString(8, qs[0]); pst.setString(9, as[0]); 
        pst.setString(10, qs[1]); pst.setString(11, as[1]);
        pst.setString(12, qs[2]); pst.setString(13, as[2]);

        pst.executeUpdate();
        
        CustomDialog.show(this, "✓ Registration Successful!", true);
        new LoginForm().setVisible(true);
        dispose();
    } catch (Exception ex) {
        CustomDialog.show(this, "✕ Registration Failed: " + ex.getMessage(), false);
    }
}

    private void addWindowControls() {
        JLayeredPane lp = this.getLayeredPane();

        Color idleColor = Main.BG_COLOR; 
        Color minHover = new Color(0xD1D8E0); 
        Color closeHover = new Color(0xE74C3C);

        // -- CLOSE BUTTON --
        JButton closeBtn = new JButton("X");
        closeBtn.setBounds(545, 10, 45, 30); // Positioned for 600 width
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
        minBtn.setBounds(495, 10, 45, 30); // Positioned to the left of Close
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

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Register().setVisible(true);
        });
    }
}