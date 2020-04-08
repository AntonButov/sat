package pro.butovanton.satellite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser {
    private Context context;

    Parser(Context context) {
        this.context = context;
    }

    private static final String ns = null;

    public List parse() throws XmlPullParserException, IOException {

        XmlResourceParser parser = context.getResources().getXml(R.xml.satellites);

        List sats = new ArrayList();

        while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                if (parser.getName().equals("sat")) {
                    Sat sat = new Sat();

                    sat.setName(parser.getAttributeValue(null, "name"));
                    Log.d("DEBUG", "Sat name = " + parser.getAttributeValue(null, "name"));
                    parser.next();
                    HashSet<String> providers = parseProviders(parser);
                    sat.setProviders(providers);
                    sats.add(sat);
                }
            }
            parser.next();
        }
        return sats;
    }

    HashSet<String> parseProviders(XmlPullParser parser) throws IOException, XmlPullParserException {
        HashSet<String> providers = new HashSet<>();
        while ( parser.getName().equals("transponder")) {
            if (parser.getAttributeValue(null, "provider") != null) {
                providers.add(parser.getAttributeValue(null, "provider"));
            }
            parser.next();
        }
        return providers;
    }
}









