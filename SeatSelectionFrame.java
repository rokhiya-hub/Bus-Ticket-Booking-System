import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class SeatSelectionFrame extends JFrame {
    private Dashboard dashboard;
    private int busId;
    private String busNo, route, type, boarding, dropping, journeyDate;
    private double fare;
    private Set<Integer> bookedSeats  = new HashSet<>();
    private Set<Integer> selectedSeats = new LinkedHashSet<>();
    private Map<Integer, JButton> seatBtns = new HashMap<>();
    private JSpinner passengerSpinner;
    private JLabel selectedLbl, totalFareLbl;
    private UITheme.RoundedButton proceedBtn;
    private int maxPassengers = 1;

    public SeatSelectionFrame(Dashboard dashboard, int busId, String busNo, String route,
            String type, double fare, String boarding, String dropping, String journeyDate) {
        this.dashboard   = dashboard;
        this.busId       = busId;
        this.busNo       = busNo;
        this.route       = route;
        this.type        = type;
        this.fare        = fare;
        this.boarding    = boarding;
        this.dropping    = dropping;
        this.journeyDate = journeyDate;

        setTitle("BusWay — Seat Selection");
        setSize(1060, 690);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        UITheme.GradientPanel root = new UITheme.GradientPanel(UITheme.BG_DARK, new Color(220,232,255), false);
        root.setLayout(new BorderLayout());
        setContentPane(root);

        /* ── HEADER ─────────────────────────────────────────── */
        JPanel hdr = UITheme.buildHeader("💺  Select Your Seats",
            "Bus: " + busNo + "   |   " + route + "   |   ₹" + fare + " / seat");
        JPanel hRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        hRight.setOpaque(false);
        addLegend(hRight, "Available", new Color(219,234,254));
        addLegend(hRight, "Selected",  UITheme.ACCENT_BLUE);
        addLegend(hRight, "Booked",    new Color(200,200,210));
        UITheme.RoundedButton backBtn = new UITheme.RoundedButton("← Back", UITheme.BG_CARD);
        backBtn.setForeground(UITheme.TEXT_MUTED);
        hRight.add(backBtn);
        hdr.add(hRight, BorderLayout.EAST);
        root.add(hdr, BorderLayout.NORTH);

        loadBookedSeats();

        /* ── LEFT: PASSENGER COUNT + SEAT MAP ───────────────── */
        JPanel left = new JPanel(new BorderLayout(0,8));
        left.setOpaque(false);
        left.setBorder(BorderFactory.createEmptyBorder(12,18,12,6));

        // Passenger count selector
        UITheme.RoundedPanel countCard = new UITheme.RoundedPanel(12, UITheme.BG_CARD);
        countCard.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));
        JLabel cLbl = new JLabel("Number of Passengers:");
        cLbl.setFont(UITheme.FONT_BOLD); cLbl.setForeground(UITheme.TEXT_WHITE);
        passengerSpinner = new JSpinner(new SpinnerNumberModel(1,1,6,1));
        passengerSpinner.setPreferredSize(new Dimension(72,34));
        passengerSpinner.setFont(UITheme.FONT_BOLD);
        JSpinner.DefaultEditor ed = (JSpinner.DefaultEditor) passengerSpinner.getEditor();
        ed.getTextField().setBackground(UITheme.BG_INPUT);
        ed.getTextField().setForeground(UITheme.TEXT_WHITE);
        countCard.add(cLbl);
        countCard.add(passengerSpinner);
        countCard.add(UITheme.createLabel("  ← select this many seats below"));
        passengerSpinner.addChangeListener(e -> {
            maxPassengers = (int) passengerSpinner.getValue();
            while (selectedSeats.size() > maxPassengers) {
                int last = 0;
                for (int s : selectedSeats) last = s;
                deselectSeat(last);
            }
            updateSummary();
            proceedBtn.setEnabled(selectedSeats.size() == maxPassengers);
        });

        // Seat grid
        UITheme.RoundedPanel seatCard = new UITheme.RoundedPanel(16, UITheme.BG_CARD);
        seatCard.setLayout(new BorderLayout());
        seatCard.setBorder(BorderFactory.createEmptyBorder(8,10,10,10));

        JPanel driverRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        driverRow.setOpaque(false);
        driverRow.add(UITheme.badgeChip("🚘  Driver", UITheme.TEXT_MUTED));

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);

        // Column headers
        gc.gridy = 0;
        String[] ch = {"A","B","","C","D"};
        for (int c=0; c<5; c++) {
            gc.gridx = c;
            JLabel cl = new JLabel(ch[c], SwingConstants.CENTER);
            cl.setFont(new Font("Segoe UI",Font.BOLD,11));
            cl.setForeground(UITheme.TEXT_MUTED);
            cl.setPreferredSize(new Dimension(c==2?24:52, 18));
            grid.add(cl, gc);
        }
        // Seat rows
        for (int row=0; row<10; row++) {
            gc.gridy = row+1;
            int[] nums = { row*4+1, row*4+2, -1, row*4+3, row*4+4 };
            for (int col=0; col<5; col++) {
                gc.gridx = col;
                if (col == 2) {
                    JLabel a = new JLabel(String.valueOf(row+1), SwingConstants.CENTER);
                    a.setFont(new Font("Segoe UI",Font.PLAIN,9));
                    a.setForeground(new Color(120,140,170));
                    a.setPreferredSize(new Dimension(24,44));
                    grid.add(a, gc);
                } else {
                    int sn = nums[col];
                    JButton btn = buildSeatBtn(sn);
                    seatBtns.put(sn, btn);
                    grid.add(btn, gc);
                }
            }
        }

        JScrollPane ss = new JScrollPane(grid);
        ss.setOpaque(false); ss.getViewport().setOpaque(false);
        ss.setBorder(BorderFactory.createEmptyBorder());
        ss.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
            protected void configureScrollBarColors(){ thumbColor=UITheme.BORDER_COL; trackColor=UITheme.BG_CARD; }
        });
        seatCard.add(driverRow, BorderLayout.NORTH);
        seatCard.add(ss, BorderLayout.CENTER);

        left.add(countCard, BorderLayout.NORTH);
        left.add(seatCard,  BorderLayout.CENTER);

        /* ── RIGHT: SUMMARY CARD ─────────────────────────────── */
        UITheme.RoundedPanel sumCard = new UITheme.RoundedPanel(16, UITheme.BG_CARD);
        sumCard.setLayout(new BoxLayout(sumCard, BoxLayout.Y_AXIS));
        sumCard.setBorder(BorderFactory.createEmptyBorder(18,16,18,16));
        sumCard.setPreferredSize(new Dimension(270, 0));

        JLabel sumTitle = new JLabel("Booking Summary");
        sumTitle.setFont(UITheme.FONT_HEADING); sumTitle.setForeground(UITheme.ACCENT_CYAN);
        sumTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sumCard.add(sumTitle);
        sumCard.add(Box.createRigidArea(new Dimension(0,12)));

        String[][] info = {{"Bus",busNo},{"Route",route},{"Type",type},
                           {"Boarding",boarding},{"Dropping",dropping},
                           {"Date",journeyDate},{"Fare/Seat","₹ "+fare}};
        for (String[] row : info) {
            sumCard.add(infoRow(row[0], row[1]));
            sumCard.add(Box.createRigidArea(new Dimension(0,5)));
        }
        sumCard.add(Box.createRigidArea(new Dimension(0,10)));
        JSeparator sep = new JSeparator(); sep.setForeground(UITheme.BORDER_COL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        sumCard.add(sep);
        sumCard.add(Box.createRigidArea(new Dimension(0,10)));

        selectedLbl  = new JLabel("Seats: None");
        selectedLbl.setFont(UITheme.FONT_BODY); selectedLbl.setForeground(UITheme.TEXT_MUTED);
        selectedLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        totalFareLbl = new JLabel("Total: ₹ 0");
        totalFareLbl.setFont(new Font("Segoe UI",Font.BOLD,20));
        totalFareLbl.setForeground(UITheme.ACCENT_YELLOW);
        totalFareLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        sumCard.add(selectedLbl);
        sumCard.add(Box.createRigidArea(new Dimension(0,6)));
        sumCard.add(totalFareLbl);
        sumCard.add(Box.createVerticalGlue());

        proceedBtn = new UITheme.RoundedButton("Proceed to Details →", UITheme.ACCENT_GREEN);
        proceedBtn.setEnabled(false);
        proceedBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        proceedBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        sumCard.add(proceedBtn);

        JPanel rightWrap = new JPanel(new BorderLayout());
        rightWrap.setOpaque(false);
        rightWrap.setBorder(BorderFactory.createEmptyBorder(12,6,12,18));
        rightWrap.add(sumCard);

        root.add(left,      BorderLayout.CENTER);
        root.add(rightWrap, BorderLayout.EAST);

        /* ── ACTION LISTENERS ────────────────────────────────── */
        backBtn.addActionListener(e -> {
            try {
                BusSearchFrame bf = new BusSearchFrame(dashboard);
                bf.setVisible(true);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                dashboard.setVisible(true);
                dispose();
            }
        });

        // ★ KEY FIX: create next frame FIRST, then dispose current frame
        proceedBtn.addActionListener(e -> {
            if (selectedSeats.size() < maxPassengers) {
                JOptionPane.showMessageDialog(this,
                    "Please select " + maxPassengers + " seat(s).",
                    "Seats Needed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                java.util.List<Integer> seatList = new ArrayList<>(selectedSeats);
                PassengerDetailsFrame next = new PassengerDetailsFrame(
                    dashboard, busId, busNo, route, type, fare,
                    boarding, dropping, journeyDate, seatList);
                next.setVisible(true);   // ← show FIRST
                dispose();               // ← THEN close this one
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /* ── HELPERS ─────────────────────────────────────────────── */
    private JButton buildSeatBtn(int sn) {
        JButton btn = new JButton("S" + sn) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(getBackground().brighter());
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(new Color(255,255,255,18));
                g2.fillRoundRect(3,3,getWidth()-8,9,5,5);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI",Font.BOLD,9));
        btn.setPreferredSize(new Dimension(52,44));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);

        if (bookedSeats.contains(sn)) {
            btn.setBackground(new Color(220,220,225)); btn.setForeground(new Color(160,160,180));
            btn.setEnabled(false); btn.setToolTipText("Booked");
        } else {
            btn.setBackground(new Color(219,234,254)); btn.setForeground(UITheme.TEXT_WHITE);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setToolTipText("S" + sn + " — Available. Click to select.");
            btn.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){ if(!selectedSeats.contains(sn)) btn.setBackground(new Color(190,215,255)); }
                public void mouseExited (MouseEvent e){ if(!selectedSeats.contains(sn)) btn.setBackground(new Color(219,234,254)); }
            });
            btn.addActionListener(e -> {
                if (selectedSeats.contains(sn)) {
                    deselectSeat(sn);
                } else {
                    if (selectedSeats.size() >= maxPassengers) {
                        JOptionPane.showMessageDialog(this,
                            "You can only select " + maxPassengers + " seat(s).\n" +
                            "Increase the passenger count at the top if needed.",
                            "Limit Reached", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    selectedSeats.add(sn);
                    btn.setBackground(UITheme.ACCENT_BLUE);
                    btn.setForeground(Color.WHITE);
                }
                updateSummary();
                proceedBtn.setEnabled(selectedSeats.size() == maxPassengers);
            });
        }
        return btn;
    }

    private void deselectSeat(int sn) {
        selectedSeats.remove(sn);
        JButton b = seatBtns.get(sn);
        if (b != null) { b.setBackground(new Color(219,234,254)); b.setForeground(UITheme.TEXT_WHITE); }
    }

    private void updateSummary() {
        if (selectedSeats.isEmpty()) {
            selectedLbl.setText("Seats: None"); totalFareLbl.setText("Total: ₹ 0"); return;
        }
        StringBuilder sb = new StringBuilder("Seats: ");
        for (int s : selectedSeats) sb.append("S").append(s).append("  ");
        selectedLbl.setText(sb.toString().trim());
        totalFareLbl.setText("Total: ₹ " + String.format("%,.0f", fare * selectedSeats.size()));
    }

    private void loadBookedSeats() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT p.seat_no FROM passengers p " +
                "JOIN bookings b ON p.booking_id=b.booking_id " +
                "WHERE b.bus_id=? AND b.booking_status='Confirmed'");
            ps.setInt(1, busId); ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String s = rs.getString("seat_no");
                if (s!=null && s.startsWith("S")) {
                    try { bookedSeats.add(Integer.parseInt(s.substring(1))); }
                    catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) { System.err.println("loadBookedSeats: " + e.getMessage()); }
    }

    private void addLegend(JPanel p, String lbl, Color col) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0)); item.setOpaque(false);
        JLabel box = new JLabel(); box.setOpaque(true); box.setBackground(col);
        box.setPreferredSize(new Dimension(14,14));
        box.setBorder(BorderFactory.createLineBorder(col.brighter()));
        item.add(box); item.add(UITheme.createLabel(lbl)); p.add(item);
    }

    private JPanel infoRow(String lbl, String val) {
        JPanel rw = new JPanel(new BorderLayout(8,0)); rw.setOpaque(false);
        rw.setMaximumSize(new Dimension(Integer.MAX_VALUE,22)); rw.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(lbl+":"); l.setFont(UITheme.FONT_SMALL); l.setForeground(UITheme.TEXT_MUTED);
        l.setPreferredSize(new Dimension(68,16));
        JLabel v = new JLabel(val!=null?val:""); v.setFont(UITheme.FONT_BOLD); v.setForeground(UITheme.TEXT_WHITE);
        rw.add(l,BorderLayout.WEST); rw.add(v,BorderLayout.CENTER); return rw;
    }
}