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
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class RollCall extends AppCompatActivity {

    FirebaseUser user;
    public static String userID, datetime;
    DatabaseReference myRef, myRefAttendance;

    ImageView btnBack, btnAdd;
    RecyclerView recyclerView;
    studentAdapter adapter;

    ArrayList<ListStudent.MyStudent> data ;
    ArrayList<Boolean> status;

    TextView txt;
    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_student);

        //Get time now
        txt = findViewById(R.id.tenMonHoc);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        datetime = dateFormat.format(calendar.getTime());
        txt.setText(datetime);

        btnBack = (ImageView) findViewById(R.id.imageView5);

        //dùng lại layout cũ nên ẩn cái này
        linearLayout =findViewById(R.id.linearlayout);
        linearLayout.setVisibility(View.INVISIBLE);
        btnAdd = findViewById(R.id.imageView6);
        btnAdd.setVisibility(View.INVISIBLE);


        Intent intent = getIntent();
        String takeID = intent.getStringExtra("classID");

        myRefAttendance = FirebaseDatabase.getInstance().getReference("Attendance").child(takeID);
        myRef = FirebaseDatabase.getInstance().getReference("Class").child(takeID);

        btnBack.setOnClickListener(v -> {

            String title = "Thông báo tình hình học tập";
            String msg = "Bạn có thông báo mới về lớp học";
            //send notification
            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                    "/topics/all",
                    title,
                    msg,
                    getApplicationContext(),
                    RollCall.this);

            notificationsSender.SendNotifications();

            finish();
        });



        recyclerView = (RecyclerView) findViewById(R.id.listUser);
        adapter = new studentAdapter();
        data = new ArrayList<ListStudent.MyStudent>();
        status = new ArrayList<Boolean>();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(RollCall.this));

        //lấy dữ liệu từ class
        myRef.child("list_student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.removeAll(data);
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map map = (Map) dataSnapshot.getValue();
                    ListStudent.MyStudent myStudent = new ListStudent.MyStudent(String.valueOf(map.get("fullname")), String.valueOf(map.get("idUser")));
                    data.add(myStudent);
                    status.add(false);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RollCall.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class studentAdapter extends RecyclerView.Adapter<myStudentViewHolder> {
        @NonNull
        @Override
        public myStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(RollCall.this);
            View itemView = inflater.inflate(android.R.layout.simple_list_item_checked, parent, false);
            return new myStudentViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull myStudentViewHolder holder, int position) {
            ListStudent.MyStudent myStudent = data.get(position);
            holder.nameStudent.setText(myStudent.getFullName()+"\nID: "+myStudent.getIdUser());
            holder.nameStudent.setChecked(status.get(position));

            holder.nameStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status.set(position, !status.get(position));
                    adapter.notifyDataSetChanged();
                }
            });
            String check="A";
            if (status.get(position)) {
                check = "P";
            }
            myRefAttendance.child("Date: " + datetime).child(myStudent.getIdUser()).child(myStudent.getIdUser()).setValue(check);
            myRefAttendance.child("Date: " + datetime).child(myStudent.getIdUser()).child("fullName").setValue(myStudent.getFullName());

        }
        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class myStudentViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView nameStudent;
        ImageView img;
        public myStudentViewHolder(View itemView) {
            super(itemView);
            nameStudent = itemView.findViewById(android.R.id.text1);
        }
    }

}