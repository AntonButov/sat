package pro.butovanton.satellite.ui.sats;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.butovanton.satellite.R;
import pro.butovanton.satellite.Sat;

class sRecyclerAdapter extends RecyclerView.Adapter<sRecyclerAdapter.sViewHolder> {
    private final LayoutInflater mInflater;
    private List<Sat> sats;
    private satsFragment fragment;

    public sRecyclerAdapter(Context context, List<Sat> sats, satsFragment fragment) {
        this.fragment = fragment;
        mInflater = LayoutInflater.from(context);
        this.sats = sats;
    }

    @NonNull
    @Override
    public sViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item, parent, false);
         return new sViewHolder(view);
    }

    @Override
    public void onBindViewHolder(sViewHolder holder, final int position) {
        holder.setName(sats.get(position).getName());
        String providers = sats.get(position).getProviders().toString();
        providers = providers.replace("[", "");
        providers = providers.replace("]", "");
        holder.setProvidersT(providers);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.onItemClick(sats.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return sats.size();
    }

    void setSats(List<Sat> sats) {
        this.sats = sats;
        notifyDataSetChanged();
    }


    public class sViewHolder extends RecyclerView.ViewHolder  {
        private final TextView nameT, providersT;

        public sViewHolder(View view) {
            super(view);
            nameT = (TextView) view.findViewById(R.id.name);
            providersT = view.findViewById(R.id.textProviders);
        }

        public void setName(String name) {
            nameT.setText(name);
        }

        public void setProvidersT(String providers) { providersT.setText( providers);}


    }

}
