package com.example.tomer.dogsproject;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomer.dogsproject.databinding.DogExpandBinding;

/**
 * Created by Tomer on 01/06/2018.
 */

public class DogExpandFragment extends Fragment {

    private Dog dog;
    private DogExpandBinding binding;
    private Drawable image;
    private boolean isDelete;
    private ExpandDeleteListener listener;

    public interface ExpandDeleteListener{
        void onDelete();
    }

    public static final DogExpandFragment newInstance(Dog dog, Drawable image,boolean isDelete, ExpandDeleteListener listener) {
        DogExpandFragment fragment = new DogExpandFragment();
        fragment.dog = dog;
        fragment.image = image;
        fragment.isDelete = isDelete;
        fragment.listener = listener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dog_expand, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isDelete){
            binding.delete.setVisibility(View.VISIBLE);
            binding.delete.setOnClickListener(new DeleteClickListener());
        }
        binding.name.setText(dog.getName());
        binding.age.setText(dog.getAge());
        binding.city.setText(dog.getCity());
        binding.phone.setText(dog.getPhone());
        binding.image.setImageDrawable(image);
        binding.exitButton.setOnClickListener(new ExitListener());
        binding.backgroundLayout.setOnClickListener(new ExitListener());
        binding.mainLayout.setOnClickListener(null);
        binding.background.setOnClickListener(new ExitListener());
    }

    private class ExitListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private class DeleteClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Model.instance.deleteDog(dog, new DeleteListener());
        }
    }

    private class DeleteListener implements Model.DogsDeleteListener {
        @Override
        public void onDogDeleted() {
            listener.onDelete();
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
