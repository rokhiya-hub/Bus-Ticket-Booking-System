import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.UUID;

public class PaymentConfirmationFrame extends JFrame {

    public PaymentConfirmationFrame(Dashboard dashboard, int busId, String busNo, String route,
            String seatNo, String fare, String passengerName,
            String phone, int age, String gender, String paymentMethod) {

        setTitle("Booking Confirmation");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 30, 47));
        header.setPreferredSize(new Dimension(getWidth(), 65));
        JLabel titleLbl = new JLabel("  Booking Confirmation");
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(titleLbl, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // MAIN PANEL
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setBackground(new Color(244, 246, 249));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // LEFT — Booking Summary Card
        JPanel bookingCard = createCard("Booking Summary");
        bookingCard.add(makeInfoRow("Passenger", passengerName));
        bookingCard.add(makeInfoRow("Phone", phone));
        bookingCard.add(makeInfoRow("Age / Gender", age + " / " + gender));
        bookingCard.add(makeInfoRow("Bus No", busNo));
        bookingCard.add(makeInfoRow("Route", route));
        bookingCard.add(makeInfoRow("Seat", seatNo));
        bookingCard.add(makeInfoRow("Fare", "Rs " + fare));
        bookingCard.add(makeInfoRow("Payment", paymentMethod));

        // RIGHT — Payment Confirmation Card
        JPanel payCard = createCard("Payment Status");

        JLabel statusIcon = new JLabel("✓", SwingConstants.CENTER);
        statusIcon.setFont(new Font("Segoe UI", Font.BOLD, 60));
        statusIcon.setForeground(new Color(40, 167, 69));

        JLabel statusMsg = new JLabel("Payment via " + paymentMethod + " successful!", SwingConstants.CENTER);
        statusMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusMsg.setForeground(Color.DARK_GRAY);

        JLabel amountLbl = new JLabel("Rs " + fare, SwingConstants.CENTER);
        amountLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        amountLbl.setForeground(new Color(40, 167, 69));

        payCard.add(Box.createVerticalGlue());
        payCard.add(statusIcon);
        payCard.add(Box.createRigidArea(new Dimension(0, 10)));
        payCard.add(statusMsg);
        payCard.add(Box.createRigidArea(new Dimension(0, 10)));
        payCard.add(amountLbl);
        payCard.add(Box.createVerticalGlue());

        mainPanel.add(bookingCard);
        mainPanel.add(payCard);
        add(mainPanel, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        footer.setBackground(new Color(30, 30, 47));

        JButton dashBtn = new JButton("  Back to Dashboard  ");
        dashBtn.setBackground(Color.GRAY);
        dashBtn.setForeground(Color.WHITE);
        dashBtn.setFocusPainted(false);

        JButton confirmBtn = new JButton("  Confirm Booking  ");
        confirmBtn.setBackground(new Color(40, 167, 69));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmBtn.setFocusPainted(false);

        footer.add(dashBtn);
        footer.add(confirmBtn);
        add(footer, BorderLayout.SOUTH);

        // ACTIONS
        dashBtn.addActionListener(e -> {
            dispose();
            dashboard.setVisible(true);
        });

        confirmBtn.addActionListener(e -> {
            String ticketId = saveBookingToDB(busId, seatNo, passengerName, phone, age, gender, Double.parseDouble(fare));
            if (ticketId != null) {
                JOptionPane.showMessageDialog(this,
                    "Booking Confirmed!\n\nTicket ID: " + ticketId +
                    "\nPassenger: " + passengerName +
                    "\nSeat: " + seatNo +
                    "\nRoute: " + route,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                confirmBtn.setEnabled(false);
                confirmBtn.setText("  Booked  ");
            }
        });
    }

    private String saveBookingToDB(int busId, String seatNo, String passengerName,
                                    String phone, int age, String gender, double fare) {
        try (Connection con = DBConnection.getConnection()) {

            // Insert booking record
            String sql = "INSERT INTO bookings (user_id, bus_id, passenger_name, phone, age, gender, " +
                         "booking_date, status, fare, seat_no) VALUES (?, ?, ?, ?, ?, ?, CURDATE(), 'Confirmed', ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, UserSession.getUserId());
            ps.setInt(2, busId);
            ps.setString(3, passengerName);
            ps.setString(4, phone);
            ps.setInt(5, age);
            ps.setString(6, gender);
            ps.setDouble(7, fare);
            ps.setString(8, seatNo);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int bookingId = -1;
            if (keys.next()) bookingId = keys.getInt(1);

            // Decrease seat count
            PreparedStatement seatPs = con.prepareStatement(
                "UPDATE buses SET seats = seats - 1 WHERE bus_id = ?");
            seatPs.setInt(1, busId);
            seatPs.executeUpdate();

            return "TKT-" + String.format("%06d", bookingId);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Booking failed: " + e.getMessage());
            return null;
        }
    }

    private JPanel createCard(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(new Color(45, 137, 239));
        titleLbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 137, 239)));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        return panel;
    }

    private JPanel makeInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(Color.GRAY);
        lbl.setPreferredSize(new Dimension(100, 20));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 12));
        val.setForeground(new Color(30, 30, 47));
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }
}