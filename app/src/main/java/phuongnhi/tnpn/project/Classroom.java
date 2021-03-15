package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Classroom extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView btnBack, btnAdd;
    classroomAdapter adapter;
    ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        getData();
        btnBack = (ImageView) findViewById(R.id.imageView4);
        btnAdd = (ImageView) findViewById(R.id.imageAdd);
        recyclerView = (RecyclerView) findViewById(R.id.listClassroom);

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
                    }
                });
                alertDialog.show();
            }
        });

        adapter = new classroomAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getData() {
        data = new ArrayList<>();
        for(int i=1; i<=50; i++) {
            data.add("Item " + i);
        }
    }

    private class classroomAdapter extends RecyclerView.Adapter<myClassroomViewHoler> {
        @NonNull
        @Override
        public myClassroomViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(Classroom.this);
            View itemView = inflater.inflate(R.layout.listview_classroom, parent, false);
            return new myClassroomViewHoler(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull myClassroomViewHoler holder, int position) {
            holder.objectName.setText(data.get(position));
            holder.numOfPeople.setText(data.get(position));
            holder.objectName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Classroom.this, ListStudent.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class myClassroomViewHoler extends RecyclerView.ViewHolder {
        TextView objectName, numOfPeople;
        public myClassroomViewHoler(View itemView) {
            super(itemView);
            objectName = itemView.findViewById(R.id.objectName);
            numOfPeople = itemView.findViewById(R.id.numberOfPeople);
        }
    }
}