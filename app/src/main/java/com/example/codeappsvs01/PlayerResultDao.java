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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertResult(PlayerResult result);

    @Query("SELECT * FROM PlayerResult ORDER BY result DESC")
    Flowable<List<PlayerResult>> getRanking();
}
