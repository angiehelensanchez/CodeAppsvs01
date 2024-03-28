package com.example.codeappsvs01;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlayerResultDao {
    @Insert
    void insertResult(PlayerResult result);

    @Query("SELECT * FROM PlayerResult ORDER BY result DESC")
    List<PlayerResult> getRanking();

}
