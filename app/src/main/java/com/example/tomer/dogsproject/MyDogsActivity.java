package com.example.tomer.dogsproject;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomer.dogsproject.databinding.MyDogsActivityBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by Tomer on 26/05/2018.
 */

public class MyDogsActivity extends AppCompatActivity {

    private MyDogsActivityBinding binding;
    private AddDogFragment addDogFragment;
    private DogExpandFragment dogExpandFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.my_dogs_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Loading.startLoading(this);
        EventBus.getDefault().register(this);
        binding.addDog.setOnClickListener(new AddDogListener());
        initMyDogs();
    }

    private void initMyDogs() {
        binding.dogsRecycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        binding.dogsRecycler.setLayoutManager(new LinearLayoutManager(MyDogsActivity.this) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
        DogViewModel viewModel = ViewModelProviders.of(this).get(DogViewModel.class);
        viewModel.getMyDogs().observe(this, new Observer<List<Dog>>() {
            @Override
            public void onChanged(@Nullable final List<Dog> dogs) {
                // Update the cached copy of the words in the adapter.
                if (dogs != null) {
                    if (!dogs.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DogsAdapter adapter = new DogsAdapter(dogs, MyDogsActivity.this, new DogClickListener());
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
        });
    }

    private class AddDogListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (addDogFragment != null) {
                if (!addDogFragment.isAdded()) {
                    openAddDog();
                }
            } else {
                openAddDog();
            }
        }
    }

    private void openAddDog() {
        addDogFragment = AddDogFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(binding.mainFrame.getId(), addDogFragment).addToBackStack("Add").commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddDogEvent(AddDogEvent event) {
        getSupportFragmentManager().beginTransaction().remove(addDogFragment).commit();
        Model.instance.getMyDogs(new DoneListener());
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private class DogClickListener implements DogsAdapter.DogsAdapterListener {
        @Override
        public void onDogClick(Dog dog, Drawable image) {
            dogExpandFragment = DogExpandFragment.newInstance(dog, image, true, new OnDeleteListener());
            getSupportFragmentManager().beginTransaction().add(binding.mainFrame.getId(), dogExpandFragment).addToBackStack("Expand").commit();
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
                            DogsAdapter adapter = new DogsAdapter(dogs, MyDogsActivity.this, new DogClickListener());
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

    private class OnDeleteListener implements DogExpandFragment.ExpandDeleteListener {
        @Override
        public void onDelete() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Loading.startLoading(MyDogsActivity.this);
                }
            });
            Model.instance.getMyDogs(new DoneListener());
        }
    }
}
