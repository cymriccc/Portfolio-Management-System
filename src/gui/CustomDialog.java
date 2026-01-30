package gui;

import java.awt.*;
import javax.swing.*;
import main.Main;

public class CustomDialog extends JDialog {
    public CustomDialog(JFrame parent, String message, Color themeColor) {
        super(parent, true); // Modal: blocks interaction with the main window
        setUndecorated(true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        
        // Main Panel
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Main.BG_COLOR);

        panel.setBorder(BorderFactory.createLineBorder(themeColor, 2));
        add(panel);

        // Message Text
        JLabel lblMessage = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        lblMessage.setBounds(20, 40, 360, 60);
        lblMessage.setFont(new Font("Helvetica", Font.PLAIN, 18));
        lblMessage.setForeground(Main.TEXT_COLOR);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblMessage);

        // Button color changes to match theme
        JButton btnOk = new JButton("GOT IT");
        btnOk.setBounds(150, 130, 100, 40);
        btnOk.setBackground(themeColor);
        btnOk.setForeground(Color.WHITE);
        btnOk.setFont(new Font("Helvetica", Font.BOLD, 14));
        btnOk.setFocusPainted(false);
        btnOk.setBorderPainted(false);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnOk.addActionListener(e -> dispose());
        
        // Hover Effect
        btnOk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnOk.setBackground(themeColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnOk.setBackground(themeColor);
            }
        });

        panel.add(btnOk);
    }

    public static boolean showConfirm(Frame parent, String message) {
        // Create a modal dialog
        final JDialog dialog = new JDialog(parent, true);
        dialog.setUndecorated(true);
        dialog.setLayout(null);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parent);
        dialog.getContentPane().setBackground(Color.WHITE);
    
        // Border for the dialog
        JPanel content = new JPanel(null);
        content.setBounds(0, 0, 400, 200);
        content.setBorder(BorderFactory.createLineBorder(new Color(0x575FCF), 2));
        content.setBackground(Color.WHITE);
        dialog.add(content);

        // Message Label
        JLabel lblMsg = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        lblMsg.setBounds(20, 40, 360, 60);
        lblMsg.setFont(new Font("Helvetica", Font.PLAIN, 16));
        content.add(lblMsg);

        // State variable to store the user's choice
        final boolean[] result = {false};

        // --- YES BUTTON (Indigo) ---
        JButton btnYes = new JButton("YES");
        btnYes.setBounds(70, 130, 120, 40);
        btnYes.setBackground(new Color(0x575FCF));
        btnYes.setForeground(Color.WHITE);
        btnYes.setFocusPainted(false);
        btnYes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnYes.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });
        content.add(btnYes);

        // --- NO BUTTON (Slate/Light Grey) ---
        JButton btnNo = new JButton("NO");
        btnNo.setBounds(210, 130, 120, 40);
        btnNo.setBackground(new Color(0xD1D8E0));
        btnNo.setForeground(new Color(0x2D3436));
        btnNo.setFocusPainted(false);
        btnNo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNo.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });
        content.add(btnNo);

        dialog.setVisible(true);
        return result[0];
    }

    // Static helper method to call it easily
    public static void show(Component parent, String message, boolean isSuccess) {
        Color themeColor = isSuccess ? Main.ACCENT_COLOR : new Color(0xE74C3C); // Red for errors

        Window window = SwingUtilities.getWindowAncestor(parent);
        JFrame parentFrame = (window instanceof JFrame) ? (JFrame) window : null;

        new CustomDialog(parentFrame, message, themeColor).setVisible(true);
    }
}