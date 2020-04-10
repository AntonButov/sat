package pro.butovanton.satellite;

import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

 public class Azimuth {

    //где g1 - долгота спутника, g2 - долгота места приема, v - широта места приема.
    public static float conerplacesat(float longitudesat, float longitudeplace, float conersat) {
        //    g1 = 36;
        //     g2 = 37;
        //     v =56;
        float g2 = longitudesat;
        float v = longitudeplace;
        float g1 = conersat;
        g2 = (float) toRadians(g2);
        g1 = (float) toRadians(g1);
        v = (float) toRadians(v);
        float c1= (float) (cos(g2-g1)*cos(v)-0.151);
        float c2 = (float)(1-(cos(g2-g1)*cos(g2-g1)*cos(v)*cos(v)));
        return (float) toDegrees(Math.atan(c1/sqrt(c2)));
    }

    public static float azimuthsat(float longitudesat, float longitudeplace, float conersat) {
        float g2 = longitudesat;
        float v = longitudeplace;
        float g1 = conersat;
        g2 = (float) toRadians(g2);
        g1 = (float) toRadians(g1);
        v = (float) toRadians(v);
        return (float) (180 + toDegrees(atan(tan(g2-g1)/sin(v))));
    }

}
