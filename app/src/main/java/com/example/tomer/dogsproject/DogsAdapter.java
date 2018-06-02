package com.example.tomer.dogsproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.example.tomer.dogsproject.databinding.DogHolderBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomer on 26/05/2018.
 */

public class DogsAdapter extends RecyclerView.Adapter {

    private final List<Dog> dogs;
    private final Context context;
    private DogsAdapterListener listener;

    public interface DogsAdapterListener{
        void onDogClick(Dog dog, Drawable image);
    }

    public DogsAdapter(List<Dog> dogs, Context context, DogsAdapterListener listener) {
        this.dogs = dogs;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DogHolderBinding binding = DogHolderBinding.inflate(LayoutInflater.from(parent.getContext()));
        DogViewHolder viewHolder = new DogViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DogViewHolder dogViewHolder = (DogViewHolder) holder;
        Dog currDog = dogs.get(position);
        dogViewHolder.binding.name.setText(currDog.getName());
        dogViewHolder.binding.age.setText(currDog.getAge());
        dogViewHolder.binding.city.setText(currDog.getCity());
        String localFileName = Model.instance.getLocalImageFileName(currDog.getImageURL());
        Bitmap image = Model.instance.loadImageFromFile(localFileName);
        if (image != null){
            dogViewHolder.binding.image.setImageBitmap(image);
            dogViewHolder.binding.getRoot().setOnClickListener(new OnDogClickListener(currDog,dogViewHolder.binding.image.getDrawable()));
        } else {
            Glide.with(context).load(currDog.getImageURL()).listener(new DoneLoadingListener(dogViewHolder,currDog)).into(dogViewHolder.binding.image);
        }
    }

    @Override
    public int getItemCount() {
        return dogs.size();
    }

    public class DogViewHolder extends RecyclerView.ViewHolder {

        public final DogHolderBinding binding;

        public DogViewHolder(DogHolderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private class OnDogClickListener implements View.OnClickListener {

        private final Dog currDog;
        private final Drawable image;

        public OnDogClickListener(Dog currDog, Drawable drawable) {
            this.currDog = currDog;
            this.image = drawable;
        }

        @Override
        public void onClick(View view) {
            listener.onDogClick(currDog,image);
        }
    }

    private class DoneLoadingListener implements com.bumptech.glide.request.RequestListener<Drawable> {

        private final DogViewHolder dogViewHolder;
        private final Dog currDog;

        public DoneLoadingListener(DogViewHolder dogViewHolder, Dog currDog) {
            this.dogViewHolder = dogViewHolder;
            this.currDog =currDog;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            dogViewHolder.binding.getRoot().setOnClickListener(new OnDogClickListener(currDog, resource));
            return false;
        }
    }
}
