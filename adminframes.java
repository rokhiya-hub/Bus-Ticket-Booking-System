import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

// ════════════════════════════════════════════════════════════
//  AdminBusManagementFrame
// ════════════════════════════════════════════════════════════
class AdminBusManagementFrame extends JFrame {
    private JTextField busNoF,fareF,seatsF,depF,arrF,operatorF;
    private JComboBox<String> typeBox,routeBox;
    private JTable table; private DefaultTableModel model;
    private int selectedBusId=-1;

    public AdminBusManagementFrame(){
        setTitle("Admin — Manage Buses"); setSize(1100,650); setLocationRelativeTo(null);
        UITheme.GradientPanel root=new UITheme.GradientPanel(UITheme.BG_DARK,new Color(8,14,28),false);
        root.setLayout(new BorderLayout()); setContentPane(root);
        root.add(UITheme.buildHeader("🚌  Manage Buses","Add, edit or remove buses from the system"),BorderLayout.NORTH);

        // FORM
        UITheme.RoundedPanel form=new UITheme.RoundedPanel(12,UITheme.BG_CARD);
        form.setLayout(new GridBagLayout()); form.setBorder(BorderFactory.createEmptyBorder(14,16,14,16));
        form.setPreferredSize(new Dimension(340,0));
        GridBagConstraints gc=new GridBagConstraints(); gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1; gc.insets=new Insets(5,5,5,5);

        busNoF=UITheme.createTextField(""); fareF=UITheme.createTextField(""); seatsF=UITheme.createTextField("");
        depF=UITheme.createTextField("06:00 PM"); arrF=UITheme.createTextField("06:00 AM"); operatorF=UITheme.createTextField("");
        typeBox=UITheme.createComboBox(new String[]{"AC Sleeper","AC Seater","Non-AC Seater","Luxury"});
        String[] routes={"Hyderabad - Bangalore","Hyderabad - Chennai","Hyderabad - Pune",
                         "Bangalore - Chennai","Bangalore - Hyderabad","Chennai - Hyderabad",
                         "Chennai - Pune","Pune - Hyderabad","Pune - Bangalore"};
        routeBox=UITheme.createComboBox(routes);

        int r=0;
        addFormRow(form,gc,r++,"Bus Number",busNoF); addFormRow(form,gc,r++,"Route",routeBox);
        addFormRow(form,gc,r++,"Type",typeBox); addFormRow(form,gc,r++,"Fare (₹)",fareF);
        addFormRow(form,gc,r++,"Total Seats",seatsF); addFormRow(form,gc,r++,"Departure",depF);
        addFormRow(form,gc,r++,"Arrival",arrF); addFormRow(form,gc,r++,"Operator",operatorF);

        UITheme.RoundedButton addBtn=new UITheme.RoundedButton("➕ Add Bus",UITheme.ACCENT_GREEN);
        UITheme.RoundedButton updBtn=new UITheme.RoundedButton("✏ Update",UITheme.ACCENT_BLUE);
        UITheme.RoundedButton delBtn=new UITheme.RoundedButton("🗑 Delete",UITheme.ACCENT_RED);
        UITheme.RoundedButton clrBtn=new UITheme.RoundedButton("Clear",UITheme.BG_CARD); clrBtn.setForeground(UITheme.TEXT_MUTED);
        gc.gridx=0; gc.gridy=r++; gc.gridwidth=2;
        JPanel btnP=new JPanel(new GridLayout(2,2,6,6)); btnP.setOpaque(false);
        btnP.add(addBtn); btnP.add(updBtn); btnP.add(delBtn); btnP.add(clrBtn);
        form.add(btnP,gc);

        // TABLE
        model=new DefaultTableModel(new String[]{"ID","Bus No","Route","Type","Fare","Seats","Dep","Arr","Operator"},0){
            public boolean isCellEditable(int r,int c){ return false; }
        };
        table=new JTable(model); UITheme.styleTable(table);
        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object val,boolean sel,boolean foc,int row,int col){
                Component c=super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                if(sel){
                    c.setBackground(new Color(0,87,184,50)); c.setForeground(UITheme.TEXT_DARK);
                } else {
                    c.setBackground(row%2==0?Color.WHITE:UITheme.BG_MEDIUM); c.setForeground(UITheme.TEXT_DARK);
                }
                ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,10,0,10)); return c;
            }
        });
        table.getColumnModel().getColumn(0).setMinWidth(0); table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getSelectionModel().addListSelectionListener(e->loadRowToForm());

        JPanel right=new JPanel(new BorderLayout()); right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(12,0,12,18));
        right.add(UITheme.darkScrollPane(table),BorderLayout.CENTER);

        JPanel body=new JPanel(new BorderLayout()); body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(12,12,12,0));
        body.add(form,BorderLayout.WEST); body.add(right,BorderLayout.CENTER);
        root.add(body,BorderLayout.CENTER);

        addBtn.addActionListener(e->doAdd()); updBtn.addActionListener(e->doUpdate());
        delBtn.addActionListener(e->doDelete()); clrBtn.addActionListener(e->clearForm());
        loadBuses();
    }

    private void addFormRow(JPanel p,GridBagConstraints gc,int r,String lbl,JComponent f){
        gc.gridx=0; gc.gridy=r; gc.gridwidth=1; gc.weightx=0.35; p.add(UITheme.createFieldLabel(lbl),gc);
        gc.gridx=1; gc.weightx=0.65; p.add(f,gc);
    }

    private void loadBuses(){
        model.setRowCount(0);
        try(Connection con=DBConnection.getConnection()){
            ResultSet rs=con.prepareStatement("SELECT * FROM buses ORDER BY bus_id").executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getInt("bus_id"),rs.getString("bus_no"),
                rs.getString("route"),rs.getString("type"),rs.getDouble("fare"),rs.getInt("seats"),
                rs.getString("departure_time"),rs.getString("arrival_time"),rs.getString("operator_name")});
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }
    private void loadRowToForm(){
        int r=table.getSelectedRow(); if(r==-1) return;
        selectedBusId=Integer.parseInt(model.getValueAt(r,0).toString());
        busNoF.setText((String)model.getValueAt(r,1));
        routeBox.setSelectedItem(model.getValueAt(r,2));
        typeBox.setSelectedItem(model.getValueAt(r,3));
        fareF.setText(model.getValueAt(r,4).toString());
        seatsF.setText(model.getValueAt(r,5).toString());
        depF.setText((String)model.getValueAt(r,6));
        arrF.setText((String)model.getValueAt(r,7));
        operatorF.setText(model.getValueAt(r,8)!=null?(String)model.getValueAt(r,8):"");
    }
    private void doAdd(){
        try(Connection con=DBConnection.getConnection()){
            PreparedStatement ps=con.prepareStatement("INSERT INTO buses(bus_no,route,type,fare,seats,total_seats,departure_time,arrival_time,operator_name) VALUES(?,?,?,?,?,?,?,?,?)");
            int s=Integer.parseInt(seatsF.getText().trim());
            ps.setString(1,busNoF.getText().trim()); ps.setString(2,(String)routeBox.getSelectedItem());
            ps.setString(3,(String)typeBox.getSelectedItem()); ps.setDouble(4,Double.parseDouble(fareF.getText().trim()));
            ps.setInt(5,s); ps.setInt(6,s); ps.setString(7,depF.getText().trim());
            ps.setString(8,arrF.getText().trim()); ps.setString(9,operatorF.getText().trim());
            ps.executeUpdate(); JOptionPane.showMessageDialog(this,"✓  Bus added!"); clearForm(); loadBuses();
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }
    private void doUpdate(){
        if(selectedBusId==-1){ JOptionPane.showMessageDialog(this,"Select a bus first."); return; }
        try(Connection con=DBConnection.getConnection()){
            PreparedStatement ps=con.prepareStatement("UPDATE buses SET bus_no=?,route=?,type=?,fare=?,departure_time=?,arrival_time=?,operator_name=? WHERE bus_id=?");
            ps.setString(1,busNoF.getText().trim()); ps.setString(2,(String)routeBox.getSelectedItem());
            ps.setString(3,(String)typeBox.getSelectedItem()); ps.setDouble(4,Double.parseDouble(fareF.getText().trim()));
            ps.setString(5,depF.getText().trim()); ps.setString(6,arrF.getText().trim());
            ps.setString(7,operatorF.getText().trim()); ps.setInt(8,selectedBusId);
            ps.executeUpdate(); JOptionPane.showMessageDialog(this,"✓  Bus updated!"); loadBuses();
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }
    private void doDelete(){
        if(selectedBusId==-1){ JOptionPane.showMessageDialog(this,"Select a bus first."); return; }
        int ok=JOptionPane.showConfirmDialog(this,"Delete this bus?","Confirm",JOptionPane.YES_NO_OPTION);
        if(ok!=JOptionPane.YES_OPTION) return;
        try(Connection con=DBConnection.getConnection()){
            con.prepareStatement("DELETE FROM buses WHERE bus_id="+selectedBusId).executeUpdate();
            JOptionPane.showMessageDialog(this,"✓  Bus deleted!"); clearForm(); loadBuses();
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
    }
    private void clearForm(){ busNoF.setText(""); fareF.setText(""); seatsF.setText("");
        depF.setText(""); arrF.setText(""); operatorF.setText(""); selectedBusId=-1; table.clearSelection(); }
}

// ════════════════════════════════════════════════════════════
//  AdminBookingsFrame
// ════════════════════════════════════════════════════════════
class AdminBookingsFrame extends JFrame {
    public AdminBookingsFrame(Dashboard d){
        setTitle("Admin — All Bookings"); setSize(1150,660); setLocationRelativeTo(null);
        UITheme.GradientPanel root=new UITheme.GradientPanel(UITheme.BG_DARK,new Color(8,14,28),false);
        root.setLayout(new BorderLayout()); setContentPane(root);
        JPanel hdr=UITheme.buildHeader("📋  All Bookings","Complete booking records across all users");
        UITheme.RoundedButton back=new UITheme.RoundedButton("← Back",UITheme.BG_CARD); back.setForeground(UITheme.TEXT_MUTED);
        back.addActionListener(e->dispose());
        JPanel bp=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10)); bp.setOpaque(false); bp.add(back);
        hdr.add(bp,BorderLayout.EAST); root.add(hdr,BorderLayout.NORTH);

        DefaultTableModel model=new DefaultTableModel(new String[]{"Ticket ID","User","Bus","Route","Boarding","Dropping","Seats","Fare","Date","Payment","Status"},0){
            public boolean isCellEditable(int r,int c){ return false; }
        };
        JTable table=new JTable(model); UITheme.styleTable(table);
        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object val,boolean sel,boolean foc,int row,int col){
                Component c=super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                // ── row background ──
                if(sel){
                    c.setBackground(new Color(0,87,184,50));
                    c.setForeground(UITheme.TEXT_DARK);
                } else {
                    c.setBackground(row%2==0 ? Color.WHITE : UITheme.BG_MEDIUM);
                    c.setForeground(UITheme.TEXT_DARK);
                }
                // ── status column ──
                if(col==10&&val!=null){ String s=val.toString();
                    c.setForeground(s.equals("Confirmed")?UITheme.ACCENT_GREEN:UITheme.ACCENT_RED);
                    ((JLabel)c).setFont(UITheme.FONT_BOLD); }
                // ── fare column ──
                if(col==7) { c.setForeground(UITheme.ACCENT_ORANGE); ((JLabel)c).setFont(UITheme.FONT_BOLD); }
                ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,10,0,10)); return c;
            }
        });

        try(Connection con=DBConnection.getConnection()){
            PreparedStatement ps=con.prepareStatement(
                "SELECT b.booking_id, ru.name, bu.bus_no, bu.route, b.boarding_point, b.dropping_point,"+
                " GROUP_CONCAT(p.seat_no ORDER BY p.seat_no SEPARATOR ', '),"+
                " b.total_fare, b.journey_date, b.payment_method, b.booking_status"+
                " FROM bookings b JOIN register_user ru ON b.user_id=ru.user_id"+
                " JOIN buses bu ON b.bus_id=bu.bus_id LEFT JOIN passengers p ON p.booking_id=b.booking_id"+
                " GROUP BY b.booking_id ORDER BY b.booking_id DESC");
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                int id=rs.getInt(1);
                model.addRow(new Object[]{"BW-"+String.format("%07d",id),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),
                    "₹ "+rs.getDouble(8),rs.getDate(9),rs.getString(10),rs.getString(11)});
            }
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }

        JScrollPane sp=UITheme.darkScrollPane(table); sp.setBorder(BorderFactory.createEmptyBorder(12,20,12,20));
        root.add(sp,BorderLayout.CENTER);
    }
}

// ════════════════════════════════════════════════════════════
//  AdminUsersFrame
// ════════════════════════════════════════════════════════════
class AdminUsersFrame extends JFrame {
    public AdminUsersFrame(Dashboard d){
        setTitle("Admin — Manage Users"); setSize(900,580); setLocationRelativeTo(null);
        UITheme.GradientPanel root=new UITheme.GradientPanel(UITheme.BG_DARK,new Color(8,14,28),false);
        root.setLayout(new BorderLayout()); setContentPane(root);
        JPanel hdr=UITheme.buildHeader("👥  All Users","Registered users on the platform");
        UITheme.RoundedButton back=new UITheme.RoundedButton("← Back",UITheme.BG_CARD); back.setForeground(UITheme.TEXT_MUTED);
        back.addActionListener(e->dispose());
        JPanel bp=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10)); bp.setOpaque(false); bp.add(back);
        hdr.add(bp,BorderLayout.EAST); root.add(hdr,BorderLayout.NORTH);

        DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Name","Email","Phone","Joined","Total Bookings"},0){
            public boolean isCellEditable(int r,int c){ return false; }
        };
        JTable table=new JTable(model); UITheme.styleTable(table);
        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object val,boolean sel,boolean foc,int row,int col){
                Component c=super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                if(sel){
                    c.setBackground(new Color(0,87,184,50)); c.setForeground(UITheme.TEXT_DARK);
                } else {
                    c.setBackground(row%2==0?Color.WHITE:UITheme.BG_MEDIUM); c.setForeground(UITheme.TEXT_DARK);
                }
                ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,10,0,10)); return c;
            }
        });
        try(Connection con=DBConnection.getConnection()){
            ResultSet rs=con.prepareStatement(
                "SELECT ru.user_id,ru.name,ru.email,ru.phone,ru.created_at,COUNT(b.booking_id) c"+
                " FROM register_user ru LEFT JOIN bookings b ON b.user_id=ru.user_id"+
                " GROUP BY ru.user_id ORDER BY ru.user_id").executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),
                rs.getString(4),rs.getTimestamp(5),rs.getInt(6)});
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }
        JScrollPane sp=UITheme.darkScrollPane(table); sp.setBorder(BorderFactory.createEmptyBorder(12,20,12,20));
        root.add(sp,BorderLayout.CENTER);
    }
}

// ════════════════════════════════════════════════════════════
//  AdminRevenueFrame
// ════════════════════════════════════════════════════════════
class AdminRevenueFrame extends JFrame {
    public AdminRevenueFrame(Dashboard d){
        setTitle("Admin — Revenue Report"); setSize(880,580); setLocationRelativeTo(null);
        UITheme.GradientPanel root=new UITheme.GradientPanel(UITheme.BG_DARK,new Color(8,14,28),false);
        root.setLayout(new BorderLayout()); setContentPane(root);
        JPanel hdr=UITheme.buildHeader("💰  Revenue Report","Earnings summary and payment analysis");
        UITheme.RoundedButton back=new UITheme.RoundedButton("← Back",UITheme.BG_CARD); back.setForeground(UITheme.TEXT_MUTED);
        back.addActionListener(e->dispose());
        JPanel bp=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10)); bp.setOpaque(false); bp.add(back);
        hdr.add(bp,BorderLayout.EAST); root.add(hdr,BorderLayout.NORTH);

        DefaultTableModel model=new DefaultTableModel(new String[]{"Route","Total Bookings","Passengers","Revenue (₹)","Avg Fare"},0){
            public boolean isCellEditable(int r,int c){ return false; }
        };
        JTable table=new JTable(model); UITheme.styleTable(table);
        table.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object val,boolean sel,boolean foc,int row,int col){
                Component c=super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                // ── row background ──
                if(sel){
                    c.setBackground(new Color(0,87,184,50));
                    c.setForeground(UITheme.TEXT_DARK);
                } else {
                    c.setBackground(row%2==0 ? Color.WHITE : UITheme.BG_MEDIUM);
                    c.setForeground(UITheme.TEXT_DARK);
                }
                // ── revenue column (col 3) — orange accent ──
                if(col==3) c.setForeground(UITheme.ACCENT_GREEN);
                // ── avg fare column (col 4) — orange accent ──
                if(col==4) c.setForeground(UITheme.ACCENT_ORANGE);
                if(col==3||col==4) ((JLabel)c).setFont(UITheme.FONT_BOLD);
                ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,10,0,10)); return c;
            }
        });

        try(Connection con=DBConnection.getConnection()){
            ResultSet rs=con.prepareStatement(
                "SELECT bu.route, COUNT(b.booking_id) bookings, SUM(b.passenger_count) pax,"+
                " SUM(b.total_fare) rev, ROUND(AVG(b.total_fare),2) avg_fare"+
                " FROM bookings b JOIN buses bu ON b.bus_id=bu.bus_id WHERE b.booking_status='Confirmed'"+
                " GROUP BY bu.route ORDER BY rev DESC").executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getString(1),rs.getInt(2),
                rs.getInt(3),"₹ "+String.format("%,.2f",rs.getDouble(4)),"₹ "+rs.getDouble(5)});
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Error: "+e.getMessage()); }

        JScrollPane sp=UITheme.darkScrollPane(table); sp.setBorder(BorderFactory.createEmptyBorder(12,20,12,20));
        root.add(sp,BorderLayout.CENTER);
    }
}