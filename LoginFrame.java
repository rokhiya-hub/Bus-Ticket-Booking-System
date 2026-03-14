import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class LoginFrame extends JFrame {
    private JTextField     emailF;
    private JPasswordField passF;
    private JLabel         statusLbl;

    public LoginFrame() {
        setTitle("BusWay \u2014 Sign In");
        setSize(1020, 630);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Light root
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_PAGE);
        setContentPane(root);

        root.add(buildHero(), BorderLayout.WEST);
        root.add(buildForm(), BorderLayout.CENTER);
    }

    // ── LEFT HERO (coral→violet gradient) ────────────────────────────
    private JPanel buildHero() {
        JPanel left = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vivid coral → violet gradient
                GradientPaint bg = new GradientPaint(0, 0, new Color(255, 82, 82),
                                                      getWidth(), getHeight(), new Color(109, 40, 217));
                g2.setPaint(bg); g2.fillRect(0,0,getWidth(),getHeight());

                // Bright orange blob center
                RadialGradientPaint blob1 = new RadialGradientPaint(
                    new Point2D.Float(getWidth()*0.6f, getHeight()*0.4f), getWidth()*0.55f,
                    new float[]{0f,1f}, new Color[]{new Color(255,140,0,60), new Color(255,140,0,0)});
                g2.setPaint(blob1); g2.fillRect(0,0,getWidth(),getHeight());

                // White shimmer top-right
                RadialGradientPaint blob2 = new RadialGradientPaint(
                    new Point2D.Float(getWidth()*0.9f, getHeight()*0.05f), getWidth()*0.4f,
                    new float[]{0f,1f}, new Color[]{new Color(255,255,255,40), new Color(255,255,255,0)});
                g2.setPaint(blob2); g2.fillRect(0,0,getWidth(),getHeight());

                // Subtle circle rings
                g2.setColor(new Color(255,255,255,18));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(getWidth()/2-110, getHeight()/2-110, 220, 220);
                g2.setColor(new Color(255,255,255,10));
                g2.drawOval(getWidth()/2-165, getHeight()/2-165, 330, 330);

                // Bus illustration
                drawBus(g2, getWidth()/2, getHeight()/2);

                // White particle dots
                g2.setColor(new Color(255,255,255,100));
                int[][] pts = {{35,55},{290,90},{60,310},{340,240},{160,460},{310,380},{50,430},{200,50},{370,330}};
                for (int[] p : pts) g2.fillOval(p[0],p[1],3,3);
                g2.setColor(new Color(255,255,255,50));
                int[][] pts2 = {{120,140},{270,290},{80,380},{330,150}};
                for (int[] p : pts2) g2.fillOval(p[0],p[1],5,5);

                // Wordmark
                g2.setFont(new Font("Segoe UI Emoji",Font.PLAIN,28));
                g2.setColor(Color.WHITE); g2.drawString("\uD83D\uDE8C",50,66);
                g2.setFont(new Font("Segoe UI",Font.BOLD,34));
                g2.setColor(Color.WHITE); g2.drawString("BusWay",92,66);
                // underline "Way" in gold
                g2.setColor(new Color(255,220,80));
                g2.setStroke(new BasicStroke(3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                FontMetrics fm=g2.getFontMetrics();
                int bw=fm.stringWidth("Bus");
                int ww=fm.stringWidth("Way");
                g2.drawLine(92+bw, 72, 92+bw+ww-4, 72);
                // Gold dot
                g2.setColor(new Color(255,220,80)); g2.fillOval(92+bw+ww+4, 54, 7, 7);

                g2.dispose();
            }
            private void drawBus(Graphics2D g2, int cx, int cy) {
                cy -= 10;
                g2.setColor(new Color(255,255,255,70));
                g2.setStroke(new BasicStroke(2.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(cx-120,cy-50,240,100,22,22);
                g2.setColor(new Color(255,255,255,55));
                for(int i=0;i<4;i++) g2.fillRoundRect(cx-108+i*58,cy-36,46,28,8,8);
                g2.setColor(new Color(255,220,80,90));
                g2.fillRoundRect(cx+76,cy-38,32,66,6,6);
                g2.setColor(new Color(255,255,255,90));
                g2.setStroke(new BasicStroke(2.5f));
                g2.fillOval(cx-94,cy+43,40,40); g2.fillOval(cx+56,cy+43,40,40);
                g2.setColor(new Color(109,40,217,120));
                g2.fillOval(cx-88,cy+49,28,28); g2.fillOval(cx+62,cy+49,28,28);
                // Speed lines
                g2.setColor(new Color(255,255,255,45));
                g2.setStroke(new BasicStroke(2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                for(int i=0;i<4;i++) g2.drawLine(cx-170-i*18, cy-15+i*12, cx-140-i*18, cy-15+i*12);
            }
        };
        left.setOpaque(true);
        left.setPreferredSize(new Dimension(420, 630));

        // Tagline label
        JLabel tagline = new JLabel("Your journey, simplified.");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tagline.setForeground(new Color(255,255,255,200));
        tagline.setBounds(52, 82, 320, 22);

        // Animated feature label
        String[] feats = {"  \u2714  Book tickets in seconds","  \u2714  Real-time seat selection",
                          "  \u2714  Instant e-ticket & print","  \u2714  Track & cancel anytime"};
        JLabel featLbl = new JLabel(feats[0]);
        featLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        featLbl.setForeground(new Color(255,240,140));
        featLbl.setBounds(50, 420, 320, 26);
        final int[] fi = {0};
        new Timer(true).schedule(new TimerTask(){
            public void run(){ fi[0]=(fi[0]+1)%feats.length;
                SwingUtilities.invokeLater(()->featLbl.setText(feats[fi[0]])); }
        }, 2000, 2000);

        // Mini stat chips
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        stats.setOpaque(false); stats.setBounds(48, 480, 340, 76);
        stats.add(miniStat("50K+", "Riders"));
        stats.add(miniStat("200+", "Routes"));
        stats.add(miniStat("99%",  "On-Time"));

        left.add(tagline); left.add(featLbl); left.add(stats);
        return left;
    }

    private JPanel miniStat(String val, String label) {
        JPanel p = new JPanel(new GridLayout(2,1,0,2)){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,28));
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.setColor(new Color(255,255,255,60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                super.paintComponent(g); g2.dispose();
            }
        };
        p.setOpaque(false); p.setPreferredSize(new Dimension(86,60));
        p.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));
        JLabel v=new JLabel(val,SwingConstants.CENTER);
        v.setFont(new Font("Segoe UI",Font.BOLD,18)); v.setForeground(new Color(255,240,140));
        JLabel l=new JLabel(label,SwingConstants.CENTER);
        l.setFont(UITheme.FONT_SMALL); l.setForeground(new Color(255,255,255,180));
        p.add(v); p.add(l); return p;
    }

    // ── RIGHT FORM (white card on light bg) ──────────────────────────
    private JPanel buildForm() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.BG_PAGE);

        JPanel card = new JPanel(new GridBagLayout()){
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
        card.setPreferredSize(new Dimension(420, 510));
        card.setBorder(BorderFactory.createEmptyBorder(36, 42, 36, 42));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;

        // Wordmark inside card
        gc.gridx=0; gc.gridy=0; gc.insets=new Insets(0,0,6,0);
        JPanel wm = new JPanel(null){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g.create();
                UITheme.paintWordmark(g2, 0, 28, 28);
                g2.dispose();
            }
        };
        wm.setOpaque(false); wm.setPreferredSize(new Dimension(200,36));
        card.add(wm, gc);

        gc.gridy++; gc.insets=new Insets(0,0,28,0);
        JLabel sub=new JLabel("Sign in to continue your journey");
        sub.setFont(UITheme.FONT_SMALL); sub.setForeground(UITheme.TEXT_MUTED);
        card.add(sub,gc);

        gc.gridy++; gc.insets=new Insets(0,0,6,0); card.add(UITheme.createFieldLabel("Email Address"),gc);
        emailF=UITheme.createTextField("");
        gc.gridy++; gc.insets=new Insets(0,0,18,0); card.add(emailF,gc);

        gc.gridy++; gc.insets=new Insets(0,0,6,0); card.add(UITheme.createFieldLabel("Password"),gc);
        passF=UITheme.createPasswordField();
        gc.gridy++; gc.insets=new Insets(0,0,8,0); card.add(passF,gc);

        statusLbl=new JLabel(" "); statusLbl.setFont(UITheme.FONT_SMALL); statusLbl.setForeground(UITheme.ACCENT_RED);
        gc.gridy++; gc.insets=new Insets(0,0,18,0); card.add(statusLbl,gc);

        // Gradient sign-in button
        JButton loginBtn = new JButton("  Sign In  \u2192"){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp=new GradientPaint(0,0,UITheme.ACCENT_CORAL,getWidth(),0,UITheme.ACCENT_VIOLET);
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-2,14,14);
                g2.setColor(new Color(255,255,255,40));
                g2.fillRoundRect(0,0,getWidth()-1,(getHeight()-2)/2,14,14);
                super.paintComponent(g); g2.dispose();
            }
        };
        loginBtn.setContentAreaFilled(false); loginBtn.setBorderPainted(false); loginBtn.setFocusPainted(false);
        loginBtn.setForeground(Color.WHITE); loginBtn.setFont(new Font("Segoe UI",Font.BOLD,15));
        loginBtn.setPreferredSize(new Dimension(0,48));
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gc.gridy++; gc.insets=new Insets(0,0,20,0); card.add(loginBtn,gc);

        JSeparator sep=new JSeparator(); sep.setForeground(UITheme.BORDER_COL);
        gc.gridy++; gc.insets=new Insets(0,0,16,0); card.add(sep,gc);

        JPanel row=new JPanel(new FlowLayout(FlowLayout.CENTER,5,0)); row.setOpaque(false);
        JLabel q=new JLabel("New here?"); q.setFont(UITheme.FONT_SMALL); q.setForeground(UITheme.TEXT_MUTED);
        JLabel sl=new JLabel("Create an account \u2192");
        sl.setFont(new Font("Segoe UI",Font.BOLD,12)); sl.setForeground(UITheme.ACCENT_VIOLET);
        sl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sl.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){dispose();new RegisterFrame().setVisible(true);}
            public void mouseEntered(MouseEvent e){sl.setText("<html><u>Create an account \u2192</u></html>");}
            public void mouseExited (MouseEvent e){sl.setText("Create an account \u2192");}
        });
        row.add(q); row.add(sl);
        gc.gridy++; gc.insets=new Insets(0,0,0,0); card.add(row,gc);

        wrapper.add(card);
        loginBtn.addActionListener(e->doLogin());
        passF.addActionListener(e->doLogin());
        emailF.addActionListener(e->passF.requestFocus());
        return wrapper;
    }

    private void doLogin(){
        String email=emailF.getText().trim(), pass=new String(passF.getPassword()).trim();
        if(email.isEmpty()||pass.isEmpty()){show("Please fill in both fields.",UITheme.ACCENT_GOLD);return;}
        show("Signing in...",UITheme.TEXT_MUTED);
        new SwingWorker<Object[],Void>(){
            protected Object[] doInBackground() throws Exception {
                try(Connection c=DBConnection.getConnection()){
                    PreparedStatement ps=c.prepareStatement("SELECT * FROM register_user WHERE email=? AND password=?");
                    ps.setString(1,email); ps.setString(2,pass); ResultSet rs=ps.executeQuery();
                    if(rs.next()) return new Object[]{rs.getInt("user_id"),rs.getString("name"),rs.getString("email"),rs.getString("phone")};
                } return null;
            }
            protected void done(){
                try{ Object[] u=get();
                    if(u!=null){UserSession.setUser((int)u[0],(String)u[1],(String)u[2],u[3]!=null?(String)u[3]:"");
                        dispose(); new Dashboard(email.equalsIgnoreCase("admin@gmail.com")).setVisible(true);
                    } else show("Incorrect email or password.",UITheme.ACCENT_RED);
                } catch(Exception ex){show("Error: "+ex.getMessage(),UITheme.ACCENT_RED);}
            }
        }.execute();
    }
    private void show(String m,Color c){statusLbl.setForeground(c);statusLbl.setText(m);}
    public static void main(String[] args){SwingUtilities.invokeLater(()->new LoginFrame().setVisible(true));}
}