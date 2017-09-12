package dominoExpress;

//~--- non-JDK imports --------------------------------------------------------

import Outil.Courbe;
import Outil.Delete;
import Outil.Depart;
import Outil.Objet;
import Outil.Selection;
import Outil.Spiral;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 * s
 *
 * @author Thomas
 */
public class EditorGUI extends AbstractAppState implements ScreenController {
    private boolean delete      = false,
                    pause       = false;
    private int     dominoCount = 0,
                    angleCount  = 0;
    private String  etat        = null;
    public int      number      = 0;
    private boolean sizeChanged = true;
    private Screen  screen;
    private Nifty   nifty;
    private Editor  app;
    public String   outil;

    public EditorGUI() {}

    public void initialize(AppStateManager stateManager, Editor app) {
        super.initialize(stateManager, app);
        this.app = app;

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(this.app.getAssetManager(), this.app.getInputManager(),
                                           this.app.getAudioRenderer(), this.app.getGuiViewPort());

        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/editorGUI.xml", "start", this);

        // attach the nifty display to the gui view port as a processor
        this.app.getGuiViewPort().addProcessor(niftyDisplay);
        this.app.getInputManager().setCursorVisible(true);
        nifty.gotoScreen("start");    // start the screen
        screen.getFocusHandler().resetFocusElements();

        // Set Logger for only warnings
        Logger    root     = Logger.getLogger("");
        Handler[] handlers = root.getHandlers();

        for (int i = 0; i < handlers.length; i++) {
            if (handlers[i] instanceof ConsoleHandler) {
                ((ConsoleHandler) handlers[i]).setLevel(Level.WARNING);
            }
        }

        TextField txt2 = screen.findNiftyControl("input_size", TextField.class);

        txt2.setText("0");
    }

    public void printGo() {
        this.app.getViewPort().setBackgroundColor(ColorRGBA.randomColor());
        screen.getFocusHandler().resetFocusElements();
    }

    public void update(float tpf, Editor app) {
        this.app = app;
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    public void bind(Nifty nifty, Screen screen) {
        this.nifty  = nifty;
        this.screen = screen;
    }

    public void onStartScreen() {}

    public void onEndScreen() {}

    public void quitGame() {
        int t = JOptionPane.showConfirmDialog(null, "Sauvegarder avant de quiter?", "Attention !", 1, 1);

        if (t == 0) {
            getApp().save(true, app.dom.dominosList);
            app.stop();
        } else if (t == 1) {
            app.stop();
        }
    }
    
  public void help() {
        int t = JOptionPane.showConfirmDialog(null, "P,M pour Tournée les dominos\n"
                + "A/E pour descendre/monter la caméra\n"
                + "Q/D pour déplacer la caméra ver la gauche/droite\n"
                + "Z/S pour zoomer/dézoomer\n"
                + "ESC pour quitter l'outil en cours d'utilisation\n"
                + "Molette pour augmenter/diminuer la taille des dominos\n"
                + "T changer la couleur des dominos\n"
                + "R inverse la cascade de domino (effet miroir)\n"
                + "", "Aide",JOptionPane.CLOSED_OPTION, 1);
    }


    public void depart() {
        app.dom.removeGhost(app);
        app.dom.currenttool = new Depart();
        outil               = "Depart";
        etat                = "outil";
    }

    public void aToB() {
        app.dom.removeGhost(app);
        app.dom.currenttool = new Courbe();
        outil               = "Courbe";
        etat                = "outil";
    }

    public void predef() {
        app.dom.removeGhost(app);
        getApp().load(true, true);
        outil = "Prédefini";
        etat  = "outil";
    }

    public void selection() {
        app.dom.removeGhost(app);
        app.dom.currenttool = new Selection();
        outil               = "Sélection";
        etat                = "outil";
    }

    public void spiral() {
        app.dom.removeGhost(app);
        app.dom.currenttool = new Spiral();
        outil               = "Spirale";
        etat                = "outil";
    }
     //CHANGEMENT ICIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
    public void objet() {
        app.dom.removeGhost(app);
        app.dom.currenttool = new Objet(app);
        outil               = "Objet";
        etat                = "objet"; 
    }

    public void delete() {
        if (app.dom.currenttool.getType() == "Selection") {
            app.dom.currenttool.delete(app);
        } else {
            app.dom.removeGhost(app);
            app.dom.currenttool = new Delete();
            outil               = "Supression";
            etat                = "outil";
        }
    }

    public void newS() {
        app.dom.removeGhost(app);
        app.dom.removeAllDomino(app);
    }

    public void pause() {
        if (pause) {
            screen.findNiftyControl("stop", Button.class).setTextColor(Color.WHITE);
            pause = false;
        } else {
            pause = true;

            Color c = new Color(1, 0, 0, 1);

            screen.findNiftyControl("stop", Button.class).setTextColor(c);
            getApp().save(false, app.dom.dominosList);
        }
    }

    public void reset() {
        app.dom.reset(app);
    }

    public void print() {
        Printer p = new Printer(getApp().dom);
        
        p.imprimme();
    }

    public void load() {
        getApp().load(true, false);
    }

    public void save() {
        if (app.dom.currenttool.getType() == "Selection") {
            getApp().save(true, (ArrayList<Domino>) app.dom.currenttool.get("base"));
        } else {
            getApp().save(true, app.dom.dominosList);
        }
    }

    public void validCount() {
        TextField txt = screen.findNiftyControl("input_size", TextField.class);

        if (!txt.getDisplayedText().equals("")) {
            if (Integer.parseInt(txt.getDisplayedText().replaceAll("\\D+", "")) < 10001) {
                dominoCount = Integer.parseInt(txt.getDisplayedText().replaceAll("\\D+", ""));
                number      = Integer.parseInt(txt.getDisplayedText().replaceAll("\\D+", ""));
                txt.setText(String.valueOf(number));
            }
        } else {
            txt.setText("0");
            number = 0;
        }
    }

    public void setSizeChanged(boolean bol) {
        sizeChanged = bol;
    }

    public boolean getDelete() {
        return delete;
    }

    public int getDominoCount() {
        return dominoCount;
    }

    public int getAngleCount() {
        return angleCount;
    }

    public int getDeleteNumber() {
        return number;
    }

    public boolean getSizeChanged() {
        return sizeChanged;
    }

    public boolean getPause() {
        return pause;
    }

    public Editor getApp() {
        return app;
    }

    public String getEtat() {
        return etat;
    }
}
