//package com.example.tomer.dogsproject.Hemi;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.MediaController;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.VideoView;
//
//import com.bumptech.glide.Glide;
//import com.firebase.ui.auth.AuthUI;
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.firebase.auth.FirebaseAuth;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.Arrays;
//
//public class ChatActivity extends AppCompatActivity {
//
//    private static final int RC_SIGN_IN = 1;
//    private static final int REQUEST_IMAGE_CAPTURE = 333;
//    private static final int REQUEST_WRITE_STORAGE = 444;
//    private static final int GET_FROM_GALLERY = 555;
//    MessageAdapter adapter;
//    RecyclerView lstMessages;
//    ImageView imgCapture;
//    Bitmap imageBitmap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
//
//        adapter = new MessageAdapter(Model.instance.getAllMessages());
//
//        lstMessages = (RecyclerView) findViewById(R.id.lstMessages);
//        lstMessages.setLayoutManager(new LinearLayoutManager(this));
//        lstMessages.setAdapter(adapter);
//
//
//        FloatingActionButton btnNew = (FloatingActionButton) findViewById(R.id.btnNew);
//        btnNew.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CustomDialog customDialog = new CustomDialog();
//                customDialog.show(getSupportFragmentManager(), "");
//            }
//        });
//
//        // TODO: abstract the direct access here to FirebaseAuth...
//        if (FirebaseAuth.getInstance().getCurrentUser() == null)
//        {
//            startActivityForResult(
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setAvailableProviders(Arrays.asList(
//                                    new AuthUI.IdpConfig.EmailBuilder().build(),
//                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
//                            .build(),
//                    RC_SIGN_IN);
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(MessageEvent event) {
//        Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK)
//            adapter.notifyDataSetChanged();
//
//    }
//
//
//
//    class MessageHolder extends RecyclerView.ViewHolder{
//
//        public TextView tvSender;
//        public TextView tvMessage;
//        public ImageView imgMessage;
//        public VideoView vidPlayer;
//
//        public MessageHolder(View itemView) {
//            super(itemView);
//            tvSender = (TextView) itemView.findViewById(R.id.tvSender);
//            tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
//            imgMessage = (ImageView) itemView.findViewById(R.id.imgMessage);
//            vidPlayer = (VideoView) itemView.findViewById(R.id.vidPlayer);
//        }
//    }
//
//    class MessageAdapter extends FirebaseRecyclerAdapter<MyMessage, MessageHolder> {
//
//        public MessageAdapter(@NonNull FirebaseRecyclerOptions<MyMessage> options) {
//            super(options);
//        }
//
//        @Override
//        protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull MyMessage model) {
//            holder.tvSender.setText(model.getSender());
//            holder.tvMessage.setText(model.getMessage());
//            if (!model.getImage().isEmpty()) {
//                if (model.getImage().contains("mp4")){
//                    holder.imgMessage.setVisibility(View.GONE);
//                    holder.vidPlayer.setVideoURI(Uri.parse(model.getImage()));
//                    holder.vidPlayer.setMediaController(new MediaController(ChatActivity.this));
//                    holder.vidPlayer.requestFocus();
//                    holder.vidPlayer.setVisibility(View.VISIBLE);
//                    holder.vidPlayer.start();
//                }
//                else {
//                    Glide.with(ChatActivity.this)
//                            .load(model.getImage())
//                            .into(holder.imgMessage);
//                }
//            }
//        }
//
//        @NonNull
//        @Override
//        public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
//            return new MessageHolder(v);
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//        EventBus.getDefault().unregister(this);
//    }
//}
