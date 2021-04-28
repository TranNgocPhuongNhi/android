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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudySituation extends AppCompatActivity {

    ImageView btnBack;

    DatabaseReference myRef, myRefAttendance;
    RecyclerView recyclerView;
    ArrayList<Lop> data;
    List<String> date= new ArrayList<String>();
    ClassAdapter adapter;
    String takeID,takeName,datetime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        datetime = "Date: "+dateFormat.format(calendar.getTime());

        Intent intent = getIntent();
        takeID = intent.getStringExtra("idUser");
        takeName = intent.getStringExtra("fullName");
        btnBack = (ImageView) findViewById(R.id.imageView4);
        ImageView btnAdd = (ImageView) findViewById(R.id.imageAdd);
        btnAdd.setVisibility(View.INVISIBLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.listClassroom);
        data = new ArrayList<Lop>();
        adapter = new ClassAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(StudySituation.this));

        myRefAttendance = FirebaseDatabase.getInstance().getReference("Attendance");
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
            date.removeAll(date);
            holder.itemObject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(StudySituation.this);
                    View mView = getLayoutInflater().inflate(R.layout.activity_study_situation, null);

                    TextView name = mView.findViewById(R.id.studentName);
                    TextView numberAbsent = mView.findViewById(R.id.numberAbsent);
                    TextView numberAbsented = mView.findViewById(R.id.numberAbsented);
                    TextView note = mView.findViewById(R.id.note);
                    String tam = Integer.toString(lop.getCount()*20/100);

                    name.setText(takeName);
                    numberAbsent.setText(tam);
                    myRefAttendance.child(lop.getClassID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            date.removeAll(date);
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                    if(dataSnapshot1.getKey().equals(takeID)) {
                                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                            if(dataSnapshot2.getKey().equals(takeID) && dataSnapshot2.getValue().equals("A")){
                                                date.add(dataSnapshot.getKey());
                                                numberAbsented.setText(String.valueOf(date.size()));
                                                if (date.size() > lop.getCount() * 20 / 100) {
                                                    note.setVisibility(View.VISIBLE);
                                                } else note.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    alert.setView(mView);
                    AlertDialog alertDialog = alert.create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();
                }
            });
            holder.itemObject.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    myRefAttendance.child(lop.getClassID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            date.removeAll(date);
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                for(DataSnapshot dataSnapshotValue:dataSnapshot.getChildren())
                                    if(dataSnapshotValue.getKey().equals("CodeNow")){
                                        Intent intent = new Intent(StudySituation.this, QRScan.class);
                                        intent.putExtra("takeClass",lop.getClassID());
                                        intent.putExtra("takeID",takeID);
                                        intent.putExtra("name", takeName);
                                        intent.putExtra("date", datetime);
                                        startActivity(intent);
                                    }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView objectName, numOfPeople, classID ;
        View itemObject;
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            objectName = itemView.findViewById(R.id.objectName);
            numOfPeople = itemView.findViewById(R.id.objectSize);
            classID = itemView.findViewById(R.id.objectID);
            itemObject = itemView.findViewById(R.id.itemObject);
            itemView.findViewById(R.id.opMenu).setVisibility(View.INVISIBLE);
        }
    }

}