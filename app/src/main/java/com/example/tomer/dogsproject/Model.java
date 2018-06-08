package com.example.tomer.dogsproject;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.URLUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tomer on 26/05/2018.
 */

public class Model {

    static Model instance = new Model();
    private DogsRoomDataBase localDB;
    private LiveData<List<Dog>> dogstListData;
    private LiveData<List<Dog>> myDogstListData;

    public interface DogsDeleteListener {
        void onDogDeleted();
    }

    public interface DogsDoneListener {
        void onDogsLoaded(List<Dog> dogs);
    }

    private Model() {
        localDB = DogsRoomDataBase.getDatabase();
        dogstListData = new DogListData();
        myDogstListData = new MyDogListData();
    }

    class DogListData extends MutableLiveData<List<Dog>> {

        private ValueEventListener eventListener;

        @Override
        protected void onActive() {
            super.onActive();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<Dog> s = localDB.dogDao().getAllDogs();
                    postValue(s);
                    FirebaseDatabase instance = FirebaseDatabase.getInstance();
                    final DatabaseReference ref = instance.getReference().child("Dogs");
                    eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final ArrayList<Dog> dogs = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Dog dog = snapshot.getValue(Dog.class);
                                dogs.add(dog);
                            }
                            setValue(dogs);
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    localDB.dogDao().deleteAll();
                                    localDB.dogDao().insertAll(dogs);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Loading.stopLoading();

                        }
                    };
                    ref.addListenerForSingleValueEvent(eventListener);
                }
            });
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            DatabaseReference stRef = FirebaseDatabase.getInstance().getReference().child("Dogs");
            stRef.removeEventListener(eventListener);
        }

        public DogListData() {
            super();
            setValue(new LinkedList<Dog>());
        }
    }

    class MyDogListData extends MutableLiveData<List<Dog>> {

        private ValueEventListener eventListener;

        @Override
        protected void onActive() {
            super.onActive();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<Dog> s = localDB.dogDao().getMyDogs(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    postValue(s);
                    FirebaseDatabase instance = FirebaseDatabase.getInstance();
                    final DatabaseReference ref = instance.getReference().child("Dogs");
                    eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final ArrayList<Dog> dogs = new ArrayList<>();
                            final ArrayList<Dog> myDogs = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Dog dog = snapshot.getValue(Dog.class);
                                dogs.add(dog);
                                if (dog.getOwnerUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    myDogs.add(dog);
                                }
                            }
                            setValue(myDogs);
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    localDB.dogDao().deleteAll();
                                    localDB.dogDao().insertAll(dogs);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Loading.stopLoading();

                        }
                    };
                    ref.addListenerForSingleValueEvent(eventListener);
                }
            });
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            DatabaseReference stRef = FirebaseDatabase.getInstance().getReference().child("Dogs");
            stRef.removeEventListener(eventListener);
        }

        public MyDogListData() {
            super();
            setValue(new LinkedList<Dog>());
        }
    }

    public void deleteAllDogs() {
        localDB.dogDao().deleteAll();
    }

    public void deleteDog(Dog dog, DogsDeleteListener listener) {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Dogs");
        mRef.child(dog.getOwnerUserID() + dog.getName()).removeValue(new RemoveListener(dog, listener));
        deleteImage(dog);
    }

    private void deleteImage(Dog dog) {
        File dir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String localName = getLocalImageFileName(dog.getImageURL());
        File imageFile = new File(dir, localName);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
        StorageReference ref = storageRef.child(dog.getOwnerUserID() + dog.getName());
        ref.delete();
    }

    public void AddNewDog(Dog dog, Uri selectedImage) {
        saveImage(dog, selectedImage, FirebaseAuth.getInstance().getCurrentUser().getUid() + dog.getName());
    }

    private long getLastUpdate() {
        SharedPreferences sharedRef = DogsApp.getContext().getSharedPreferences("dogsUpdate", DogsApp.getContext().MODE_PRIVATE);
        long lastUpdate = sharedRef.getLong("lastUpdate", 0);
        return lastUpdate;
    }

    public LiveData<List<Dog>> getAllDogs() {
        return dogstListData;
    }

    public LiveData<List<Dog>> getMyDogs() {
        return myDogstListData;
    }

    public void getMyDogs(DogsDoneListener listener) {
        getDogs(new MyDogsListener(getLastUpdate(), listener));
    }

    private void getDogs(ValueEventListener listener) {
        FirebaseDatabase instance = FirebaseDatabase.getInstance();
        final DatabaseReference ref = instance.getReference().child("Dogs");
        ref.addListenerForSingleValueEvent(listener);
    }

    private class MyDogsListener implements ValueEventListener {

        private final DogsDoneListener listener;
        private long lastUpdate;

        public MyDogsListener(long lastUpdate, DogsDoneListener listener) {
            this.lastUpdate = lastUpdate;
            this.listener = listener;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            ArrayList<Dog> dogs = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Dog dog = snapshot.getValue(Dog.class);
                if (dog.getLastUpdate() > lastUpdate) {
                    lastUpdate = dog.getLastUpdate();
                    SharedPreferences sharedRef = DogsApp.getContext().getSharedPreferences("dogsUpdate", DogsApp.getContext().MODE_PRIVATE);
                    SharedPreferences.Editor ed = sharedRef.edit();
                    ed.putLong("lastUpdate", lastUpdate);
                    ed.commit();
                    dogs.add(dog);
                }
            }
            new MyDogsRoomAsync(dogs, listener).execute();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Loading.stopLoading();
        }
    }

    private void saveImage(final Dog dog, final Uri image, String key) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
        StorageReference ref = storageRef.child(key);
        ref.putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        dog.setImageURL(downloadUrl.toString());
                        String localName = getLocalImageFileName(downloadUrl.toString());
                        Bitmap imageBitmap = null;
                        try {
                            imageBitmap = MediaStore.Images.Media.getBitmap(DogsApp.getContext().getContentResolver(), image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        saveImageToFile(imageBitmap, localName);
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Dogs");
                                mRef.child(dog.getOwnerUserID() + dog.getName()).setValue(dog, new DoneListener());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    public String getLocalImageFileName(String url) {
        String name = URLUtil.guessFileName(url, null, null);
        return name;
    }

    private void saveImageToFile(Bitmap imageBitmap, String imageFileName) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir, imageFileName);
            imageFile.createNewFile();
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            addPicureToGallery(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPicureToGallery(File imageFile) {
        //add the picture to the gallery so we dont need to manage the cache size
        Intent mediaScanIntent = new
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        DogsApp.getContext().sendBroadcast(mediaScanIntent);
    }

    public Bitmap loadImageFromFile(String imageFileName) {
        Bitmap bitmap = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir, imageFileName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private class DoneListener implements DatabaseReference.CompletionListener {
        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            EventBus.getDefault().post(new AddDogEvent());
        }
    }

    private class RoomAsync extends AsyncTask {

        private final List<Dog> dogs;
        private final DogsDoneListener listener;

        public RoomAsync(List<Dog> dogs, DogsDoneListener listener) {
            this.dogs = dogs;
            this.listener = listener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if (dogs.size() > 0) {
                localDB.dogDao().insertAll(dogs);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<Dog> dogs = localDB.dogDao().getAllDogs();
                    listener.onDogsLoaded(dogs);
                }
            });
        }
    }

    private class MyDogsRoomAsync extends AsyncTask {

        private final List<Dog> dogs;
        private final DogsDoneListener listener;

        public MyDogsRoomAsync(List<Dog> dogs, DogsDoneListener listener) {
            this.dogs = dogs;
            this.listener = listener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if (dogs.size() > 0) {
                localDB.dogDao().insertAll(dogs);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<Dog> dogs = localDB.dogDao().getMyDogs(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    listener.onDogsLoaded(dogs);
                }
            });
        }
    }

    private class RemoveListener implements DatabaseReference.CompletionListener {

        private final DogsDeleteListener listener;
        private final Dog dog;

        public RemoveListener(Dog dog, DogsDeleteListener listener) {
            this.dog = dog;
            this.listener = listener;
        }

        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            new DeleteDogRoomAsync(dog, listener).execute();
        }
    }

    private class DeleteDogRoomAsync extends AsyncTask {

        private final Dog dog;
        private final DogsDeleteListener listener;

        public DeleteDogRoomAsync(Dog dog, DogsDeleteListener listener) {
            this.dog = dog;
            this.listener = listener;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            localDB.dogDao().delete(dog);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    listener.onDogDeleted();
                }
            });
        }
    }
}
