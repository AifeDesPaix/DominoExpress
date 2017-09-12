package dominoExpress;

//~--- non-JDK imports --------------------------------------------------------

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

//~--- JDK imports ------------------------------------------------------------

import java.awt.geom.Point2D;

/**
 *
 * @author Joan
 */
public class Convert {
    public static Vector3f stringToVector3f(String s) {
        int      i           = 1;
        String[] coordinates = new String[3];

        for (int nb = 0; nb < 3; nb++) {
            while ((s.charAt(i) != ',') && (s.charAt(i) != ')')) {
                coordinates[nb] += s.charAt(i);
                i++;
            }

            i++;
        }

        return new Vector3f(stringToFloat(numberStringCleaner(coordinates[0])),
                            stringToFloat(numberStringCleaner(coordinates[1])),
                            stringToFloat(numberStringCleaner(coordinates[2])));
    }

    public static Point2D.Double stringToPoint(String s) {
        int      i           = 1;
        String[] coordinates = new String[3];

        for (int nb = 0; nb < 3; nb++) {
            while ((s.charAt(i) != ',') && (s.charAt(i) != ')')) {
                coordinates[nb] += s.charAt(i);
                i++;
            }

            i++;
        }

        return new Point2D.Double(stringToFloat(numberStringCleaner(coordinates[0])),
                                  stringToFloat(numberStringCleaner(coordinates[1])));
    }

    public static Quaternion stringToQuaternion(String s) {
        int      i           = 1;
        String[] coordinates = new String[4];

        for (int nb = 0; nb < 4; nb++) {
            while ((s.charAt(i) != ',') && (s.charAt(i) != ')')) {
                coordinates[nb] += s.charAt(i);
                i++;
            }

            i++;
        }

        return new Quaternion(stringToFloat(numberStringCleaner(coordinates[0])),
                              stringToFloat(numberStringCleaner(coordinates[1])),
                              stringToFloat(numberStringCleaner(coordinates[2])),
                              stringToFloat(numberStringCleaner(coordinates[3])));
    }

    public static float stringToFloat(String s) {
        double  r           = 0;
        boolean beforeComma = true;
        double  nb;

        // Distance de décallage avec la virgule ( pas trouvé d'autre mot pour exprimer ça )
        int dist = 1;
        int pow;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.') {
                beforeComma = false;
            } else if (s.charAt(i) == 'E') {                                            // C'est l'exposant
                float exp = stringToFloat(s.substring(i + 1));

                r *= Math.pow(10, exp);

                break;
            } else if ((int) s.charAt(i) - 48 >= 0 && (int) s.charAt(i) - 48 <= 9) {    // Revient à vérifier si c'est bien à nombre
                nb = (int) s.charAt(i) - 48;

                if (beforeComma) {
                    r *= 10;
                    r += nb;
                } else {
                    pow = 1;

                    for (int j = 0; j < dist; j++) {
                        pow *= 10;
                    }

                    nb /= pow;
                    r  += nb;
                    dist++;
                }
            }
        }

        if (s.charAt(0) == '-') {    // Si le nombre est négatif, on le rend négatif !
            r *= -1;
        }

        return (float) r;
    }

    public static String textureUrlToString(String url) {
        String r = url.replaceAll("Texture2D ColorMap : Flip ", "");

        return r;
    }

    /**
     * Delete all character to a String
     * @nb number to clean
     * @return The "String number" cleaned
     */
    public static String numberStringCleaner(String nb) {
        String r = new String();

        for (int i = 0; i < nb.length(); i++) {

            // Convertis en entier avec le décallage ACSII (48)
            int tmp = (int) nb.charAt(i) - 48;

            if (((tmp >= 0) && (tmp <= 9)) || (tmp == -2) || (tmp == -3) || (tmp == 21)) {    // -3 = '-' et -2 = '.' (la virgule quoi) 21 = E (Exposant quoi)
                r += nb.charAt(i);
            }

            if (tmp == 69) {    //
            }
        }

        return r;
    }
}