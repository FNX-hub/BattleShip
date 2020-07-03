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
import it.tranigrillo.battleship.model.database.Achievement;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementHolder> {
    private List<Achievement> achievements = new ArrayList<>();

    @NonNull
    @Override
    public AchievementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.achievement_layout, parent, false);
        return new AchievementHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        Drawable drawable;
        holder.tvItemTitle.setText(achievement.getTitle());
        holder.tvDescription.setText(achievement.getDescription());
        if (achievement.isLocked()) drawable = holder.context.getDrawable(R.drawable.ic_achievement_locked);
        else drawable = holder.context.getDrawable(R.drawable.ic_achievement);
        holder.tvDescription.setCompoundDrawables(null, null, drawable, null);
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
        notifyDataSetChanged();
    }

    class AchievementHolder extends RecyclerView.ViewHolder {
        private TextView tvItemTitle;
        private TextView tvDescription;
        private Context context;

        public AchievementHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            context = itemView.getContext();
        }
    }
}
