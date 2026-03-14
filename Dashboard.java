import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class Dashboard extends JFrame {
    private boolean isAdmin;
    private UITheme.StatCard c1, c2, c3, c4;

    public Dashboard(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setTitle(isAdmin ? "BusWay — Admin Control Panel" : "BusWay — My Dashboard");
        setSize(1280, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        UITheme.GradientPanel root = new UITheme.GradientPanel(UITheme.BG_DARK, new Color(220,232,255), false);
        root.setLayout(new BorderLayout(0, 0));
        setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(isAdmin ? buildAdminContent() : buildUserContent(), BorderLayout.CENTER);
        loadStats();
    }

    // ══════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sb = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint bg = new GradientPaint(0, 0, new Color(18, 24, 58),
                                                      0, getHeight(), new Color(26, 16, 56));
                g2.setPaint(bg); g2.fillRect(0, 0, getWidth(), getHeight());
                RadialGradientPaint t = new RadialGradientPaint(
                    new Point2D.Float(getWidth() / 2f, 0), getWidth() * 1.3f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 82, 82, 30), new Color(255, 82, 82, 0)});
                g2.setPaint(t); g2.fillRect(0, 0, getWidth(), getHeight());
                RadialGradientPaint b = new RadialGradientPaint(
                    new Point2D.Float(getWidth() / 2f, getHeight()), getWidth() * 1.1f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(109, 40, 217, 35), new Color(109, 40, 217, 0)});
                g2.setPaint(b); g2.fillRect(0, 0, getWidth(), getHeight());
                GradientPaint line = new GradientPaint(0, 0, new Color(255, 82, 82, 160),
                                                        0, getHeight(), new Color(109, 40, 217, 160));
                g2.setPaint(line); g2.setStroke(new BasicStroke(2f));
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                super.paintComponent(g); g2.dispose();
            }
        };
        sb.setOpaque(false);
        sb.setPreferredSize(new Dimension(256, getHeight()));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));

        sb.add(buildLogoPanel());
        sb.add(sidebarDivider());
        sb.add(Box.createRigidArea(new Dimension(0, 12)));
        sb.add(buildUserBadge());
        sb.add(Box.createRigidArea(new Dimension(0, 6)));
        sb.add(sidebarDivider());
        sb.add(Box.createRigidArea(new Dimension(0, 4)));

        if (isAdmin) buildAdminMenu(sb); else buildUserMenu(sb);

        sb.add(Box.createVerticalGlue());
        sb.add(sidebarDivider());
        sb.add(Box.createRigidArea(new Dimension(0, 4)));

        UITheme.SidebarButton signOut = new UITheme.SidebarButton("  Sign Out", "");
        signOut.setText("  \uD83D\uDEAA   Sign Out");
        signOut.setForeground(new Color(255, 110, 110));
        signOut.setAlignmentX(Component.LEFT_ALIGNMENT);
        signOut.addActionListener(e -> { UserSession.clear(); dispose(); new LoginFrame().setVisible(true); });
        sb.add(signOut);
        sb.add(Box.createRigidArea(new Dimension(0, 16)));
        return sb;
    }

    private JPanel buildLogoPanel() {
        JPanel logo = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                g2.setColor(Color.WHITE);
                g2.drawString("\uD83D\uDE8C", 16, 40);
                Font f = new Font("Segoe UI", Font.BOLD, 24);
                g2.setFont(f); g2.setColor(Color.WHITE);
                g2.drawString("Bus", 48, 40);
                FontMetrics fm = g2.getFontMetrics(f);
                int bx = 48 + fm.stringWidth("Bus");
                int ww = fm.stringWidth("Way");
                GradientPaint gp = new GradientPaint(bx, 0, new Color(255, 82, 82), bx + ww, 0, new Color(255, 190, 30));
                g2.setPaint(gp);
                g2.drawString("Way", bx, 40);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                GradientPaint ul = new GradientPaint(bx, 0, new Color(255, 82, 82, 220), bx + ww, 0, new Color(255, 190, 30, 0));
                g2.setPaint(ul);
                g2.drawLine(bx, 45, bx + ww - 2, 45);
                g2.setColor(new Color(255, 210, 40));
                g2.fillOval(bx + ww + 4, 30, 6, 6);
                super.paintComponent(g); g2.dispose();
            }
        };
        logo.setOpaque(false);
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        logo.setPreferredSize(new Dimension(256, 62));
        return logo;
    }

    private JPanel buildUserBadge() {
        JPanel outer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        outer.setOpaque(false);
        outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel badge = new JPanel(new BorderLayout(10, 0)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.setColor(new Color(255, 255, 255, 28)); g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 2, getHeight() - 2, 12, 12));
                super.paintComponent(g); g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(232, 54));
        badge.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JPanel avatar = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 82, 82), getWidth(), getHeight(), new Color(109, 40, 217));
                g2.setPaint(gp); g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                super.paintComponent(g); g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(36, 36));
        JLabel av = new JLabel(isAdmin ? "\u26A1" : "\uD83D\uDC64", SwingConstants.CENTER);
        av.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        avatar.add(av, BorderLayout.CENTER);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        JLabel name = new JLabel(UserSession.getName() != null ? UserSession.getName() : "User");
        name.setFont(new Font("Segoe UI", Font.BOLD, 12)); name.setForeground(Color.WHITE);
        JLabel role = new JLabel(isAdmin ? "\u25CF  Administrator" : "\u25CF  Passenger");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(isAdmin ? new Color(255, 160, 100) : new Color(80, 230, 160));
        info.add(name); info.add(role);

        badge.add(avatar, BorderLayout.WEST);
        badge.add(info, BorderLayout.CENTER);
        outer.add(badge);
        return outer;
    }

    private JComponent sidebarDivider() {
        JPanel d = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(255, 82, 82, 0),
                    getWidth() * 0.5f, 0, new Color(255, 100, 100, 100));
                g2.setPaint(gp); g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        d.setOpaque(false);
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        d.setPreferredSize(new Dimension(256, 1));
        return d;
    }

    private void buildUserMenu(JPanel sb) {
        navSection(sb, "TRAVEL");
        navBtn(sb, "Book Ticket",    "\uD83C\uDFDF", e -> { setVisible(false); new BusSearchFrame(this).setVisible(true); });
        navBtn(sb, "Search Buses",   "\uD83D\uDD0D", e -> { setVisible(false); new BusSearchFrame(this).setVisible(true); });
        navBtn(sb, "My Bookings",    "\uD83D\uDCC4", e -> { setVisible(false); new ViewCancelBookingFrame(this).setVisible(true); });
        navBtn(sb, "Cancel Ticket",  "\u274C",       e -> { setVisible(false); new ViewCancelBookingFrame(this).setVisible(true); });
        navSection(sb, "ACCOUNT");
        navBtn(sb, "My Profile",     "\uD83D\uDC64", e -> { setVisible(false); new ProfileFrame(this).setVisible(true); });
        navBtn(sb, "Offers & Deals", "\uD83C\uDF81", e -> showOffers());
        navBtn(sb, "Help & Support", "\u2753",       e -> showHelp());
    }

    private void buildAdminMenu(JPanel sb) {
        navSection(sb, "MANAGEMENT");
        navBtn(sb, "Manage Buses",   "\uD83D\uDE8C", e -> new AdminBusManagementFrame().setVisible(true));
        navBtn(sb, "All Bookings",   "\uD83D\uDCCB", e -> new AdminBookingsFrame(this).setVisible(true));
        navBtn(sb, "Manage Users",   "\uD83D\uDC65", e -> new AdminUsersFrame(this).setVisible(true));
        navSection(sb, "ANALYTICS");
        navBtn(sb, "Revenue Report", "\uD83D\uDCB0", e -> new AdminRevenueFrame(this).setVisible(true));
        navBtn(sb, "System Stats",   "\uD83D\uDCCA", e -> showAdminStats());
        navSection(sb, "SETTINGS");
        navBtn(sb, "Routes & Stops", "\uD83D\uDDFA", e -> new AdminBusManagementFrame().setVisible(true));
    }

    private void navBtn(JPanel sb, String label, String emoji, ActionListener a) {
        UITheme.SidebarButton b = new UITheme.SidebarButton(emoji, label);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addActionListener(a);
        sb.add(b);
    }

    private void navSection(JPanel sb, String title) {
        JLabel l = new JLabel("   " + title);
        l.setFont(new Font("Segoe UI", Font.BOLD, 9));
        l.setForeground(new Color(170, 145, 210));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(14, 0, 4, 0));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        sb.add(l);
    }

    // ══════════════════════════════════════════════════════════════════
    //  MAIN CONTENT
    // ══════════════════════════════════════════════════════════════════
    private JPanel buildUserContent() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        root.add(buildTopBar("Welcome back, " + firstName() + "! \uD83D\uDC4B",
            "Where are you travelling today?", UITheme.ACCENT_BLUE), BorderLayout.NORTH);
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(buildStatCards(false));
        center.add(Box.createRigidArea(new Dimension(0, 24)));
        center.add(buildSectionTitle("Quick Actions"));
        center.add(Box.createRigidArea(new Dimension(0, 12)));
        center.add(buildUserActionCards());
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildAdminContent() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setOpaque(false);
        root.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        root.add(buildTopBar("Admin Control Panel \u26A1",
            "System overview & full management access", UITheme.ACCENT_BLUE), BorderLayout.NORTH);
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(buildStatCards(true));
        center.add(Box.createRigidArea(new Dimension(0, 24)));
        center.add(buildSectionTitle("Admin Quick Actions"));
        center.add(Box.createRigidArea(new Dimension(0, 12)));
        center.add(buildAdminActionCards());
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildTopBar(String title, String subtitle, Color accent) {
        JPanel bar = new JPanel(new BorderLayout(0, 0));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 4)); left.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 24)); t.setForeground(UITheme.TEXT_DARK);
        JLabel s = new JLabel(subtitle);
        s.setFont(UITheme.FONT_BODY); s.setForeground(UITheme.TEXT_MUTED);
        left.add(t); left.add(s);
        UITheme.RoundedButton refresh = new UITheme.RoundedButton("  \u21BB  Refresh", accent);
        refresh.setPreferredSize(new Dimension(128, 38));
        refresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refresh.addActionListener(e -> loadStats());
        bar.add(left, BorderLayout.WEST);
        bar.add(refresh, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildStatCards(boolean admin) {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        row.setPreferredSize(new Dimension(Integer.MAX_VALUE, 110));
        if (admin) {
            c1 = new UITheme.StatCard("\uD83D\uDE8C", "Total Buses",    "...", UITheme.ACCENT_BLUE);
            c2 = new UITheme.StatCard("\uD83C\uDFDF", "Total Bookings", "...", UITheme.ACCENT_GREEN);
            c3 = new UITheme.StatCard("\uD83D\uDC65", "Total Users",    "...", UITheme.ACCENT_PURPLE);
            c4 = new UITheme.StatCard("\uD83D\uDCB0", "Total Revenue",  "...", UITheme.ACCENT_YELLOW);
        } else {
            c1 = new UITheme.StatCard("\uD83D\uDE8C", "Total Buses",    "...", UITheme.ACCENT_BLUE);
            c2 = new UITheme.StatCard("\uD83C\uDFDF", "My Bookings",    "...", UITheme.ACCENT_GREEN);
            c3 = new UITheme.StatCard("\uD83D\uDCBA", "Avail. Seats",   "...", UITheme.ACCENT_CYAN);
            c4 = new UITheme.StatCard("\uD83D\uDCB0", "Total Spent",    "...", UITheme.ACCENT_PURPLE);
        }
        row.add(c1); row.add(c2); row.add(c3); row.add(c4);
        return row;
    }

    private JPanel buildSectionTitle(String text) {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(UITheme.TEXT_DARK);
        p.add(l, BorderLayout.WEST);
        return p;
    }

    private JPanel buildUserActionCards() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.add(actionCard("\uD83C\uDFDF", "Book Ticket",   "Find & book your next trip",    UITheme.ACCENT_GREEN,
            e -> { setVisible(false); new BusSearchFrame(this).setVisible(true); }));
        grid.add(actionCard("\uD83D\uDCC4", "My Bookings",   "View all your reservations",    UITheme.ACCENT_BLUE,
            e -> { setVisible(false); new ViewCancelBookingFrame(this).setVisible(true); }));
        grid.add(actionCard("\u274C",       "Cancel Ticket", "Request cancellation & refund", UITheme.ACCENT_RED,
            e -> { setVisible(false); new ViewCancelBookingFrame(this).setVisible(true); }));
        grid.add(actionCard("\uD83D\uDC64", "My Profile",    "Update your personal details",  UITheme.ACCENT_PURPLE,
            e -> { setVisible(false); new ProfileFrame(this).setVisible(true); }));
        return grid;
    }

    private JPanel buildAdminActionCards() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.add(actionCard("\uD83D\uDE8C", "Manage Buses",   "Add, edit & remove buses",    UITheme.ACCENT_BLUE,
            e -> new AdminBusManagementFrame().setVisible(true)));
        grid.add(actionCard("\uD83D\uDCCB", "All Bookings",   "View & manage reservations",  UITheme.ACCENT_GREEN,
            e -> new AdminBookingsFrame(this).setVisible(true)));
        grid.add(actionCard("\uD83D\uDC65", "Manage Users",   "User accounts & permissions", UITheme.ACCENT_PURPLE,
            e -> new AdminUsersFrame(this).setVisible(true)));
        grid.add(actionCard("\uD83D\uDCB0", "Revenue Report", "Analytics & financial data",  UITheme.ACCENT_YELLOW,
            e -> new AdminRevenueFrame(this).setVisible(true)));
        return grid;
    }

    private JPanel actionCard(String emoji, String title, String subtitle, Color ac, ActionListener al) {
        JPanel card = new JPanel(new BorderLayout(16, 0)) {
            boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                public void mouseClicked(MouseEvent e) { al.actionPerformed(new ActionEvent(this, 0, "")); }
            }); }
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shadow
                g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), hovered ? 28 : 14));
                g2.fill(new RoundRectangle2D.Double(3, 5, getWidth() - 4, getHeight() - 4, 16, 16));
                // White card body
                g2.setColor(hovered ? new Color(252, 252, 255) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 3, getHeight() - 3, 16, 16));
                // Left accent stripe
                GradientPaint stripe = new GradientPaint(0, 0, ac, 0, getHeight(),
                    new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 70));
                g2.setPaint(stripe);
                g2.fill(new RoundRectangle2D.Double(0, 0, 5, getHeight() - 3, 4, 4));
                // Border
                g2.setColor(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), hovered ? 80 : 35));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 4, getHeight() - 4, 16, 16));
                super.paintComponent(g); g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 18));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ── Icon: plain emoji — NO oval/circle background ──────────────
        JLabel iconLbl = new JLabel(emoji, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        iconLbl.setPreferredSize(new Dimension(46, 46));

        // Text block
        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(UITheme.TEXT_DARK);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(UITheme.TEXT_MUTED);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        textBlock.add(Box.createVerticalGlue());
        textBlock.add(titleLbl);
        textBlock.add(Box.createRigidArea(new Dimension(0, 4)));
        textBlock.add(subLbl);
        textBlock.add(Box.createVerticalGlue());

        // Arrow
        JLabel arrow = new JLabel("\u203A");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 22));
        arrow.setForeground(new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 150));

        card.add(iconLbl,   BorderLayout.WEST);
        card.add(textBlock, BorderLayout.CENTER);
        card.add(arrow,     BorderLayout.EAST);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════
    //  DATABASE
    // ══════════════════════════════════════════════════════════════════
    private void loadStats() {
        new SwingWorker<long[], Void>() {
            protected long[] doInBackground() throws Exception {
                long[] s = new long[4];
                try (Connection con = DBConnection.getConnection()) {
                    if (isAdmin) {
                        s[0] = q(con, "SELECT COUNT(*) FROM buses");
                        s[1] = q(con, "SELECT COUNT(*) FROM bookings WHERE booking_status='Confirmed'");
                        s[2] = q(con, "SELECT COUNT(*) FROM register_user");
                        s[3] = q(con, "SELECT COALESCE(SUM(total_fare),0) FROM bookings WHERE booking_status='Confirmed'");
                    } else {
                        s[0] = q(con, "SELECT COUNT(*) FROM buses");
                        s[1] = q(con, "SELECT COUNT(*) FROM bookings WHERE user_id=" + UserSession.getUserId() + " AND booking_status='Confirmed'");
                        s[2] = q(con, "SELECT COALESCE(SUM(seats),0) FROM buses");
                        s[3] = q(con, "SELECT COALESCE(SUM(total_fare),0) FROM bookings WHERE user_id=" + UserSession.getUserId() + " AND booking_status='Confirmed'");
                    }
                }
                return s;
            }
            protected void done() {
                try {
                    long[] s = get();
                    c1.setValue(String.valueOf(s[0]));
                    c2.setValue(String.valueOf(s[1]));
                    c3.setValue(String.valueOf(s[2]));
                    c4.setValue("\u20B9 " + String.format("%,d", s[3]));
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private long q(Connection c, String sql) throws Exception {
        ResultSet r = c.prepareStatement(sql).executeQuery();
        return r.next() ? r.getLong(1) : 0;
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════
    private String firstName() {
        String n = UserSession.getName();
        return (n != null && !n.isEmpty()) ? n.split(" ")[0] : "there";
    }

    private void showOffers() {
        JOptionPane.showMessageDialog(this,
            "Current Offers:\n\n" +
            "  FIRST10   - 10% off your first booking\n" +
            "  WEEKEND20 - 20% off all weekend trips\n" +
            "  STUDENT15 - 15% off with student ID\n" +
            "  MONSOON25 - 25% off this monsoon season",
            "Offers & Deals", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHelp() {
        JOptionPane.showMessageDialog(this,
            "Help & Support\n\n" +
            "  Email : support@busway.in\n" +
            "  Phone : 1800-101-2006  (Toll Free)\n" +
            "  Hours : Mon-Sat, 8 AM - 10 PM",
            "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAdminStats() {
        try (Connection c = DBConnection.getConnection()) {
            long today     = q(c, "SELECT COUNT(*) FROM bookings WHERE DATE(booking_date)=CURDATE()");
            long cancelled = q(c, "SELECT COUNT(*) FROM bookings WHERE booking_status='Cancelled'");
            long seats     = q(c, "SELECT COALESCE(SUM(seats),0) FROM buses");
            JOptionPane.showMessageDialog(this,
                "Live System Stats:\n\n" +
                "  Bookings Today:        " + today + "\n" +
                "  Cancelled Bookings:    " + cancelled + "\n" +
                "  Total Seats Available: " + seats,
                "System Stats", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}