import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PaymentFrame extends JFrame {
    private Dashboard  dashboard;
    private int        busId;
    private String     busNo, route, type, boarding, dropping, journeyDate;
    private double     fare, totalFare;
    private java.util.List<Integer>  seats;
    private java.util.List<String[]> passengers;

    // Payment input fields
    private JTextField upiIdF;
    private JTextField cardNumF, expiryF, cvvF, postalF;
    private JRadioButton upiRad, cardRad, walletRad;
    private CardLayout payLayout;
    private JPanel     payDeck;

    public PaymentFrame(Dashboard dashboard, int busId, String busNo,
            String route, String type, double fare,
            String boarding, String dropping, String journeyDate,
            java.util.List<Integer> seats, java.util.List<String[]> passengers) {

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
        this.passengers  = passengers;
        this.totalFare   = fare * seats.size();

        setTitle("BusWay — Payment");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        UITheme.GradientPanel root = new UITheme.GradientPanel(UITheme.BG_DARK, new Color(8,14,28), false);
        root.setLayout(new BorderLayout());
        setContentPane(root);

        /* ── HEADER ─────────────────────────────────────────── */
        JPanel hdr = UITheme.buildHeader("💳  Secure Payment",
            "Complete your booking for " + seats.size() + " passenger(s)");
        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        badges.setOpaque(false);
        badges.add(UITheme.badgeChip("🚌 " + busNo,                          UITheme.ACCENT_BLUE));
        badges.add(UITheme.badgeChip("💺 " + seatStr(),                       UITheme.ACCENT_CYAN));
        badges.add(UITheme.badgeChip("₹ " + String.format("%,.0f", totalFare), UITheme.ACCENT_YELLOW));
        hdr.add(badges, BorderLayout.EAST);
        root.add(hdr, BorderLayout.NORTH);

        /* ── MAIN SCROLLABLE AREA ────────────────────────────── */
        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        // ── Booking summary strip ──
        UITheme.RoundedPanel sumStrip = new UITheme.RoundedPanel(10, UITheme.BG_CARD);
        sumStrip.setLayout(new GridLayout(1, 4, 12, 0));
        sumStrip.setBorder(BorderFactory.createEmptyBorder(12,16,12,16));
        sumStrip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        sumStrip.setAlignmentX(Component.LEFT_ALIGNMENT);
        addSummaryCell(sumStrip, "ROUTE",    route);
        addSummaryCell(sumStrip, "BOARDING", boarding);
        addSummaryCell(sumStrip, "DROPPING", dropping);
        addSummaryCell(sumStrip, "DATE",     journeyDate);
        main.add(sumStrip);
        main.add(Box.createRigidArea(new Dimension(0,14)));

        // ── Payment method selector ──
        UITheme.RoundedPanel methodCard = new UITheme.RoundedPanel(12, UITheme.BG_CARD);
        methodCard.setLayout(new BoxLayout(methodCard, BoxLayout.Y_AXIS));
        methodCard.setBorder(BorderFactory.createEmptyBorder(14,16,14,16));
        methodCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel mt = new JLabel("Payment Method");
        mt.setFont(UITheme.FONT_BOLD); mt.setForeground(UITheme.ACCENT_CYAN);
        mt.setAlignmentX(Component.LEFT_ALIGNMENT);
        methodCard.add(mt);
        methodCard.add(Box.createRigidArea(new Dimension(0,10)));

        upiRad    = UITheme.styledRadio("📱  UPI (Google Pay, PhonePe, Paytm…)");
        cardRad   = UITheme.styledRadio("💳  Credit / Debit Card");
        walletRad = UITheme.styledRadio("💵  Cash on Board");
        ButtonGroup bg = new ButtonGroup();
        bg.add(upiRad); bg.add(cardRad); bg.add(walletRad);
        upiRad.setSelected(true);

        JPanel radRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        radRow.setOpaque(false); radRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        radRow.add(upiRad); radRow.add(cardRad); radRow.add(walletRad);
        methodCard.add(radRow);
        main.add(methodCard);
        main.add(Box.createRigidArea(new Dimension(0,12)));

        // ── Payment input deck (CardLayout) ──
        payLayout = new CardLayout();
        payDeck   = new JPanel(payLayout);
        payDeck.setOpaque(false);
        payDeck.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -- UPI panel --
        UITheme.RoundedPanel upiPanel = new UITheme.RoundedPanel(12, UITheme.BG_CARD);
        upiPanel.setLayout(new GridBagLayout());
        upiPanel.setBorder(BorderFactory.createEmptyBorder(16,18,16,18));
        GridBagConstraints ug = new GridBagConstraints();
        ug.fill = GridBagConstraints.HORIZONTAL; ug.weightx = 1; ug.insets = new Insets(6,0,6,0);
        upiIdF = UITheme.createTextField("");
        ug.gridx=0; ug.gridy=0; upiPanel.add(UITheme.createFieldLabel("UPI ID  (e.g. 9876543210@paytm)"), ug);
        ug.gridy=1; upiPanel.add(upiIdF, ug);
        ug.gridy=2;
        JLabel upiNote = new JLabel("🔒  You will receive a payment request on your UPI app.");
        upiNote.setFont(UITheme.FONT_SMALL); upiNote.setForeground(UITheme.ACCENT_GREEN);
        upiPanel.add(upiNote, ug);

        // -- Card panel --
        UITheme.RoundedPanel cardPanel = new UITheme.RoundedPanel(12, UITheme.BG_CARD);
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(16,18,16,18));
        GridBagConstraints cg = new GridBagConstraints();
        cg.fill = GridBagConstraints.HORIZONTAL; cg.insets = new Insets(5,0,5,14);
        cardNumF = UITheme.createTextField(""); cardNumF.setPreferredSize(new Dimension(300,38));
        expiryF  = UITheme.createTextField("MM/YY"); expiryF.setPreferredSize(new Dimension(110,38));
        cvvF     = UITheme.createTextField(""); cvvF.setPreferredSize(new Dimension(90,38));
        postalF  = UITheme.createTextField(""); postalF.setPreferredSize(new Dimension(130,38));

        cg.gridx=0; cg.gridy=0; cg.gridwidth=4; cg.weightx=1;
        cardPanel.add(UITheme.createFieldLabel("Card Number  (16 digits)"), cg);
        cg.gridy=1; cardPanel.add(cardNumF, cg);

        cg.gridwidth=1; cg.gridy=2;
        cg.gridx=0; cg.weightx=0.3; cardPanel.add(UITheme.createFieldLabel("Expiry (MM/YY)"), cg);
        cg.gridx=1; cg.weightx=0.2; cardPanel.add(UITheme.createFieldLabel("CVV"), cg);
        cg.gridx=2; cg.weightx=0.3; cardPanel.add(UITheme.createFieldLabel("Postal Code"), cg);
        cg.gridx=3; cg.weightx=0.2; cardPanel.add(new JLabel(""), cg);

        cg.gridy=3;
        cg.gridx=0; cardPanel.add(expiryF,  cg);
        cg.gridx=1; cardPanel.add(cvvF,     cg);
        cg.gridx=2; cardPanel.add(postalF,  cg);
        cg.gridx=3; cardPanel.add(new JLabel(""), cg);

        cg.gridy=4; cg.gridx=0; cg.gridwidth=4;
        JLabel cNote = new JLabel("🔒  128-bit SSL encrypted. Your card details are safe.");
        cNote.setFont(UITheme.FONT_SMALL); cNote.setForeground(UITheme.ACCENT_GREEN);
        cardPanel.add(cNote, cg);

        // -- Cash panel --
        UITheme.RoundedPanel walletPanel = new UITheme.RoundedPanel(12, UITheme.BG_CARD);
        walletPanel.setLayout(new BoxLayout(walletPanel, BoxLayout.Y_AXIS));
        walletPanel.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));

        JLabel cashIcon = new JLabel("💵");
        cashIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        cashIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cashTitle = new JLabel("Pay Cash on Board");
        cashTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cashTitle.setForeground(UITheme.ACCENT_GREEN);
        cashTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cashAmt = new JLabel("Amount to Pay: ₹ " + String.format("%,.2f", totalFare));
        cashAmt.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cashAmt.setForeground(UITheme.ACCENT_GREEN);
        cashAmt.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cashInfo1 = new JLabel("• Pay the conductor when you board the bus.");
        cashInfo1.setFont(UITheme.FONT_BODY); cashInfo1.setForeground(UITheme.TEXT_MUTED);
        cashInfo1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cashInfo2 = new JLabel("• Please carry exact change if possible.");
        cashInfo2.setFont(UITheme.FONT_BODY); cashInfo2.setForeground(UITheme.TEXT_MUTED);
        cashInfo2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cashInfo3 = new JLabel("• Your seat is reserved. Ticket is valid for this journey.");
        cashInfo3.setFont(UITheme.FONT_BODY); cashInfo3.setForeground(UITheme.TEXT_MUTED);
        cashInfo3.setAlignmentX(Component.LEFT_ALIGNMENT);

        walletPanel.add(cashIcon);
        walletPanel.add(Box.createRigidArea(new Dimension(0,8)));
        walletPanel.add(cashTitle);
        walletPanel.add(Box.createRigidArea(new Dimension(0,6)));
        walletPanel.add(cashAmt);
        walletPanel.add(Box.createRigidArea(new Dimension(0,12)));
        walletPanel.add(cashInfo1);
        walletPanel.add(Box.createRigidArea(new Dimension(0,4)));
        walletPanel.add(cashInfo2);
        walletPanel.add(Box.createRigidArea(new Dimension(0,4)));
        walletPanel.add(cashInfo3);

        payDeck.add(upiPanel,    "UPI");
        payDeck.add(cardPanel,   "CARD");
        payDeck.add(walletPanel, "WALLET");
        main.add(payDeck);

        // Switch panels on radio select
        upiRad.addActionListener(e    -> payLayout.show(payDeck,"UPI"));
        cardRad.addActionListener(e   -> payLayout.show(payDeck,"CARD"));
        walletRad.addActionListener(e -> payLayout.show(payDeck,"WALLET"));

        JScrollPane scroll = new JScrollPane(main);
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);

        /* ── FOOTER ──────────────────────────────────────────── */
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.BG_SIDEBAR);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0,UITheme.BORDER_COL),
            BorderFactory.createEmptyBorder(14,25,14,25)));

        UITheme.RoundedButton backBtn = new UITheme.RoundedButton("← Back", UITheme.BG_CARD);
        backBtn.setForeground(UITheme.TEXT_MUTED);
        UITheme.RoundedButton payBtn  = new UITheme.RoundedButton(
            "Pay  ₹" + String.format("%,.0f", totalFare) + "  →", UITheme.ACCENT_GREEN);
        payBtn.setPreferredSize(new Dimension(240, 42));

        footer.add(backBtn, BorderLayout.WEST);
        footer.add(payBtn,  BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        /* ── LISTENERS ───────────────────────────────────────── */
        backBtn.addActionListener(e -> {
            try {
                PassengerDetailsFrame prev = new PassengerDetailsFrame(
                    dashboard, busId, busNo, route, type, fare,
                    boarding, dropping, journeyDate, seats);
                prev.setVisible(true);   // show FIRST
                dispose();               // then close
            } catch (Exception ex) {
                ex.printStackTrace();
                dashboard.setVisible(true);
                dispose();
            }
        });

        payBtn.addActionListener(e -> handlePayment());
    }

    /* ── PAYMENT HANDLERS ───────────────────────────────────── */
    private void handlePayment() {
        if      (upiRad.isSelected())    handleUPI();
        else if (cardRad.isSelected())   handleCard();
        else                             handleWallet();
    }

    private void handleUPI() {
        String id = upiIdF.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your UPI ID."); return;
        }
        if (!id.matches("[\\w.\\-]+@[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(this,
                "Invalid UPI ID format.\nExample: 9876543210@paytm or name@okaxis"); return;
        }
        showOTP("UPI  |  " + id);
    }

    private void handleCard() {
        String cn = cardNumF.getText().replaceAll("[\\s-]","");
        String ex = expiryF.getText().trim();
        String cv = cvvF.getText().trim();
        String pz = postalF.getText().trim();

        if (cn.isEmpty()||ex.isEmpty()||cv.isEmpty()||pz.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please fill in all card details."); return; }
        if (!cn.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this,"Card number must be exactly 16 digits."); return; }
        if (!ex.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            JOptionPane.showMessageDialog(this,"Expiry must be in MM/YY format."); return; }
        if (!cv.matches("\\d{3,4}")) {
            JOptionPane.showMessageDialog(this,"CVV must be 3 or 4 digits."); return; }
        if (!pz.matches("\\d{6}")) {
            JOptionPane.showMessageDialog(this,"Postal code must be 6 digits."); return; }
        showOTP("Card  ****" + cn.substring(12));
    }

    private void handleWallet() {
        // Cash on Board — no OTP needed, just confirm and generate ticket
        int ok = JOptionPane.showConfirmDialog(this,
            "Confirm Cash on Board payment?\n\nAmount to pay: ₹" + String.format("%,.2f", totalFare)
            + "\nPlease pay the conductor when you board.",
            "Confirm Cash Payment", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) openTicket("Cash on Board");
    }

    private void showOTP(String payDesc) {
        String otp = String.format("%06d", (int)(Math.random() * 1000000));
        System.out.println("[DEV] Simulated OTP: " + otp);

        // Build OTP dialog panel
        UITheme.RoundedPanel p = new UITheme.RoundedPanel(14, UITheme.BG_CARD);
        p.setLayout(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,24,20,24));
        p.setPreferredSize(new Dimension(400, 230));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; g.insets = new Insets(6,0,6,0);

        g.gridy=0; JLabel h = new JLabel("🔐  OTP Verification");
        h.setFont(UITheme.FONT_HEADING); h.setForeground(UITheme.TEXT_WHITE); p.add(h,g);

        g.gridy=1; JLabel d = UITheme.createLabel("A 6-digit OTP has been sent to your registered mobile number.");
        p.add(d,g);

        g.gridy=2; JLabel sim = new JLabel("(Demo OTP for testing: " + otp + ")");
        sim.setFont(UITheme.FONT_SMALL); sim.setForeground(UITheme.ACCENT_YELLOW); p.add(sim,g);

        g.gridy=3; p.add(UITheme.createFieldLabel("Enter OTP"), g);
        JTextField otpF = UITheme.createTextField("");
        otpF.setPreferredSize(new Dimension(220,38));
        g.gridy=4; p.add(otpF,g);

        g.gridy=5; JLabel pm = UITheme.createLabel("Payment via: " + payDesc);
        pm.setForeground(UITheme.ACCENT_CYAN); p.add(pm,g);

        int result = JOptionPane.showConfirmDialog(this, p,
            "OTP Verification", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String entered = otpF.getText().trim();
            if (entered.equals(otp)) {
                openTicket(payDesc);
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌  Incorrect OTP. Please try again.",
                    "OTP Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openTicket(String payMethod) {
        try {
            TicketFrame tf = new TicketFrame(
                dashboard, busId, busNo, route, type, fare,
                boarding, dropping, journeyDate, seats, passengers, payMethod, totalFare);
            tf.setVisible(true);   // show FIRST
            dispose();             // then close
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error opening ticket:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ── HELPERS ─────────────────────────────────────────────── */
    private String seatStr() {
        StringBuilder sb = new StringBuilder();
        for (int s : seats) sb.append("S").append(s).append(" ");
        return sb.toString().trim();
    }
    private void addSummaryCell(JPanel p, String label, String value) {
        JPanel cell = new JPanel(new GridLayout(2,1,0,2)); cell.setOpaque(false);
        cell.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        JLabel l = new JLabel(label); l.setFont(UITheme.FONT_SMALL); l.setForeground(UITheme.TEXT_MUTED);
        JLabel v = new JLabel(value!=null?value:""); v.setFont(UITheme.FONT_BOLD); v.setForeground(UITheme.TEXT_WHITE);
        cell.add(l); cell.add(v); p.add(cell);
    }
}