import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;

public class ViewBookingFrame extends JFrame {

    private Dashboard dashboard;
    private JTable bookingTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private JLabel totalLabel;

    public ViewBookingFrame(Dashboard dashboard) {
        this.dashboard = dashboard;

        setTitle("View Bookings - Ticket Booking System");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.TEXT_WHITE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("All Bookings");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel searchLbl = new JLabel("Search: ");
        searchLbl.setForeground(Color.WHITE);
        searchField = new JTextField(15);
        JButton refreshBtn = new JButton("⟳ Refresh");
        refreshBtn.setBackground(UITheme.ACCENT_YELLOW);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);

        JButton backBtn = new JButton("Back");
        backBtn.setBackground(UITheme.TEXT_DARK);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(refreshBtn);
        searchPanel.add(backBtn);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // TABLE
        model = new DefaultTableModel(
                new String[]{"Booking ID", "Passenger Name", "Phone", "Age", "Gender",
                        "Bus No", "Route", "Type", "Fare (₹)", "Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        bookingTable = new JTable(model);
        bookingTable.setRowHeight(30);
        bookingTable.setFont(new Font("Arial", Font.PLAIN, 12));
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setGridColor(new Color(220, 220, 220));

        // Header styling
        bookingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        bookingTable.getTableHeader().setBackground(UITheme.TEXT_WHITE);
        bookingTable.getTableHeader().setForeground(Color.WHITE);
        bookingTable.getTableHeader().setPreferredSize(new Dimension(0, 35));

        // Alternating row colors
        bookingTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(0, 87, 184, 50));
                    c.setForeground(UITheme.TEXT_WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : UITheme.ACCENT_BLUE);
                    c.setForeground(UITheme.TEXT_WHITE);
                }
                // ── status column colour ──
                if (column == 10 && value != null) {
                    String status = value.toString();
                    if (status.equals("Confirmed"))      c.setForeground(UITheme.ACCENT_GREEN);
                    else if (status.equals("Cancelled")) c.setForeground(UITheme.ACCENT_RED);
                    else c.setForeground(UITheme.TEXT_MUTED);
                }
                // ── fare column colour ──
                if (column == 8) c.setForeground(UITheme.ACCENT_YELLOW);
                return c;
            }
        });

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        bookingTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        add(scrollPane, BorderLayout.CENTER);

        // FOOTER
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(UITheme.ACCENT_BLUE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        totalLabel = new JLabel("Total Bookings: 0");
        totalLabel.setFont(UITheme.FONT_BOLD); totalLabel.setForeground(UITheme.TEXT_WHITE); //;
        footerPanel.add(totalLabel, BorderLayout.WEST);
        add(footerPanel, BorderLayout.SOUTH);

        // LISTENERS
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        refreshBtn.addActionListener(e -> loadBookings());
        backBtn.addActionListener(e -> { dispose(); dashboard.setVisible(true); });

        // Load data on open
        loadBookings();
    }

    private void loadBookings() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT b.booking_id, b.passenger_name, b.phone, b.age, b.gender, " +
                         "bu.bus_no, bu.route, bu.type, b.fare, b.booking_date, b.status " +
                         "FROM bookings b JOIN buses bu ON b.bus_id = bu.bus_id " +
                         "ORDER BY b.booking_id DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int count = 0;
            while (rs.next()) {
                count++;
                model.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("passenger_name"),
                        rs.getString("phone"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("bus_no"),
                        rs.getString("route"),
                        rs.getString("type"),
                        "₹ " + rs.getDouble("fare"),
                        rs.getDate("booking_date"),
                        rs.getString("status")
                });
            }
            totalLabel.setText("Total Bookings: " + count);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage());
        }
    }

    private void filterTable() {
        String text = searchField.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) bookingTable.getRowSorter();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
}