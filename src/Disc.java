import shape.Circle;
import shape.PPoint;

import java.util.Collections;
import java.util.List;

public class Disc {

    // Just the raw algorithm

    public Disc(){}

    public static Circle computeMiniDisc(List<PPoint> points){

        if(points.size() == 0)
            return new Circle(new PPoint(0,0), 0);
        if(points.size() == 1) {
            PPoint p = points.get(0);
            return new Circle(new PPoint(p.x, p.y), 0);
        }

        Collections.shuffle(points);
        Circle disc = Circle.diameter(points.get(0), points.get(1));

        for(int i = 2; i < points.size(); i++){
            PPoint p_i = points.get(i);
            if(disc.contains(p_i))
                continue;
            else
                disc = miniDiscOnePoint(points.subList(0, i), p_i);
        }
        return disc;
    }

    private static Circle miniDiscOnePoint(List<PPoint> points, PPoint q){

        Circle disc = Circle.diameter(q, points.get(0));
        for(int j = 1; j < points.size(); j++){
            if(disc.contains(points.get(j)))
                continue;
            else
                disc = miniDiscTwoPoints(points.subList(0,j), q, points.get(j));
        }
        return disc;
    }

    private static Circle miniDiscTwoPoints(List<PPoint> points, PPoint q1, PPoint q2){

        Circle disc = Circle.diameter(q1, q2);
        for(int k = 0; k < points.size(); k++){
            if(disc.contains(points.get(k)))
                continue;
            else
                disc = Circle.computeFrom(points.get(k), q1, q2);
        }
        return disc;
    }

}
