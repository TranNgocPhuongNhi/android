package phuongnhi.tnpn.project;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Map;

import static phuongnhi.tnpn.project.TeacherHome.getDayNow;

public class ListStudent extends AppCompatActivity {

    ImageView btnBack, btnAdd;
    FirebaseUser user;
    ImageView img;
    RecyclerView recyclerView;
    TextView tenMH, diemDanh, siso, qrCode;
    studentAdapter adapter;
    ArrayList<MyStudent> data;

    String userID, takeID;
    DatabaseReference myRef, myRefUser, myRefAttendance;

    int SELECT_PHOTO = 1;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_student);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID  = user.getUid();

        data = new ArrayList<MyStudent>();

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        btnBack = findViewById(R.id.imageView5);
        btnAdd = findViewById(R.id.imageView6);
        diemDanh = findViewById(R.id.diemDanh);
        qrCode = findViewById(R.id.qrCode);
        tenMH = findViewById(R.id.tenMonHoc);
        siso = findViewById(R.id.siSo);

        recyclerView = findViewById(R.id.listUser);
        adapter = new studentAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListStudent.this));

        // l???y d??? li???u t??? Classroom
        Intent intent = getIntent();
        takeID = intent.getStringExtra("classID");
        tenMH.setText(intent.getStringExtra("className"));

        myRefAttendance = FirebaseDatabase.getInstance().getReference("Attendance").child(takeID);
        myRef = FirebaseDatabase.getInstance().getReference("Class").child(takeID);
        myRefUser = FirebaseDatabase.getInstance().getReference("Users");

        myRef.child("list_student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siso.setText(snapshot.getChildrenCount()+"");
                data.removeAll(data);
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map map = (Map) dataSnapshot.getValue();
                    MyStudent myStudent = new MyStudent(String.valueOf(map.get("fullname")), String.valueOf(map.get("idUser")));
                    data.add(myStudent);
                }

                if(adapter.getItemCount()==0){
                    diemDanh.setVisibility(View.INVISIBLE);
                }else diemDanh.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListStudent.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });

        diemDanh.setOnClickListener(v -> {
            Intent intent1 = new Intent(ListStudent.this, RollCall.class);
            intent1.putExtra("classID", takeID);
            startActivityForResult(intent1, 1);
        });
        btnBack.setOnClickListener(v -> finish());
        qrCode.setOnClickListener(v -> {


            AlertDialog.Builder alert = new AlertDialog.Builder(ListStudent.this);
            View mView = getLayoutInflater().inflate(R.layout.add_student, null);

            TextView textView = mView.findViewById(R.id.txtID);
            textView.setHint("enter QR code here");
            Button btnCancel = mView.findViewById(R.id.thoatThem);
            Button btnSave = mView.findViewById(R.id.luuThem);
            btnSave.setText("T???o code");
            EditText txtID = mView.findViewById(R.id.txtID);
            String tam = "0ma1qr2code3nay4rat5dai6de7khoi8bi9biet10"; //code nh???p s???n
            txtID.setText(tam);
            alert.setView(mView);
            AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            btnCancel.setOnClickListener(v1 -> {
                myRefAttendance.child("Date: " + getDayNow()).child("CodeNow").removeValue();
                alertDialog.dismiss();

                String title = "Th??ng b??o t??nh h??nh h???c t???p";
                String msg = "B???n c?? th??ng b??o m???i v??? l???p h???c";
                //send notification
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                        "/topics/all",
                        title,
                        msg,
                        getApplicationContext(),
                        ListStudent.this);

                notificationsSender.SendNotifications();
            });
            btnSave.setOnClickListener(v12 -> {
                for(MyStudent myStudent: data){
                    myRefAttendance.child("Date: " + getDayNow()).child(myStudent.getIdUser()).child(myStudent.getIdUser()).setValue("A");
                    myRefAttendance.child("Date: " + getDayNow()).child(myStudent.getIdUser()).child("fullName").setValue(myStudent.getFullName());
                }
                myRefAttendance.child("Date: " + getDayNow()).child("CodeNow").setValue(txtID.getText().toString());
            });
            alertDialog.show();
        });
        btnAdd.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(ListStudent.this);
            View mView = getLayoutInflater().inflate(R.layout.add_student, null);

            Button btnCancel = mView.findViewById(R.id.thoatThem);
            Button btnSave = mView.findViewById(R.id.luuThem);
            EditText txtID = mView.findViewById(R.id.txtID);

            alert.setView(mView);
            AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            btnCancel.setOnClickListener(v13 -> alertDialog.dismiss());
            btnSave.setOnClickListener(v14 -> {
                String ID_user = txtID.getText().toString();
                checkUser(ID_user);
                txtID.setText("");
            });
            alertDialog.show();
        });
    }

    private void checkUser(String ID_user) {
        myRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String message = "Sinh vi??n kh??ng t???n t???i!";
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if(!users.getId().equals(userID) && users.getAuthority().equals("Sinh vi??n")) {
                        if (users.getIdUser().equals(ID_user)) {
                            myRef.child("list_student").child(ID_user).child("idUser").setValue(ID_user);
                            myRef.child("list_student").child(ID_user).child("fullname").setValue(users.getFullname());
                            message = "Th??m th??nh c??ng";
                            break;
                        }
                    }
                }
                Toast.makeText(ListStudent.this, message, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListStudent.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private class studentAdapter extends RecyclerView.Adapter<myStudentViewHolder> {
        @NonNull
        @Override
        public myStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ListStudent.this);
            View itemView = inflater.inflate(R.layout.list_users, parent, false);
            return new myStudentViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull myStudentViewHolder holder, int position) {
            MyStudent myStudent = data.get(position);
            holder.nameStudent.setText(myStudent.getFullName());
            holder.nameStudent.setOnLongClickListener(v -> {
                new AlertDialog.Builder(ListStudent.this)
                    .setTitle("B???n c?? ch???c mu???n x??a h???c vi??n kh???i l???p?")
                    .setMessage(myStudent.getFullName()+"\nID: " + myStudent.getIdUser())
                    .setPositiveButton("Yes", (dialog, which) -> {
                        myRef.child("list_student").child(myStudent.getIdUser()).removeValue();
                        myRefAttendance.child("Date: " + getDayNow()).child(myStudent.getIdUser()).removeValue();
                        data.remove(position);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
                return false;
            });
        }
        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class myStudentViewHolder extends RecyclerView.ViewHolder {
        TextView nameStudent, btnOptions;
        ImageView img;
        public myStudentViewHolder(View itemView) {
            super(itemView);
            nameStudent = itemView.findViewById(R.id.nameSV);
//            img = itemView.findViewById(R.id.imgSV);
//            btnOptions = itemView.findViewById(R.id.textViewOptions);
        }
    }

    public static class MyStudent {
        String fullName;
        String idUser;
        public MyStudent() {
        }
        public MyStudent(String fullName, String idUser) {
            this.fullName = fullName;
            this.idUser = idUser;
        }
        public String getFullName() {
            return fullName;
        }
        public String getIdUser() {
            return idUser;
        }
    }
}