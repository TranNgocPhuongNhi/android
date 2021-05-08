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

public class Classroom extends AppCompatActivity {

    DatabaseReference myRef;
    RecyclerView recyclerView;
    ImageView btnBack, btnAdd;
    ClassAdapter adapter = new ClassAdapter();
    ArrayList<Lop> arrayList = new ArrayList<>();
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        CreateLink();
        TeacherHome.getDayNow();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Classroom.this));

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        myRef = FirebaseDatabase.getInstance().getReference("Class");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.removeAll(arrayList);
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                    Lop lop = dataSnapshot.getValue(Lop.class);
                    Map map = (Map)dataSnapshot.getValue();
                    Lop lop = new Lop(map.get("id_user").toString(),map.get("classID").toString(),map.get("lessonName").toString(),Integer.parseInt(map.get("count").toString()));
                    if(userID.equals(lop.getID_user())) {
                        arrayList.add(lop);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Classroom.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        });
        btnBack.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(Classroom.this);
            View mView = getLayoutInflater().inflate(R.layout.add_class, null);

            EditText txt1 = mView.findViewById(R.id.className);
            EditText txt2 = mView.findViewById(R.id.classSize);
            EditText txt3 = mView.findViewById(R.id.classID);
            Button btnCancel = mView.findViewById(R.id.button3);
            Button btnSave = mView.findViewById(R.id.button4);

            alert.setView(mView);
            AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);

            btnCancel.setOnClickListener(v1 -> alertDialog.dismiss());
            btnSave.setOnClickListener(v2 -> {
                String classID = txt3.getText().toString();
                int count = Integer.parseInt(txt2.getText().toString());
                String lessonName = txt1.getText().toString();

                myRef.child(classID).setValue(new Lop(userID, classID, lessonName, count));
                alertDialog.dismiss();
            });
            alertDialog.show();
        });
    }

    private void CreateLink() {
        btnBack = findViewById(R.id.imageView4);
        btnAdd = findViewById(R.id.imageAdd);
        recyclerView = findViewById(R.id.listClassroom);
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
            holder.numOfPeople.setText("Số ca : "+ lop.getCount());
            holder.classID.setText("Mã lớp: "+lop.getClassID());

            holder.itemObject.setOnClickListener(v -> {
                Intent intent = new Intent(Classroom.this,ListStudent.class);
                intent.putExtra("classID", lop.getClassID());
                intent.putExtra("className",lop.getLessonName());
                intent.putExtra("classCount",Integer.toString(lop.getCount()));
                startActivityForResult(intent, 1);
            });
            holder.opMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(Classroom.this, holder.opMenu);
                    popupMenu.getMenuInflater().inflate(R.menu.options_menu_student, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()) {
                                case R.id.exportFile:
                                    Intent intent = new Intent(Classroom.this, exportFile.class);
                                    intent.putExtra("maxAbsent", ""+lop.getCount());
                                    intent.putExtra("classID",lop.getClassID());

                                    startActivity(intent);
                                    break;
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
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView objectName, numOfPeople, classID ;
        View itemObject;
        ImageView opMenu;
        public MyViewHolder(View itemView) {
            super(itemView);
            objectName = itemView.findViewById(R.id.objectName);
            numOfPeople = itemView.findViewById(R.id.objectSize);
            classID = itemView.findViewById(R.id.objectID);
            itemObject = itemView.findViewById(R.id.itemObject);
            opMenu = itemView.findViewById(R.id.opMenu);
        }
    }
}