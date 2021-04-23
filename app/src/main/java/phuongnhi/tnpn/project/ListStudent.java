package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ListStudent extends AppCompatActivity {

    ImageView btnBack, btnAdd;
    FirebaseUser user;
    ImageView img;
    RecyclerView recyclerView;
    TextView tenMH, diemDanh, siso;
    studentAdapter adapter;
    ArrayList<MyStudent> data;

    String userID, takeID;
    DatabaseReference myRef, myRefUser;

    int SELECT_PHOTO = 1;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_student);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID  = user.getUid();

        data = new ArrayList<MyStudent>();
//        getData();
        btnBack = (ImageView) findViewById(R.id.imageView5);
        btnAdd = (ImageView) findViewById(R.id.imageView6);
        diemDanh = (TextView) findViewById(R.id.diemDanh);
        tenMH = findViewById(R.id.tenMonHoc);
        siso = findViewById(R.id.siSo);

        recyclerView = (RecyclerView) findViewById(R.id.listStudent);
        adapter = new studentAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListStudent.this));

        // lấy dữ liệu từ Classroom
        Intent intent = getIntent();
        takeID = intent.getStringExtra("classID");
        tenMH.setText(intent.getStringExtra("className"));
//        siso.setText(intent.getStringExtra("classCount"));

        myRef = FirebaseDatabase.getInstance().getReference("Class").child(takeID);
        myRefUser = FirebaseDatabase.getInstance().getReference("Users");

        myRef.child("list_student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                siso.setText(snapshot.getChildrenCount()+"");
                data.removeAll(data);
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                    MyStudent myStudent = dataSnapshot.getValue(MyStudent.class);
                    Map map = (Map) dataSnapshot.getValue();
//                    data.add(map.get("fullname").toString());
//                    data.add(myStudent.getFullname());
                    MyStudent myStudent = new MyStudent(String.valueOf(map.get("fullname")), String.valueOf(map.get("idUser")));
//                    String key = String.valueOf(map.get("fullname"));
                    data.add(myStudent);
                }
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ListStudent.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListStudent.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });

        diemDanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListStudent.this, RollCall.class);
                intent.putExtra("classID", takeID);
                startActivityForResult(intent, 1);
//                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListStudent.this, Classroom.class);
                startActivity(intent);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ListStudent.this);
                View mView = getLayoutInflater().inflate(R.layout.add_student, null);

                Button btnCancel = (Button) mView.findViewById(R.id.thoatThem);
                Button btnSave = (Button) mView.findViewById(R.id.luuThem);
                EditText txtID = mView.findViewById(R.id.txtID);

                alert.setView(mView);
                AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ID_user = txtID.getText().toString();
                        checkUser(ID_user);
                        txtID.setText("");
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void checkUser(String ID_user) {
        myRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if(!users.getId().equals(userID) && users.getAuthority().equals("Sinh viên")) {
                        if (users.getIdUser().equals(ID_user)) {
                            myRef.child("list_student").child(ID_user).child("idUser").setValue(ID_user);
                            myRef.child("list_student").child(ID_user).child("fullname").setValue(users.getFullname());
                            Toast.makeText(ListStudent.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
//                Toast.makeText(ListStudent.this, "Không có học sinh này", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListStudent.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == SELECT_PHOTO && requestCode == RESULT_OK && data != null && data.getData() != null) {
//            uri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                img.setImageBitmap(bitmap);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private void getData() {
//        data = new ArrayList<>();
//        for(int i=1; i<=10; i++) {
//            MyStudent myStudent = new MyStudent();
//            data.add(myStudent);
//        }
//    }
    private class studentAdapter extends RecyclerView.Adapter<myStudentViewHolder> {
        @NonNull
        @Override
        public myStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ListStudent.this);
            View itemView = inflater.inflate(R.layout.list_student, parent, false);
            return new myStudentViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull myStudentViewHolder holder, int position) {
            MyStudent myStudent = data.get(position);
            holder.nameStudent.setText(myStudent.getFullName());

            holder.btnOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(ListStudent.this, holder.btnOptions);
                    popupMenu.getMenuInflater().inflate(R.menu.options_menu_student, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()) {
                                case R.id.deleteOp:
                                    new AlertDialog.Builder(ListStudent.this)
                                        .setTitle("Bạn có chắc muốn xóa học viên khỏi lớp?")
                                        .setMessage(myStudent.getFullName()+"\nID: " + myStudent.getIdUser())
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                myRef.child("list_student").child(myStudent.getIdUser()).removeValue();
                                                data.remove(position);
                                                adapter.notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
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
            img = itemView.findViewById(R.id.imgSV);
            btnOptions = itemView.findViewById(R.id.textViewOptions);
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