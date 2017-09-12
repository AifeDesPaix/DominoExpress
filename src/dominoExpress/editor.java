
package dominoExpress;

//~--- non-JDK imports --------------------------------------------------------

import Outil.Predefini;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

//~--- JDK imports ------------------------------------------------------------

import java.awt.Component;
import java.awt.geom.Point2D;

import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Thomas
 */
public class Editor extends SimpleApplication {

    // Initialisation de l'interface(boutons ect..)
    private static Editor      app = new Editor();
    public static EditorGUI    gui = new EditorGUI();
    private static AppSettings cfg = new AppSettings(true);

    // Création de la physique pour les domino et le sol
    // Différents vecteurs utilisés pour les calculs
    public Vector3f  pt        = new Vector3f();

    // Boolean qui permet de savoir si les dominos sont de type fantome ou non
    public boolean ghost = true;
    public Dominos dom   = new Dominos();

    // Sauvegarde de la map
    private Save           save           = new Save();
    // Gestionnaire de physique
    public BulletAppState bulletAppState;

    // Node qui regroupe les objets ou peuvent être placés les dominos
    private Node collidable;

    // Résultat des collisions
    private CollisionResults results;
    public  Component        parent;
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            
            if (name.equals("RightClic") &&!keyPressed) {
                ghost = false;

                if (setPt()) {
                    if (dom.currenttool != null) {
                        dom.currenttool.OnRightClic(app);
                    }
                }

                ghost = true;
            } else if (name.equals("sizeLess") &&!keyPressed) {
                dom.grow(false);
            } else if (name.equals("sizeMore") &&!keyPressed) {
                dom.grow(true);
            } else if (name.equals("spreadMore") &&!keyPressed) {
                dom.setEcartement(dom.getEcartement()*1.1f);
            } else if (name.equals("spreadLess") &&!keyPressed) {
                dom.setEcartement(dom.getEcartement()*0.9f);
            }else if (name.equals("texture") &&!keyPressed) {
                dom.colorchange = false;

                if (dom.currenttool != null) {
                    if (dom.currenttool.getType() == "Selection") {
                        dom.colorchange = true;
                    }
                }

                Dominos.textureChange(assetManager);
            } else if (name.equals("LeftClic") &&!keyPressed) {
                if (dom.currenttool != null) {
                    dom.currenttool.OnLeftClic(app);
                }
            } else if (name.equals("reverse") &&!keyPressed) {
                if (dom.currenttool != null) {
                    dom.currenttool.OnPressR(app);
                }
            }else if (name.equals("space") &&!keyPressed) {
                if (dom.currenttool != null) {
                    dom.currenttool.OnPressSpace(app);
                }
            }
            else if (name.equals("cancel") &&!keyPressed) {
                if (dom.currenttool != null) {
                    dom.currenttool.OnEscape(app);
                }
            }
        }
    };
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("angleLess")) {
                dom.setRotateAngle(dom.getRotateAngle() - 0.03);

                if (dom.getRotateAngle() <= 0) {
                    dom.setRotateAngle(2 * Math.PI);
                }
            } else if (name.equals("anglePlus")) {
                dom.setRotateAngle(dom.getRotateAngle() + 0.03);

                if (dom.getRotateAngle() >= 2 * Math.PI) {
                    dom.setRotateAngle(0);
                }
            } else if (name.equals("molettebas")) {
                dom.grow(false);
            } else if (name.equals("molettehaut")) {
                dom.grow(true);
            }
        }
    };



    public static void main(String[] args) {
        cfg.setTitle("Dominos Express");    // branding: window name
        cfg.setFrameRate(60);
        cfg.setStereo3D(false);
        app.setSettings(cfg);
        app.start();
    }

    // Initialisation globale de l'environnement
    @Override
    public void simpleInitApp() {
        dom.initMaterials(assetManager);

        Init_Scene in1 = new Init_Scene();
   
        in1.initMaterials(assetManager);
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        flyCam.setMoveSpeed(50);
        cam.setLocation(new Vector3f(-1, 25, 0));
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(true);
        flyCam.setZoomSpeed(0);
        gui.initialize(stateManager, app);

        // permet de voire le hitbox :
        
       this.bulletAppState.setDebugEnabled(true);
        // permet de changer la vitesse global de la physique
         this.bulletAppState.setSpeed(1.2f);
         this.bulletAppState.physicsTick(null,1);
        
        inputManager.clearMappings();
        inputManager.setCursorVisible(true);
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        collidable = new Node("Collidable");
        collidable.attachChild(in1.initFloor(bulletAppState));
        rootNode.attachChild(collidable);
        rootNode.addLight(in1.setUpLight2());
        initKeys();
    }

    private void initKeys() {
        flyCam.registerWithInput(inputManager);
        inputManager.clearMappings();
        inputManager.addMapping("anglePlus", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("angleLess", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("RightClic", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("LeftClic", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("molettebas", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("molettehaut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("save", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addMapping("cancel", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("load", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("reverse", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("space", new KeyTrigger(KeyInput.KEY_RCONTROL));

        // touche de la camera :
        inputManager.addMapping("FLYCAM_Forward", new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("FLYCAM_Backward", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("FLYCAM_StrafeLeft", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("FLYCAM_StrafeRight", new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("FLYCAM_Rise", new KeyTrigger(KeyInput.KEY_E), new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("FLYCAM_Lower", new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("print", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("sizeLess", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("sizeMore", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("spreadLess", new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addMapping("spreadMore", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("texture", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "RightClic");
        inputManager.addListener(analogListener, "anglePlus", "angleLess");
        inputManager.addListener(actionListener, "save");
        inputManager.addListener(actionListener, "cancel");
        inputManager.addListener(actionListener, "reverse");
        inputManager.addListener(actionListener, "space");
        inputManager.addListener(actionListener, "load");
        inputManager.addListener(actionListener, "print");
        inputManager.addListener(actionListener, "sizeLess");
        inputManager.addListener(actionListener, "sizeMore");
        inputManager.addListener(actionListener, "spreadLess");
        inputManager.addListener(actionListener, "spreadMore");
        inputManager.addListener(actionListener, "texture");
        inputManager.addListener(actionListener, "LeftClic");
        inputManager.addListener(analogListener, "molettebas");
        inputManager.addListener(analogListener, "molettehaut");
        inputManager.addListener(analogListener, "FLYCAM_Backward");
    }

    @Override
    public void simpleUpdate(float tpf) {
     
        /** Write text on the screen (HUD) */
        hud();
        bulletAppState.setEnabled(gui.getPause());
        ghost = true;

        if (setPt()) {
            if (dom.currenttool != null) {
                dom.currenttool.OnUpdate(app);
            }
        }
    }

    public void quitGame() {
        app.stop();
    }

    // permet de charger une simulation depuis le fichier voulus
    //CHANGEMENT ICIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
    public void load(boolean t, boolean predef) {
        ghost = false;

        if (t) {
            JFileChooser            chooser = new JFileChooser();
            FileNameExtensionFilter filter  = new FileNameExtensionFilter("map", "map");

            chooser.setFileFilter(filter);

            int returnVal = chooser.showOpenDialog(parent);

            if (returnVal == JFileChooser.APPROVE_OPTION) {}
            save.loadFile(chooser.getSelectedFile().getPath(), "");

        } else {
            save.loadFile("save/reset.map", "");
        }

            if(predef) {
                  ArrayList<Domino> domino = new ArrayList<Domino>();

                for (int i = 0; i <save.getPosition().size(); i++) {
                    Dominos.textureChange(assetManager, save.getTexture().get(i));
                    dom.setDominoHeight(save.getTaille().get(i).y);
                    domino.add(new Domino(save.getPosition().get(i),save.getOrientation().get(i), save.start.get(i), this));
                }

                dom.currenttool = new Predefini(domino);
                
            } else {
                 dom.removeAllDomino(this);

                for (int i = 0; i <save.getPosition().size(); i++) {
                    Dominos.textureChange(assetManager,save.getTexture().get(i));
                    dom.setDominoHeight(save.getTaille().get(i).y);
                    dom.makeDomino(new Point2D.Double(save.getPosition().get(i).x,save.getPosition().get(i).y),save.getOrientation().get(i),
                                   save.start.get(i), this);
                }
            }
               ghost = true;
               save  = new Save();
         
    }

    // permet de sauvgarder les dominos dans un fichier
    public void save(boolean t, ArrayList<Domino> list) {
        Save s = new Save();
        
        s.buildSave(app, list);

        if (t) {
            JFileChooser            chooser = new JFileChooser();
            FileNameExtensionFilter filter  = new FileNameExtensionFilter("map", "map");

            chooser.setFileFilter(filter);

            int returnVal = chooser.showSaveDialog(parent);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                if (chooser.getSelectedFile().getPath().contains(".map")) {
                    s.saveInFile(chooser.getSelectedFile().getPath(), "");
                } else {
                    s.saveInFile(chooser.getSelectedFile().getPath(), ".map");
                }
            }
        } else {
            s.saveInFile("save/reset.map", "");
        }
    }

    private boolean setPt() {
             
     
        results   = new CollisionResults();
        Vector3f origin    = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
        Vector3f direction  = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();

        Ray ray = new Ray(origin, direction);

        collidable.collideWith(ray, results);

        if (results.size() > 0) {
            pt = results.getCollision(0).getContactPoint();

            return true;
        }

        return false;
    }

    private void hud() {
        guiNode.detachAllChildren();

        // nombre de dominos
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        BitmapText nbdom = new BitmapText(guiFont, false);

        nbdom.setSize(guiFont.getCharSet().getRenderedSize());
        nbdom.setText("Dominos : " + dom.dominosList.size() + "(" + dom.ghostList.size() + ")");
        nbdom.setLocalTranslation(350, nbdom.getLineHeight(), 0);
        guiNode.attachChild(nbdom);
         // taille
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        BitmapText taille = new BitmapText(guiFont, false);

        taille.setSize(guiFont.getCharSet().getRenderedSize());
        taille.setText("Taille : " + dom.dominoHeight);
        taille.setLocalTranslation(0, nbdom.getLineHeight()+17, 0);
        guiNode.attachChild(taille);
         // ecartement
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        BitmapText ecartement = new BitmapText(guiFont, false);

        ecartement.setSize(guiFont.getCharSet().getRenderedSize());
        ecartement.setText("Ecartement : " + dom.getEcartement());
        ecartement.setLocalTranslation(0, nbdom.getLineHeight()+0, 0);
        guiNode.attachChild(ecartement);
        // outil courant
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        BitmapText outil = new BitmapText(guiFont, false);

        outil.setSize(guiFont.getCharSet().getRenderedSize());

        if (gui.outil != null) {
            outil.setText("Outil: " + gui.outil);
        } else {
            outil.setText("Outil: aucun");
        }

        outil.setLocalTranslation(200, outil.getLineHeight(), 0);
        guiNode.attachChild(outil);
         // description outil 
      /*  guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        BitmapText help = new BitmapText(guiFont, false);

        help.setSize(guiFont.getCharSet().getRenderedSize());
        if (gui.outil != null) {
            help.setText("madafaka" + this.dom.currenttool.getHelp());
        } else {
            help.setText("madafaka");
        }help.setLocalTranslation(350, help.getLineHeight()-300, 0);
        guiNode.attachChild(help);*/
    }
    
    public Node getCollidable() {
        return collidable;
    }
}
