package pro.butovanton.satellite.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import pro.butovanton.satellite.R;

public class DatailSatellite extends Fragment {

    //private ImageView imageViewAzim, imageViewDiametr;
    private TextView tAzim, tConer,tDiametr;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle bundle = getArguments();
        String name = bundle.getString("name");
        int azimit = bundle.getInt("azimut");
        int coner = bundle.getInt("coner");
        String diametr = bundle.getString("diametr");
        tAzim = root.findViewById(R.id.textViewAzim);
        tConer = root.findViewById(R.id.textViewElevazione);
        tDiametr = root.findViewById(R.id.textViewDiametr);
        tAzim.setText("Azimut: " + azimit);
        tConer.setText("Elevazion: " + coner);
        tDiametr.setText("Diametr: ~" + diametr);
        return root;

    }
}
