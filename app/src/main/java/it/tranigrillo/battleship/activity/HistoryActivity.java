package it.tranigrillo.battleship.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
import it.tranigrillo.battleship.model.database.Clash;
import it.tranigrillo.battleship.view.ClashAdapter;

public class HistoryActivity extends AppCompatActivity {

    private static final int RESULT = 108;
    private DataViewModel dataViewModel;
    private RecyclerView rvClash;

    private class Holder {

        Holder(Context context) {
            rvClash = findViewById(R.id.rvClash);

            final ClashAdapter adapter = new ClashAdapter();
            rvClash.setLayoutManager(new LinearLayoutManager(context));
            rvClash.setAdapter(adapter);

            dataViewModel = new ViewModelProvider(HistoryActivity.this).get(DataViewModel.class);
            dataViewModel.getAllClashes().observe(HistoryActivity.this, new Observer<List<Clash>>() {
                @Override
                public void onChanged(List<Clash> clashes) {
                    adapter.setAchievements(clashes);
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clash_history_layout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        new Holder(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.removeItem(R.id.miLoogbook);
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
                activityIntent = new Intent(HistoryActivity.this, OptionActivity.class);
                startActivityForResult(activityIntent, RESULT);
                return true;
            case R.id.miProfile:
                activityIntent = new Intent(HistoryActivity.this, ProfileActivity.class);
                startActivityForResult(activityIntent, RESULT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
