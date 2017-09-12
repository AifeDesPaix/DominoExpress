/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dominoExpress;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.input.controls.ActionListener;
import com.jme3.light.AmbientLight;
import com.jme3.material.RenderState;
import com.jme3.math.Ray;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas
 */
public class editor extends SimpleApplication{
    //Gestionnaire de physique
    private BulletAppState bulletAppState;
    //Node qui regroupe les objets ou peuvent être placés les dominos
    private Node collidable;
    //Différents matériaux utilisés
    private Material domino_mat;
    private Material floor_mat;
    private Material wall_mat;
    //Initialisation de l'interface(boutons ect..)
    private static editor app = new editor();
    private static editorGUI gui = new editorGUI();
    //Création de la physique pour les domino et le sol
    private RigidBodyControl    domino_phy;
    private static final Box    domino;
    private RigidBodyControl    floor_phy;
    private static final Box    floor;
    //Différents vecteurs utilisés pour les calculs
    private Vector3f pt = new Vector3f();
    private Vector3f origin = new Vector3f();
    private Vector3f direction = new Vector3f();
    //Variables qui permettent de donner un nom à chaque domino
    private int dominoNumber = 0, ghostNumber = 0;
    //Taille du domino, pour la modifié facilement plus tard
    private static final float dominoLength = 0.5f;
    private static final float dominoWidth  = 1;
    private static final float dominoHeight = 0.1f;
    //Résultat des collisions
    private CollisionResults results;
    //boolean qui permet de savoir si les dominos sont de type fantome ou non
    private boolean ghost = true;
       
    static {
    //On donne la taille voulue au dominos et au sol, avec la répétition de texture désirée
    domino = new Box(dominoLength, dominoWidth, dominoHeight);
    domino.scaleTextureCoordinates(new Vector2f(1f, .5f));
    floor = new Box(200f, 0.01f, 200f);
    floor.scaleTextureCoordinates(new Vector2f(100, 100));
    }
    
    public static void main(String[] args) {
        //lancement du jeu
        app.start();
    }
    //Initialisation globale de l'environnement
    @Override
    public void simpleInitApp() {
        //la physique
        bulletAppState = new BulletAppState();
        //On l'atache au gestionnaire d'état
        stateManager.attach(bulletAppState);
        //Vitesse+placement caméra
        flyCam.setMoveSpeed(50);
        cam.setLocation(new Vector3f(-1,4,0));
        //on initialise la GUI
        gui.initialize(stateManager, app);
        //Permet d'avoir la souri de visible et de cliquer pour rotate
        flyCam.setEnabled(true);
        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);
        //Couleur de fond
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        //initialisation du node
        collidable = new Node("Collidable");
        //initialisation de la lumière des matérieux et des inputs
        setUpLight();
        initMaterials();
        initKeys();
        //On attache le sol au node collidable(sensible a la pose de domino)
        collidable.attachChild(initFloor());
        //Et on l'attache a rootNode ( celui auquel tout est rattaché )
        rootNode.attachChild(collidable);
    }
    //Initialisation des matériaux, la flemme de commenter
    public void initMaterials(){
        floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/FloorTextures/floor_2.png");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(Texture.WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
        
        wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key1 = new TextureKey("Textures/FloorTextures/floor_1.png");
        key3.setGenerateMips(true);
        Texture tex1 = assetManager.loadTexture(key1);
        tex1.setWrap(Texture.WrapMode.Repeat);
        wall_mat.setTexture("ColorMap", tex1);
        
        domino_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/DominoTextures/domino_1.png");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        domino_mat.setTexture("ColorMap", tex2);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //NOUVEAU si différent de 60fps, on fait rien ( réduit le LAG )
        if(System.currentTimeMillis()%(1000/60)!=0)return;
        results = new CollisionResults();
        //On prend les coordonnées de la souris et on les projette celon un vecteur normal
        origin    = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
        direction = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        //On test la collision de ce vecteur avec un collidable
        Ray ray = new Ray(origin, direction);
        collidable.collideWith(ray, results);
        // si c'est rentré en collision, on ajoute tel ou tel type d'enchainement de domino en fonction des boutons
        //Tout le reste c'est juste pour les ghost
        if(results.size() > 0){
             pt = results.getCollision(0).getContactPoint();
             if(gui.getAddLine()) {
                 makeLine(gui.getDominoCount());
                 for(int i = 0 ; i < ghostNumber-gui.getDominoCount() ; i ++){
                 if(rootNode.getChild("dominoG"+i)!=null){
                      removeDomino("dominoG" + i);
                 }     
             }
                }
             if(gui.getAddCurve()){
                  makeCurve(gui.getDominoCount());
                  for(int i = 0 ; i < ghostNumber-gui.getDominoCount()+1 ; i ++){
                        if(rootNode.getChild("dominoG"+i)!=null){
                        removeDomino("dominoG" + i);
                 }     
             }
                }
             if(gui.getAddPush()) {
                 startDomino();
                     for(int i = 0 ; i < ghostNumber ; i ++){
                        if(rootNode.getChild("dominoG"+i)!=null){
                        removeDomino("dominoG" + i);
                     }     
                 }
             }
             if(gui.getDelete()) {
                 for( int i = 0 ; i <= dominoNumber  ; i ++ ){
                   if(rootNode.getChild("domino"+i)!=null)
                    rootNode.detachChildNamed("domino"+i);
                 }
                 gui.setDelete(false);
             }
        }
    }
    //Permet de créer une ligne.
    public void makeLine(int dominoNumber) {
      Quaternion rotate = new Quaternion(); 
      Vector3f vt;
      rotate.fromAngleAxis( 0, new Vector3f(0,0,0) ); 
      for (int j = 0; j <dominoNumber; j++) {
          if(gui.getRotate()){
              rotate.fromAngleAxis(FastMath.PI/2, new Vector3f(0,1,0));
              //on ce sert de pt ( résultat de la collision = > vecteur indiquant les coordonnée du point de collision ) pour posisitionner les dominos.
              vt = new Vector3f(j*dominoLength*1.05f+pt.x, dominoHeight+0.85f , dominoWidth*1.05f+pt.z);
          }else{
              vt = new Vector3f(dominoLength+pt.x, dominoHeight+0.85f , j*dominoWidth+pt.z);
          }
          makeDomino(vt, j, rotate);
        }
      }
    
    public void makeCurve(int dominoNumber) {
        Quaternion rotate = new Quaternion(); 
        float angleIterator=0;
        for (int j = 0; j < dominoNumber; j++) {
          
          rotate.fromAngleAxis(FastMath.PI/(dominoNumber+1-j), new Vector3f(0, 1, 0));
          angleIterator +=0.2*j;
          Vector3f vt = new Vector3f(dominoLength+angleIterator+pt.x, dominoHeight+0.85f , j*dominoWidth*1.05f+pt.z);
          makeDomino(vt, j, rotate);
        }
    }
    //création du domino
    public void makeDomino(Vector3f loc, int j, Quaternion rotate) {
      Geometry domino_geo;
      if(ghost){
          ghostNumber++;
          domino_geo = new Geometry("dominoG"+ghostNumber, domino);
      }
        
        else{
           dominoNumber++;
           domino_geo = new Geometry("domino"+dominoNumber, domino);
      }

      domino_geo.setMaterial(domino_mat); 
      domino_geo.setLocalRotation(rotate);
      rootNode.attachChild(domino_geo);
      domino_geo.setLocalTranslation(loc);
      if(!ghost){
        domino_mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        domino_phy = new RigidBodyControl(3f);
        domino_geo.addControl(domino_phy);
        bulletAppState.getPhysicsSpace().add(domino_phy);
      }
    }
    //création du sol
    public Geometry initFloor() {
       Geometry floor_geo = new Geometry("Floor1", floor);
       floor_geo.setMaterial(floor_mat); 
       floor_geo.setLocalTranslation(0, -0.1f, 0);
       floor_phy = new RigidBodyControl(0.0f);
       floor_geo.addControl(floor_phy);
       bulletAppState.getPhysicsSpace().add(floor_phy);
       return floor_geo;
    }
    //création de la lumière
    private void setUpLight() {
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1.3f));
    rootNode.addLight(al);
  }
    
    private void initKeys() {
    inputManager.addMapping("Menu",  new KeyTrigger(KeyInput.KEY_M));
    inputManager.addMapping("action",
      new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); 
    inputManager.addListener(actionListener,"Menu","action");
  }
 
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
        
      if (name.equals("action") && !keyPressed) {
        ghost = false;
        //meme principe que pour les ghost
        CollisionResults results = new CollisionResults(); 
        Vector3f origin    = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.0f);
        Vector3f direction = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();

        Ray ray = new Ray(origin, direction);
        collidable.collideWith(ray, results);
        if(results.size() > 0){
            //pt est le vecteur resultat de la collision ( le point de collision ).
             pt = results.getCollision(0).getContactPoint();
             //String hit = results.getCollision(0).getGeometry().getName();
             if(gui.getAddLine()) {
                    makeLine(gui.getDominoCount());
                }
             if(gui.getAddCurve()){
                    makeCurve(gui.getDominoCount());
                }
             if(gui.getAddPush()) {
                    startDomino();
             }
             /*if(gui.getDelete()) {
                    rootNode.collideWith(ray, results);
                    hit = results.getCollision(0).getGeometry().getName();
                    removeDomino(hit);
             }*/
        }
        ghost = true;
      } 
    }
  };
  
  public void startDomino(){
      Geometry domino_geo;
       if(ghost){
          ghostNumber++;
          domino_geo = new Geometry("dominoG"+ghostNumber, domino);
      }
        else{
           dominoNumber++;
           domino_geo = new Geometry("domino"+dominoNumber, domino);
      }
      Quaternion rotate = new Quaternion();
      domino_geo.setMaterial(domino_mat); 
      rotate.fromAngleAxis(FastMath.PI/4, new Vector3f(1, 0, 0));
      domino_geo.setLocalRotation(rotate);
      rootNode.attachChild(domino_geo);
      domino_geo.setLocalTranslation(new Vector3f(pt.x, pt.y+1, pt.z));
      if(!ghost){
        domino_phy = new RigidBodyControl(3f);
        domino_geo.addControl(domino_phy);
        bulletAppState.getPhysicsSpace().add(domino_phy);
        domino_phy.setGravity(new Vector3f(0,-10,0));
      }
  }
  
  public void removeDomino(String yougonnadie){
      if(yougonnadie!="Floor")
        rootNode.detachChild(rootNode.getChild(yougonnadie));
  }
  
  public void quitGame(){
      app.stop();
  }
  
  public void testStyle(){
      
  }
}
          