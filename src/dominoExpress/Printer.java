package dominoExpress;

//~--- non-JDK imports --------------------------------------------------------

import com.jme3.math.Vector3f;

//~--- JDK imports ------------------------------------------------------------

import java.awt.*;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import java.util.ArrayList;

/**
 *
 * @author Basile
 */
public class Printer implements Printable, Pageable {
    int                  PAGEMAX    = 2874;
    int                  pageIndexI = 0;
    ArrayList<Rectangle> dominos    = new ArrayList<Rectangle>();
    public ArrayList<Domino>           base;
    Boolean              lol        = false;
    Dominos              environement;

    Printer(Dominos d) {
        environement = d;
        makePrint(environement);
    }

    /*
     *   graphics: Contexte graphique pour le rendu de la page Ã  imprimer.
     *   pageFormat: Informations sur le format de la page Ã  imprimer comme
     * la taille et l'orientation.
     *   pageIndex: Index de la page Ã  imprimer
     *  RETURN : Printable.PAGE_EXISTS si le rendu de la page
     *  s'est bien dÃ©roulÃ©e et Printable.NO_SUCH_PAGE si pageIndex spÃ©cifie
     *   une page non existante.
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D g = (Graphics2D) graphics;
        if (pageIndexI > PAGEMAX - 1) {    // si on est a la derniÃ¨re page on s'arrÃ¨te la.
            return Printable.NO_SUCH_PAGE;
        }

        if (lol) {
            System.out.println("creation de la page : " + (pageIndex + 1) + "/" + PAGEMAX);    // indicateur qui permet de savoir ou en est l'impression

            int width  = (int) pageFormat.getImageableWidth();
            int height = (int) pageFormat.getImageableHeight();

            g.setColor(Color.BLACK);

            int nbDrawDominos = 0;

            switch (pageIndex) {
            default :
                while ((nbDrawDominos == 0) && (pageIndexI <= PAGEMAX)) {
                    nbDrawDominos = 0;

                    for (int i = 0; i < dominos.size(); i++) {    // on parcour la liste de tout les dominos
                        if (pageIndexI == 0) {}
                        else {
                            if (pageIndexI % 54 == 0) {
                                dominos.get(i).translate((width) * 54, -height);
                            }

                            dominos.get(i).translate(-width, 0);
                        }

                        if ((dominos.get(i).centre.x < width) && (dominos.get(i).centre.y < height)
                                && (dominos.get(i).centre.x > 0) && (dominos.get(i).centre.y > 0)) {
                            nbDrawDominos++;
                            dominos.get(i).draw(g);    // on dessine les dominos sur la page
                            // dominos.remove(i);
                        }
                        /*
                         *  if(dominos.get(i).centre.x<width*3f
                         * &&dominos.get(i).centre.x<height*3f
                         * &&dominos.get(i).centre.x>-300
                         * &&dominos.get(i).centre.x>-300){
                         *    dominos.get(i).draw(g);
                         * }
                         */
                    }
                    pageIndexI++;
                }
                break;
            }

            graphics.drawString("Page :" + (char) ('A' + (int) (pageIndexI / 54)) + ((pageIndexI % 54) + 1), width / 2,
                                height / 2);           // ecriture du numero de page sur la page
            lol = false;

            if (pageIndexI+1 >= PAGEMAX) {    // si on est a la derniÃ¨re page on s'arrÃ¨te la.
                return Printable.NO_SUCH_PAGE;
            } else {
                return Printable.PAGE_EXISTS;
            }
        }

        lol = true;

        if (pageIndexI >= PAGEMAX) {    // si on est a la derniÃ¨re page on s'arrÃ¨te la.
            return Printable.NO_SUCH_PAGE;
        } else {
            return Printable.PAGE_EXISTS;
        }
    }

    @Override
    public int getNumberOfPages() {
        return PAGEMAX;
    }

    @Override
    public PageFormat getPageFormat(int arg0) throws IndexOutOfBoundsException {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Printable getPrintable(int arg0) throws IndexOutOfBoundsException {

        // TODO Auto-generated method stub
        return null;
    }

    /*
     *  sert a convertige les dominos(3D) en rectangle (2D)
     * xs position x du dominos
     * ys position Z du dominos qui devient y en 2D
     * angle du dominos sur l'axe Y
     */
    public void imprimme() {
        PrinterJob job = PrinterJob.getPrinterJob();

        job.printDialog();

        // RÃ©cupÃ¨re un PrinterJob
        Printer t = new Printer(environement);

        // DÃ©finit son contenu Ã  imprimer
        job.setPrintable(t);

        // Affiche une boÃ®te de choix d'imprimante
        try {
            job.print();
        } catch (PrinterException ex) {}
    }

    /*
     * convetion des dominos en rectangle
     */
    public final void makePrint(Dominos d) {
        if("Selection".equals(d.currenttool.getType())){
            base = (ArrayList<Domino>) d.currenttool.get("base");
            for (int i = 0; i < base.size(); i++) {
               Point2D.Double position = base.get(i).getPosition();
               double         angle    =base.get(i).getAngle();
               Vector3f       s        = base.get(i).getTaille();

               dominos.add(new Rectangle(2.8 * (400f / 21) * (position.x + 200), 4 * (400f / 29.7) * (position.y + 200),
                                         angle, s));
           }   
        }else {
            for (int i = 0; i < d.dominosList.size(); i++) {
                Point2D.Double position = d.dominosList.get(i).getPosition();
                double         angle    = d.dominosList.get(i).getAngle();
                Vector3f       s        = d.getDominoSize("domino" + i);

                dominos.add(new Rectangle(2.8 * (400f / 21) * (position.x + 200), 4 * (400f / 29.7) * (position.y + 200),
                                          angle, s));
            }
        }
    }
}
