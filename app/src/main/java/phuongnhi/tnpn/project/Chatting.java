package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chatting extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView imgUser, btnBack;
    TextView name;
    ImageButton btnSend;
    EditText textSend;
    DatabaseReference reference;
    FirebaseUser user;
    String userID;
    List<Chat> data;
    MessageAdapter adapter;
    int MSG_TYPE_LEFT = 0;
    int MSG_TYPE_RIGHT = 1;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        AnhXa();

        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = snapshot.getValue(Users.class);
                if(users != null) {
                    name.setText(users.getFullname());
                    Picasso.with(Chatting.this).load(users.getImage()).into(imgUser);
                    readMessage(user.getUid(), userId, users.getImage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Chatting.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = textSend.getText().toString();
                if(!msg.equals("")) {
                    sendMessage( user.getUid(),userId, msg);
                }
                else {
                    Toast.makeText(Chatting.this, "Hãy nhập tin nhắn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void AnhXa() {
        btnBack = (ImageView) findViewById(R.id.imageView6);
        imgUser = (ImageView) findViewById(R.id.imgUser);
        name = (TextView) findViewById(R.id.name);
        textSend = (EditText) findViewById(R.id.text_send);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        recyclerView = (RecyclerView) findViewById(R.id.chatFrame);
    }

    private void sendMessage(String sender, String receiver, String message) {
        reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        reference.child("Chats").push().setValue(hashMap);
    }

    private class MessageAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == MSG_TYPE_RIGHT) {
                LayoutInflater inflater = LayoutInflater.from(Chatting.this);
                View itemView = inflater.inflate(R.layout.chat_item_right, parent, false);
                return new MyViewHolder(itemView);
            }
            else {
                LayoutInflater inflater = LayoutInflater.from(Chatting.this);
                View itemView = inflater.inflate(R.layout.chat_item_left, parent, false);
                return new MyViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Chat chat = data.get(position);
            holder.message.setText(chat.getMessage());
            Picasso.with(Chatting.this).load(users.getImage()).into(holder.profile_img);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(data.get(position).sender.equals(user.getUid())) {
                return MSG_TYPE_RIGHT;
            }
            else {
                return MSG_TYPE_LEFT;
            }
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        ImageView profile_img;
        public MyViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.receiverMsg);
            profile_img = itemView.findViewById(R.id.profile_img);
        }
    }

    private void readMessage(String myid, String userid, String imgURL ) {
        data = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSender().equals(myid) && chat.getReceiver().equals(userid) ||
                            chat.getSender().equals(userid) && chat.getReceiver().equals(myid)) {
                        data.add(chat);
                        textSend.setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Chatting.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}