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
        location = getLocationWithCheckNetworkAndGPS(contextActivity.getApplicationContext());
        return sats;
    }

    public List<Sat> getSatsList() {
        return sats.getValue();
    }

    public Location getLocationWithCheckNetworkAndGPS(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        boolean isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        android.location.Location networkLoacation = null;
        android.location.Location gpsLocation = null;
        android.location.Location finalLoc = null;
        if (isGpsEnabled)
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }
        gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isNetworkLocationEnabled)
            networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gpsLocation != null && networkLoacation != null) {

            //smaller the number more accurate result will
            if (gpsLocation.getAccuracy() > networkLoacation.getAccuracy())
                return finalLoc =networkLoacation;
            else
                return finalLoc = gpsLocation;

        } else {

            if (gpsLocation != null) {
                return finalLoc = gpsLocation;
            } else if (networkLoacation != null) {
                return finalLoc = networkLoacation;
            }
        }
        return finalLoc;
    }

    public Location getLocation() {
        return location;
    }

}