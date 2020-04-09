package pro.butovanton.satellite.ui.sats;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pro.butovanton.satellite.Parser;
import pro.butovanton.satellite.Sat;

public class satsViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private List<Sat> sats;

    public satsViewModel() {

        mText = new MutableLiveData<>();
        Log.d("DEBUG", "ViewModelSats start");
        //Parser parser = new Parser(context);

    }

    public LiveData<String> getSats() {
        return mText;
    }
}