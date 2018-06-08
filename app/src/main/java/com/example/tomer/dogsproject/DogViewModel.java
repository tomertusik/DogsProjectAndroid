package com.example.tomer.dogsproject;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.net.Uri;

import java.util.List;

/**
 * Created by Tomer on 08/06/2018.
 */

public class DogViewModel extends AndroidViewModel {

    private Model model;
    private LiveData<List<Dog>> dogs;
    private LiveData<List<Dog>> myDogs;

    public DogViewModel (Application application) {
        super(application);
        model = Model.instance;
        dogs = model.getAllDogs();
        myDogs = model.getMyDogs();
    }

    public LiveData<List<Dog>> getAllDogs() { return dogs; }

    public LiveData<List<Dog>> getMyDogs() {
        return myDogs;
    }
}
