import shape.Circle;
import shape.PPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Algorithm from Chapter 4 of BCKO Textbook,
 * no optimizations (except keeping permutation)
 * so it is as in the textbook.
 * */
public class MinDiscPanel extends JPanel {

    private final Color LIGHT_BLUE = new Color(141, 196, 243, 255);
    private final Color PINK2 = new Color(236, 188, 203, 255);
    private final Color PINK3 = new Color(236, 159, 191, 255);

    // ----- GUI stuff ----- //

    private double width;   // screen width
    private double height;  // screen height

    private Point2D lastPt;  // latest click point of mouse
    private Point origin;    // origin point to allow panning

    private int size = 5;   // size of a PPoint
    private Timer timer;    // timer because animation
    private int iIter;      // iteration of outermost loop

    private boolean focusPoint = false;
    private boolean focusDisc = false;


    // ----- Algorithm stuff ----- //

    /** The smallest enclosing disc */
    private Circle disc = null;

    private enum STEP {
        MINIDISC,
        MINIDISC_ONEPOINT,
        MINIDISC_TWOPOINT,
        DIAMETER, DIAMETER_j, DIAMETER_k
    }
    private STEP step;
    private List<PPoint> P, P_j, P_k;
    private PPoint q1, q2;
    private int ix_i = 2, ix_j, ix_k;


    private void reset(){
        P = randomPointSet(40);
        disc = Circle.diameter(P.get(0), P.get(1));
        step = STEP.DIAMETER;
        ix_i = 2;
        ix_j = 0;
        ix_k = 0;
        q1 = null;
        q2 = null;
        P_j = null;
        P_k = null;
    }



    public MinDiscPanel(){

        // set up the user interface dimensions
        Dimension d = getPreferredSize();
        d.setSize(1000, 700);
        width = d.getWidth();
        height = d.getHeight();
        setPreferredSize(d);

        origin = new Point();
        configureMouseListeners();
        setBackground(Color.WHITE);
        revalidate();

        P = randomPointSet(40);
        disc = Circle.diameter(P.get(0), P.get(1));
        step = STEP.DIAMETER;
        start();

        repaint();
    }


    // --------- Visual interface set up --------- //


    /** PaintComponent method to update the visual display */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2.0f));

        if(P != null){
            g2.setColor(Color.LIGHT_GRAY);
            for(PPoint p: P) {
                if(p.isDone())
                    g2.setColor(Color.BLACK);
                drawPoint(p, g2);
            }
        }

        if(disc != null){
            //System.out.println(disc.toString());
            g2.setColor(LIGHT_BLUE);
            drawEllipse(g2);
        }
    }

    /** helper to draw a point */
    private void drawPoint(PPoint p, Graphics2D g2){
        Ellipse2D e = new Ellipse2D.Double(p.x-size/2+origin.getX(),
                p.y-size/2+origin.getY(), size, size);
        g2.fill(e);
        g2.draw(e);
    }

    /** helper to draw an elipse */
    private void drawEllipse(Graphics2D g2){
        PPoint c = disc.getCenter();
        double r = disc.getRadius();
        Ellipse2D elli = new Ellipse2D.Double(c.x-r-size/2+origin.getX(),
                c.y-r-size/2+origin.getY(), r*2+size,r*2+size);
        g2.draw(elli);
    }

    /** Allow map to respond to clicks and drags of the mouse. */
    private void configureMouseListeners(){

        // Mouse Click
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                lastPt = e.getPoint(); // set the new mouse point.
            }

            @Override
            public void mouseClicked(MouseEvent e){
                //lastPt = new Point2D.Double(e.getX(), e.getY());
//                PPoint pt =  new PPoint(e.getX(), e.getY());
//                P.add(pt);
//                disc = Circle.computeFrom(P.get(P.size()-2), P.get(P.size()-3), pt);
//                repaint();


                // clicked a node; a double click.
                if(e.getClickCount() == 2){
                    //
                }
            }
        });

        // Mouse Motion
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                double dx = e.getX() - lastPt.getX();
                double dy = e.getY() - lastPt.getY();
                origin.setLocation(origin.getX() + dx, origin.getY() + dy);

                lastPt = e.getPoint();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e){

            }
        });
    }



    // ------------- Algorithm stuff ------------- //

    /**
     * Generate some random points P of size n = count.
     */
    private ArrayList<PPoint> randomPointSet(int count){
        int seed = 23489;
        Random rand = new Random();
        ArrayList<PPoint> points = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            PPoint pp = new PPoint(rand.nextDouble()*width/1.5 + width/7,
                    rand.nextDouble()*height/1.5 + height/7);
            //System.out.println(pp.toString());
            points.add(pp);
        }
        Collections.shuffle(points, rand);
        return points;
    }


    public void start(){

        reset();

        // Todo need to account for MODE
        iIter = 2;
        timer = new Timer(0, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(iIter >= P.size()){
                    //System.out.println("Done");
                    timer.stop();

                    for(int i = 0; i < P.size(); i++){
                        if(!disc.contains(P.get(i)))
                            System.out.println("Iter " + iIter + P.get(i).toString() + " " + i);
                    }
                }
                switch (step){
                    case DIAMETER:
                        diameter(P.get(0), P.get(1));
                        step = STEP.MINIDISC;
                        break;
                    case MINIDISC:
                        miniDisc();
                        iIter++;
                        break;

                    case DIAMETER_j:
                        diameter(q1, P_j.get(0));
                        step = STEP.MINIDISC_ONEPOINT;
                        break;
                    case MINIDISC_ONEPOINT:
                        miniDiscWithPoint();
                        break;

                    case DIAMETER_k:
                        diameter(q1, q2);
                        step = STEP.MINIDISC_TWOPOINT;
                        break;
                    case MINIDISC_TWOPOINT:
                        miniDiscWith2Points();
                        break;
                }
                repaint();
            }
        });
        timer.start();
    }

    private void diameter(PPoint p1, PPoint p2){
        disc = Circle.diameter(p1, p2);
    }

    /**
     * Input: a set P of n points in the plane
     * Output: The smallest enclosing disc p1..pn of P
     */
    private void miniDisc(){
        for(int i = ix_i; i < P.size();){
            PPoint p_i = P.get(i);
            if(disc.contains(p_i)){
                i++;
                continue;
            } else {
                P_j = P.subList(0, i);
                q1 = p_i;
                ix_i = ++i;
                ix_j = 1;
                step = STEP.DIAMETER_j;
                return;
            }
        }
    }

    /**
     * Input: A set P of n points, and a point q such that there
     * exists an enclosing disc for P with q on its boundary.
     */
    private void miniDiscWithPoint()
    {
        for(int j = ix_j; j < P_j.size();){
            PPoint p_j = P_j.get(j);
            if(disc.contains(p_j)) {
                j++;
                continue;
            } else {
                P_k = P_j.subList(0, j);
                q1 = q1;
                q2 = p_j;
                ix_j = ++j;
                ix_k = 0;
                step = STEP.DIAMETER_k;
                return;
            }
        }
        step = STEP.MINIDISC;
    }

    /**
     * Lowest routine, three points must determine a circle.
     * We have added the points and maintained the optimal disc.
     * */
    private void miniDiscWith2Points()
    {
        for(int k = ix_k; k < P_k.size();){
            PPoint p_k = P_k.get(k);
            if(disc.contains(p_k)){
                k++;
                continue;
            } else {
                disc = Circle.computeFrom(q1, q2, p_k);
                step = STEP.MINIDISC_TWOPOINT;
                ix_k = ++k;
                return;
            }
        }
        step = STEP.MINIDISC_ONEPOINT;
    }


}
