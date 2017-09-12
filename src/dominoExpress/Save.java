
package dominoExpress;

//~--- non-JDK imports --------------------------------------------------------

import com.jme3.math.Vector3f;

//~--- JDK imports ------------------------------------------------------------

import java.awt.geom.Point2D;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

/**
 *
 * @author Joan
 */
public class Save {

    // Destination du fichier de sauvegarde
    private static String urlSave = "save/";

    // Deux listes contenant les positions et orientations de chaque domino
    private ArrayList<Point2D.Double> position    = new ArrayList<Point2D.Double>();
    private ArrayList<Vector3f>       taille      = new ArrayList<Vector3f>();
    private ArrayList<Double>         orientation = new ArrayList<Double>();
    private ArrayList<String>         texture     = new ArrayList<String>();
    public ArrayList<Boolean>         start       = new ArrayList<Boolean>();

    // Chaine de caractères contenant les infos sur les dominos à mettre dans le fichier.
    private String  fileContain;
    private boolean isLoad;

    public Save() {
        fileContain = "";
        isLoad      = false;
    }

    /**
     * Remplis la chaine de caractères fileContain prête à être inscrite dans le fichier
     * @param e
     */
    public void buildSave(Editor e, ArrayList<Domino> list) {
        fileContain = "";

        for (int i = 0; i < list.size(); i++) {
            Vector3d v = new Vector3d(list.get(i).getPosition().x, list.get(i).getPosition().y,
                                      list.get(i).getPosition().y);
            Double   q = list.get(i).getAngle();
            Vector3f s = list.get(i).getTaille();
            String   t = list.get(i).getTexture();
            boolean  b = list.get(i).isStart();

            fileContain += v + "\n" + q + "\n" + s + "\n" + t + "\n" + b + "\n\n";
        }

        fileContain += "E";
    }

    /**
     * Sauvegarde dans le fichier
     * @param url du fichier cible
     */
    public void saveInFile(String url, String fileName) {
        try {
            urlSave = url;

            FileWriter     fileWriter     = new FileWriter(urlSave + fileName, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(fileContain);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {}
    }

    /**
     * Recupere le contenu du fichier sauvegardé
     * @param url
     */
    public void loadFile(String url, String fileName) {
        try {
            InputStream ips;

            urlSave = url;
            ips     = new FileInputStream(urlSave + fileName);

            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader    br   = new BufferedReader(ipsr);

            try {
                String line = br.readLine();

                while (line.charAt(0) != 'E') {
                    position.add(Convert.stringToPoint(line));
                    line = br.readLine();
                    orientation.add(Double.parseDouble(line));
                    line = br.readLine();
                    taille.add(Convert.stringToVector3f(line));
                    line = br.readLine();
                    texture.add(Convert.textureUrlToString(line));
                    line = br.readLine();
                    start.add(Boolean.parseBoolean(line));
                    br.readLine();    // Lignes vide
                    line = br.readLine();
                }
            } catch (IOException e) {}

            br.close();
        } catch (IOException e) {}
    }

    public ArrayList<Point2D.Double> getPosition() {
        return position;
    }

    public ArrayList<Double> getOrientation() {
        return orientation;
    }

    public ArrayList<Vector3f> getTaille() {
        return taille;
    }

    public ArrayList<String> getTexture() {
        return texture;
    }

    public boolean getIsLoad() {
        return isLoad;
    }

    public void setIsLoad(Boolean b) {
        isLoad = b;
    }
}

