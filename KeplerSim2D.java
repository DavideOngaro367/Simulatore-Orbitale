import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.*;

public class KeplerSim2D extends JFrame {

    // Valori iniziali variabili (in milioni)
    private double eccentricita = 0.5;
    private double semiasseMaggiore = 15000000.0;
    private double raggioPianeta = 6378106.0;     

    private JLabel lblEccentricita;
    private JLabel lblSemiasse;
    private JLabel lblRaggio;
    
    private scena areaDisegno;

    public KeplerSim2D() {
        super("KeplerSim 2D - Simulatore Orbitale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900); // Dimensioni finestra
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        // Pannello controlli
        JPanel pnlControlli = new JPanel();
        pnlControlli.setLayout(new BoxLayout(pnlControlli, BoxLayout.Y_AXIS));
        pnlControlli.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margine interno

        // CONTROLLO ECCENTRICITÀ 
        lblEccentricita = new JLabel("Eccentricità: " + eccentricita);
        pnlControlli.add(lblEccentricita);
        JSlider sliderE = new JSlider(JSlider.HORIZONTAL, 0, 99, 50);
        pnlControlli.add(sliderE);
        pnlControlli.add(Box.createRigidArea(new Dimension(0, 15))); 

        // CONTROLLO SEMIASSE MAGGIORE 
        lblSemiasse = new JLabel("Semiasse (milioni di metri): 15");
        pnlControlli.add(lblSemiasse);
        JSlider sliderA = new JSlider(JSlider.HORIZONTAL, 7, 30, 15);
        pnlControlli.add(sliderA);
        pnlControlli.add(Box.createRigidArea(new Dimension(0, 15)));

        // CONTROLLO RAGGIO PIANETA
        lblRaggio = new JLabel("Raggio Pianeta (milioni di metri): 6.3");
        pnlControlli.add(lblRaggio);
        JSlider sliderR = new JSlider(JSlider.HORIZONTAL, 2, 10, 6);
        pnlControlli.add(sliderR);

        add(pnlControlli, BorderLayout.WEST);

        // Pannello area disegno
        areaDisegno = new scena();
        add(areaDisegno, BorderLayout.CENTER);
        
        sliderE.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                eccentricita = sliderE.getValue() / 100.0;
                lblEccentricita.setText("Eccentricità: " + eccentricita);
                areaDisegno.repaint();
            }
        });

        sliderA.addChangeListener(e -> {
            semiasseMaggiore = sliderA.getValue() * 1000000.0;
            lblSemiasse.setText("Semiasse (milioni di metri): " + sliderA.getValue());
            areaDisegno.repaint();
        });

        sliderR.addChangeListener(e -> {
            raggioPianeta = sliderR.getValue() * 1000000.0;
            lblRaggio.setText("Raggio Pianeta (milioni di metri): " + sliderR.getValue());
            areaDisegno.repaint();
        });
    }

    private class scena extends JPanel {
        public scena() {
            setBackground(Color.black);
        }

        @Override
        protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int centroX = getWidth() / 2;
        int centroY = getHeight() / 2;

        // Disegno pianeta 
        double scala = 100000.0; 
        int rPianetaPix = (int) (raggioPianeta / scala);
        g.setColor(Color.BLUE);
        g.fillOval(centroX - rPianetaPix, centroY - rPianetaPix, 
                rPianetaPix * 2, rPianetaPix * 2);

        // Disegno orbita 
        g.setColor(Color.GRAY);
        
        double thetaGradi = 0;
        double step = 1.0;

        // Calcolo perigeo per eventuale collisione
        double perigeo = semiasseMaggiore * (1 - eccentricita);
        double apogeo = semiasseMaggiore * (1 + eccentricita);
        boolean collisione = perigeo < raggioPianeta;

        double rIniziale = (semiasseMaggiore * (1 - eccentricita * eccentricita)) / (1 + eccentricita * Math.cos(Math.toRadians(0)));
        double xIniziale = centroX + (rIniziale * Math.cos(Math.toRadians(0))) / scala;
        double yIniziale = centroY - (rIniziale * Math.sin(Math.toRadians(0))) / scala;

        double xPrecedente = xIniziale;
        double yPrecedente = yIniziale;

        //Disegno asse X
        int yScena = getHeight(); 
        g.drawLine(0, yScena / 2, getWidth(), yScena / 2);

        //Disegno asse Y
        int xScena = getWidth(); 
        g.drawLine(xScena / 2, 0, xScena / 2, getHeight());

        while (thetaGradi < 360) {

            if(eccentricita != 0){
                g.setColor(Color.GREEN);
                if(collisione == false){
                    double xPerigeo = centroX + (perigeo * Math.cos(Math.toRadians(0))) / scala;
                    double yPerigeo = centroY;
                    g.fillOval((int) xPerigeo - 3, (int) yPerigeo - 3, 6, 6);
                    g.setColor(Color.white);
                    String msgPerigeo = String.format("Perigeo: %.2f km", perigeo / 1000.0);
                    g.drawString(msgPerigeo, (int) xPerigeo + 6, (int) yPerigeo + 4);

                    g.setColor(Color.ORANGE);
                    double xApogeo = centroX - (apogeo / scala); 
                    double yApogeo = centroY; 
                    g.fillOval((int) xApogeo - 3, (int) yApogeo - 3, 6, 6); 
                    g.setColor(Color.white);
                    String msgApogeo = String.format("Apogeo: %.2f km", apogeo / 1000.0);
                    g.drawString(msgApogeo, (int) xApogeo + 6, (int) yApogeo + 4);
                }
            }

            if (collisione)
                g.setColor(Color.RED); 
            else 
                g.setColor(Color.WHITE);
            
            thetaGradi += step;
            double thetaRadianti = Math.toRadians(thetaGradi);
            
            double r = (semiasseMaggiore * (1 - eccentricita * eccentricita)) / (1 + eccentricita * Math.cos(thetaRadianti));
            
            double xCorrente = centroX + (r * Math.cos(thetaRadianti)) / scala;
            double yCorrente = centroY - (r * Math.sin(thetaRadianti)) / scala;

            g2d.draw(new Line2D.Double(xPrecedente, yPrecedente, xCorrente, yCorrente));

            xPrecedente = xCorrente;
            yPrecedente = yCorrente;           
        }

        g.drawLine((int)xPrecedente, (int)yPrecedente, (int)xIniziale, (int)yIniziale);
        
        if (collisione) {
            g.setColor(Color.RED);
            g.drawString("ATTENZIONE: Collisione rilevata!!", 10, 20);
        }
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new KeplerSim2D().setVisible(true);
        });
    }
}