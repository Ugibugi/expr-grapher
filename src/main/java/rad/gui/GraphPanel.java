package rad.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * Panel rysujący wykresy funkcji.<br>
 * <pre>"A więc to jest potęga matematyki maturalnej..."</pre>
 */
public class GraphPanel extends JPanel {
    double scale;
    public int vx,vy;
    int segnum, gridDensity,maxDepth;
    public double scalestep, scalefactor, maxError;
    int W,H;
    public boolean autostep=true;
    public LinkedList<Function<Double,Double>> functions;
    Point mousePt;
    public boolean showDivisions=false;

    public GraphPanel()
    {
        super();
        scale = 1.0f;
        segnum=20;
        functions = new LinkedList<>();
        gridDensity = 10;
        scalefactor=1;
        maxError = 0.25f;
        maxDepth = 8;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePt = e.getPoint();
                repaint();
            }


        });
        this.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                scalefactor *= Math.pow(2,e.getWheelRotation());
                //if(scalefactor == 0) scalefactor -= e.getWheelRotation();
                repaint();
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - mousePt.x;
                int dy = e.getY() - mousePt.y;
                vx += dx;
                vy-=dy;
                mousePt = e.getPoint();
                repaint();
            }
        });
    }
    /**
     * Rysuje graf funkcji w średnio zoptymalizowany sposób
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {

        W = getWidth();
        H = getHeight();
        scalestep = W/gridDensity;
        scale = scalefactor*scalestep;

        Graphics2D g2d = (Graphics2D)g;
        g2d.setBackground(Color.WHITE);
        g2d.setColor(Color.BLACK);


        g.clearRect(0,0,W,H);
        drawGrid(g2d);

        int step = W/segnum;
        for(Function<Double,Double> fun : functions)
        {
            if(autostep)
            {
                step = W/20;
                for(int i=0;i<W;i+=step)
                {
                   drawAutostep(g2d,i,i+step,(x) ->  H-(int)(scale*fun.apply((x-vx)/scale)+vy),maxDepth);
                }
            }
            else
            {
                //transformacja funkcji do postaci w "przestrzeni pikseli"
                Function<Integer,Integer> f = (x) ->  H-(int)(scale*fun.apply((x-vx)/scale)+vy);
                int y1 = f.apply(0);
                for(int i=0;i<W;i+=step)
                {
                    int y2 = f.apply(i+step);
                    if(y2 > 0 || y1 > 0) g2d.drawLine(i,y1,i+step,y2);
                    y1=y2;
                }
            }


        }
        drawInfo(g2d);
    }

    private void drawInfo(Graphics2D g2d) {
        Font reset = g2d.getFont();

        g2d.setFont(new Font("Arial Bold",Font.ITALIC,14));
        g2d.drawString("scale = "+Double.toString(scale),0,14);
        g2d.drawString("vx = "+Double.toString(vx),0,2*14);
        g2d.drawString("vy = "+Double.toString(vy),0,3*14);

        g2d.setFont(reset);

    }



    private void drawGrid(Graphics2D g2d) {
        int gridStep = W/gridDensity;
        int xgridStart = vx%gridStep;
        int xlabelHeight = H-Util.clamp(0,vy,H);


        int ygridStart = vy%gridStep;
        int ylabel = Util.clamp(0,vx,W);

        Stroke reset = g2d.getStroke();
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, Math.abs(vy)));

            for(int i = xgridStart;i<W;i+=gridStep)
            {
                g2d.drawLine(i,0,i,H);
                g2d.drawString(Double.toString(((i-vx)/scale)),i,xlabelHeight+10);
            }
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, Math.abs(H-vx)));
            for(int i = ygridStart;i<H;i+=gridStep)
            {
                g2d.drawLine(0,H-i, W,H-i);
                g2d.drawString(Double.toString(((i-vy)/scale)),ylabel+10,H-i);
            }
        g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(0,xlabelHeight,W,xlabelHeight);
            g2d.drawLine( ylabel,0, ylabel,H);
        g2d.setStroke(reset);
    }

    /**
     * Funkcja w sposób rekurencyjny rysuje odcinek funkcji z automatycznie dobieraną długością odcinka
     * @param g2d Obiekt Graphics2D do rysowania
     * @param x1 Początek zakresu argumentów
     * @param x2 Koniec zakresu argumentów
     * @param f funkcja rysowana, argumenty i wartosci podawane w pikselach
     * @param depth max glebokosc rekursji
     */
    private void drawAutostep(Graphics2D g2d,int x1,int x2,Function<Integer,Integer> f,int depth)
    {
        int y1 = f.apply(x1);
        int y2 = f.apply(x2);
        //nie rysuj odcinka jeżeli jest poza ekranem
        if(!Util.inrange(0,y1,H) && !Util.inrange(0,y2,H)) return;

        int xmid = Util.midpoint(x1,x2);
        int ymid = f.apply(xmid);
        
        //obliczenie bledu miedzy linia rysowaną a prawdziwym przebiegiem funkcji
        double relerr = (Math.hypot(xmid-x1,ymid-y1) + Math.hypot(x2-xmid,y2-ymid) - Math.hypot(x2-x1,y2-y1));
        if(Math.abs(relerr) < maxError || depth == 0)
        {
            g2d.drawLine(x1,y1,xmid,ymid);
            g2d.drawLine(xmid,ymid,x2,y2);

            //punkty podziałki do podziwiania efektu xd
            if(showDivisions)
            {
                g2d.drawRect(x1,y1,5,5);
                g2d.drawRect(xmid,ymid,5,5);
                g2d.drawRect(x2,y2,5,5);
            }

        }
        else
        {
            drawAutostep(g2d,x1,xmid,f,depth-1);
            drawAutostep(g2d,xmid,x2,f,depth-1);
        }


    }

}
