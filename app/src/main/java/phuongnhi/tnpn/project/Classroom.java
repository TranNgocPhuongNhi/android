package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Classroom extends AppCompatActivity {

    DatabaseReference myRef;
    RecyclerView recyclerView;
    ImageView btnBack, btnAdd;
    Adapter adapter;
    ArrayList<lop> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

//        getData();

        btnBack = (ImageView) findViewById(R.id.imageView4);
        btnAdd = (ImageView) findViewById(R.id.imageAdd);

        recyclerView = findViewById(R.id.listClassroom);
        myRef = FirebaseDatabase.getInstance().getReference("Class");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Map map = (Map) ds.getValue();
                    lop sad = new lop(map.get("classID").toString(),map.get("lessonName").toString(),map.get("count").toString());
                    arrayList.add(sad);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Classroom.this));
                    adapter = new Adapter(arrayList,Classroom.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                        Toast.makeText(Classroom.this, "Save", Toast.LENGTH_SHORT).show();

                        String codeC = txt3.getText().toString();
                        myRef.child(codeC).child("ID_user").setValue(1043623);
                        myRef.child(codeC).child("classID").setValue(txt3.getText().toString());
                        myRef.child(codeC).child("count").setValue((txt2.getText().toString()));
                        myRef.child(codeC).child("lessonName").setValue(txt1.getText().toString());
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

    }

    public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder>{

        ArrayList<lop> list;
        Context context;

        public Adapter(ArrayList<lop> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_classroom,parent,false);
            return new MyHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.objectName.setText(list.get(position).getName());
            holder.numOfPeople.setText(list.get(position).getSize());
            holder.classID.setText(list.get(position).getID());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyHolder extends RecyclerView.ViewHolder{
            TextView objectName, numOfPeople, classID ;
            public  MyHolder(@NonNull View itemView){
                super(itemView);
                objectName = itemView.findViewById(R.id.objectName);
                numOfPeople = itemView.findViewById(R.id.objectSize);
                classID = itemView.findViewById(R.id.objectID);
            }
        }
    }
//    private class classroomAdapter extends RecyclerView.Adapter<myClassroomViewHolder> {
//        @NonNull
//        @Override
//        public myClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(Classroom.this);
//            View itemView = inflater.inflate(R.layout.listview_classroom, parent, false);
//            return new myClassroomViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull myClassroomViewHolder holder, int position) {
//            holder.objectName.setText(arrayList.get(position).getName());
//            holder.numOfPeople.setText(arrayList.get(position).getSize());
//            holder.classID.setText(arrayList.get(position).getID());
//
//            holder.objectName.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Classroom.this, ListStudent.class);
//                    startActivity(intent);
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return arrayList.size();
//        }
//    }
//
//    private class myClassroomViewHolder extends RecyclerView.ViewHolder {
//        TextView objectName, numOfPeople, classID ;
//        public myClassroomViewHolder(View itemView) {
//            super(itemView);
//            objectName = itemView.findViewById(R.id.objectName);
//            numOfPeople = itemView.findViewById(R.id.objectSize);
//            classID = itemView.findViewById(R.id.objectID);
//        }
//
//    }

    private class lop {
        String  name, ID,size;

        public lop() {
        }
        public lop(String ID, String name, String size) {
            this.ID = ID;
            this.name = name;
            this.size = size;
        }
        public String getName() {
            return name;
        }
        public String getID() {
            return ID;
        }
        public String getSize() {
            return size;
        }
    }
}