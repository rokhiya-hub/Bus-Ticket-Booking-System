import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PassengerDetailsFrame extends JFrame {
    private Dashboard  dashboard;
    private int        busId;
    private String     busNo, route, type, boarding, dropping, journeyDate;
    private double     fare;
    private java.util.List<Integer>            seats;
    private java.util.List<JTextField>         nameFs    = new ArrayList<>();
    private java.util.List<JTextField>         phoneFs   = new ArrayList<>();
    private java.util.List<JTextField>         ageFs     = new ArrayList<>();
    private java.util.List<JComboBox<String>>  genderBxs = new ArrayList<>();

    public PassengerDetailsFrame(Dashboard dashboard, int busId, String busNo,
            String route, String type, double fare,
            String boarding, String dropping, String journeyDate,
            java.util.List<Integer> seats) {

        this.dashboard   = dashboard;
        this.busId       = busId;
        this.busNo       = busNo;
        this.route       = route;
        this.type        = type;
        this.fare        = fare;
        this.boarding    = boarding;
        this.dropping    = dropping;
        this.journeyDate = journeyDate;
        this.seats       = seats;

        setTitle("BusWay - Passenger Details");
        int height = Math.min(900, 280 + seats.size() * 280);
        setSize(860, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        UITheme.GradientPanel root = new UITheme.GradientPanel(UITheme.BG_DARK, new Color(8,14,28), false);
        root.setLayout(new BorderLayout());
        setContentPane(root);

        JPanel hdr = UITheme.buildHeader(
            "Passenger Details",
            seats.size() + " passenger(s)  |  " + seats.size() + " seat(s)  |  Total Rs"
                + String.format("%,.0f", fare * seats.size()));
        root.add(hdr, BorderLayout.NORTH);

        JPanel formOuter = new JPanel();
        formOuter.setOpaque(false);
        formOuter.setLayout(new BoxLayout(formOuter, BoxLayout.Y_AXIS));
        formOuter.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        for (int i = 0; i < seats.size(); i++) {
            int seat = seats.get(i);

            UITheme.RoundedPanel card = new UITheme.RoundedPanel(12, UITheme.BG_CARD);
            card.setLayout(new BorderLayout(0, 10));
            card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Title row
            JPanel titleRow = new JPanel(new BorderLayout());
            titleRow.setOpaque(false);
            titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
            JLabel title = new JLabel("Passenger " + (i + 1));
            title.setFont(UITheme.FONT_HEADING);
            title.setForeground(UITheme.ACCENT_CYAN);
            JLabel seatTag = UITheme.badgeChip("Seat S" + seat, UITheme.ACCENT_BLUE);
            titleRow.add(title,   BorderLayout.WEST);
            titleRow.add(seatTag, BorderLayout.EAST);

            // Fields: 2-column grid with label above each field
            JPanel fields = new JPanel(new GridBagLayout());
            fields.setOpaque(false);
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill    = GridBagConstraints.HORIZONTAL;
            gc.weightx = 0.5;

            JTextField nf = UITheme.createTextField("");
            JTextField pf = UITheme.createTextField("");
            JTextField af = UITheme.createTextField("");
            JComboBox<String> gb = UITheme.createComboBox(new String[]{"Male", "Female", "Other"});

            nf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            af.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            gb.setPreferredSize(new Dimension(10, 44));

            if (i == 0) {
                if (UserSession.getName()  != null) nf.setText(UserSession.getName());
                if (UserSession.getPhone() != null) pf.setText(UserSession.getPhone());
            }

            nameFs.add(nf); phoneFs.add(pf); ageFs.add(af); genderBxs.add(gb);

            // Row 0: Full Name label | Phone label
            gc.gridy = 0;
            gc.gridx = 0; gc.insets = new Insets(0, 0, 4, 16);
            fields.add(UITheme.createFieldLabel("Full Name *"), gc);
            gc.gridx = 1; gc.insets = new Insets(0, 0, 4, 0);
            fields.add(UITheme.createFieldLabel("Phone *"), gc);

            // Row 1: name field | phone field
            gc.gridy = 1;
            gc.gridx = 0; gc.insets = new Insets(0, 0, 16, 16);
            fields.add(nf, gc);
            gc.gridx = 1; gc.insets = new Insets(0, 0, 16, 0);
            fields.add(pf, gc);

            // Row 2: Age label | Gender label
            gc.gridy = 2;
            gc.gridx = 0; gc.insets = new Insets(0, 0, 4, 16);
            fields.add(UITheme.createFieldLabel("Age *"), gc);
            gc.gridx = 1; gc.insets = new Insets(0, 0, 4, 0);
            fields.add(UITheme.createFieldLabel("Gender"), gc);

            // Row 3: age field | gender combo
            gc.gridy = 3;
            gc.gridx = 0; gc.insets = new Insets(0, 0, 0, 16);
            fields.add(af, gc);
            gc.gridx = 1; gc.insets = new Insets(0, 0, 0, 0);
            fields.add(gb, gc);

            card.add(titleRow, BorderLayout.NORTH);
            card.add(fields,   BorderLayout.CENTER);

            formOuter.add(card);
            formOuter.add(Box.createRigidArea(new Dimension(0, 12)));
        }

        JScrollPane scroll = UITheme.darkScrollPane(formOuter);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.BG_SIDEBAR);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_COL),
            BorderFactory.createEmptyBorder(14, 25, 14, 25)));

        UITheme.RoundedButton backBtn = new UITheme.RoundedButton("Back to Seats", UITheme.BG_CARD);
        backBtn.setForeground(UITheme.TEXT_MUTED);

        JLabel fareTag = new JLabel("Total: Rs" + String.format("%,.0f", fare * seats.size()));
        fareTag.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fareTag.setForeground(UITheme.ACCENT_ORANGE);

        UITheme.RoundedButton nextBtn = new UITheme.RoundedButton(
            "Proceed to Payment  ->", UITheme.ACCENT_BLUE);
        nextBtn.setPreferredSize(new Dimension(240, 42));

        JPanel footLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        footLeft.setOpaque(false);
        footLeft.add(backBtn);
        footLeft.add(fareTag);

        footer.add(footLeft, BorderLayout.WEST);
        footer.add(nextBtn,  BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> {
            try {
                SeatSelectionFrame prev = new SeatSelectionFrame(
                    dashboard, busId, busNo, route, type, fare,
                    boarding, dropping, journeyDate);
                prev.setVisible(true);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                dashboard.setVisible(true);
                dispose();
            }
        });

        nextBtn.addActionListener(e -> validateAndProceed());
    }

    private void validateAndProceed() {
        java.util.List<String[]> passengers = new ArrayList<>();
        for (int i = 0; i < seats.size(); i++) {
            String name   = nameFs.get(i).getText().trim();
            String phone  = phoneFs.get(i).getText().trim();
            String age    = ageFs.get(i).getText().trim();
            String gender = (String) genderBxs.get(i).getSelectedItem();
            int pNum = i + 1;

            if (name.isEmpty() || phone.isEmpty() || age.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please fill in all fields for Passenger " + pNum + ".",
                    "Missing Info", JOptionPane.WARNING_MESSAGE);
                nameFs.get(i).requestFocus();
                return;
            }
            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this,
                    "Passenger " + pNum + ": Phone must be exactly 10 digits.");
                phoneFs.get(i).requestFocus();
                return;
            }
            if (!age.matches("\\d+") || Integer.parseInt(age) < 1 || Integer.parseInt(age) > 120) {
                JOptionPane.showMessageDialog(this,
                    "Passenger " + pNum + ": Enter a valid age (1-120).");
                ageFs.get(i).requestFocus();
                return;
            }
            passengers.add(new String[]{name, phone, age, gender});
        }

        try {
            PaymentFrame pf = new PaymentFrame(
                dashboard, busId, busNo, route, type, fare,
                boarding, dropping, journeyDate, seats, passengers);
            pf.setVisible(true);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error opening payment:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}