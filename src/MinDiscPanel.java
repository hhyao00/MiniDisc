import shape.Circle;
import shape.PPoint;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * Algorithm from Chapter 4 of BCKO Textbook,
 * no optimizations (except keeping permutation)
 * so it is as in the textbook.
 */
public class MinDiscPanel extends JPanel {

    private final Color DISC_COLOR = new Color(154, 195, 238, 255);
    private final Color POINT_COLOR = new Color(218, 218, 218, 255);
    private final Color POINT_COLOR2 = new Color(106, 106, 106, 255);
    private final Color PINK = new Color(244, 180, 208, 255);

    // ----- GUI stuff ----- //

    private double width;   // screen width
    private double height;  // screen height

    private Point2D lastPt; // latest click point of mouse
    private Point origin;   // origin point to allow panning,
                            // panning is enabled for all but freehand

    private int size = 6;   // size of a PPoint circle
    private Timer timer;    // timer because animation
    private int delay = 160;

    private enum MODE {
        NONE,
        RUNNING,    // either random or compute mode
        FREEHAND
    }
    private MODE mode;

    private enum PSET {
        Pi, Pj, Pk
    }
    private PSET pset;


    // Details panel to show numbers and stuff
    private DetailsPanel detailsPanel;


    // ----- Algorithm stuff ----- //

    /** The smallest enclosing disc */
    private Circle disc = null;

    // -- These are state variables for MiniDisc() -- //
    private int iIter;      // iteration of outermost loop
    private List<PPoint> P, P_j, P_k;
    private PPoint q1, q2;  // points to be on boundary
    private int ix_i = 2, ix_j = 1, ix_k = 0;   // index of routine

    // animating this required pausing the algorithm
    // every time a new disc was computed. This was such a pain.
    private enum STEP {
        MINIDISC,
        MINIDISC_ONEPOINT,
        MINIDISC_TWOPOINT,
        DIAMETER, DIAMETER_j, DIAMETER_k
    }
    private STEP step;  // next action


    // --------- end of fields ------------ //



    public MinDiscPanel() {

        // set up the user interface dimensions
        Dimension d = getPreferredSize();
        d.setSize(900, 700);
        width = d.getWidth();
        height = d.getHeight();
        setPreferredSize(d);

        origin = new Point();
        configureMouseListeners();
        P = new ArrayList<>();
        mode = MODE.NONE;

        setBackground(Color.WHITE);
        revalidate();
        repaint();
    }


    // --------- Related details panel ----------- //

    public void receiveDetailsPanel(JPanel detailsPanel){
        this.detailsPanel = (DetailsPanel) detailsPanel;
    }

    /** Update the detailsPanel, track which routine we are on. */
    private void updateRoutine(PSET pset){
        if(mode != MODE.RUNNING) return;
        switch(pset) {
            case Pi:
                detailsPanel.updateRoutine("MiniDisc", 1);
                break;
            case Pj:
                detailsPanel.updateRoutine("MiniDiscOnePoint", 2);
                break;
            case Pk:
                detailsPanel.updateRoutine("MiniDiscTwoPoints", 3);
                break;
        }
    }


    // --------- Visual interface set up --------- //

    /** PaintComponent method to update the visual display */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2.1f));

        if (P != null) {
            for (PPoint p : P) {
                g2.setColor(POINT_COLOR);
                if(P_j != null
                        && mode == MODE.RUNNING
                        && P_j.contains(p))
                    g2.setColor(POINT_COLOR2);
                drawPoint(p, g2);
            }
            if(q1 != null && mode == MODE.RUNNING){
                g2.setColor(PINK);
                drawPoint(q1, g2);
            }
            if(q2 != null && mode == MODE.RUNNING){
                g2.setColor(PINK);
                drawPoint(q2, g2);
            }
        }

        if (lastPt != null && mode == MODE.FREEHAND) {
            g2.setColor(POINT_COLOR);
            drawPoint(new PPoint(lastPt.getX() + origin.getX(),
                    lastPt.getY() + origin.getY()), g2);
        }

        if (disc != null) {
            g2.setColor(DISC_COLOR);
            drawEllipse(g2);
        }
    }

    /** Helper to draw a point */
    private void drawPoint(PPoint p, Graphics2D g2) {
        Ellipse2D e = new Ellipse2D.Double(p.x - size/ 2 + origin.getX(),
                p.y - size / 2 + origin.getY(), size, size);
        g2.fill(e);
        g2.draw(e);
    }

    /** helper to draw an ellipse */
    private void drawEllipse(Graphics2D g2) {
        PPoint c = disc.getCenter();
        double r = disc.getRadius();
        Ellipse2D elli = new Ellipse2D.Double(
                c.x - r - size / 2 + origin.getX(),
                c.y - r - size / 2 + origin.getY(),
                r * 2 + size,
                r * 2 + size);
        g2.draw(elli);
    }

    /** Helper, add a point (clicked) to list P */
    private void addPoint(MouseEvent e) {
        PPoint pt = new PPoint(e.getX() - origin.getX(), e.getY() - origin.getY());
        P.add(pt);
        detailsPanel.totalPoints(P.size());
        repaint();
    }

    /** Allow map to respond to clicks and drags of the mouse. */
    private void configureMouseListeners() {

        // Mouse Click
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                // set the new mouse point.
                lastPt = e.getPoint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                if (mode == MODE.FREEHAND) {
                    P.add(new PPoint(e.getX(), e.getY()));
                    disc = Disc.computeMiniDisc(P);
                    // detailsPanel.totalPoints(P.size());
                } else if (mode != MODE.RUNNING) {
                    addPoint(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (mode == MODE.FREEHAND) {
                    addPoint(e);
                    disc = Disc.computeMiniDisc(P);
                }
            }
        });

        // Mouse Motion
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                if (mode != MODE.FREEHAND) {
                    double dx = e.getX() - lastPt.getX();
                    double dy = e.getY() - lastPt.getY();
                    origin.setLocation(origin.getX() + dx, origin.getY() + dy);
                    lastPt = e.getPoint();
                    repaint();
                }
                else if (mode == MODE.FREEHAND) {
                    lastPt = e.getPoint();
                    List<PPoint> temp = new ArrayList<>(P);
                    temp.add(new PPoint(e.getX(), e.getY()));
                    disc = Disc.computeMiniDisc(temp);
                    repaint();
                }
            }
        });

    }


    // ----------- For gui buttons ---------- //

    /**
     * Nullify/clear all the variables
     */
    private void reset() {
        disc = null;
        ix_i = 2;
        ix_j = 1;
        ix_k = 0;
        q1 = null;
        q2 = null;
        P_j = null;
        P_k = null;
        lastPt = null;
        detailsPanel.clear();
    }

    /**
     * Button endpoint: Stop, but keep P
     */
    public void timerStop() {
        if (timer != null && timer.isRunning())
            timer.stop();
        repaint();
    }

    /**
     * Button endpoint: restart the timer
     */
    public void timerStart() {
        if(mode != MODE.RUNNING) return;
        if (timer != null && !timer.isRunning())
            timer.start();
        repaint();
    }

    /**
     * Button endpoint, clear canvas
     */
    public void clear() {
        mode = MODE.NONE;
        timerStop();
        reset();
        removeAll();
        P = new ArrayList<>();
        repaint();
    }

    /**
     * Button endpoint; random generation of points
     */
    public boolean random(int count) {
        clear();
        P = randomPointSet(count);
        start();
        return true;
    }

    /**
     * Button endpoint; mini disc for user inputted points
     */
    public boolean normal() {
        timerStop();
        reset();
        removeAll();
        start();
        return true;
    }

    /**
     * Button endpoint: freehand. No panning
     */
    public void freehand() {
        origin.setLocation(0, 0);
        clear();
        repaint();
        mode = MODE.FREEHAND;
    }

    public void shuffle(){
        if(P == null)
            return;
        Collections.shuffle(P);
    }

    public void setTimerDelay(int value){
        if(timer!=null && mode == MODE.RUNNING) {
            timer.stop();
            delay = value;
            timer.setDelay(value);
            timer.restart();
        }
    }


    // ------------- Algorithm stuff ------------- //

    /**
     * Generate some random points P of size n = count.
     */
    private ArrayList<PPoint> randomPointSet(int count) {
        Random rand = new Random();
        ArrayList<PPoint> points = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PPoint pp = new PPoint(rand.nextDouble() * width / 1.5 + width / 7,
                    rand.nextDouble() * height / 1.2 + height / 7);
            points.add(pp);
        }
        Collections.shuffle(points, rand);
        return points;
    }

    /** Start computing the miniDisc. Sets mode = RUNNING. */
    public void start() {

        detailsPanel.totalPoints(P.size());

        // base cases (?)
        if (P.size() == 0) {
            disc = new Circle(new PPoint(0, 0), 0);
            return;
        }
        if (P.size() == 1) {
            PPoint p = P.get(0);
            disc = new Circle(new PPoint(p.x, p.y), 0);
            return;
        }

        mode = MODE.RUNNING;
        disc = Circle.diameter(P.get(0), P.get(1));
        step = STEP.DIAMETER;

        iIter = 0;
        timer = new Timer(delay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                detailsPanel.incRunFrame();
                if (timer.isRunning() && iIter >= P.size()) {
                    timer.stop();
                    mode = MODE.NONE;
                }
                switch (step) {
                    case DIAMETER:
                        diameter(P.get(0), P.get(1));
                        step = STEP.MINIDISC;
                        break;
                    case MINIDISC:
                        updateRoutine(PSET.Pi);
                        miniDisc();
                        iIter++;
                        break;

                    case DIAMETER_j:
                        updateRoutine(PSET.Pj);
                        diameter(q1, P_j.get(0));
                        step = STEP.MINIDISC_ONEPOINT;
                        break;
                    case MINIDISC_ONEPOINT:
                        miniDiscWithPoint();
                        break;

                    case DIAMETER_k:
                        updateRoutine(PSET.Pk);
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

    private void diameter(PPoint p1, PPoint p2) {
        disc = Circle.diameter(p1, p2);
    }

    /**
     * Input: a set P of n points in the plane
     * Output: The smallest enclosing disc p1..pn of P
     */
    private void miniDisc() {
        for (int i = ix_i; i < P.size(); ) {
            PPoint p_i = P.get(i);
            if (disc.contains(p_i)) {
//                p_i.setDone();
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
    private void miniDiscWithPoint() {
        for (int j = ix_j; j < P_j.size(); ) {
            PPoint p_j = P_j.get(j);
            if (disc.contains(p_j)) {
                p_j.setDone();
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
     */
    private void miniDiscWith2Points() {
        for (int k = ix_k; k < P_k.size(); ) {
            PPoint p_k = P_k.get(k);
            if (disc.contains(p_k)) {
                p_k.setDone();
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
