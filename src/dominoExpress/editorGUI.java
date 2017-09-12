package dominoExpress;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 *
 * @author mifth
 */
public class editorGUI extends AbstractAppState implements ScreenController {
    
    private Screen screen;
    private Nifty  nifty;
    private SimpleApplication app;
    private boolean add_line = false, add_curve = false, add_push = false, delete = false, rotate = false;
    private int dominoCount = 0;
    
    public editorGUI() {

    }

   @Override
    public void initialize(AppStateManager stateManager, Application app) {

    super.initialize(stateManager, app);
    this.app=(SimpleApplication)app;
    
      
       
     NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(this.app.getAssetManager(), 
                                        this.app.getInputManager(),
                                        this.app.getAudioRenderer(),
                                        this.app.getGuiViewPort());
      nifty = niftyDisplay.getNifty();
//     nifty.loadStyleFile("nifty-default-styles.xml");
//     nifty.loadControlFile("nifty-default-controls.xml");        
     nifty.fromXml("Interface/editorGUI.xml",  "start", this);


     // attach the nifty display to the gui view port as a processor
     this.app.getGuiViewPort().addProcessor(niftyDisplay);
     this.app.getInputManager().setCursorVisible(true);     
     
     nifty.gotoScreen("start"); // start the screen 
     screen.getFocusHandler().resetFocusElements();
     

     
//    Element niftyElement = nifty.getCurrentScreen().findElementByName("button1");
//    niftyElement.getElementInteraction().getPrimary().setOnClickMethod(new NiftyMethodInvoker(nifty, "printGo()", this));
     
     
    // Set Logger for only warnings     
    Logger root = Logger.getLogger("");
    Handler[] handlers = root.getHandlers();
    for (int i = 0; i < handlers.length; i++) {
        if (handlers[i] instanceof ConsoleHandler) {
        ((ConsoleHandler) handlers[i]).setLevel(Level.WARNING);
      }
     }     
     
    }

    public void printGo() {
        System.out.println("XXXXXX");
        
        this.app.getViewPort().setBackgroundColor(ColorRGBA.randomColor());
        screen.getFocusHandler().resetFocusElements();
//        application.update();
    }   
   

    public void update(float tpf, Application app) {
        this.app=(SimpleApplication)app;
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }    
    
    
    
    
    public void bind(Nifty nifty, Screen screen) {
    this.nifty = nifty;
    this.screen = screen;
    }

    public void onStartScreen() {
        
    }

    public void onEndScreen() {

    }
    
     public void quitGame(){
      app.stop();
  }
     public void addLine(){
         add_line = true;
         add_curve = false;
         add_push = false;
         delete = false;
     }
     
     public void addCurve(){
         add_curve = true;
         add_line = false;
         add_push = false;
         delete = false;
     }
     
     public void addPush() {
         add_curve = false;
         add_line = false;
         add_push = true;
         delete = false;
     }
     
     public void delete(){
         add_curve = false;
         add_line = false;
         add_push = false;
         delete = true;
     }
     
     public void rotate(){
         add_curve = false;
         add_line = false;
         add_push = false;
         delete = false;
         if(rotate)
            rotate = false;
         else
            rotate = true;
         System.out.println(""+rotate);
     }
     public void validCount(){
         TextField txt = screen.findNiftyControl("input", TextField.class);
         if(Integer.parseInt(txt.getText())>0 && Integer.parseInt(txt.getText()) < 500)
                dominoCount = Integer.parseInt(txt.getText());
     }
     
     public boolean getAddLine(){
         return add_line;
     }
     public boolean getAddCurve(){
        return add_curve;
     }
      public boolean getAddPush(){
         return add_push;
     }
      
      public boolean getDelete(){
         return delete;
     }
      
      public boolean getRotate(){
         return rotate;
     }
      
      public int getDominoCount(){
          return dominoCount;
      }
      
      public void setDelete(Boolean delete) {
          this.delete = delete;
      }
    
}
