package com.example.tomer.dogsproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomer.dogsproject.databinding.AddDogFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Tomer on 26/05/2018.
 */

public class AddDogFragment extends Fragment {

    private AddDogFragmentBinding binding;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri selectedImage;

    public static final AddDogFragment newInstance() {
        AddDogFragment fragment = new AddDogFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_dog_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.exitIcon.setOnClickListener(new ExitListener());
        binding.background.setOnClickListener(new ExitListener());
        binding.mainLayout.setOnClickListener(null);
        binding.Image.setOnClickListener(new OpenCameraListener());
        binding.next.setOnClickListener(new ContinueListener());
    }

    private class ExitListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private class OpenCameraListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent takePictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE &&
                resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            binding.Image.setImageURI(selectedImage);
        }
    }

    private class ContinueListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            validationCheck();
        }
    }

    private void validationCheck() {
        String name = binding.NameEdit.getText().toString();
        String age = binding.ageEdit.getText().toString();
        String city = binding.cityEdit.getText().toString();
        String phone = binding.phoneEdit.getText().toString();

        if (name.isEmpty()) {
            binding.errorName.setVisibility(View.VISIBLE);
            return;
        }
        if (age.isEmpty()) {
            binding.errorAge.setVisibility(View.VISIBLE);
            return;
        }
        if (city.isEmpty()) {
            binding.errorCity.setVisibility(View.VISIBLE);
            return;
        }
        if (phone.isEmpty()) {
            binding.errorPhone.setVisibility(View.VISIBLE);
            return;
        }
        if (selectedImage == null) {
            binding.errorPic.setVisibility(View.VISIBLE);
            return;
        }
        Dog dog = new Dog(FirebaseAuth.getInstance().getCurrentUser().getUid(), name, age, city, phone,
                Calendar.getInstance().getTime().getTime());
        Model.instance.AddNewDog(dog, selectedImage);
        Loading.startLoading(getContext());
    }
}

