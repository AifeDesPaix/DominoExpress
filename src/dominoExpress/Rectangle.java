
package dominoExpress;

//~--- non-JDK imports --------------------------------------------------------

import com.jme3.math.Vector3f;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Nkplizz
 */
public class Rectangle {
    private Point2D.Double p1;
    private Point2D.Double p2;
    private Point2D.Double p3;
    private Point2D.Double p4;
    public Point2D.Double  centre;
    double                 angle;
    Vector3f               taille;

    public Rectangle(double x, double y, double angle, Vector3f taille) {
        this.angle  = angle;
        this.taille = taille;

        double l         = taille.x * 50;    // largeur d'un domino/2
        double d         = taille.z * 50;    // epaisseur d'un domino/2
        double xpoints[] = { (x - l), (x + l), (x + l), (x - l) };
        double ypoints[] = { (y - d), (y - d), (y + d), (y + d) };

        for (int i = 0; i < xpoints.length; i++) {
            double dx = (xpoints[i] - x);
            double dy = (ypoints[i] - y);

            xpoints[i] = x - dx * Math.cos(-angle) + dy * Math.sin(-angle);
            ypoints[i] = y - dx * Math.sin(-angle) - dy * Math.cos(-angle);
        }

        centre = new Point2D.Double(x, y);
        p1     = new Point2D.Double(xpoints[0], ypoints[0]);
        p2     = new Point2D.Double(xpoints[1], ypoints[1]);
        p3     = new Point2D.Double(xpoints[2], ypoints[2]);
        p4     = new Point2D.Double(xpoints[3], ypoints[3]);
    }

    public void translate(double d, double d0) {
        p1     = new Point2D.Double(p1.x + d, p1.y + d0);
        p2     = new Point2D.Double(p2.x + d, p2.y + d0);
        p3     = new Point2D.Double(p3.x + d, p3.y + d0);
        p4     = new Point2D.Double(p4.x + d, p4.y + d0);
        centre = new Point2D.Double(centre.x + d, centre.y + d0);
    }

    public void draw(Graphics2D g) {
        g.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
        g.draw(new Line2D.Double(p2.x, p2.y, p3.x, p3.y));
        g.draw(new Line2D.Double(p3.x, p3.y, p4.x, p4.y));
        g.draw(new Line2D.Double(p4.x, p4.y, p1.x, p1.y));
        g.draw(new Line2D.Double(centre.x, centre.y, centre.x, centre.y));
    }
}

