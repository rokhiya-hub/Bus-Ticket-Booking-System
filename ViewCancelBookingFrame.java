import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ViewCancelBookingFrame extends JFrame {
    private Dashboard dashboard;
    private DefaultTableModel model;
    private JTable table;
    private JLabel totalLbl;
    private UITheme.RoundedButton cancelBtn;

    public ViewCancelBookingFrame(Dashboard dashboard){
        this.dashboard=dashboard;
        setTitle("BusWay — My Bookings"); setSize(1100,650);
        setLocationRelativeTo(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.GradientPanel root=new UITheme.GradientPanel(UITheme.BG_DARK,new Color(8,14,28),false);
        root.setLayout(new BorderLayout()); setContentPane(root);

        JPanel hdr=UITheme.buildHeader("📄  My Bookings","All your trips in one place");
        JPanel btnRow=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,8)); btnRow.setOpaque(false);
        UITheme.RoundedButton rfBtn=new UITheme.RoundedButton("⟳ Refresh",UITheme.ACCENT_BLUE);
        UITheme.RoundedButton backBtn=new UITheme.RoundedButton("← Back",UITheme.BG_CARD); backBtn.setForeground(UITheme.TEXT_MUTED);
        btnRow.add(rfBtn); btnRow.add(backBtn); hdr.add(btnRow,BorderLayout.EAST); root.add(hdr,BorderLayout.NORTH);

        model=new DefaultTableModel(new String[]{"BID","Ticket ID","Bus No","Route","Boarding","Dropping","Seats","Fare","Date","Payment","Status"},0){
            public boolean isCellEditable(int r,int c){ return false; }
        };
        table=new JTable(model); UITheme.styleTable(table); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMinWidth(0); table.getColumnModel().getColumn(0).setMaxWidth(0);

        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object val,boolean sel,boolean foc,int row,int col){
                Component c=super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                c.setBackground(sel?new Color(64,156,255,80):UITheme.BG_CARD);
                c.setForeground(UITheme.TEXT_WHITE);
                if(col==10&&val!=null){ String s=val.toString();
                    c.setForeground(s.equals("Confirmed")?UITheme.ACCENT_GREEN:UITheme.ACCENT_RED);
                    ((JLabel)c).setFont(UITheme.FONT_BOLD); }
                if(col==7) c.setForeground(UITheme.ACCENT_CYAN);
                ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,10,0,10)); return c;
            }
        });

        JScrollPane sp=UITheme.darkScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(12,20,0,20)); root.add(sp,BorderLayout.CENTER);

        JPanel footer=new JPanel(new BorderLayout()); footer.setBackground(UITheme.BG_SIDEBAR);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0,UITheme.BORDER_COL),
            BorderFactory.createEmptyBorder(12,25,12,25)));
        totalLbl=new JLabel("Total: 0"); totalLbl.setFont(UITheme.FONT_BOLD); totalLbl.setForeground(UITheme.TEXT_MUTED);
        cancelBtn=new UITheme.RoundedButton("❌  Cancel Booking",UITheme.ACCENT_RED);
        cancelBtn.setEnabled(false); cancelBtn.setPreferredSize(new Dimension(200,40));
        footer.add(totalLbl,BorderLayout.WEST); footer.add(cancelBtn,BorderLayout.EAST);
        root.add(footer,BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e->{
            int r=table.getSelectedRow();
            if(r!=-1){ String s=model.getValueAt(r,10).toString(); cancelBtn.setEnabled(s.equals("Confirmed")); }
        });
        cancelBtn.addActionListener(e->doCancel());
        rfBtn.addActionListener(e->loadData());
        backBtn.addActionListener(e->{ dispose(); dashboard.setVisible(true); });
        loadData();
    }

    private void loadData(){
        model.setRowCount(0); cancelBtn.setEnabled(false);
        try(Connection con=DBConnection.getConnection()){
            PreparedStatement ps=con.prepareStatement(
                "SELECT b.booking_id, bu.bus_no, bu.route, b.boarding_point, b.dropping_point, "+
                "GROUP_CONCAT(p.seat_no ORDER BY p.seat_no SEPARATOR ', ') AS seats, "+
                "b.total_fare, b.journey_date, b.payment_method, b.booking_status "+
                "FROM bookings b JOIN buses bu ON b.bus_id=bu.bus_id "+
                "LEFT JOIN passengers p ON p.booking_id=b.booking_id "+
                "WHERE b.user_id=? GROUP BY b.booking_id ORDER BY b.booking_id DESC");
            ps.setInt(1,UserSession.getUserId()); ResultSet rs=ps.executeQuery();
            int cnt=0;
            while(rs.next()){ cnt++;
                int id=rs.getInt("booking_id");
                model.addRow(new Object[]{id,"BW-"+String.format("%07d",id),
                    rs.getString("bus_no"),rs.getString("route"),
                    rs.getString("boarding_point"),rs.getString("dropping_point"),
                    rs.getString("seats"),"₹ "+rs.getDouble("total_fare"),
                    rs.getDate("journey_date"),rs.getString("payment_method"),rs.getString("booking_status")});
            }
            totalLbl.setText("Total Bookings: "+cnt);
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }

    private void doCancel(){
        int row=table.getSelectedRow(); if(row==-1) return;
        int    bid = Integer.parseInt(model.getValueAt(row,0).toString());
        String tid = model.getValueAt(row,1).toString();

        // ── Fetch total_fare so we can show 50% refund amount before confirming ──
        double totalFare = 0;
        try(Connection con=DBConnection.getConnection()){
            ResultSet fr=con.prepareStatement(
                "SELECT total_fare FROM bookings WHERE booking_id="+bid).executeQuery();
            if(fr.next()) totalFare=fr.getDouble("total_fare");
        } catch(Exception ignored){}

        double refundAmt = totalFare * 0.50;

        // ── Confirmation dialog with refund info ──
        JPanel msg = new JPanel();
        msg.setLayout(new BoxLayout(msg, BoxLayout.Y_AXIS));
        msg.setBackground(Color.WHITE);
        msg.setBorder(BorderFactory.createEmptyBorder(12,16,12,16));

        JLabel l1 = new JLabel("Cancel Ticket: " + tid);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l1.setForeground(new Color(15,23,42));
        l1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l2 = new JLabel("Total Paid:  ₹ " + String.format("%,.2f", totalFare));
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l2.setForeground(new Color(100,116,139));
        l2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l3 = new JLabel("Refund (50%):  ₹ " + String.format("%,.2f", refundAmt));
        l3.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l3.setForeground(new Color(22,163,74));   // green
        l3.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l4 = new JLabel("Cancellation Fee (50%):  ₹ " + String.format("%,.2f", refundAmt));
        l4.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l4.setForeground(new Color(220,38,38));   // red
        l4.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l5 = new JLabel("Refund will be credited within 5–7 business days.");
        l5.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        l5.setForeground(new Color(100,116,139));
        l5.setAlignmentX(Component.LEFT_ALIGNMENT);

        msg.add(l1);
        msg.add(Box.createRigidArea(new Dimension(0,8)));
        msg.add(l2);
        msg.add(l3);
        msg.add(l4);
        msg.add(Box.createRigidArea(new Dimension(0,8)));
        msg.add(l5);

        int ok = JOptionPane.showConfirmDialog(this, msg,
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(ok != JOptionPane.YES_OPTION) return;

        try(Connection con=DBConnection.getConnection()){
            // Count passengers for seat release
            ResultSet cr=con.prepareStatement(
                "SELECT COUNT(*) FROM passengers WHERE booking_id="+bid).executeQuery();
            int cnt=cr.next()?cr.getInt(1):1;
            // Get bus_id
            ResultSet br=con.prepareStatement(
                "SELECT bus_id FROM bookings WHERE booking_id="+bid).executeQuery();
            int busId=br.next()?br.getInt(1):-1;

            // Mark cancelled + store refund amount
            try {
                // Try with refund_amount column (add if not already in your DB)
                con.prepareStatement(
                    "UPDATE bookings SET booking_status='Cancelled', refund_amount="+refundAmt
                    +" WHERE booking_id="+bid).executeUpdate();
            } catch(Exception ex) {
                // Fallback if refund_amount column doesn't exist yet
                con.prepareStatement(
                    "UPDATE bookings SET booking_status='Cancelled' WHERE booking_id="+bid).executeUpdate();
            }

            // Release seats back to bus
            if(busId>0) con.prepareStatement(
                "UPDATE buses SET seats=seats+"+cnt+" WHERE bus_id="+busId).executeUpdate();

            // ── Success message with refund confirmation ──
            JPanel successMsg = new JPanel();
            successMsg.setLayout(new BoxLayout(successMsg, BoxLayout.Y_AXIS));
            successMsg.setBackground(Color.WHITE);
            successMsg.setBorder(BorderFactory.createEmptyBorder(12,16,12,16));

            JLabel s1 = new JLabel("✅  Ticket " + tid + " Cancelled Successfully");
            s1.setFont(new Font("Segoe UI", Font.BOLD, 14));
            s1.setForeground(new Color(22,163,74));
            s1.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel s2 = new JLabel(cnt + " seat(s) released.");
            s2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            s2.setForeground(new Color(100,116,139));
            s2.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel s3 = new JLabel("Refund of ₹ " + String.format("%,.2f", refundAmt) + " (50%) will be");
            s3.setFont(new Font("Segoe UI", Font.BOLD, 13));
            s3.setForeground(new Color(37,99,235));
            s3.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel s4 = new JLabel("credited to your original payment method");
            s4.setFont(new Font("Segoe UI", Font.BOLD, 13));
            s4.setForeground(new Color(37,99,235));
            s4.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel s5 = new JLabel("within 5–7 business days.");
            s5.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            s5.setForeground(new Color(100,116,139));
            s5.setAlignmentX(Component.LEFT_ALIGNMENT);

            successMsg.add(s1);
            successMsg.add(Box.createRigidArea(new Dimension(0,6)));
            successMsg.add(s2);
            successMsg.add(Box.createRigidArea(new Dimension(0,10)));
            successMsg.add(s3);
            successMsg.add(s4);
            successMsg.add(Box.createRigidArea(new Dimension(0,4)));
            successMsg.add(s5);

            JOptionPane.showMessageDialog(this, successMsg, "Cancellation Confirmed",
                JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }
}