
package dominoExpress;

//~--- non-JDK imports --------------------------------------------------------

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 *
 * @author Thomas
 */
public class Init_Scene {
    private static final Box floor;

    static {
        floor = new Box(200f, 1f, 200f);
        floor.scaleTextureCoordinates(new Vector2f(100, 100));
    }

    private String           unshaded          = "Common/MatDefs/Misc/Unshaded.j3md";
    private String           scene1_floor_text = "Textures/FloorTextures/floor_2.png";
    private RigidBodyControl floor_phy;
    private Material         floor_mat;

    public void initMaterials(AssetManager assetManager) {
        floor_mat = new Material(assetManager, unshaded);

        TextureKey key3 = new TextureKey(scene1_floor_text);

        key3.setGenerateMips(true);

        Texture tex3 = assetManager.loadTexture(key3);

        tex3.setWrap(Texture.WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
    }

    // création du sol
    public Geometry initFloor(BulletAppState bulletAppState) {
        Geometry floor_geo = new Geometry("Floor1", floor);

        floor_geo.setMaterial(floor_mat);
        floor_geo.setLocalTranslation(0, -0.1f, 0);
        floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);

        return floor_geo;
    }

    public Spatial loadScene(String name, AssetManager assetManager, BulletAppState bulletAppState) {
        RigidBodyControl landscape;
        Spatial          scene = assetManager.loadModel(name);

        scene.setLocalTranslation(0, 0, 0);
        scene.setLocalScale(5);

        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) scene);

        landscape = new RigidBodyControl(sceneShape, 0);
        scene.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(scene);

        return scene;
    }

    // création de la lumière
    public AmbientLight setUpLight() {
        AmbientLight al = new AmbientLight();

        al.setColor(ColorRGBA.White.mult(1.3f));

        return al;
    }

    public PointLight setUpLight2() {
        PointLight pl = new PointLight();

        pl.setPosition(new Vector3f(0, 0.5f, 0));
        pl.setRadius(0.005f);

        return pl;
    }
}
