import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class BusSearchFrame extends JFrame {
    private Dashboard dashboard;
    private DefaultTableModel model;
    private JTable    busTable;
    private JComboBox<String> srcBox, dstBox, boardBox, dropBox;
    private JTextField dateF;
    private JLabel    statusLbl;
    private UITheme.RoundedButton bookBtn;

    private static final String[] CITIES =
        {"Select City","Hyderabad","Bangalore","Chennai","Pune"};

    public BusSearchFrame(Dashboard dashboard) {
        this.dashboard = dashboard;
        setTitle("BusWay — Search Buses");
        setSize(1100, 670);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        UITheme.GradientPanel root = new UITheme.GradientPanel(UITheme.BG_DARK, new Color(8,14,28), false);
        root.setLayout(new BorderLayout());
        setContentPane(root);

        /* ── HEADER ─────────────────────────────────────────── */
        JPanel hdr = UITheme.buildHeader("🔍  Search Buses",
            "Find the best buses for your route");
        UITheme.RoundedButton backBtn = new UITheme.RoundedButton("← Back", UITheme.BG_CARD);
        backBtn.setForeground(UITheme.TEXT_MUTED);
        JPanel hp = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        hp.setOpaque(false); hp.add(backBtn);
        hdr.add(hp, BorderLayout.EAST);
        root.add(hdr, BorderLayout.NORTH);

        /* ── SEARCH CARD ─────────────────────────────────────── */
        UITheme.RoundedPanel searchCard = new UITheme.RoundedPanel(14, UITheme.BG_CARD);
        searchCard.setLayout(new GridBagLayout());
        searchCard.setBorder(BorderFactory.createEmptyBorder(14,18,14,18));
        GridBagConstraints sc = new GridBagConstraints();
        sc.insets = new Insets(5,8,5,8); sc.fill = GridBagConstraints.HORIZONTAL;

        srcBox   = UITheme.createComboBox(CITIES);   srcBox.setPreferredSize(new Dimension(155,36));
        dstBox   = UITheme.createComboBox(CITIES);   dstBox.setPreferredSize(new Dimension(155,36));
        boardBox = UITheme.createComboBox(new String[]{"-- Select Boarding --"});
        dropBox  = UITheme.createComboBox(new String[]{"-- Select Dropping --"});
        boardBox.setPreferredSize(new Dimension(210,36));
        dropBox.setPreferredSize (new Dimension(210,36));
        dateF = UITheme.createTextField("");
        dateF.setText(java.time.LocalDate.now().toString());
        dateF.setPreferredSize(new Dimension(210,44));

        UITheme.RoundedButton searchBtn = new UITheme.RoundedButton("  Search  ", UITheme.ACCENT_BLUE);

        // Row 1: From → To  Date  [Search button spans both rows]
        sc.gridy=0;
        sc.gridx=0; searchCard.add(UITheme.createFieldLabel("From"), sc);
        sc.gridx=1; searchCard.add(srcBox, sc);
        sc.gridx=2; JLabel ar = new JLabel("→",SwingConstants.CENTER);
        ar.setFont(new Font("Segoe UI",Font.BOLD,18)); ar.setForeground(UITheme.ACCENT_CYAN);
        searchCard.add(ar, sc);
        sc.gridx=3; searchCard.add(UITheme.createFieldLabel("To"), sc);
        sc.gridx=4; searchCard.add(dstBox, sc);
        sc.gridx=5; searchCard.add(UITheme.createFieldLabel("Date (YYYY-MM-DD)"), sc);
        sc.gridx=6; searchCard.add(dateF, sc);
        sc.gridx=7; sc.gridheight=2; searchCard.add(searchBtn, sc); sc.gridheight=1;

        // Row 2: Boarding  Dropping
        sc.gridy=1;
        sc.gridx=0; searchCard.add(UITheme.createFieldLabel("Boarding Point"), sc);
        sc.gridx=1; sc.gridwidth=3; searchCard.add(boardBox, sc); sc.gridwidth=1;
        sc.gridx=4; searchCard.add(UITheme.createFieldLabel("Dropping Point"), sc);
        sc.gridx=5; sc.gridwidth=2; searchCard.add(dropBox, sc);  sc.gridwidth=1;

        JPanel swrap = new JPanel(new BorderLayout()); swrap.setOpaque(false);
        swrap.setBorder(BorderFactory.createEmptyBorder(12,18,2,18));
        swrap.add(searchCard);

        /* ── STATUS + TABLE ──────────────────────────────────── */
        statusLbl = new JLabel(
            "Select source and destination then click Search.",
            SwingConstants.CENTER);
        statusLbl.setFont(new Font("Segoe UI",Font.ITALIC,12));
        statusLbl.setForeground(UITheme.TEXT_MUTED);
        statusLbl.setBorder(BorderFactory.createEmptyBorder(8,0,4,0));

        model = new DefaultTableModel(
            new String[]{"ID","Bus No","Operator","Route","Type","Dep","Arr","Fare (₹)","Seats"}, 0) {
            public boolean isCellEditable(int r,int c){ return false; }
        };
        busTable = new JTable(model);
        UITheme.styleTable(busTable);
        busTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        busTable.getColumnModel().getColumn(0).setMinWidth(0);
        busTable.getColumnModel().getColumn(0).setMaxWidth(0);

        busTable.setDefaultRenderer(Object.class,
            new javax.swing.table.DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable t, Object val,
                        boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                    setOpaque(true);

                    // ── Background: solid colours only (no alpha) ──
                    if (sel) {
                        setBackground(new Color(37, 99, 235));   // solid blue
                        setForeground(Color.WHITE);
                    } else {
                        setBackground(row % 2 == 0 ? Color.WHITE : new Color(241, 245, 255));
                        setForeground(UITheme.TEXT_DARK);        // dark text on light bg
                    }

                    // ── Fare column: orange accent ──
                    if (col == 7) {
                        setForeground(sel ? Color.WHITE : UITheme.ACCENT_ORANGE);
                        setFont(UITheme.FONT_BOLD);
                    }

                    // ── Seats column: colour-coded ──
                    if (col == 8 && val != null) {
                        try {
                            int s = Integer.parseInt(val.toString());
                            if (!sel) setForeground(s == 0 ? UITheme.ACCENT_RED
                                                 : s <= 5  ? UITheme.ACCENT_ORANGE
                                                           : UITheme.ACCENT_GREEN);
                            setFont(UITheme.FONT_BOLD);
                        } catch (Exception ignored) {}
                    }

                    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                    return this;
                }
            });

        JPanel ctr = new JPanel(new BorderLayout()); ctr.setOpaque(false);
        ctr.setBorder(BorderFactory.createEmptyBorder(0,18,0,18));
        ctr.add(statusLbl, BorderLayout.NORTH);
        ctr.add(UITheme.darkScrollPane(busTable), BorderLayout.CENTER);

        /* ── FOOTER ──────────────────────────────────────────── */
        JPanel footer = new JPanel(new BorderLayout()); footer.setBackground(UITheme.BG_SIDEBAR);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0,UITheme.BORDER_COL),
            BorderFactory.createEmptyBorder(12,25,12,25)));
        bookBtn = new UITheme.RoundedButton("🎟  Book This Bus", UITheme.ACCENT_GREEN);
        bookBtn.setEnabled(false); bookBtn.setPreferredSize(new Dimension(200,40));
        footer.add(UITheme.createLabel("Select a bus then click Book"), BorderLayout.WEST);
        footer.add(bookBtn, BorderLayout.EAST);

        JPanel body = new JPanel(new BorderLayout()); body.setOpaque(false);
        body.add(swrap, BorderLayout.NORTH);
        body.add(ctr,   BorderLayout.CENTER);
        root.add(body,   BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        /* ── LISTENERS ───────────────────────────────────────── */
        srcBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                loadStops((String)srcBox.getSelectedItem(), boardBox);
        });
        dstBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                loadStops((String)dstBox.getSelectedItem(), dropBox);
        });
        searchBtn.addActionListener(e -> doSearch());
        dateF.addActionListener(e    -> doSearch());

        busTable.getSelectionModel().addListSelectionListener(e -> {
            int r = busTable.getSelectedRow();
            if (r != -1) {
                int seats = Integer.parseInt(model.getValueAt(r,8).toString());
                bookBtn.setEnabled(seats > 0);
                bookBtn.setBaseColor(seats > 0 ? UITheme.ACCENT_GREEN : UITheme.ACCENT_RED);
            }
        });

        bookBtn.addActionListener(e -> openSeatSelection());

        backBtn.addActionListener(e -> {
            dashboard.setVisible(true);
            dispose();
        });
    }

    /* ── LOAD STOPS FROM DB ──────────────────────────────────── */
    private void loadStops(String city, JComboBox<String> box) {
        box.removeAllItems();
        if (city == null || city.startsWith("Select")) { box.addItem("-- Select --"); return; }
        box.addItem("-- Select --");
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT stop_name FROM city_stops WHERE city=? ORDER BY stop_name");
            ps.setString(1, city); ResultSet rs = ps.executeQuery();
            while (rs.next()) box.addItem(rs.getString("stop_name"));
        } catch (Exception ex) {
            // Fallback local data
            Map<String,String[]> fb = new HashMap<>();
            fb.put("Hyderabad", new String[]{"MGBS","Secunderabad","LB Nagar","Dilsukhnagar","Kukatpally","Miyapur","Gachibowli"});
            fb.put("Bangalore", new String[]{"Majestic","Silk Board","Electronic City","Whitefield","Hebbal","Marathahalli"});
            fb.put("Chennai",   new String[]{"CMBT","Tambaram","Anna Nagar","Guindy","Chromepet"});
            fb.put("Pune",      new String[]{"Swargate","Shivajinagar","Hinjewadi","Wakad","Hadapsar"});
            for (String s : fb.getOrDefault(city, new String[]{})) box.addItem(s);
        }
    }

    /* ── SEARCH ──────────────────────────────────────────────── */
    private void doSearch() {
        String src  = (String) srcBox.getSelectedItem();
        String dst  = (String) dstBox.getSelectedItem();
        String date = dateF.getText().trim();
        model.setRowCount(0); bookBtn.setEnabled(false);

        if (src==null||src.startsWith("Select")||dst==null||dst.startsWith("Select")) {
            setStatus("Please select Source and Destination.", UITheme.ACCENT_ORANGE); return; }
        if (src.equals(dst)) {
            setStatus("Source and destination cannot be same!", UITheme.ACCENT_RED); return; }
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            setStatus("Enter journey date as YYYY-MM-DD.", UITheme.ACCENT_RED); return; }

        setStatus("Searching buses...", UITheme.TEXT_MUTED);
        String route = src + " - " + dst;

        new SwingWorker<Void,Object[]>(){
            protected Void doInBackground() throws Exception {
                try (Connection con = DBConnection.getConnection()) {
                    PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM buses WHERE route=? ORDER BY fare");
                    ps.setString(1, route); ResultSet rs = ps.executeQuery();
                    while (rs.next())
                        publish(new Object[]{rs.getInt("bus_id"),rs.getString("bus_no"),
                            rs.getString("operator_name"),rs.getString("route"),
                            rs.getString("type"),rs.getString("departure_time"),
                            rs.getString("arrival_time"),rs.getDouble("fare"),rs.getInt("seats")});
                } return null;
            }
            protected void process(java.util.List<Object[]> chunks){
                for (Object[] r : chunks) model.addRow(r);
            }
            protected void done(){
                if (model.getRowCount()==0)
                    setStatus("No buses found for " + src + " → " + dst + " on " + date, UITheme.ACCENT_ORANGE);
                else
                    setStatus("Found " + model.getRowCount() + " bus(es) for " + src + " → " + dst
                        + " on " + date, UITheme.ACCENT_GREEN);
            }
        }.execute();
    }

    /* ── OPEN SEAT SELECTION ─────────────────────────────────── */
    private void openSeatSelection() {
        int row = busTable.getSelectedRow();
        if (row == -1) return;

        String boarding = (String) boardBox.getSelectedItem();
        String dropping = (String) dropBox.getSelectedItem();

        if (boarding==null || boarding.startsWith("--")) {
            JOptionPane.showMessageDialog(this,
                "Please select a Boarding Point before booking.",
                "Boarding Point Required", JOptionPane.WARNING_MESSAGE); return;
        }
        if (dropping==null || dropping.startsWith("--")) {
            JOptionPane.showMessageDialog(this,
                "Please select a Dropping Point before booking.",
                "Dropping Point Required", JOptionPane.WARNING_MESSAGE); return;
        }

        int    busId = Integer.parseInt(model.getValueAt(row,0).toString());
        String busNo = (String) model.getValueAt(row,1);
        String route = (String) model.getValueAt(row,3);
        String type  = (String) model.getValueAt(row,4);
        double fare  = Double.parseDouble(model.getValueAt(row,7).toString());
        String date  = dateF.getText().trim();

        try {
            SeatSelectionFrame sf = new SeatSelectionFrame(
                dashboard, busId, busNo, route, type, fare, boarding, dropping, date);
            sf.setVisible(true);   // show FIRST
            dispose();             // then close
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setStatus(String m, Color c) {
        statusLbl.setForeground(c); statusLbl.setText(m);
    }
}