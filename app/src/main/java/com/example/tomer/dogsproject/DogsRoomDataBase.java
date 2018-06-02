package com.example.tomer.dogsproject;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Tomer on 01/06/2018.
 */

@Database(entities = {Dog.class}, version = 1)
public abstract class DogsRoomDataBase extends RoomDatabase {

    private static DogsRoomDataBase instance;

    public abstract DogDao dogDao();

    public static DogsRoomDataBase getDatabase() {
        if (instance == null) {
            instance = Room.databaseBuilder(DogsApp.getContext(), DogsRoomDataBase.class, "dogs_db").build();
        }
        return instance;
    }
}
