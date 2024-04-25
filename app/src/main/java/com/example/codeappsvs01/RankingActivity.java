package com.example.codeappsvs01;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


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
            finish();
        });

        exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            finishAffinity(); // Cierra la aplicación completamente.
        });

        // Llamar a loadRankingData() después de que el RecyclerView se haya inicializado
        loadRankingData();
    }
    @SuppressLint("CheckResult")
    private void loadRankingData() {
        PlayerResultDao dao = AppDatabase.Db.getInstance(getApplicationContext()).getDAO();
        dao.getRanking()
                .subscribeOn(Schedulers.io()) // Ejecuta la consulta en el hilo IO
                .observeOn(AndroidSchedulers.mainThread()) // Observa los resultados en el hilo principal
                .subscribe(results -> {
                    if (results.isEmpty()) {
                        // Configurar RecyclerView con el adaptador de respaldo EmptyAdapter
                        rankingRecyclerView.setAdapter(new EmptyAdapter());
                    } else {
                        // Si hay resultados, actualiza la interfaz de usuario con los resultados
                        rankingAdapter = new RankingAdapter(results);
                        rankingRecyclerView.setAdapter(rankingAdapter);
                    }
                }, error -> {
                    // Maneja posibles errores aquí
                    Log.e("RankingActivity", "Error loading ranking data", error);
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Asegura que el ExecutorService se detenga al cerrar la actividad.

    }
}

