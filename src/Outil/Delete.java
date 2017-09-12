
package Outil;

//~--- non-JDK imports --------------------------------------------------------

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

import dominoExpress.Editor;

import static dominoExpress.Editor.gui;

/**
 *
 * @author Nkplizz
 */
public class Delete implements Outil {
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
                app.dom.removeDomino(app, target);
            }
        }
    }

    @Override
    public void OnUpdate(Editor app) {}

    @Override
    public void OnRightClic(Editor app) {
        gui.validCount();

        if (app.dom.dominosList.size() - gui.getDeleteNumber() >= 0) {
            int t = app.dom.dominosList.size();

            for (int i = app.dom.dominosList.size() - gui.getDeleteNumber(); i < t; i++) {
                app.dom.removeDomino(app, app.dom.dominosList.get(app.dom.dominosList.size() - 1).getName());
            }
        } else {
            int t = app.dom.dominosList.size();

            for (int i = 0; i <= t - 1; i++) {
                app.dom.removeDomino(app, app.dom.dominosList.get(app.dom.dominosList.size() - 1).getName());
            }
        }
    }

    public void OnEscape(Editor app) {
        app.dom.currenttool = null;
        app.gui.outil       = null;
        app.dom.removeGhost(app);
    }

    public void OnPressR(Editor app) {}

    public String getType() {
        return "Delete";
    }

    public void delete(Editor app) {
    }

    public Object get(String atribut) {
        return null;
    }

    public void OnPressSpace(Editor app) {
    }

    public String getHelp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
