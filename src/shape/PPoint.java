package shape;


public class PPoint {

    public double x;
    public double y;

    private boolean done;
    private boolean done2;



    public PPoint(double x, double y){
        done = false;
        done2 = false;
        this.x = x;
        this.y = y;
    }


    public void setDone() {
        done = true;
    }

    public void notDone() {
        done = false;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone2() {
        done2 = true;
    }

    public boolean isDone2() {
        return done2;
    }


    public double distance(PPoint p) {
        return Math.hypot(this.x - p.x, this.y - p.y);
    }

    public String toString(){
        return "[x: " + x + ", y: " + y + "]";
    }
}