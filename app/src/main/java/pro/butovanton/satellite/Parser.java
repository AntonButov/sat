package pro.butovanton.satellite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private Context context;

    Parser(Context context) {
        this.context = context;
    }
    private static final String ns = null;

    @SuppressLint("ResourceType")
    public List parse() throws XmlPullParserException, IOException {

        XmlResourceParser parser = context.getResources().getXml(R.xml.satellites);

        List sats = new ArrayList();
        String tmp;

        while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
            switch (parser.getEventType()) {
                // начало документа
                case XmlPullParser.START_DOCUMENT:
                    Log.d("DEBUG", "START_DOCUMENT");
                    break;
                // начало тэга
                case XmlPullParser.START_TAG:
                    Log.d("DEBUG", "START_TAG: name = " + parser.getName()
                            + ", depth = " + parser.getDepth() + ", attrCount = "
                            + parser.getAttributeCount());
                    tmp = "";
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        tmp = tmp + parser.getAttributeName(6) + " = "
                                + parser.getAttributeValue(i) + ", ";
                    }
                    if (!TextUtils.isEmpty(tmp))
                        Log.d("DEBUG", "Attributes: " + tmp);
                    break;
                // конец тэга
                case XmlPullParser.END_TAG:
                    Log.d("DEBUG", "END_TAG: name = " + parser.getName());
                    break;
                // содержимое тэга

                default:
                    break;
            }
            // следующий элемент
            parser.next();
        }
        Log.d("DEBUG", "END_DOCUMENT");

        return sats;
    }

    sat reeadSat (){
        return new sat();
    }
}



