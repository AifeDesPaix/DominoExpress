
package Outil;

//~--- non-JDK imports --------------------------------------------------------

import com.jme3.math.Vector3f;

import dominoExpress.Domino;
import dominoExpress.Editor;

//~--- JDK imports ------------------------------------------------------------

import java.awt.geom.Point2D.Double;

import java.util.ArrayList;

public class Predefini implements Outil {
    public double                      rotateAngle = 0;
    private boolean                    reverse     = false;
    public ArrayList<Domino>           base;
    public ArrayList<Double>           patron;
    public ArrayList<java.lang.Double> angle;
    public ArrayList<Double>           points;

    public Predefini(ArrayList<Domino> base) {
        angle     = new ArrayList<java.lang.Double>();
        points    = new ArrayList<Double>();
        patron    = new ArrayList<Double>();
        this.base = base;
    }

    public ArrayList<Double> calcule(Vector3f pt, double anglee) {
        angle.clear();
        points.clear();
        calculePatron(pt, anglee);

        for (int j = 0; j < base.size(); j++) {
            if (reverse) {
                angle.add(-base.get(j).getAngle() - anglee);
            } else {
                angle.add(base.get(j).getAngle() - anglee);
            }

            points.add(new Double(patron.get(j).x, patron.get(j).y));
        }

        return points;
    }

    public static Double tournerPoint(Double centre, Double p1, double angle) {    // angle entre deux point
        p1.x = p1.x - centre.x;
        p1.y = p1.y - centre.y;

        Double p = new Double(p1.x * Math.cos(angle) - p1.y * Math.sin(angle),    // x
                              p1.x * Math.sin(angle) + p1.y * Math.cos(angle)     // y
                                  );

        p.x = p.x + centre.x;
        p.y = p.y + centre.y;

        return p;
    }

    public static double calculeAngle(Double pb1, Double pb2) {    // angle entre deux point
        double anglee = Math.atan2(pb1.getY() - pb2.getY(), pb1.getX() - pb2.getX()) - Math.atan2(0 - 0, 0 - 10000000);

        return anglee;
    }

    public static double calcPoint(Double p1, Double p2) {    // calcule millieux
        double pv = Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));

        return pv;
    }

    private void calculePatron(Vector3f pt, double angle) {
        patron = new ArrayList<Double>();

        if (reverse) {
            patron.add(new Double(pt.x, pt.z));

            for (int i = 1; i < base.size(); i++) {
                Double d = new Double(pt.x - base.get(i).getPosition().x + base.get(0).getPosition().x,
                                      pt.z + base.get(i).getPosition().y - base.get(0).getPosition().y);

                patron.add(tournerPoint(new Double(pt.x, pt.z), d, angle));
            }
        } else {
            patron.add(new Double(pt.x, pt.z));

            for (int i = 1; i < base.size(); i++) {
                Double d = new Double(pt.x + base.get(i).getPosition().x - base.get(0).getPosition().x,
                                      pt.z + base.get(i).getPosition().y - base.get(0).getPosition().y);

                patron.add(tournerPoint(new Double(pt.x, pt.z), d, angle));
            }
        }
    }

    @Override
    public void OnUpdate(Editor app) {
        rotateAngle = app.dom.getRotateAngle();
        this.calcule(app.pt, rotateAngle);
        app.dom.makePredefini(this, app);
    }

    @Override
    public void OnLeftClic(Editor app) {}

    @Override
    public void OnRightClic(Editor app) {
        rotateAngle = app.dom.getRotateAngle();
        this.calcule(app.pt, rotateAngle);
        app.dom.makePredefini(this, app);
    }

    public void OnEscape(Editor app) {
        app.dom.currenttool = null;
        app.gui.outil       = null;
        app.dom.removeGhost(app);
    }

    public void OnPressR(Editor app) {
        if (reverse) {
            reverse = false;
        } else {
            reverse = true;
        }
    }

    public String getType() {
        return "Predefini";
    }

    public void delete(Editor app) {}

    public Object get(String atribut) {
        return null;
    }

    public void OnPressSpace(Editor app) {
    }

    public String getHelp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
