
package dominoExpress;

//~--- non-JDK imports --------------------------------------------------------

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

//~--- JDK imports ------------------------------------------------------------

import java.awt.geom.Point2D;

/**
 *
 * @author Nkplizz
 */
public class Domino extends Geometry {
    private Point2D.Double   position;
    private java.lang.Double angle;
    private Vector3f         taille;
    private String           texture;
    private boolean          start;
    private RigidBodyControl domino_phy;

    public Domino(Point2D.Double position, java.lang.Double angle, boolean start, Editor app) {
        super();

        if (app.ghost) {
            this.name = "dominoG" + app.dom.ghostList.size();
        } else {
            this.name = "domino" + app.dom.dominosList.size();
        }

        this.taille   = new Vector3f(app.dom.dominoLength, app.dom.dominoHeight, app.dom.dominoWidth);
        this.mesh     = new Box(taille.x, taille.y, taille.z);
        this.start    = start;
        this.angle    = angle;
        this.position = position;
        this.setQueueBucket(RenderQueue.Bucket.Transparent);
        this.setMaterial(Dominos.domino_mat.clone());
        this.texture = Dominos.domino_text;
        this.mesh    = new Box(taille.x, taille.y, taille.z);
        this.mesh.scaleTextureCoordinates(new Vector2f(1f, 0.5f));

        // angle
        Quaternion rotate = new Quaternion();

        rotate.fromAngleAxis(angle.floatValue(), new Vector3f(0, 1, 0));
        this.setLocalRotation(rotate);

        // position
        Vector3f loc = new Vector3f((float) (position.x), Dominos.y + taille.y, (float) (position.y));

        this.setLocalTranslation(loc);

        // texture
        if (start) {
            Quaternion rotate2 = new Quaternion();

            rotate2.fromAngleAxis(FastMath.PI / 4, new Vector3f(1, 0, 0));
            this.rotate(rotate2);
        }
    }

    public void reset(Editor app) {

        // angle
        Quaternion rotate = new Quaternion();

        rotate.fromAngleAxis(angle.floatValue(), new Vector3f(0, 1, 0));
        this.setLocalRotation(rotate);

        // position
        Vector3f loc = new Vector3f((float) (position.x), Dominos.y + taille.y, (float) (position.y));

        this.setLocalTranslation(loc);

        if (start) {
            Quaternion rotate2 = new Quaternion();

            rotate2.fromAngleAxis(FastMath.PI / 4, new Vector3f(1, 0, 0));
            this.rotate(rotate2);
        }

        // physique
        app.bulletAppState.getPhysicsSpace().remove(domino_phy);
        this.removeControl(domino_phy);
        domino_phy = new RigidBodyControl(3f);
        this.addControl(domino_phy);
        app.bulletAppState.getPhysicsSpace().add(domino_phy);
    }

    public void attach(Editor app) {
        if (!app.ghost) {
            domino_phy = new RigidBodyControl(3f);
            this.addControl(domino_phy);
            app.bulletAppState.getPhysicsSpace().add(domino_phy);
        }

        app.getRootNode().attachChild(this);
    }

    public void move(Point2D.Double position, java.lang.Double angle, Editor app) {
        this.angle    = angle;
        this.position = position;

        Quaternion rotate = new Quaternion();

        rotate.fromAngleAxis(angle.floatValue(), new Vector3f(0, 1, 0));
        this.setLocalRotation(rotate);

        if (this.isStart()) {
            Quaternion rotate2 = new Quaternion();
            rotate2.fromAngleAxis(FastMath.PI / 4, new Vector3f(1, 0, 0));
            this.rotate(rotate2);
        }

        Vector3f loc = new Vector3f((float) (position.x), Dominos.y + taille.y, (float) (position.y));

        this.setLocalTranslation(loc);

         if(!app.ghost || app.dom.currenttool.getType()=="Selection"){//autorise ou non la selection lorsque la pause est activer
        app.getRootNode().detachChild(this);
        this.removeControl(domino_phy);
        app.bulletAppState.getPhysicsSpace().remove(domino_phy);
        domino_phy = new RigidBodyControl(3f);
        this.addControl(domino_phy);
        app.bulletAppState.getPhysicsSpace().add(domino_phy);

        }
        app.getRootNode().attachChild(this);
    }

    // /Getter and setter

    public Point2D.Double getPosition() {
        return position;
    }

    public void setPosition(Point2D.Double position) {
        this.position = position;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Vector3f getTaille() {
        return taille;
    }

    public void setTaille(float dominoHeight) {
        taille    = new Vector3f(0.4857f * dominoHeight, dominoHeight, 0.185714f * dominoHeight);
        this.mesh = new Box(taille.x, taille.y, taille.z);
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture() {
        this.setMaterial(Dominos.domino_mat.clone());
        this.texture = Dominos.domino_text;
        this.mesh    = new Box(taille.x, taille.y, taille.z);
        this.mesh.scaleTextureCoordinates(new Vector2f(1f, 0.5f));
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public RigidBodyControl getDomino_phy() {
        return domino_phy;
    }
}
