//package com.example.tomer.dogsproject.Hemi;
//
//import android.net.Uri;
//import android.support.annotation.NonNull;
//
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//public class Model {
//
//    static Model instance = new Model();
//
//    private Model() {
//    }
//
//    public void addNewMessage(String message, String img) {
//        MyMessage m = new MyMessage(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), message, img);
//
//        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("MyMessages");
//        mRef.child(mRef.push().getKey()).setValue(m);
//    }
//
//    public FirebaseRecyclerOptions<MyMessage> getAllMessages() {
//
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//
//        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("MyMessages");
//
//        FirebaseRecyclerOptions<MyMessage> options = new FirebaseRecyclerOptions.Builder<MyMessage>()
//                .setQuery(mRef, MyMessage.class)
//                .build();
//
//        return options;
//    }
//
//    public void upload(Uri file, String name, final String msg){
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
//
//        StorageReference ref = storageRef.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child(name);
//
//        ref.putFile(file)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                        Model.instance.addNewMessage(msg, downloadUrl.toString());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                    }
//                });
//    }
//
//
//}
