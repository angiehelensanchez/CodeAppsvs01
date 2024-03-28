package com.example.codeappsvs01;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RankingActivity extends AppCompatActivity {

    private RecyclerView rankingRecyclerView;
    private Button playAgainButton;
    private Button exitButton;
    private RankingAdapter rankingAdapter;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);

        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playAgainButton = findViewById(R.id.playAgainButton);
        playAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(RankingActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finaliza RankingActivity para no volver a ella al presionar "atrás".
        });

        exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            finishAffinity(); // Cierra la aplicación completamente.
        });

        loadRankingData();
    }

    private void loadRankingData() {
        executor.execute(() -> {
            PlayerResultDao dao = AppDatabase.getInstance(getApplicationContext()).playerResultDao();
            List<PlayerResult> results = dao.getRanking();
            handler.post(() -> {
                rankingAdapter = new RankingAdapter(results);
                rankingRecyclerView.setAdapter(rankingAdapter);
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Asegura que el ExecutorService se detenga al destruir la actividad.
    }
}
