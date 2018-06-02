package com.example.tomer.dogsproject;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Tomer on 01/06/2018.
 */

@Dao
public interface DogDao {

    @Query("SELECT * FROM dog")
    List<Dog> getAllDogs();

    @Query("SELECT * FROM dog WHERE user_id LIKE :ownerID")
    List<Dog> getMyDogs(String ownerID);

    @Delete
    void delete(Dog dog);

    @Insert
    void insertAll(List<Dog> dogs);

    @Query("DELETE FROM dog")
    void deleteAll();
}
