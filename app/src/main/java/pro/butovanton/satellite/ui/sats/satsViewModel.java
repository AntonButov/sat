package pro.butovanton.satellite.ui.sats;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import pro.butovanton.satellite.Parser;
import pro.butovanton.satellite.Sat;

public class satsViewModel extends ViewModel {

    private MutableLiveData<List<Sat>> sats;

    public satsViewModel() {
        sats = new MutableLiveData<>();
        Log.d("DEBUG", "ViewModelSats start");
    }

    public LiveData<List<Sat>> getSats(Context contextActivity) {
        if (sats.getValue() == null) {
            Parser parser = new Parser(contextActivity);
            try {
                sats.setValue(parser.parse());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sats;
    }

    public List<Sat> getSatsList() {
        return sats.getValue();
    }
}