package it.tranigrillo.battleship.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.database.Clash;


// RecycleView Adapter per l'activity HistoryActivity.java
// fa l'inflate del layout clash_layout.xml

public class ClashAdapter extends RecyclerView.Adapter<ClashAdapter.ClashHolder> {
    private List<Clash> clashes = new ArrayList<>();

    @NonNull
    @Override
    public ClashHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.clash_layout, parent, false);
        return new ClashHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClashHolder holder, int position) {
        Clash clash = clashes.get(position);
        Drawable drawable = null;
        holder.tvItemTitle.setText(clash.getTitle());
        if (clash.isVictory()) {
            drawable = holder.context.getDrawable(R.drawable.ic_victory);
        }
        if (clash.isVictory()) {
            drawable = holder.context.getDrawable(R.drawable.ic_lose);
        }
        if (clash.getGameTime() != null) {
            holder.tvDescription.setText(clash.getGameTime());
        }
        else {
            holder.tvDescription.setVisibility(View.GONE);
            drawable = null;
        }
        if (drawable != null) {
            int h = drawable.getIntrinsicHeight();
            int w = drawable.getIntrinsicWidth();
            drawable.setBounds(0, 0, w, h);
            holder.tvItemTitle.setCompoundDrawables(null, null, drawable, null);
        }
    }

    @Override
    public int getItemCount() {
        return clashes.size();
    }

    public void setAchievements(List<Clash> clashes) {
        this.clashes = clashes;
        notifyDataSetChanged();
    }

    class ClashHolder extends RecyclerView.ViewHolder {
        private TextView tvItemTitle;
        private TextView tvDescription;
        private Context context;

        public ClashHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            context = itemView.getContext();
        }
    }
}
