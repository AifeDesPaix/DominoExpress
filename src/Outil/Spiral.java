
package Outil;

//~--- non-JDK imports --------------------------------------------------------

import static Outil.Spiral.calcPoint;

import dominoExpress.Editor;

//~--- JDK imports ------------------------------------------------------------

import java.awt.geom.Point2D.Double;

import java.util.ArrayList;

public class Spiral implements Outil {
    private boolean                    reverse = false;
    public double                      ecartement;
    public double                      rotateAngle;
    public ArrayList<java.lang.Double> angle;
    public ArrayList<Double>           points;
    public Double                      centre=null;
    public Double                      fin=null;

    public Spiral() {
        angle  = new ArrayList<java.lang.Double>();
        points = new ArrayList<Double>();
    }

    public ArrayList<Double> calcule() {
        angle  = new ArrayList<java.lang.Double>();
        points = new ArrayList<Double>();

        Double t = centre;
        double i = 0;
        if(!reverse){
         t      = calcPoint(centre, i/100+ecartement+ecartement*ecartement,Math.toRadians(i)+rotateAngle);
        }else{
         t      = calcPoint(centre, i/100+ecartement+ecartement*ecartement,Math.toRadians(i)-rotateAngle);
        }
         angle.add(-calculeAngle(t, centre));
                points.add(t);
        while(centre.distance(t)<centre.distance(fin)){
            i++;
            double a = Math.toRadians(i);
            if(reverse){
                 t      = calcPoint(centre,i/100+ecartement+ecartement*ecartement,-a-rotateAngle);
            }else{
                 t      = calcPoint(centre,i/100+ecartement+ecartement*ecartement,a+rotateAngle);
            }
           
             if(points.get(points.size()-1).distance(t)>ecartement){
                angle.add(-calculeAngle(t, centre));
                points.add(t);    
             }
               
        }
        points.remove(0);

        return points;
    }

    public static double calculeAngle(Double pb1, Double pb2) {
        double anglee = Math.atan2(pb1.getY() - pb2.getY(), pb1.getX() - pb2.getX()) - Math.atan2(0 - 0, 0 - 10000000);

        return anglee;
    }

    public static Double calcPoint(Double p1,double d,double angle) {
        return new Double (p1.x+d*Math.cos(angle),p1.y+d*Math.sin(angle));
    }

    public void OnLeftClic(Editor app) {
        if(centre == null){
            centre = new Double(app.pt.x, app.pt.z);
        }else{
            fin = new Double(app.pt.x, app.pt.z); 
        }
    }

    public void OnUpdate(Editor app) {
        app.dom.removeGhost(app);
        rotateAngle = app.dom.getRotateAngle();
        ecartement = app.dom.getEcartement();
        if(centre == null){
            //centre = new Double(app.pt.x, app.pt.z);
        }else{
            fin = new Double(app.pt.x, app.pt.z); 
        }
        if(centre != null  ){
            this.calcule();
            app.dom.makeSpiral(this, app);
        }
    }

    public void OnRightClic(Editor app) {
        if(centre != null){
            this.calcule();
            app.dom.makeSpiral(this, app);
            centre = null;
            fin = null;
        }
    }

    public void OnEscape(Editor app) {
        if(centre == null){
            app.dom.currenttool = null;
            app.gui.outil       = null;
            app.dom.removeGhost(app);
        }else{
            centre = null;
            fin = null;
            app.dom.removeGhost(app);
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
        return "Spiral";
    }

    public void delete(Editor app) {
    }

    public Object get(String atribut) {
        return null;
    }

    public void OnPressSpace(Editor app) {
    }

    public String getHelp() {
        return null;
    }
}
