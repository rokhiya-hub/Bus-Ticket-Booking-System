import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfileFrame extends JFrame {
    private Dashboard dashboard;
    private JTextField nameF,emailF,phoneF;
    private JPasswordField newPassF,confPassF;
    private JLabel bookingsLbl;

    public ProfileFrame(Dashboard dashboard){
        this.dashboard=dashboard;
        setTitle("BusWay — My Profile"); setSize(640,660);
        setLocationRelativeTo(null); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UITheme.GradientPanel root=new UITheme.GradientPanel(UITheme.BG_DARK,new Color(8,14,28),false);
        root.setLayout(new BorderLayout()); setContentPane(root);

        // HEADER
        JPanel hdr=new JPanel(new BorderLayout()); hdr.setBackground(UITheme.BG_SIDEBAR);
        hdr.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,UITheme.BORDER_COL),
            BorderFactory.createEmptyBorder(16,25,16,25)));
        UITheme.RoundedPanel avt=new UITheme.RoundedPanel(50,new Color(30,50,90));
        avt.setPreferredSize(new Dimension(64,64)); avt.setLayout(new BorderLayout());
        JLabel ai=new JLabel("👤",SwingConstants.CENTER); ai.setFont(new Font("Segoe UI Emoji",Font.PLAIN,30));
        avt.add(ai);
        JPanel nc=new JPanel(new GridLayout(3,1,0,2)); nc.setOpaque(false);
        nc.setBorder(BorderFactory.createEmptyBorder(0,14,0,0));
        JLabel un=new JLabel(UserSession.getName()!=null?UserSession.getName():"User");
        un.setFont(new Font("Segoe UI",Font.BOLD,18)); un.setForeground(UITheme.TEXT_WHITE);
        JLabel ue=new JLabel(UserSession.getEmail()!=null?UserSession.getEmail():"");
        ue.setFont(UITheme.FONT_SMALL); ue.setForeground(UITheme.TEXT_MUTED);
        bookingsLbl=new JLabel("Loading..."); bookingsLbl.setFont(UITheme.FONT_SMALL); bookingsLbl.setForeground(UITheme.ACCENT_CYAN);
        nc.add(un); nc.add(ue); nc.add(bookingsLbl);
        JPanel hl=new JPanel(new BorderLayout()); hl.setOpaque(false);
        hl.add(avt,BorderLayout.WEST); hl.add(nc,BorderLayout.CENTER);
        JLabel idLbl=new JLabel("ID #"+UserSession.getUserId()); idLbl.setFont(UITheme.FONT_BOLD); idLbl.setForeground(UITheme.ACCENT_CYAN);
        hdr.add(hl,BorderLayout.WEST); hdr.add(idLbl,BorderLayout.EAST);
        root.add(hdr,BorderLayout.NORTH);

        // FORM
        UITheme.RoundedPanel card=new UITheme.RoundedPanel(16,UITheme.BG_CARD);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(24,36,24,36));
        GridBagConstraints gc=new GridBagConstraints(); gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1;

        nameF=UITheme.createTextField(""); emailF=UITheme.createTextField(""); phoneF=UITheme.createTextField("");
        newPassF=UITheme.createPasswordField(); confPassF=UITheme.createPasswordField();

        gc.gridx=0; gc.gridy=0; gc.gridwidth=2; gc.insets=new Insets(0,0,14,0);
        card.add(UITheme.createSectionHeader("Personal Information"),gc); gc.gridwidth=1;

        addRow(card,gc,1,"Full Name",nameF); addRow(card,gc,2,"Email Address",emailF); addRow(card,gc,3,"Phone Number",phoneF);

        gc.gridx=0; gc.gridy=4; gc.gridwidth=2; gc.insets=new Insets(16,0,14,0);
        card.add(UITheme.createSectionHeader("Change Password  (leave blank to keep current)"),gc); gc.gridwidth=1;
        addRow(card,gc,5,"New Password",newPassF); addRow(card,gc,6,"Confirm Password",confPassF);

        JPanel wrap=new JPanel(new BorderLayout()); wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(14,20,10,20)); wrap.add(card,BorderLayout.CENTER);
        JScrollPane sp=new JScrollPane(wrap); sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder()); root.add(sp,BorderLayout.CENTER);

        JPanel footer=new JPanel(new BorderLayout()); footer.setBackground(UITheme.BG_SIDEBAR);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0,UITheme.BORDER_COL),
            BorderFactory.createEmptyBorder(14,25,14,25)));
        UITheme.RoundedButton backBtn=new UITheme.RoundedButton("← Back",UITheme.BG_CARD); backBtn.setForeground(UITheme.TEXT_MUTED);
        UITheme.RoundedButton saveBtn=new UITheme.RoundedButton("💾  Save Changes",UITheme.ACCENT_BLUE); saveBtn.setPreferredSize(new Dimension(180,40));
        footer.add(backBtn,BorderLayout.WEST); footer.add(saveBtn,BorderLayout.EAST);
        root.add(footer,BorderLayout.SOUTH);

        saveBtn.addActionListener(e->doUpdate());
        backBtn.addActionListener(e->{ dispose(); dashboard.setVisible(true); });
        loadProfile();
    }

    private void loadProfile(){
        try(Connection con=DBConnection.getConnection()){
            PreparedStatement ps=con.prepareStatement("SELECT * FROM register_user WHERE user_id=?");
            ps.setInt(1,UserSession.getUserId()); ResultSet rs=ps.executeQuery();
            if(rs.next()){ nameF.setText(rs.getString("name")); emailF.setText(rs.getString("email"));
                phoneF.setText(rs.getString("phone")!=null?rs.getString("phone"):""); }
            ResultSet cr=con.prepareStatement(
                "SELECT COUNT(*) t, SUM(CASE WHEN booking_status='Confirmed' THEN 1 ELSE 0 END) a FROM bookings WHERE user_id="+UserSession.getUserId()).executeQuery();
            if(cr.next()) bookingsLbl.setText(cr.getInt("t")+" bookings  •  "+cr.getInt("a")+" active");
        } catch(Exception e){ System.err.println("Profile load: "+e.getMessage()); }
    }

    private void doUpdate(){
        String name=nameF.getText().trim(), email=emailF.getText().trim(), phone=phoneF.getText().trim(),
               np=new String(newPassF.getPassword()).trim(), cp=new String(confPassF.getPassword()).trim();
        if(name.isEmpty()||email.isEmpty()){ JOptionPane.showMessageDialog(this,"Name and Email cannot be empty."); return; }
        if(!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")){ JOptionPane.showMessageDialog(this,"Enter valid email."); return; }
        if(!phone.isEmpty()&&!phone.matches("\\d{10}")){ JOptionPane.showMessageDialog(this,"Phone must be 10 digits."); return; }
        if(!np.isEmpty()){ if(np.length()<6){ JOptionPane.showMessageDialog(this,"Password min 6 chars."); return; }
            if(!np.equals(cp)){ JOptionPane.showMessageDialog(this,"Passwords don't match."); return; } }
        try(Connection con=DBConnection.getConnection()){
            PreparedStatement ps;
            if(!np.isEmpty()){
                ps=con.prepareStatement("UPDATE register_user SET name=?,email=?,phone=?,password=? WHERE user_id=?");
                ps.setString(1,name); ps.setString(2,email); ps.setString(3,phone); ps.setString(4,np); ps.setInt(5,UserSession.getUserId());
            } else {
                ps=con.prepareStatement("UPDATE register_user SET name=?,email=?,phone=? WHERE user_id=?");
                ps.setString(1,name); ps.setString(2,email); ps.setString(3,phone); ps.setInt(4,UserSession.getUserId());
            }
            if(ps.executeUpdate()>0){
                UserSession.setUser(UserSession.getUserId(),name,email,phone);
                JOptionPane.showMessageDialog(this,"✓  Profile updated!","Saved",JOptionPane.INFORMATION_MESSAGE);
                newPassF.setText(""); confPassF.setText(""); setTitle("BusWay — "+name);
            }
        } catch(Exception e){
            JOptionPane.showMessageDialog(this,e.getMessage().contains("Duplicate")?"Email already used.":"Error: "+e.getMessage());
        }
    }

    private void addRow(JPanel p,GridBagConstraints g,int row,String lbl,JComponent f){
        g.gridx=0; g.gridy=row; g.gridwidth=1; g.insets=new Insets(6,0,3,12); g.weightx=0.3; p.add(UITheme.createFieldLabel(lbl),g);
        g.gridx=1; g.weightx=0.7; g.insets=new Insets(6,0,3,0); p.add(f,g);
    }
}