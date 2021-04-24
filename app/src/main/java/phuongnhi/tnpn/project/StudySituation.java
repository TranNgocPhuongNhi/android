package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudySituation extends AppCompatActivity {

    ImageView btnBack;

    DatabaseReference myRef;
    RecyclerView recyclerView;
    ArrayList<Lop> data;
    ClassAdapter adapter;
    String takeID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        Intent intent = getIntent();
        takeID = intent.getStringExtra("idUser");
        btnBack = (ImageView) findViewById(R.id.imageView4);
        ImageView btnAdd = (ImageView) findViewById(R.id.imageAdd);
        btnAdd.setVisibility(View.INVISIBLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudySituation.this, StudentHome.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.listClassroom);
        data = new ArrayList<Lop>();
        adapter = new ClassAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(StudySituation.this));

        myRef = FirebaseDatabase.getInstance().getReference("Class");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.removeAll(data);
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Map map =(Map)dataSnapshot.getValue();
                    try{
                        Map newMap =(Map) map.get("list_student");
                        if(newMap.get(takeID)!= null) {

                            Lop lop = new Lop(String.valueOf(map.get("ID_user")), String.valueOf(map.get("classID")),String.valueOf(map.get("lessonName")),Integer.parseInt(String.valueOf(map.get("count"))));
                            data.add(lop);
                        }
                    }catch (Exception e){}
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
            Lop lop = data.get(position);
            holder.objectName.setText(lop.getLessonName());
            holder.numOfPeople.setText("Số ca : "+Integer.toString(lop.getCount()));
            holder.classID.setText("Mã lớp: "+lop.getClassID());

//            holder.itemObject.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(StudySituation.this,ListStudent.class);
//                    intent.putExtra("classID", lop.getClassID());
//                    intent.putExtra("className",lop.getLessonName());
//                    intent.putExtra("classCount",Integer.toString(lop.getCount()));
//                    startActivityForResult(intent, 1);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return data.size();
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