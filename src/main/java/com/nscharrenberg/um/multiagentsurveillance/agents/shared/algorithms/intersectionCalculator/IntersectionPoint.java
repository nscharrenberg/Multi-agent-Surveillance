package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.intersectionCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class IntersectionPoint {

    /**
     * Compute the intersection point of two lines
     * @param A point of the first line
     * @param B point of the first line
     * @param C point of the second line
     * @param D point of the second line
     * @return the intersection point of two lines
     */
    public static Tile calculateIntersectionPoint(Point A, Point B, Point C, Point D){

        // Line X represented as a1x + b1y = c1
        double a1 = B.y() - A.y();
        double b1 = A.x() - B.x();
        double c1 = a1*(A.x()) + b1*(A.y());

        // Line Y represented as a2x + b2y = c2
        double a2 = D.y() - C.y();
        double b2 = C.x() - D.x();
        double c2 = a2*(C.x())+ b2*(C.y());

        double determinant = a1*b2 - a2*b1;

        if (determinant == 0) {
            throw new RuntimeException("Impossible action, Lines are parallel (Mistake)");
        } else {
            int x = (int) Math.round((b2*c1 - b1*c2)/determinant);
            int y = (int) Math.round((a1*c2 - a2*c1)/determinant);
            return new Tile(x, y);
        }
    }
}
