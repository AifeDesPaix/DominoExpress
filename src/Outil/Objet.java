/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Outil;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;
import dominoExpress.Editor;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Syron
 */
public class Objet implements Outil{

    private Spatial objet;
    
    public Objet(Editor app){
                    JFileChooser            chooser = new JFileChooser();
            FileNameExtensionFilter filter  = new FileNameExtensionFilter("obj", "obj");

            chooser.setFileFilter(filter);

            int returnVal = chooser.showOpenDialog(app.parent);

            if (returnVal == JFileChooser.APPROVE_OPTION) {}

        //l'url est correcte mais ça veux pas charger
        objet =  app.getAssetManager().loadModel(chooser.getSelectedFile().getName());
        System.out.println(chooser.getSelectedFile().getName());
           
    }
    
    public void OnLeftClic(Editor app) {
        if(objet != null)
        makeObjet(app, objet);
    }

    public void OnUpdate(Editor app) {
        
    }

    public void OnRightClic(Editor app) {
        
    }

    public void OnEscape(Editor app) {
        
    }

    public void OnPressR(Editor app) {
       
    }

    public String getType() {
        return null;
    }

    public void delete(Editor app) {
        
    }

    public Spatial get(String atribut) {
        return objet;
    }
    
       public void makeObjet(Editor app, Spatial objet) {
        objet.scale(10f, 10f, 10f);
        objet.rotate(0.0f, 0.0f, 0.0f);
        objet.setLocalTranslation(app.pt);
        RigidBodyControl domino_phy = new RigidBodyControl(3f);
            objet.addControl(domino_phy);
            app.bulletAppState.getPhysicsSpace().add(domino_phy);
        app.getCollidable().attachChild(objet);
    }

    public void OnPressSpace(Editor app) {
    }

    public String getHelp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
