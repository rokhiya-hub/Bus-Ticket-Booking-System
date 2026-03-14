import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * BusWay UI Theme — "Sunrise Express"
 * Crisp white base · coral-to-violet hero gradients · electric blue accents.
 */
public class UITheme {

    // ── LIGHT PALETTE ────────────────────────────────────────────────
    public static final Color BG_PAGE      = new Color(245, 247, 255);
    public static final Color BG_DARK      = new Color(245, 247, 255); // alias kept for compat
    public static final Color BG_MEDIUM    = new Color(235, 240, 255);
    public static final Color BG_SIDEBAR   = new Color(18,  24,  58);   // deep indigo sidebar
    public static final Color BG_CARD      = Color.WHITE;
    public static final Color BG_CARD_SOLID= Color.WHITE;
    public static final Color BG_INPUT     = new Color(245, 247, 255);

    // Vivid accent system
    public static final Color ACCENT_CORAL  = new Color(255,  82,  82);
    public static final Color ACCENT_ORANGE = new Color(255, 138,  50);
    public static final Color ACCENT_VIOLET = new Color(109,  40, 217);
    public static final Color ACCENT_BLUE   = new Color(37,   99, 235);
    public static final Color ACCENT_TEAL   = new Color(6,  182, 212);
    public static final Color ACCENT_GREEN  = new Color(16, 185, 129);
    public static final Color ACCENT_PINK   = new Color(236,  72, 153);
    public static final Color ACCENT_GOLD   = new Color(245, 158,  11);
    public static final Color ACCENT_RED    = new Color(239,  68,  68);
    public static final Color ACCENT_PURPLE = new Color(139,  92, 246);
    public static final Color ACCENT_CYAN   = new Color(6,  182, 212);
    public static final Color ACCENT_YELLOW = new Color(245, 158,  11);

    // Hero gradient colours (used on left panels + sidebar)
    public static final Color HERO_START = new Color(255,  82,  82);
    public static final Color HERO_END   = new Color(109,  40, 217);

    // Text — dark on light backgrounds
    public static final Color TEXT_WHITE  = new Color(15,  23,  42);
    public static final Color TEXT_MUTED  = new Color(100, 116, 139);
    public static final Color TEXT_DIM    = new Color(148, 163, 184);
    public static final Color TEXT_DARK   = new Color(15,  23,  42);
    public static final Color BORDER_COL  = new Color(226, 232, 240);
    public static final Color BORDER_GLOW = new Color(37,   99, 235, 60);

    // ── FONTS ────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  24);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_LARGE   = new Font("Segoe UI", Font.BOLD,  30);
    public static final Font FONT_MONO    = new Font("Consolas",  Font.BOLD,  13);

    // ── BACKGROUND PANEL ─────────────────────────────────────────────
    public static class GradientPanel extends JPanel {
        private Color c1, c2; private boolean h;
        public GradientPanel(Color c1, Color c2, boolean h) {
            this.c1=c1; this.c2=c2; this.h=h; setOpaque(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Crisp white-lavender base
            g2.setColor(BG_PAGE); g2.fillRect(0,0,getWidth(),getHeight());
            // Coral blob top-right
            RadialGradientPaint b1 = new RadialGradientPaint(
                new Point2D.Float(getWidth()*0.85f, getHeight()*0.08f), getWidth()*0.38f,
                new float[]{0f,1f}, new Color[]{new Color(255,82,82,30), new Color(255,82,82,0)});
            g2.setPaint(b1); g2.fillRect(0,0,getWidth(),getHeight());
            // Violet blob bottom-left
            RadialGradientPaint b2 = new RadialGradientPaint(
                new Point2D.Float(getWidth()*0.12f, getHeight()*0.88f), getWidth()*0.35f,
                new float[]{0f,1f}, new Color[]{new Color(109,40,217,22), new Color(109,40,217,0)});
            g2.setPaint(b2); g2.fillRect(0,0,getWidth(),getHeight());
            // Blue blob center
            RadialGradientPaint b3 = new RadialGradientPaint(
                new Point2D.Float(getWidth()*0.5f, getHeight()*0.5f), getWidth()*0.45f,
                new float[]{0f,1f}, new Color[]{new Color(37,99,235,10), new Color(37,99,235,0)});
            g2.setPaint(b3); g2.fillRect(0,0,getWidth(),getHeight());
            // Dot-grid texture
            g2.setColor(new Color(99,120,180,20));
            for (int x=20; x<getWidth(); x+=32)
                for (int y=20; y<getHeight(); y+=32)
                    g2.fillOval(x,y,2,2);
            super.paintComponent(g); g2.dispose();
        }
    }

    // ── WHITE CARD ───────────────────────────────────────────────────
    public static class RoundedPanel extends JPanel {
        private int r; private Color bg;
        public RoundedPanel(int r, Color bg) { this.r=r; this.bg=bg; setOpaque(false); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(100,120,200,20));
            g2.fill(new RoundRectangle2D.Double(4,6,getWidth()-5,getHeight()-5,r,r));
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth()-3,getHeight()-3,r,r));
            g2.setColor(BORDER_COL); g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Double(0.5,0.5,getWidth()-4,getHeight()-4,r,r));
            super.paintComponent(g); g2.dispose();
        }
    }

    // ── VIVID BUTTON ─────────────────────────────────────────────────
    public static class RoundedButton extends JButton {
        private Color base, hover, press, cur; private int rad; private boolean ov=false;
        public RoundedButton(String t, Color c) { this(t,c,12); }
        public RoundedButton(String t, Color c, int r) {
            super(t); base=c; hover=lighten(c,20); press=darken(c,20); cur=c; rad=r;
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setForeground(Color.WHITE); setFont(FONT_BOLD);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){ov=true; cur=hover; repaint();}
                public void mouseExited (MouseEvent e){ov=false;cur=base; repaint();}
                public void mousePressed(MouseEvent e){cur=press; repaint();}
                public void mouseReleased(MouseEvent e){cur=ov?hover:base; repaint();}
            });
        }
        public void setBaseColor(Color c){base=c;hover=lighten(c,20);press=darken(c,20);cur=c;repaint();}
        protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(cur.getRed(),cur.getGreen(),cur.getBlue(),55));
            g2.fill(new RoundRectangle2D.Double(2,4,getWidth()-2,getHeight()-2,rad,rad));
            GradientPaint gp=new GradientPaint(0,0,lighten(cur,15),0,getHeight(),darken(cur,10));
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth()-2,getHeight()-3,rad,rad));
            g2.setColor(new Color(255,255,255,50));
            g2.fill(new RoundRectangle2D.Double(1,1,getWidth()-4,(getHeight()-4)/2,rad-1,rad-1));
            g2.dispose(); super.paintComponent(g);
        }
        static Color lighten(Color c,int a){return new Color(Math.min(255,c.getRed()+a),Math.min(255,c.getGreen()+a),Math.min(255,c.getBlue()+a));}
        static Color darken (Color c,int a){return new Color(Math.max(0,c.getRed()-a),Math.max(0,c.getGreen()-a),Math.max(0,c.getBlue()-a));}
    }

    // ── BORDER ───────────────────────────────────────────────────────
    public static class RoundedBorder implements Border {
        private Color c; private int th,r;
        public RoundedBorder(Color c,int th,int r){this.c=c;this.th=th;this.r=r;}
        public void paintBorder(Component cp,Graphics g,int x,int y,int w,int h){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c); g2.setStroke(new BasicStroke(th));
            g2.draw(new RoundRectangle2D.Double(x+1,y+1,w-2,h-2,r,r)); g2.dispose();
        }
        public Insets getBorderInsets(Component c){return new Insets(r/2,r/2,r/2,r/2);}
        public boolean isBorderOpaque(){return false;}
    }

    // ── STAT CARD ────────────────────────────────────────────────────
    public static class StatCard extends JPanel {
        private JLabel valLbl; private Color ac;
        public StatCard(String icon,String title,String val,Color ac){
            this.ac=ac; setLayout(new BorderLayout(0,8)); setOpaque(false);
            JPanel top=new JPanel(new BorderLayout(12,0)); top.setOpaque(false);
            JPanel iconCircle=new JPanel(new BorderLayout());
            iconCircle.setOpaque(false); iconCircle.setPreferredSize(new Dimension(52,52));
            JLabel il=new JLabel(icon,SwingConstants.CENTER);
            il.setFont(new Font("Segoe UI Emoji",Font.PLAIN,22)); iconCircle.add(il,BorderLayout.CENTER);
            JLabel tl=new JLabel(title); tl.setFont(FONT_SMALL); tl.setForeground(TEXT_MUTED);
            valLbl=new JLabel(val); valLbl.setFont(new Font("Segoe UI",Font.BOLD,26)); valLbl.setForeground(TEXT_DARK);
            JPanel tp=new JPanel(new GridLayout(2,1,0,3)); tp.setOpaque(false); tp.add(tl); tp.add(valLbl);
            top.add(iconCircle,BorderLayout.WEST); top.add(tp,BorderLayout.CENTER);
            JPanel bar=new JPanel(){protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                GradientPaint gp=new GradientPaint(0,0,ac,getWidth()*0.6f,0,new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),40));
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth(),getHeight(),4,4); g2.dispose();}};
            bar.setPreferredSize(new Dimension(0,4)); bar.setOpaque(false);
            add(top,BorderLayout.CENTER); add(bar,BorderLayout.SOUTH);
        }
        protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),18));
            g2.fill(new RoundRectangle2D.Double(4,6,getWidth()-5,getHeight()-5,18,18));
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth()-4,getHeight()-4,18,18));
            GradientPaint stripe=new GradientPaint(0,0,ac,0,getHeight(),new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),60));
            g2.setPaint(stripe); g2.fill(new RoundRectangle2D.Double(0,0,5,getHeight()-4,4,4));
            g2.setColor(new Color(ac.getRed(),ac.getGreen(),ac.getBlue(),55));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Double(0.5,0.5,getWidth()-5,getHeight()-5,18,18));
            super.paintComponent(g); g2.dispose();
        }
        public void setValue(String v){valLbl.setText(v);valLbl.repaint();}
    }

    // ── SIDEBAR BUTTON ───────────────────────────────────────────────
    public static class SidebarButton extends JButton {
        private boolean active=false;
        public SidebarButton(String icon,String text){
            super(icon+"  "+text);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setForeground(new Color(148,163,200)); setFont(FONT_BODY);
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
            setBorder(BorderFactory.createEmptyBorder(11,22,11,22));
            addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){if(!active){setForeground(Color.WHITE);repaint();}}
                public void mouseExited (MouseEvent e){if(!active){setForeground(new Color(148,163,200));repaint();}}
            });
        }
        public void setActive(boolean a){active=a;setForeground(a?Color.WHITE:new Color(148,163,200));repaint();}
        protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            if(active){
                GradientPaint gp=new GradientPaint(8,0,ACCENT_CORAL,getWidth()-8,0,ACCENT_VIOLET);
                g2.setPaint(gp); g2.fillRoundRect(8,3,getWidth()-16,getHeight()-6,10,10);
                g2.setColor(new Color(255,255,255,30)); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(8,3,getWidth()-17,getHeight()-7,10,10);
            } else if(getModel().isRollover()){
                g2.setColor(new Color(255,255,255,12));
                g2.fillRoundRect(8,3,getWidth()-16,getHeight()-6,10,10);
            }
            g2.dispose(); super.paintComponent(g);
        }
    }

    // ── FACTORY HELPERS ──────────────────────────────────────────────
    public static JTextField createTextField(String ph){
        JTextField f=new JTextField(){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                super.paintComponent(g); g2.dispose();
            }
        };
        f.setOpaque(false); f.setForeground(TEXT_DARK); f.setCaretColor(ACCENT_BLUE);
        f.setFont(FONT_BODY); f.setPreferredSize(new Dimension(200,44));
        f.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(BORDER_COL,1,12),BorderFactory.createEmptyBorder(8,14,8,14)));
        f.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){f.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(ACCENT_BLUE,2,12),BorderFactory.createEmptyBorder(7,13,7,13)));}
            public void focusLost(FocusEvent e) {f.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(BORDER_COL,1,12),BorderFactory.createEmptyBorder(8,14,8,14)));}
        });
        return f;
    }

    public static JPasswordField createPasswordField(){
        JPasswordField f=new JPasswordField(){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                super.paintComponent(g); g2.dispose();
            }
        };
        f.setOpaque(false); f.setForeground(TEXT_DARK); f.setCaretColor(ACCENT_BLUE);
        f.setFont(FONT_BODY); f.setPreferredSize(new Dimension(200,44));
        f.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(BORDER_COL,1,12),BorderFactory.createEmptyBorder(8,14,8,14)));
        f.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){f.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(ACCENT_BLUE,2,12),BorderFactory.createEmptyBorder(7,13,7,13)));}
            public void focusLost(FocusEvent e) {f.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(BORDER_COL,1,12),BorderFactory.createEmptyBorder(8,14,8,14)));}
        });
        return f;
    }

    public static JComboBox<String> createComboBox(String[] items){
        JComboBox<String> cb=new JComboBox<>(items);
        cb.setBackground(BG_INPUT); cb.setForeground(TEXT_DARK); cb.setFont(FONT_BODY);
        cb.setBorder(new RoundedBorder(BORDER_COL,1,10));
        cb.setPreferredSize(new Dimension(200,40)); return cb;
    }
    public static JLabel createTitle(String t){JLabel l=new JLabel(t);l.setFont(FONT_TITLE);l.setForeground(TEXT_DARK);return l;}
    public static JLabel createLabel(String t){JLabel l=new JLabel(t);l.setFont(FONT_BODY);l.setForeground(TEXT_MUTED);return l;}
    public static JLabel createFieldLabel(String t){JLabel l=new JLabel(t);l.setFont(FONT_BOLD);l.setForeground(TEXT_MUTED);return l;}
    public static JPanel createSectionHeader(String title){
        JPanel p=new JPanel(new BorderLayout(10,0)); p.setOpaque(false);
        JLabel l=new JLabel(title); l.setFont(FONT_BOLD); l.setForeground(ACCENT_BLUE);
        JSeparator s=new JSeparator(); s.setForeground(BORDER_COL);
        p.add(l,BorderLayout.WEST); p.add(s,BorderLayout.CENTER); return p;
    }
    public static void styleTable(JTable t){
        t.setBackground(Color.WHITE); t.setForeground(TEXT_DARK); t.setFont(FONT_BODY);
        t.setRowHeight(40); t.setGridColor(new Color(241,245,249));
        t.setSelectionBackground(new Color(37,99,235,40)); t.setSelectionForeground(TEXT_DARK);
        t.setShowVerticalLines(false); t.setIntercellSpacing(new Dimension(0,1));
        t.getTableHeader().setBackground(new Color(18,24,58));
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(FONT_BOLD); t.getTableHeader().setPreferredSize(new Dimension(0,44));
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,3,0,ACCENT_CORAL));
    }
    public static JScrollPane darkScrollPane(JComponent c){
        JScrollPane sp=new JScrollPane(c);
        sp.setBackground(Color.WHITE); sp.getViewport().setBackground(Color.WHITE);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI(){
            protected void configureScrollBarColors(){thumbColor=new Color(37,99,235,120);trackColor=BG_PAGE;}
        });
        return sp;
    }
    public static JRadioButton styledRadio(String text){
        JRadioButton r=new JRadioButton(text);
        r.setFont(FONT_BODY);r.setForeground(TEXT_DARK);r.setOpaque(false);r.setFocusPainted(false);return r;
    }
    public static JLabel badgeChip(String text,Color color){
        JLabel l=new JLabel("  "+text+"  ");
        l.setFont(FONT_BOLD);l.setForeground(color);l.setOpaque(true);
        l.setBackground(new Color(color.getRed(),color.getGreen(),color.getBlue(),18));
        l.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(color,1,8),BorderFactory.createEmptyBorder(3,6,3,6)));
        return l;
    }
    public static JPanel buildHeader(String titleText,String subtitleText){
        JPanel h=new JPanel(new BorderLayout()){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                GradientPaint gp=new GradientPaint(0,0,new Color(18,24,58),getWidth(),0,new Color(30,42,90));
                g2.setPaint(gp); g2.fillRect(0,0,getWidth(),getHeight());
                super.paintComponent(g); g2.dispose();
            }
        };
        h.setOpaque(false);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,3,0,ACCENT_CORAL),
            BorderFactory.createEmptyBorder(16,28,16,28)));
        JPanel col=new JPanel(new GridLayout(2,1,0,3)); col.setOpaque(false);
        JLabel title=new JLabel(titleText); title.setFont(FONT_TITLE); title.setForeground(Color.WHITE);
        JLabel sub=new JLabel(subtitleText); sub.setFont(FONT_SMALL); sub.setForeground(new Color(180,190,220));
        col.add(title); col.add(sub);
        h.add(col,BorderLayout.WEST); return h;
    }
    public static void addSectionLabel(JPanel panel,String text){
        JLabel l=new JLabel("  \u25b8  "+text);
        l.setFont(new Font("Segoe UI",Font.BOLD,9));
        l.setForeground(new Color(180,160,220));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(12,0,4,0));
        panel.add(l);
    }

    /** Shared wordmark — paints "BusWay" with indigo "Bus" + coral→violet "Way" + underline + gold dot */
    public static void paintWordmark(Graphics2D g2, int x, int y, int fontSize){
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Font f=new Font("Segoe UI",Font.BOLD,fontSize);
        g2.setFont(f);
        FontMetrics fm=g2.getFontMetrics(f);
        // "Bus" deep indigo
        g2.setColor(new Color(18,24,58)); g2.drawString("Bus",x,y);
        int busW=fm.stringWidth("Bus");
        int wayW=fm.stringWidth("Way");
        // "Way" coral→violet
        GradientPaint gp=new GradientPaint(x+busW,0,ACCENT_CORAL,x+busW+wayW,0,ACCENT_VIOLET);
        g2.setPaint(gp); g2.drawString("Way",x+busW,y);
        // Underline under "Way"
        g2.setStroke(new BasicStroke(3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        GradientPaint ul=new GradientPaint(x+busW,0,ACCENT_CORAL,x+busW+wayW,0,new Color(109,40,217,0));
        g2.setPaint(ul); g2.drawLine(x+busW,y+5,x+busW+wayW-4,y+5);
        // Gold dot
        g2.setColor(ACCENT_GOLD); g2.fillOval(x+busW+wayW+3,(int)(y-fontSize*0.42f),7,7);
    }
}