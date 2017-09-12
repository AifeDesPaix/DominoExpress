
package Outil;

//~--- non-JDK imports --------------------------------------------------------

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import dominoExpress.Domino;
import dominoExpress.Editor;

import static Outil.Predefini.tournerPoint;

//~--- JDK imports ------------------------------------------------------------

import java.awt.geom.Point2D;

import java.util.ArrayList;

/**
 *
 * @author NKplizz
 */
public class Selection implements Outil {
    public Point2D.Double[] points = { new Point2D.Double(0, 0), new Point2D.Double(0, 0), new Point2D.Double(0, 0),
                                       new Point2D.Double(0, 0) };
    public double                      rotateAngle = 0;
    private boolean                    reverse     = false;
    public  boolean                    move        = false;
    public Box                         boite;
    private Geometry                   cube2Geo;
    public ArrayList<Domino>           base;
    public ArrayList<Point2D.Double>   patron;
    public ArrayList<java.lang.Double> angle;
    public ArrayList<Point2D.Double>   point;
    public ArrayList<java.lang.Double> anglebase;
    public ArrayList<Point2D.Double>   pointbase;

    public Selection() {
        angle     = new ArrayList<java.lang.Double>();
        point     = new ArrayList<Point2D.Double>();
        anglebase = new ArrayList<java.lang.Double>();
        pointbase = new ArrayList<Point2D.Double>();
        patron    = new ArrayList<Point2D.Double>();
    }

    public ArrayList<Point2D.Double> calcule(Vector3f pt, double anglee) {
        angle.clear();
        point.clear();
        calculePatron(pt, anglee);

        for (int j = 0; j < base.size(); j++) {
            if (reverse) {
                angle.add(-anglebase.get(j) - anglee);
            } else {
                angle.add(anglebase.get(j) - anglee);
            }

            point.add(new Point2D.Double(patron.get(j).x, patron.get(j).y));
        }

        return point;
    }

    public static Point2D.Double tournerPoint(Point2D.Double centre, Point2D.Double p1, double angle) {    // angle entre deux point
        p1.x = p1.x - centre.x;
        p1.y = p1.y - centre.y;

        Point2D.Double p = new Point2D.Double(p1.x * Math.cos(angle) - p1.y * Math.sin(angle),    // x
            p1.x * Math.sin(angle) + p1.y * Math.cos(angle)                                       // y
                );

        p.x = p.x + centre.x;
        p.y = p.y + centre.y;

        return p;
    }

    private void calculePatron(Vector3f pt, double angle) {
        patron = new ArrayList<Point2D.Double>();

        if (reverse) {
            patron.add(new Point2D.Double(pt.x, pt.z));

            for (int i = 1; i < base.size(); i++) {
                Point2D.Double d = new Point2D.Double(pt.x - pointbase.get(i).x + pointbase.get(0).x,
                                       pt.z + pointbase.get(i).y - pointbase.get(0).y);

                patron.add(tournerPoint(new Point2D.Double(pt.x, pt.z), d, angle));
            }
        } else {
            patron.add(new Point2D.Double(pt.x, pt.z));

            for (int i = 1; i < base.size(); i++) {
                Point2D.Double d = new Point2D.Double(pt.x + pointbase.get(i).x - pointbase.get(0).x,
                                       pt.z + pointbase.get(i).y - pointbase.get(0).y);

                patron.add(tournerPoint(new Point2D.Double(pt.x, pt.z), d, angle));
            }
        }
    }

    @Override
    public void OnUpdate(Editor app) {
        if (base != null ) {
            rotateAngle = app.dom.getRotateAngle();
            this.calcule(app.pt, rotateAngle);
            app.dom.makeSelection(this, app);
        }

        if (cube2Geo != null) {
            app.getRootNode().detachChild(cube2Geo);
        }

        if (!points[0].equals(new Point2D.Double(0, 0)) && points[1].equals(new Point2D.Double(0, 0))) {

            // creation de la boite de selection bleu
            points[1] = new Point2D.Double(app.pt.x, app.pt.z);
            points[2] = new Point2D.Double(points[0].x, points[1].y);
            points[3] = new Point2D.Double((points[0].x + points[1].x) / 2, (points[0].y + points[1].y) / 2);

            Box boxMesh = new Box(new Vector3f((float) points[3].x, 0, (float) points[3].y),
                                  (float) points[1].distance(points[2]) / 2, (float) 20,
                                  (float) points[0].distance(points[2]) / 2);

            cube2Geo = new Geometry("window frame", boxMesh);

            Material cube2Mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");

            cube2Mat.setTexture("ColorMap", app.getAssetManager().loadTexture("Textures/Selection/transparence.png"));
            cube2Mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            cube2Geo.setQueueBucket(Bucket.Transparent);
            cube2Geo.setMaterial(cube2Mat);
            app.getRootNode().attachChild(cube2Geo);
            points[1] = new Point2D.Double(0, 0);
            points[2] = new Point2D.Double(0, 0);
            points[3] = new Point2D.Double(0, 0);
        }
    }

    @Override
    public void OnLeftClic(Editor app) {
        if (points[0].equals(new Point2D.Double(0, 0))) {
            points[0] = new Point2D.Double(app.pt.x, app.pt.z);
        } else if (points[1].equals(new Point2D.Double(0, 0))) {
            points[1] = new Point2D.Double(app.pt.x, app.pt.z);
            points[2] = new Point2D.Double(points[0].x, points[1].y);
            points[3] = new Point2D.Double((points[0].x + points[1].x) / 2, (points[0].y + points[1].y) / 2);
            boite     = new Box(new Vector3f((float) points[3].x, 0, (float) points[3].y),
                                (float) points[1].distance(points[2]) / 2, (float) 20,
                                (float) points[0].distance(points[2]) / 2);
            app.dom.setRotateAngle(0);
            this.setDominos(app);
        }
    }

    @Override
    public void OnRightClic(Editor app) {
        if (base != null) {
            rotateAngle = app.dom.getRotateAngle();
            this.calcule(app.pt, rotateAngle);
            app.dom.makeSelection(this, app);
        }

        points[0]           = new Point2D.Double(0, 0);
        points[1]           = new Point2D.Double(0, 0);
        points[2]           = new Point2D.Double(0, 0);
        base                = null;
        app.dom.colorchange = false;
    }

    public void OnEscape(Editor app) {
        app.dom.currenttool = null;
        app.gui.outil       = null;
        app.dom.removeGhost(app);
    }

    private void setDominos(Editor app) {
        anglebase = new ArrayList<java.lang.Double>();
        pointbase = new ArrayList<Point2D.Double>();
        base      = new ArrayList<Domino>();

        for (int i = 0; i < app.dom.dominosList.size(); i++) {
            if (boite.getBound().intersects(app.dom.dominosList.get(i).getLocalTranslation())) {
                base.add(app.dom.dominosList.get(i));
                pointbase.add(app.dom.dominosList.get(i).getPosition());
                anglebase.add(app.dom.dominosList.get(i).getAngle());
            }
        }
    }

    public void OnPressR(Editor app) {
        if (reverse) {
            reverse = false;
        } else {
            reverse = true;
        }
    }

    public String getType() {
        return "Selection";
    }

    public void delete(Editor app) {
        for (int i = 0; i < base.size(); i++) {
            app.dom.removeDomino(app, base.get(i).getName());
        }

        base = null;
    }

    public Object get(String atribut) {
        if (atribut == "base") {
            return base;
        }
        return null;
    }

    public void OnPressSpace(Editor app) {
        if(move){
            move = false;
        }else{
            move = true;
        }
    }

    public String getHelp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
