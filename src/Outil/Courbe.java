package Outil;

//~--- non-JDK imports --------------------------------------------------------

import static Outil.Courbe.calcPoint;
import dominoExpress.Editor;

//~--- JDK imports ------------------------------------------------------------

import java.awt.geom.Point2D.Double;

import java.util.ArrayList;

public class Courbe implements Outil {
    public Double p1,p2,p14,p15;

    public double                       ecartement;//ecartement enter chaque dominos
    public ArrayList<java.lang.Double>  angle;//liste des angle de chaque dominos
    public ArrayList<Double>            points;//liste des position de chaque dominos
    public ArrayList<ArrayList<Double>> listp;//list,des list de point des courbe (inception) voir le gif de wikipedia http://fr.wikipedia.org/wiki/Courbe_de_B%C3%A9zier
    public ArrayList<Double>            pointsdep;//liste des  point de depart ceux que place l'utilisateur
    public ArrayList<Double>            previous;//sa sa sauvagrde la courbe de la frame d'avant pour verifier si c pas la mm ou pas 
   

    public Courbe() {//rien de special ici
        angle       = new ArrayList<java.lang.Double>();
        points = new ArrayList<Double>();
        previous = new ArrayList<Double>();
        listp       = new ArrayList<ArrayList<Double>>();
        pointsdep   = new ArrayList<Double>();
    }

    //c'est ici que se passe le gros du boulot pour toi cette methode doit renvoyer la position de chaque dominos 
    
    public ArrayList<Double> calcule() {
        listp  = new ArrayList<ArrayList<Double>>();
         ArrayList<Double> p = new ArrayList<Double>();
        listp.add(pointsdep);

        Double p11 = listp.get(0).get(0);
        Double p22 = listp.get(0).get(1);

        p.add(p11);

        double anglee = Math.atan2(p11.getY() - p22.getY(), p11.getX() - p22.getX()) - Math.atan2(0 - 0, 0 - 10000000);

        angle.add(-anglee + Math.PI / 2);

        for (double i = 0; i < 1; i += 0.0001) {
            listp = new ArrayList<ArrayList<Double>>();
            listp.add(pointsdep);

            for (int j = 0; j < listp.get(0).size() - 2; j++) {
                listp.add(new ArrayList<Double>());

                for (int k = 1; k < listp.get(listp.size() - 2).size(); k++) {
                    listp.get(listp.size()
                              - 1).add(calcPoint(listp.get(listp.size() - 2).get(k - 1),
                                                 listp.get(listp.size() - 2).get(k), i));
                }
            }

            anglee = Math.atan2(listp.get(listp.size() - 1).get(0).y - listp.get(listp.size() - 1).get(1).y,
                                listp.get(listp.size() - 1).get(0).x
                                - listp.get(listp.size() - 1).get(1).x) - Math.atan2(0 - 0, 0 - 10000000);

            Double pr1 = calcPoint(listp.get(listp.size() - 1).get(0), listp.get(listp.size() - 1).get(1), i);

            if (Double.distance(pr1.x, pr1.y, p.get(p.size() - 1).x, p.get(p.size() - 1).y)
                    >= ecartement) {
                angle.add(-anglee + Math.PI / 2);
                p.add(pr1);
            }
        }
        return p;
    }

    
    // sa fait une rotation de p2 de x d"egrée autour de p1
    public static Double calcPoint(Double p1, Double p2, double percent) {
        double x = (p2.getX() - p1.getX()) * percent + p1.getX();
        double y = (p2.getY() - p1.getY()) * percent + p1.getY();
        return new Double(x, y);
    }

    @Override
    public void OnUpdate(Editor app) {//se qui est fait a chaque frame de l'aplication
        ecartement = app.dom.dominoHeight*app.dom.getEcartement();
        app.dom.removeGhost(app);
        points.removeAll(points.subList(points.size()-previous.size(),points.size()));
        angle.removeAll(angle.subList(angle.size()-previous.size(),angle.size()));
        p15=null;
        if(p2==null&& p1!=null){
            p2 = new Double(app.pt.x,app.pt.z);
        }
        else if(p2!=null&& p1!=null){
           // p15 =new Double(2*p2.x-app.pt.x,2*p2.y-app.pt.z); 
             p15 =new Double(app.pt.x,app.pt.z);
        }
        if(p2!=null){
            pointsdep.clear();
            pointsdep.add(p1);
            if(p14!=null)pointsdep.add(p14);
            if(p15!=null)pointsdep.add(p15);
            pointsdep.add(p2);  
            previous = calcule();
            points.addAll(previous);

                  while(points.size() > angle.size()){
                        if(points.size() > angle.size()){
                            angle.add(angle.get(angle.size()-1));
                        } 
                  }
            app.dom.makeCourbe(this, app);
            if(p15==null)p2=null;   
        } 
    }

    @Override
    public void OnLeftClic(Editor app) {//se qui est fait quand on fait un clic gauche
        ecartement = app.dom.dominoHeight;
        if(p1 == null)p1 = new Double(app.pt.x,app.pt.z);
        else if(p2==null)p2 = new Double(app.pt.x,app.pt.z);
        else {//p15 =new Double(2*p2.x-app.pt.x,2*p2.y-app.pt.z);
            p15 =new Double(app.pt.x,app.pt.z);
        }
        if(p15!= null){
            pointsdep.clear();
            pointsdep.add(p1);
            if(p14!=null)pointsdep.add(p14);
            pointsdep.add(p15);
            pointsdep.add(p2);
            p1 = points.get(points.size()-1);
            p14 =new Double(2*p2.x-p15.x,2*p2.y-p15.y);
            p2 = null;
            p15 = null;
            points.removeAll(points.subList(points.size()-previous.size(),points.size()));
            angle.removeAll(angle.subList(angle.size()-previous.size(),angle.size()));
            previous.clear();
            ArrayList<Double> t = calcule();
            t.remove(t.size()-1);
            angle.remove(angle.size()-1);
            points.addAll(t);

            app.dom.removeGhost(app);
  while(points.size() > angle.size()){
                     if(points.size() > angle.size()){
                    angle.add(angle.get(angle.size()-1));
                } 
                  }
            app.dom.makeCourbe(this, app);
        }
    }

    @Override
    public void OnRightClic(Editor app) {//se qui est fait quand on fait un clic droit
    //    pointsdep.add(new Double(app.pt.x, app.pt.z));

        if (points.size() >= 1) {
  while(points.size() > angle.size()){
                     if(points.size() > angle.size()){
                    angle.add(angle.get(angle.size()-1).doubleValue());
                } 
                  }
            app.dom.makeCourbe(this, app);
            app.dom.removeGhost(app);
        }
        points.clear();
        angle.clear();
        p1 = null;
        p2 = null;
        p15 = null;
        p14 = null;
        previous = new ArrayList<Double>();
        pointsdep = new ArrayList<Double>();
    }

    @Override
    public void OnEscape(Editor app) {//se qui est fait quand on fait echape
            app.dom.currenttool = null;
            app.gui.outil       = null;
            app.dom.removeGhost(app);
        
    }

    public void OnPressR(Editor app) {}//se qui est fait quand on appuis sur r (tu a rien a ecrire ici sa concerne pas els courbe

    public String getType() {//sa renvoie le nom de la class en gros tu a rien a toucher ici
        return "Courbe";
    }

    public void delete(Editor app) {}//tu a rien a toucher ici

    public Object get(String atribut) {//tu a rien a toucher ici (c'est une methode experimentale xD)
        return null;
    }

    public void OnPressSpace(Editor app) {
    }

    public String getHelp() {
        return"salut je suis l'aide de la Courbe";
    }
}
