
package dominoExpress;

//~--- non-JDK imports --------------------------------------------------------

import Outil.*;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;

//~--- JDK imports ------------------------------------------------------------

import java.awt.geom.Point2D;

import java.util.ArrayList;

/**
 *
 * @author Thomas
 */
public class Dominos {
    public static float      y        = 0.89999996f;
    private static String    unshaded = "Common/MatDefs/Misc/Unshaded.j3md";
    public static Material   domino_mat;
    public static String     domino_text;
    public ArrayList<Domino> dominosList  = new ArrayList<Domino>();
    public ArrayList<Domino> ghostList    = new ArrayList<Domino>();
    public float             dominoHeight = 1f;
    public float             dominoLength = 0.4857f * dominoHeight;
    public float             dominoWidth  = 0.185714f * dominoHeight;
    private float             coefEcartement = 1f;
    public boolean           colorchange  = false;
    public Outil             currenttool;
    private double           rotateAngle;

    // Initialisation des matériaux, la flemme de commenter
    public void initMaterials(AssetManager assetManager) {
        domino_text = "Textures/DominoTextures/domino_" + (int) (Math.random() * (6)) + ".png";
        domino_mat  = new Material(assetManager, unshaded);

        TextureKey key2 = new TextureKey(domino_text);

        key2.setGenerateMips(true);

        Texture tex2 = assetManager.loadTexture(key2);

        domino_mat.setTexture("ColorMap", tex2);

//      domino_text= "Textures/DominoTextures/domino_"+(int)(Math.random()*(6))+".png";
//      domino_mat = new Material(assetManager,  "Common/MatDefs/Misc/Unshaded.j3md");
//      domino_mat.setColor("Color",new ColorRGBA(0,0.5f,0.7f,1)); 
//      TextureKey key3 = new TextureKey("Textures/Selection/dominos.png");
//      key3.setGenerateMips(true);
//      //domino_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//      Texture tex2 = assetManager.loadTexture(key3);
//      domino_mat.setTexture("ColorMap", tex2);
//      domino_mat.setTexture("ColorMap", tex2);
    }

    // création du domino
    public void makeDomino(Point2D.Double position, java.lang.Double angle, boolean start, Editor app) {
        Domino tmp = new Domino(position, angle, start, app);

        if (app.ghost) {
            ghostList.add(tmp);
        } else {
            dominosList.add(tmp);
        }

        tmp.attach(app);
    }

    public void grow(boolean up) {
        if (up && (dominoHeight <= 10f)) {
            dominoLength += (1f / 5f) * 0.4857f;
            dominoWidth  += (1f / 5f) * 0.185714f;
            dominoHeight += (1f / 5f) * 1f;
            coefEcartement = 1;
        } else if (dominoHeight - (1f / 5f) * 1 >= 0.2f) {
            dominoLength -= (1f / 5f) * 0.4857f;
            dominoWidth  -= (1f / 5f) * 0.185714f;
            dominoHeight -= (1f / 5f) * 1f;
            coefEcartement = 1;
        }

        System.out.println("taille = " + dominoHeight);
    }

    public static Material textureChange(AssetManager assetManager) {
        domino_text = "Textures/DominoTextures/domino_" + (int) (Math.random() * (6)) + ".png";
        domino_mat  = new Material(assetManager, unshaded);

        TextureKey key2 = new TextureKey(domino_text);

        key2.setGenerateMips(true);

        Texture tex2 = assetManager.loadTexture(key2);

        domino_mat.setTexture("ColorMap", tex2);

        return domino_mat;
    }

    public static Material textureChange(AssetManager assetManager, String dest) {
        domino_text = dest;
        domino_mat  = new Material(assetManager, unshaded);

        TextureKey key2 = new TextureKey(domino_text);

        key2.setGenerateMips(true);

        Texture tex2 = assetManager.loadTexture(key2);

        domino_mat.setTexture("ColorMap", tex2);

        return domino_mat;
    }

    public float getDominoHeight() {
        return dominoWidth;
    }

    public void setDominoHeight(float dominoHeight) {
        this.dominoHeight = dominoHeight;
        dominoLength      = 0.4857f * dominoHeight;
        dominoWidth       = 0.185714f * dominoHeight;
        coefEcartement = 1;
    }

    //////////////////////
    //////////////////////
    //////////////////////
    //////////////////////

    public void makeLine(int dominoNumber, boolean start, Point2D.Double point, Editor app) {
        double angle1, angle2;

        angle1 = point.x;
        angle2 = point.y;

        for (int j = 0; j < dominoNumber; j++) {
            angle1 = angle1 + Math.cos(-rotateAngle + Math.PI / 2) * -dominoLength;
            angle2 = angle2 + Math.sin(-rotateAngle + Math.PI / 2) * -dominoLength;

            Point2D.Double p = new Point2D.Double(angle1, angle2);

            makeDomino(p, rotateAngle + Math.PI, start, app);
        }
    }

    public void makeStart(Editor app) {
        double angle1, angle2;

        angle1 = app.pt.x;
        angle2 = app.pt.z;
        angle1 = angle1 + Math.cos(-rotateAngle + Math.PI / 2) * -dominoLength;
        angle2 = angle2 + Math.sin(-rotateAngle + Math.PI / 2) * -dominoLength;

        Point2D.Double p = new Point2D.Double(angle1, angle2);

        removeGhost(app);
        makeDomino(p, rotateAngle + Math.PI, true, app);
    }

    public void makeCourbe(Courbe c, Editor app) {
         for (int i = 0; i < c.points.size(); i++) {
           
            makeDomino(c.points.get(i), c.angle.get(i), false, app);
        }
    }

    public void makeSpiral(Spiral s, Editor app) {

            removeGhost(app);

            for (int i = 0; i < s.points.size(); i++) {
                makeDomino(s.points.get(i), s.angle.get(i), false, app);
            }  
    }
    
    public void makePredefini(Predefini p, Editor app) {
        removeGhost(app);

        for (int i = 0; i < p.points.size(); i++) {
            textureChange(app.getAssetManager(), p.base.get(i).getTexture());
            setDominoHeight(p.base.get(i).getTaille().y);
            makeDomino(p.points.get(i), p.angle.get(i), p.base.get(i).isStart(), app);
        }
    }
 

    public void makeSelection(Selection p, Editor app) {
        removeGhost(app);

        if (p.point.size() == 1) {
            p.base.get(0).setTaille(app.dom.dominoHeight);
        }

        for (int i = 0; i < p.point.size(); i++) {
            if (colorchange) {
                p.base.get(i).setTexture();
            }
            System.out.println(p.move);
            if(p.move){
                p.base.get(i).move(p.point.get(i), p.angle.get(i), app);
            }
        }

        colorchange = false;
    }

    public double getRotateAngle() {
        return rotateAngle;
    }

    public void setRotateAngle(double rotateAngle) {
        this.rotateAngle = rotateAngle;
    }

    public void removeGhost(Editor app) {
        if (ghostList.size() >= 1) {
            for (int i = 0; i < ghostList.size(); i++) {
                app.getRootNode().detachChild(ghostList.get(i));
            }

            ghostList.clear();
        }
    }

    public void removeDomino(Editor app, String nom) {
        if (dominosList.size() >= 1) {
            int t = dominosList.size();

            for (int i = 0; i < t; i++) {
                if (dominosList.get(i).getName().equals(nom)) {
                    app.bulletAppState.getPhysicsSpace().remove(app.getRootNode().getChild(nom));
                    app.getRootNode().detachChild(app.getRootNode().getChild(nom));
                    dominosList.remove(i);

                    return;
                }
            }
        }
    }

    void removeAllDomino(Editor app) {
        for (int i = 0; i < dominosList.size(); i++) {
            app.bulletAppState.getPhysicsSpace().remove(app.getRootNode().getChild(dominosList.get(i).getName()));
            app.getRootNode().detachChildNamed(dominosList.get(i).getName());
        }

        dominosList.clear();
    }

    Vector3f getDominoSize(String nom) {
        for (int i = 0; i < dominosList.size(); i++) {
            if (dominosList.get(i).getName().equals(nom)) {
                return dominosList.get(i).getTaille();
            }
        }

        return null;
    }

    void reset(Editor app) {
        removeGhost(app);

        for (int i = 0; i < dominosList.size(); i++) {
            dominosList.get(i).reset(app);
        }
    }
    
    public void setEcartement(float e){
        if(e<1.5f*dominoHeight && e>dominoLength*1.5f)
        coefEcartement = e;
    }
    
    public float getEcartement(){
        return coefEcartement;
    }
}
