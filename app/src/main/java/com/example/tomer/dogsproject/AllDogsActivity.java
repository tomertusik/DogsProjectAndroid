package com.example.tomer.dogsproject;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomer.dogsproject.databinding.AllDogsActivityBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class AllDogsActivity extends AppCompatActivity {

    private static final int SIGN_IN = 9999;
    private AllDogsActivityBinding binding;
    private boolean loggedIn;
    private DogExpandFragment dogExpandFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.all_dogs_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DogsApp.setContext(getApplicationContext());
        Loading.startLoading(this);
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                Model.instance.deleteAllDogs();
//            }
//        });
        checkPermissions();
        initAllDogs();
        checkForLogin();
        binding.myDogs.setOnClickListener(new MyDogsClickListener());
        binding.arrow.setOnClickListener(new MyDogsClickListener());
    }

    private void checkPermissions() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    private void initAllDogs() {
        binding.dogsRecycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        binding.dogsRecycler.setLayoutManager(new LinearLayoutManager(AllDogsActivity.this) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
        Model.instance.getAllDogs(new DoneListener());
    }

    private void checkForLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            loggedIn = true;
        }
        if (loggedIn) {
            binding.logOut.setVisibility(View.VISIBLE);
            binding.logOut.setOnClickListener(new LogOutClickListener());
        } else {
            binding.logOut.setVisibility(View.INVISIBLE);
        }
    }

    private void moveToSignIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()
                        )).setTheme(R.style.LoginTheme)
                        .build(),
                SIGN_IN);
    }

    private class MyDogsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (loggedIn) {
                moveToMyDogs();
            } else {
                moveToSignIn();
            }
        }
    }

    private void moveToMyDogs() {
        Intent intent = new Intent(this, MyDogsActivity.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                moveToMyDogs();
            } else {
                // Sign in failed
                // TODO: show error message
            }
        }
    }

    private class LogOutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Loading.startLoading(AllDogsActivity.this);
            AuthUI.getInstance()
                    .signOut(AllDogsActivity.this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            loggedIn = false;
                            Loading.stopLoading();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.logOut.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });
        }
    }

    private class DogClickListener implements DogsAdapter.DogsAdapterListener {
        @Override
        public void onDogClick(Dog dog, Drawable image) {
            dogExpandFragment = DogExpandFragment.newInstance(dog, image,false, null);
            getSupportFragmentManager().beginTransaction().add(binding.mainFrame.getId(), dogExpandFragment).addToBackStack("Expand").commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private class DoneListener implements Model.DogsDoneListener {
        @Override
        public void onDogsLoaded(final List<Dog> dogs) {
            if (dogs != null) {
                if (!dogs.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DogsAdapter adapter = new DogsAdapter(dogs, AllDogsActivity.this, new DogClickListener());
                            binding.dogsRecycler.setAdapter(adapter);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.dogsRecycler.setAdapter(null);
                        }
                    });
                }
            }
            Loading.stopLoading();
        }
    }
}