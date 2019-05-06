package shape;

public class Circle {

    private PPoint center;
    private double radius;

    public Circle(PPoint center, double radius){
        this.center = center;
        this.radius = radius;
    }


    public PPoint getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }


    /** The collinear() check based on Area2() from lecture slides */
    private static boolean collinear(PPoint a, PPoint b, PPoint c){
        Double ret = (b.x - a.x) * (c.y - a.y)
                - (c.x - a.x) * (b.y - a.y);
        return ret.intValue() == 0;
    }

    /** Smallest enclosing disc for two points */
    public static Circle diameter(PPoint p1, PPoint p2){
        PPoint c = new PPoint((p1.x + p2.x)/2, (p1.y + p2.y)/2);
        return new Circle(c, c.distance(p1));
    }

    /**
     * Construct a "circumscribed circle" given three points on
     * the boundary/circumference of the circle. Source code from:
     * https://www.geeksforgeeks.org/equation-of-circle-when-
     * three-points-on-the-circle-are-given/
     */
    public static Circle computeFrom(PPoint p1, PPoint p2, PPoint p3){

        // These three points cannot be collinear
        if(collinear(p1, p2, p3))
            return null;

        double x1 = p1.x;
        double y1 = p1.y;

        double x2 = p2.x;
        double y2 = p2.y;

        double x3 = p3.x;
        double y3 = p3.y;

        double x12 = x1 - x2;
        double x13 = x1 - x3;

        double y12 = y1 - y2;
        double y13 = y1 - y3;

        double y31 = y3 - y1;
        double y21 = y2 - y1;

        double x31 = x3 - x1;
        double x21 = x2 - x1;

        double sx13 = Math.pow(x1, 2) -
                Math.pow(x3, 2);

        double sy13 = Math.pow(y1, 2) -
                Math.pow(y3, 2);

        double sx21 = Math.pow(x2, 2) -
                Math.pow(x1, 2);

        double sy21 = Math.pow(y2, 2) -
                Math.pow(y1, 2);

        double f = ((sx13) * (x12)
                + (sy13) * (x12)
                + (sx21) * (x13)
                + (sy21) * (x13))
                / (2 * ((y31) * (x12) - (y21) * (x13)));
        double g = ((sx13) * (y12)
                + (sy13) * (y12)
                + (sx21) * (y13)
                + (sy21) * (y13))
                / (2 * ((x31) * (y12) - (x21) * (y13)));

        double c = -Math.pow(x1, 2) - Math.pow(y1, 2) -
                2 * g * x1 - 2 * f * y1;

        // eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
        // where centre is (h = -g, k = -f) and radius r
        // as r^2 = h^2 + k^2 - c
        double h = -g;
        double k = -f;
        double sqr_of_r = h * h + k * k - c;

        // r is the radius
        double r = Math.sqrt(sqr_of_r);

        return new Circle(new PPoint(h, k), r);
    }

    /** contains test based on Pythagoras */
    public boolean contains(PPoint p){

        double dx = Math.abs(p.x - center.x);
        double dy = Math.abs(p.y - center.y);

        if(dx > radius || dy > radius)
            return false;

        if(dx*dx + dy*dy < (radius*radius+1e-6))
            return true; // considering onCircle != contains

        return false;
    }

    public String toString(){
        return "[Center: " + center.x + ", " + center.y + " Radius: " + radius + "]";
    }

}
