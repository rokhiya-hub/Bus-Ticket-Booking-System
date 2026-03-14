import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.sql.*;
import java.util.*;

public class TicketFrame extends JFrame {
    private Dashboard  dashboard;
    private int        busId;
    private String     busNo, route, type, boarding, dropping, journeyDate, paymentMethod;
    private double     fare, totalFare;
    private java.util.List<Integer>  seats;
    private java.util.List<String[]> passengers;
    private String     ticketId = "BW-PENDING";
    private JPanel     ticketArea;  // used by print

    public TicketFrame(Dashboard dashboard, int busId, String busNo,
            String route, String type, double fare,
            String boarding, String dropping, String journeyDate,
            java.util.List<Integer> seats, java.util.List<String[]> passengers,
            String paymentMethod, double totalFare) {

        this.dashboard     = dashboard;
        this.busId         = busId;
        this.busNo         = busNo;
        this.route         = route;
        this.type          = type;
        this.fare          = fare;
        this.boarding      = boarding;
        this.dropping      = dropping;
        this.journeyDate   = journeyDate;
        this.seats         = seats;
        this.passengers    = passengers;
        this.paymentMethod = paymentMethod;
        this.totalFare     = totalFare;

        setTitle("BusWay — E-Ticket");
        setSize(780, 740);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        UITheme.GradientPanel root = new UITheme.GradientPanel(UITheme.BG_DARK, new Color(220,232,255), false);
        root.setLayout(new BorderLayout());
        setContentPane(root);

        root.add(UITheme.buildHeader("🎫  E-Ticket","Your booking is confirmed!"), BorderLayout.NORTH);

        // Save booking to DB (get ticket ID)
        ticketId = saveBooking();

        // Build ticket display
        ticketArea = buildTicket();
        JScrollPane scroll = UITheme.darkScrollPane(ticketArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);

        /* ── FOOTER ──────────────────────────────────────────── */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 14));
        footer.setBackground(UITheme.BG_SIDEBAR);
        footer.setBorder(BorderFactory.createMatteBorder(1,0,0,0, UITheme.BORDER_COL));

        UITheme.RoundedButton printBtn = new UITheme.RoundedButton("🖨  Print Ticket", UITheme.ACCENT_BLUE);
        UITheme.RoundedButton dashBtn  = new UITheme.RoundedButton("🏠  Dashboard",    UITheme.ACCENT_GREEN);
        UITheme.RoundedButton viewBtn  = new UITheme.RoundedButton("📄  My Bookings",  UITheme.ACCENT_PURPLE);
        printBtn.setPreferredSize(new Dimension(170,40));
        dashBtn.setPreferredSize(new Dimension(170,40));
        viewBtn.setPreferredSize(new Dimension(170,40));
        footer.add(printBtn); footer.add(dashBtn); footer.add(viewBtn);
        root.add(footer, BorderLayout.SOUTH);

        printBtn.addActionListener(e -> doPrint());
        dashBtn.addActionListener(e  -> { dashboard.setVisible(true); dispose(); });
        viewBtn.addActionListener(e  -> {
            try {
                ViewCancelBookingFrame vf = new ViewCancelBookingFrame(dashboard);
                vf.setVisible(true);
                dispose();
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }

    /* ── BUILD TICKET UI ─────────────────────────────────────── */
    private JPanel buildTicket() {
        JPanel outer = new JPanel();
        outer.setOpaque(false);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(BorderFactory.createEmptyBorder(16,24,16,24));

        // ── Success banner ──
        UITheme.RoundedPanel banner = new UITheme.RoundedPanel(12, new Color(220,252,231));
        banner.setLayout(new BorderLayout(12,0));
        banner.setBorder(BorderFactory.createEmptyBorder(12,16,12,16));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE,62));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel chk = new JLabel("✓");
        chk.setFont(new Font("Segoe UI",Font.BOLD,32)); chk.setForeground(UITheme.ACCENT_GREEN);
        JPanel bText = new JPanel(new GridLayout(2,1)); bText.setOpaque(false);
        JLabel bT = new JLabel("Payment Successful  —  Booking Confirmed");
        bT.setFont(UITheme.FONT_BOLD); bT.setForeground(new Color(22,101,52));
        JLabel bS = new JLabel("Ticket: " + ticketId + "   |   Payment: " + paymentMethod);
        bS.setFont(UITheme.FONT_SMALL); bS.setForeground(new Color(100,116,139));
        bText.add(bT); bText.add(bS);
        banner.add(chk,   BorderLayout.WEST);
        banner.add(bText, BorderLayout.CENTER);
        outer.add(banner);
        outer.add(Box.createRigidArea(new Dimension(0,14)));

        // ── Ticket card ──
        UITheme.RoundedPanel ticket = new UITheme.RoundedPanel(16, UITheme.BG_CARD);
        ticket.setLayout(new BorderLayout());
        ticket.setAlignmentX(Component.LEFT_ALIGNMENT);
        ticket.setBorder(new UITheme.RoundedBorder(UITheme.ACCENT_BLUE,2,16));

        // Ticket header bar
        JPanel tkHdr = new JPanel(new BorderLayout());
        tkHdr.setBackground(new Color(22,34,56));
        tkHdr.setBorder(BorderFactory.createEmptyBorder(12,18,12,18));
        JLabel brand = new JLabel("🚌  BusWay — E-Ticket");
        brand.setFont(new Font("Segoe UI",Font.BOLD,15)); brand.setForeground(UITheme.ACCENT_CYAN);
        JLabel tidLbl = new JLabel(ticketId);
        tidLbl.setFont(UITheme.FONT_BOLD); tidLbl.setForeground(UITheme.ACCENT_YELLOW);
        tkHdr.add(brand, BorderLayout.WEST);
        tkHdr.add(tidLbl, BorderLayout.EAST);

        // Bus info cells
        JPanel busRow = new JPanel(new GridLayout(1,4,0,0));
        busRow.setBackground(new Color(240,247,255));
        busRow.setBorder(BorderFactory.createEmptyBorder(12,16,12,16));
        addCell(busRow, "BUS",   busNo,       UITheme.ACCENT_BLUE);
        addCell(busRow, "TYPE",  type,        UITheme.ACCENT_CYAN);
        addCell(busRow, "DATE",  journeyDate, UITheme.ACCENT_PURPLE);
        addCell(busRow, "TOTAL FARE", "₹ " + String.format("%,.0f",totalFare), UITheme.ACCENT_GREEN);
        // Route row
        JPanel routeRow = new JPanel(new BorderLayout(0,6));
        routeRow.setBackground(UITheme.BG_CARD);
        routeRow.setBorder(BorderFactory.createEmptyBorder(14,20,14,20));
        String[] parts = route.split(" - ");
        String fromCity = parts.length>0 ? parts[0] : route;
        String toCity   = parts.length>1 ? parts[1] : "";
        JPanel routeLine = new JPanel(new GridLayout(1,3,0,0));
        routeLine.setOpaque(false);
        // From
        JPanel fromP = new JPanel(new GridLayout(2,1,0,2)); fromP.setOpaque(false);
        JLabel fc = new JLabel(fromCity); fc.setFont(new Font("Segoe UI",Font.BOLD,20)); fc.setForeground(UITheme.TEXT_WHITE);
        JLabel fb = new JLabel(boarding!=null?boarding:""); fb.setFont(UITheme.FONT_SMALL); fb.setForeground(UITheme.TEXT_MUTED);
        fromP.add(fc); fromP.add(fb);
        // Arrow
        JLabel arw = new JLabel("──── 🚌 ────", SwingConstants.CENTER);
        arw.setFont(new Font("Segoe UI",Font.BOLD,14)); arw.setForeground(UITheme.ACCENT_CYAN);
        // To
        JPanel toP = new JPanel(new GridLayout(2,1,0,2)); toP.setOpaque(false);
        JLabel tc2 = new JLabel(toCity, SwingConstants.RIGHT); tc2.setFont(new Font("Segoe UI",Font.BOLD,20)); tc2.setForeground(UITheme.TEXT_WHITE);
        JLabel td  = new JLabel(dropping!=null?dropping:"", SwingConstants.RIGHT); td.setFont(UITheme.FONT_SMALL); td.setForeground(UITheme.TEXT_MUTED);
        toP.add(tc2); toP.add(td);
        routeLine.add(fromP); routeLine.add(arw); routeLine.add(toP);
        routeRow.add(routeLine, BorderLayout.CENTER);

        // Dashed separator
        JPanel dash = new JPanel(){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create(); g2.setColor(UITheme.BORDER_COL);
                g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10,new float[]{8},0));
                g2.drawLine(20,getHeight()/2,getWidth()-20,getHeight()/2); g2.dispose();
            }
        };
        dash.setOpaque(false); dash.setPreferredSize(new Dimension(0,18));

        // Passenger table
        JPanel passSection = new JPanel(new BorderLayout(0,6));
        passSection.setBackground(UITheme.BG_CARD);
        passSection.setBorder(BorderFactory.createEmptyBorder(10,18,16,18));
        JLabel passTitle = new JLabel("PASSENGER DETAILS");
        passTitle.setFont(UITheme.FONT_SMALL); passTitle.setForeground(UITheme.TEXT_MUTED);
        passTitle.setBorder(BorderFactory.createEmptyBorder(0,0,6,0));

        String[] cols  = {"Seat","Passenger Name","Age","Gender","Phone"};
        Object[][] data = new Object[passengers.size()][5];
        for (int i=0; i<passengers.size(); i++) {
            data[i][0] = "S" + seats.get(i);
            data[i][1] = passengers.get(i)[0];
            data[i][2] = passengers.get(i)[2];
            data[i][3] = passengers.get(i)[3];
            data[i][4] = passengers.get(i)[1];
        }
        JTable tbl = new JTable(data, cols) {
            public boolean isCellEditable(int r,int c){ return false; }
        };
        UITheme.styleTable(tbl);
        tbl.setRowHeight(30);
        tbl.setPreferredScrollableViewportSize(new Dimension(0, 30 + passengers.size()*30));
        JScrollPane tsp = UITheme.darkScrollPane(tbl);
        tsp.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COL));
        passSection.add(passTitle, BorderLayout.NORTH);
        passSection.add(tsp,       BorderLayout.CENTER);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(busRow); body.add(routeRow); body.add(dash); body.add(passSection);

        ticket.add(tkHdr, BorderLayout.NORTH);
        ticket.add(body,  BorderLayout.CENTER);
        outer.add(ticket);
        return outer;
    }

    /* ── DB SAVE ─────────────────────────────────────────────── */
    private String saveBooking() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement bs = con.prepareStatement(
                "INSERT INTO bookings(user_id,bus_id,boarding_point,dropping_point," +
                "journey_date,total_fare,passenger_count,payment_method,payment_status,booking_status)" +
                " VALUES(?,?,?,?,?,?,?,?,'Paid','Confirmed')",
                Statement.RETURN_GENERATED_KEYS);
            bs.setInt   (1, UserSession.getUserId());
            bs.setInt   (2, busId);
            bs.setString(3, boarding);
            bs.setString(4, dropping);
            bs.setDate  (5, java.sql.Date.valueOf(journeyDate));
            bs.setDouble(6, totalFare);
            bs.setInt   (7, passengers.size());
            bs.setString(8, paymentMethod);
            bs.executeUpdate();
            ResultSet keys = bs.getGeneratedKeys();
            int bookingId = keys.next() ? keys.getInt(1) : -1;

            // Insert one row per passenger
            for (int i = 0; i < passengers.size(); i++) {
                String[] p = passengers.get(i);
                PreparedStatement pp = con.prepareStatement(
                    "INSERT INTO passengers(booking_id,seat_no,passenger_name,age,gender,phone)" +
                    " VALUES(?,?,?,?,?,?)");
                pp.setInt   (1, bookingId);
                pp.setString(2, "S" + seats.get(i));
                pp.setString(3, p[0]);
                pp.setInt   (4, Integer.parseInt(p[2]));
                pp.setString(5, p[3]);
                pp.setString(6, p[1]);
                pp.executeUpdate();
            }
            // Decrement available seats
            con.prepareStatement(
                "UPDATE buses SET seats = seats - " + passengers.size() +
                " WHERE bus_id = " + busId).executeUpdate();

            return "BW-" + String.format("%07d", bookingId);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Booking saved with error: " + e.getMessage() +
                "\nTicket shown is valid but may not appear in My Bookings.");
            return "BW-OFFLINE";
        }
    }

    /* ── PRINT ───────────────────────────────────────────────── */
    private void doPrint() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pf, page) -> {
            if (page > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            double sw = pf.getImageableWidth(), sh = pf.getImageableHeight();
            double cw = ticketArea.getWidth(), ch = ticketArea.getHeight();
            if (cw > 0 && ch > 0) g2.scale(Math.min(sw/cw, sh/ch), Math.min(sw/cw, sh/ch));
            ticketArea.paint(g2);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try { job.print(); }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage());
            }
        }
    }

    /* ── HELPER ──────────────────────────────────────────────── */
    private void addCell(JPanel p, String label, String val, Color col) {
        JPanel cell = new JPanel(new GridLayout(2,1,0,2)); cell.setOpaque(false);
        cell.setBorder(BorderFactory.createEmptyBorder(0,12,0,12));
        JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI",Font.PLAIN,10)); l.setForeground(UITheme.TEXT_MUTED);
        JLabel v = new JLabel(val!=null?val:""); v.setFont(new Font("Segoe UI",Font.BOLD,14)); v.setForeground(col);
        cell.add(l); cell.add(v); p.add(cell);
    }
}