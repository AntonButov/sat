package pro.butovanton.satellite.ui.sats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import pro.butovanton.satellite.R;

public class satsFragment extends Fragment {

    private satsViewModel satsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        satsViewModel =
                ViewModelProviders.of(requireActivity()).get(satsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sats, container, false);
     //   final TextView textView = root.findViewById(R.id.text_home);
        satsViewModel.getSats().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
           //     textView.setText(s);
            }
        });
        return root;
    }
}
