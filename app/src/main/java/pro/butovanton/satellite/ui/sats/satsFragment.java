package pro.butovanton.satellite.ui.sats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pro.butovanton.satellite.R;
import pro.butovanton.satellite.Sat;

public class satsFragment extends Fragment implements ItemClickListener {

    private RecyclerView recyclerView;
    private sRecyclerAdapter adapter;

    private satsViewModel satsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        satsViewModel =
                ViewModelProviders.of(requireActivity()).get(satsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sats, container, false);
        recyclerView = root.findViewById(R.id.reciclerView);
        final sRecyclerAdapter adapter = new sRecyclerAdapter(getActivity(),  new ArrayList<Sat>(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        satsViewModel.getSats(getActivity()).observe(getViewLifecycleOwner(), new Observer<List<Sat>>() {
            @Override
            public void onChanged(@Nullable List<Sat> sats) {
                adapter.setSats(sats);
            }
        });
        return root;
    }


    @Override
    public void onItemClick(Sat sat) {

    }
}
