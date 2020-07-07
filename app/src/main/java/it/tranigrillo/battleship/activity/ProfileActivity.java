package it.tranigrillo.battleship.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.DataViewModel;
import it.tranigrillo.battleship.model.database.Achievement;
import it.tranigrillo.battleship.view.AchievementAdapter;

public class ProfileActivity extends AppCompatActivity {

    private static final int RESULT = 107;

    private DataViewModel dataViewModel;
    private RecyclerView rvAchievements;

    private class Holder implements View.OnClickListener {

        Holder(Context context) {
            rvAchievements = findViewById(R.id.rvAchievement);

            final AchievementAdapter adapter = new AchievementAdapter();
            rvAchievements.setLayoutManager(new LinearLayoutManager(context));
            rvAchievements.setAdapter(adapter);

            dataViewModel = new ViewModelProvider(ProfileActivity.this).get(DataViewModel.class);
            dataViewModel.getAllAchievements().observe(ProfileActivity.this, new Observer<List<Achievement>>() {
                @Override
                public void onChanged(List<Achievement> achievements) {
                    Log.d("tag", String.valueOf(achievements.size()));
                    adapter.setAchievements(achievements);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent activityIntent = new Intent(ProfileActivity.this, HistoryActivity.class);
            startActivityForResult(activityIntent, RESULT);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        new Holder(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.removeItem(R.id.miProfile);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent activityIntent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.miOption:
                activityIntent = new Intent(ProfileActivity.this, OptionActivity.class);
                startActivityForResult(activityIntent, RESULT);
                return true;
            case R.id.miLoogbook:
                activityIntent = new Intent(ProfileActivity.this, HistoryActivity.class);
                startActivityForResult(activityIntent, RESULT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
