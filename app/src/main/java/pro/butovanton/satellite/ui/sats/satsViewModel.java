package pro.butovanton.satellite.ui.sats;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import pro.butovanton.satellite.MLocation;
import pro.butovanton.satellite.Parser;
import pro.butovanton.satellite.Sat;

public class satsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Sat>> sats;
    private Location location;
    private Application application;

    public satsViewModel(Application application) {
        super(application);
        this.application = application;
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

    public void setLocation() {

           location = MLocation.getLocationWithCheckNetworkAndGPS(application);
    }

    public Location getLocation() {
        if (location == null) {
            location = new Location("");
            location.setLongitude(35);
            location.setLatitude(40);
        }
        return location;
    }


}