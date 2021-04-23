package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
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
import java.util.HashMap;
import java.util.Map;

public class Classroom extends AppCompatActivity {

    DatabaseReference myRef;
    FirebaseUser user;
    RecyclerView recyclerView;
    ImageView btnBack, btnAdd;
    ClassAdapter adapter;
    ArrayList<Lop> arrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        btnBack = (ImageView) findViewById(R.id.imageView4);
        btnAdd = (ImageView) findViewById(R.id.imageAdd);

        recyclerView = findViewById(R.id.listClassroom);
        arrayList = new ArrayList<>();
        adapter = new ClassAdapter();

        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("Class");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.removeAll(arrayList);
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Lop lop = dataSnapshot.getValue(Lop.class);
                    if(user.getUid().equals(lop.getID_user())) {    // Nếu id_user trong class bằng với id đang đăng nhập thì mới lấy
                        arrayList.add(lop);
                    }
                }
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(Classroom.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Classroom.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Classroom.this, TeacherHome.class);
                startActivity(intent);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Classroom.this);
                View mView = getLayoutInflater().inflate(R.layout.add_class, null);

                EditText txt1 = mView.findViewById(R.id.className);
                EditText txt2 = mView.findViewById(R.id.classSize);
                EditText txt3 = mView.findViewById(R.id.classID);

                Button btnCancel = (Button) mView.findViewById(R.id.button3);
                Button btnSave = (Button) mView.findViewById(R.id.button4);

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
//                        String codeC = txt3.getText().toString();
                        String ID_user = user.getUid();
                        String classID = txt3.getText().toString();
                        int count = Integer.parseInt(txt2.getText().toString());
                        String lessonName = txt1.getText().toString();
                        // Thêm lớp mới vào
                        Lop lop = new Lop(ID_user, classID, lessonName, count);
                        myRef.child(classID).setValue(lop);

                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }

    private class ClassAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_classroom,parent,false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Lop lop = arrayList.get(position);
            holder.objectName.setText(lop.getLessonName());
            holder.numOfPeople.setText("Số ca : "+Integer.toString(lop.getCount()));
            holder.classID.setText("Mã lớp: "+lop.getClassID());

            holder.itemObject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Classroom.this,ListStudent.class);
                    intent.putExtra("classID", lop.getClassID());
                    intent.putExtra("className",lop.getLessonName());
                    intent.putExtra("classCount",Integer.toString(lop.getCount()));
                    startActivityForResult(intent, 1);
//                    startActivity(intent);
                }
            });
            holder.itemObject.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(Classroom.this, holder.itemObject);
                    popupMenu.getMenuInflater().inflate(R.menu.options_menu_student, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()) {
                                case  R.id.editOp:
                                    AlertDialog.Builder alert = new AlertDialog.Builder(Classroom.this);
                                    View mView = getLayoutInflater().inflate(R.layout.add_class, null);

                                    EditText txt1 = mView.findViewById(R.id.className);
                                    EditText txt2 = mView.findViewById(R.id.classSize);
                                    EditText txt3 = mView.findViewById(R.id.classID);

                                    txt3.setEnabled(false);
                                    txt3.setTextColor(Color.GRAY);
                                    txt3.setGravity(Gravity.CENTER);
                                    txt1.setText(lop.getLessonName());
                                    txt2.setText(Integer.toString(lop.getCount()));
                                    txt3.setText(lop.getClassID());
                                    Button btnCancel = (Button) mView.findViewById(R.id.button3);
                                    Button btnSave = (Button) mView.findViewById(R.id.button4);

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
                                            Map<String, Object> updateClass = new HashMap<String, Object>();
                                            updateClass.put("lessonName", txt1.getText().toString());
                                            updateClass.put("classID", txt3.getText().toString());
                                            updateClass.put("count", Integer.parseInt(txt2.getText().toString()));

                                            myRef.child(lop.getClassID()).updateChildren(updateClass);
                                            alertDialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                    break;
                                case R.id.deleteOp:
                                    new AlertDialog.Builder(Classroom.this)
                                            .setTitle("Bạn có chắc muốn xóa lớp học này?")
                                            .setMessage(lop.getLessonName()+"\nID: " + lop.getClassID())
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    myRef.child(lop.getClassID()).removeValue();
                                                    arrayList.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .setNegativeButton("No", null).show();
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView objectName, numOfPeople, classID ;
        View itemObject;
        public MyViewHolder(View itemView) {
            super(itemView);
            objectName = itemView.findViewById(R.id.objectName);
            numOfPeople = itemView.findViewById(R.id.objectSize);
            classID = itemView.findViewById(R.id.objectID);
            itemObject = itemView.findViewById(R.id.itemObject);
        }
    }
}