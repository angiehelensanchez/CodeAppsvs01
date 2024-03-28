package com.example.codeappsvs01;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;


@Dao
public interface PlayerResultDao {
    // Inserta un resultado y devuelve un Completable que completa o emite un error.
    // OnConflictStrategy define cómo manejar inserciones con conflictos, por ejemplo,
    // puedes usar REPLACE, IGNORE, etc., según tus necesidades.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertResult(PlayerResult result);

    // Obtiene un listado de resultados ordenados por su valor de resultado de manera descendente.
    // Flowable se suscribe a los datos y observa cambios automáticamente,
    // emitiendo los últimos datos cada vez que cambian.
    @Query("SELECT * FROM PlayerResult ORDER BY result DESC")
    Flowable<List<PlayerResult>> getRanking();
}
