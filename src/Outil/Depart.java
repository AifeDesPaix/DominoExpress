
package Outil;

//~--- non-JDK imports --------------------------------------------------------

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import dominoExpress.Editor;

/**
 *
 * @author Nkplizz
 */
public class Depart implements Outil {
    @Override
    public void OnUpdate(Editor app) {
    }

    @Override
    public void OnLeftClic(Editor app) {
            CollisionResults resultss   = new CollisionResults();
        Vector3f         originn    = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(),
                                          0.0f);
        Vector3f         directionn = app.getCamera().getWorldCoordinates(app.getInputManager().getCursorPosition(),
                                          0.3f);

        directionn.subtractLocal(originn).normalizeLocal();

        Ray ray = new Ray(originn, directionn);

        app.getRootNode().collideWith(ray, resultss);

        if (resultss.size() > 0) {
            String target = resultss.getCollision(0).getGeometry().getName();

            if (!"Floor1".equals(target) && (app.getRootNode().getChild(target) != null)) {
                 if (app.dom.dominosList.size() >= 1) {

                    for (int i = 0; i < app.dom.dominosList.size(); i++) {
                        
                        if (app.dom.dominosList.get(i).getName().equals(target)) {
                            
                            if(!app.dom.dominosList.get(i).isStart()){
                                app.dom.dominosList.get(i).setStart(true);
                                app.dom.dominosList.get(i).reset(app);
                            }else{
                                app.dom.dominosList.get(i).setStart(false); 
                                app.dom.dominosList.get(i).reset(app);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void OnRightClic(Editor app) {     
    }

    public void OnEscape(Editor app) {
        app.dom.currenttool = null;
        app.gui.outil       = null;
        app.dom.removeGhost(app);
    }

    public void OnPressR(Editor app) {}

    public String getType() {
        return "Depart";
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
