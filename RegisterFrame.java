import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    private JTextField     nameF, emailF, phoneF;
    private JPasswordField passF, confF;
    private JLabel         statusLbl;

    public RegisterFrame(){
        setTitle("BusWay \u2014 Create Account");
        setSize(1080,680); setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); setResizable(true);

        JPanel root=new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_PAGE);
        setContentPane(root);
        root.add(buildHero(),BorderLayout.WEST);
        root.add(buildForm(),BorderLayout.CENTER);
    }

    // ── LEFT HERO ─────────────────────────────────────────────────────
    private JPanel buildHero(){
        JPanel left=new JPanel(null){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

                // Violet→coral reversed for variety
                GradientPaint bg=new GradientPaint(0,0,new Color(109,40,217),getWidth(),getHeight(),new Color(255,82,82));
                g2.setPaint(bg); g2.fillRect(0,0,getWidth(),getHeight());

                // Teal blob center
                RadialGradientPaint b1=new RadialGradientPaint(
                    new Point2D.Float(getWidth()*0.5f,getHeight()*0.5f),getWidth()*0.5f,
                    new float[]{0f,1f},new Color[]{new Color(0,200,220,50),new Color(0,200,220,0)});
                g2.setPaint(b1); g2.fillRect(0,0,getWidth(),getHeight());

                // White shimmer top-left
                RadialGradientPaint b2=new RadialGradientPaint(
                    new Point2D.Float(getWidth()*0.05f,getHeight()*0.05f),getWidth()*0.4f,
                    new float[]{0f,1f},new Color[]{new Color(255,255,255,45),new Color(255,255,255,0)});
                g2.setPaint(b2); g2.fillRect(0,0,getWidth(),getHeight());

                // Decorative circles
                g2.setColor(new Color(255,255,255,15));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(getWidth()/2-130,getHeight()/2-130,260,260);
                g2.drawOval(getWidth()/2-195,getHeight()/2-195,390,390);

                // Steps
                drawSteps(g2,getWidth(),getHeight());

                // Particles
                g2.setColor(new Color(255,255,255,100));
                int[][] pts={{32,65},{298,95},{65,315},{346,245},{168,455},{315,378},{52,428},{206,52},{374,335}};
                for(int[] p:pts) g2.fillOval(p[0],p[1],3,3);

                // Wordmark
                g2.setFont(new Font("Segoe UI Emoji",Font.PLAIN,26)); g2.setColor(Color.WHITE);
                g2.drawString("\uD83D\uDE8C",46,62);
                g2.setFont(new Font("Segoe UI",Font.BOLD,32)); g2.setColor(Color.WHITE);
                g2.drawString("BusWay",86,62);
                FontMetrics fm=g2.getFontMetrics();
                int bw=fm.stringWidth("Bus"); int ww=fm.stringWidth("Way");
                g2.setColor(new Color(255,240,100));
                g2.setStroke(new BasicStroke(3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g2.drawLine(86+bw,68,86+bw+ww-4,68);
                g2.fillOval(86+bw+ww+4,50,7,7);

                g2.dispose();
            }
            private void drawSteps(Graphics2D g2,int w,int h){
                String[][] steps={{"1","Create Account"},{"2","Search Buses"},{"3","Select Seats"},{"4","Pay & Travel"}};
                Color[] cols={new Color(255,220,80),new Color(255,255,255),new Color(255,160,100),new Color(200,255,200)};
                int base=h-256;
                JLabel tmp=new JLabel("How it works:");
                g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                g2.setColor(new Color(255,255,255,150));
                g2.drawString("How it works:",50,base-10);
                for(int i=0;i<4;i++){
                    int y=base+i*44;
                    g2.setColor(new Color(cols[i].getRed(),cols[i].getGreen(),cols[i].getBlue(),40));
                    g2.fillOval(50,y-11,26,26);
                    g2.setColor(cols[i]); g2.setStroke(new BasicStroke(1.8f));
                    g2.drawOval(50,y-11,26,26);
                    g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                    g2.drawString(steps[i][0],59,y+7);
                    if(i<3){
                        g2.setColor(new Color(255,255,255,25));
                        g2.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,0,new float[]{4,4},0));
                        g2.drawLine(63,y+17,63,y+44);
                    }
                    g2.setStroke(new BasicStroke(1f));
                    g2.setFont(new Font("Segoe UI",Font.PLAIN,13));
                    g2.setColor(new Color(255,255,255,210));
                    g2.drawString(steps[i][1],86,y+7);
                }
            }
        };
        left.setOpaque(true);
        left.setPreferredSize(new Dimension(400,680));

        JLabel tagline=new JLabel("Join thousands of happy travellers.");
        tagline.setFont(new Font("Segoe UI",Font.ITALIC,13));
        tagline.setForeground(new Color(255,255,255,200));
        tagline.setBounds(50,78,320,22);
        left.add(tagline);
        return left;
    }

    // ── RIGHT FORM ─────────────────────────────────────────────────────
    private JPanel buildForm(){
        JPanel wrapper=new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.BG_PAGE);

        JPanel card=new JPanel(new GridBagLayout()){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(100,120,200,22));
                g2.fill(new RoundRectangle2D.Double(5,7,getWidth()-5,getHeight()-5,22,22));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0,0,getWidth()-4,getHeight()-4,22,22));
                g2.setColor(UITheme.BORDER_COL); g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Double(0.5,0.5,getWidth()-5,getHeight()-5,22,22));
                super.paintComponent(g); g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(600,610));
        card.setBorder(BorderFactory.createEmptyBorder(28,44,28,44));

        GridBagConstraints gc=new GridBagConstraints();
        gc.fill=GridBagConstraints.HORIZONTAL; gc.weightx=1;

        // Wordmark
        gc.gridx=0; gc.gridy=0; gc.gridwidth=2; gc.insets=new Insets(0,0,4,0);
        JPanel wm=new JPanel(null){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g.create();
                UITheme.paintWordmark(g2,0,26,26);
                g2.dispose();
            }
        };
        wm.setOpaque(false); wm.setPreferredSize(new Dimension(200,34));
        card.add(wm,gc);

        gc.gridy++; gc.insets=new Insets(0,0,24,0);
        JLabel sub=new JLabel("Start booking buses in seconds \u2014 it's free");
        sub.setFont(UITheme.FONT_SMALL); sub.setForeground(UITheme.TEXT_MUTED); card.add(sub,gc);

        nameF =UITheme.createTextField(""); nameF.setPreferredSize(new Dimension(180,44));
        phoneF=UITheme.createTextField(""); phoneF.setPreferredSize(new Dimension(180,44));
        emailF=UITheme.createTextField(""); emailF.setPreferredSize(new Dimension(0,44));
        passF =UITheme.createPasswordField(); passF.setPreferredSize(new Dimension(180,44));
        confF =UITheme.createPasswordField(); confF.setPreferredSize(new Dimension(180,44));

        gc.gridwidth=1; gc.gridy++;
        gc.gridx=0; gc.insets=new Insets(0,0,6,16); card.add(UITheme.createFieldLabel("Full Name *"),gc);
        gc.gridx=1; gc.insets=new Insets(0,16,6,0);  card.add(UITheme.createFieldLabel("Phone Number *"),gc);
        gc.gridy++;
        gc.gridx=0; gc.insets=new Insets(0,0,18,16); card.add(nameF,gc);
        gc.gridx=1; gc.insets=new Insets(0,16,18,0);  card.add(phoneF,gc);

        gc.gridx=0; gc.gridy++; gc.gridwidth=2; gc.insets=new Insets(0,0,6,0);
        card.add(UITheme.createFieldLabel("Email Address *"),gc);
        gc.gridy++; gc.insets=new Insets(0,0,18,0); card.add(emailF,gc);

        gc.gridwidth=1; gc.gridy++;
        gc.gridx=0; gc.insets=new Insets(0,0,6,16); card.add(UITheme.createFieldLabel("Password *"),gc);
        gc.gridx=1; gc.insets=new Insets(0,16,6,0);  card.add(UITheme.createFieldLabel("Confirm Password *"),gc);
        gc.gridy++;
        gc.gridx=0; gc.insets=new Insets(0,0,8,16); card.add(passF,gc);
        gc.gridx=1; gc.insets=new Insets(0,16,8,0);  card.add(confF,gc);

        statusLbl=new JLabel(" "); statusLbl.setFont(UITheme.FONT_SMALL); statusLbl.setForeground(UITheme.ACCENT_RED);
        gc.gridx=0; gc.gridy++; gc.gridwidth=2; gc.insets=new Insets(2,0,12,0); card.add(statusLbl,gc);

        // Gradient register button
        JButton regBtn=new JButton("  Create Account  \u2192"){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,UITheme.ACCENT_VIOLET,getWidth(),0,UITheme.ACCENT_CORAL);
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-2,14,14);
                g2.setColor(new Color(255,255,255,40));
                g2.fillRoundRect(0,0,getWidth()-1,(getHeight()-2)/2,14,14);
                super.paintComponent(g); g2.dispose();
            }
        };
        regBtn.setContentAreaFilled(false); regBtn.setBorderPainted(false); regBtn.setFocusPainted(false);
        regBtn.setForeground(Color.WHITE); regBtn.setFont(new Font("Segoe UI",Font.BOLD,15));
        regBtn.setPreferredSize(new Dimension(0,48));
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gc.gridy++; gc.insets=new Insets(0,0,18,0); card.add(regBtn,gc);

        JSeparator sep=new JSeparator(); sep.setForeground(UITheme.BORDER_COL);
        gc.gridy++; gc.insets=new Insets(0,0,14,0); card.add(sep,gc);

        JPanel row=new JPanel(new FlowLayout(FlowLayout.CENTER,5,0)); row.setOpaque(false);
        JLabel q=new JLabel("Already have an account?"); q.setFont(UITheme.FONT_SMALL); q.setForeground(UITheme.TEXT_MUTED);
        JLabel sl=new JLabel("Sign In \u2192");
        sl.setFont(new Font("Segoe UI",Font.BOLD,12)); sl.setForeground(UITheme.ACCENT_CORAL);
        sl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sl.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){dispose();new LoginFrame().setVisible(true);}
            public void mouseEntered(MouseEvent e){sl.setText("<html><u>Sign In \u2192</u></html>");}
            public void mouseExited (MouseEvent e){sl.setText("Sign In \u2192");}
        });
        row.add(q); row.add(sl);
        gc.gridy++; gc.insets=new Insets(0,0,0,0); card.add(row,gc);

        wrapper.add(card);
        regBtn.addActionListener(e->doRegister());
        confF.addActionListener(e->doRegister());
        return wrapper;
    }

    private void doRegister(){
        String name=nameF.getText().trim(),email=emailF.getText().trim(),
               phone=phoneF.getText().trim(),pass=new String(passF.getPassword()).trim(),
               conf=new String(confF.getPassword()).trim();
        if(name.isEmpty()||email.isEmpty()||phone.isEmpty()||pass.isEmpty()){
            show("Please fill in all fields.",UITheme.ACCENT_GOLD); return;}
        if(!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")){
            show("Enter a valid email address.",UITheme.ACCENT_RED); return;}
        if(!phone.matches("\\d{10}")){
            show("Phone must be exactly 10 digits.",UITheme.ACCENT_RED); return;}
        if(pass.length()<6){show("Password min 6 characters.",UITheme.ACCENT_RED); return;}
        if(!pass.equals(conf)){show("Passwords do not match.",UITheme.ACCENT_RED); return;}
        show("Creating account...",UITheme.TEXT_MUTED);
        new SwingWorker<Boolean,Void>(){
            protected Boolean doInBackground() throws Exception {
                try(Connection c=DBConnection.getConnection()){
                    PreparedStatement ps=c.prepareStatement("INSERT INTO register_user(name,email,phone,password) VALUES(?,?,?,?)");
                    ps.setString(1,name); ps.setString(2,email); ps.setString(3,phone); ps.setString(4,pass);
                    ps.executeUpdate(); return true;
                }
            }
            protected void done(){
                try{ if(get()){
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                        "\uD83C\uDF89  Account created! Please sign in to start your journey.",
                        "Welcome to BusWay!",JOptionPane.INFORMATION_MESSAGE);
                    dispose(); new LoginFrame().setVisible(true);}}
                catch(Exception ex){
                    show(ex.getMessage().contains("Duplicate")?"Email already registered.":"Error: "+ex.getMessage(),UITheme.ACCENT_RED);}
            }
        }.execute();
    }
    private void show(String m,Color c){statusLbl.setForeground(c);statusLbl.setText(m);}
}