package phuongnhi.tnpn.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class TeacherHome extends AppCompatActivity {

    CardView classroom;
    ImageView optionMenu;
    TextView nameofTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        optionMenu = (ImageView) findViewById(R.id.optionMenu);
        classroom = (CardView) findViewById(R.id.classroom);
        nameofTeacher = (TextView) findViewById(R.id.nameofTeacher);

        Intent intent = getIntent();
        String tenGV = intent.getStringExtra("tenGV");
        nameofTeacher.setText(tenGV);

        classroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherHome.this, Classroom.class);
                startActivity(intent);
            }
        });

        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(TeacherHome.this, optionMenu);
                popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sua:
                                AlertDialog.Builder alert = new AlertDialog.Builder(TeacherHome.this);
                                View mView = getLayoutInflater().inflate(R.layout.edit_info_teacher, null);

                                Button btnCancel = (Button) mView.findViewById(R.id.thoatSua);
                                Button btnSave = (Button) mView.findViewById(R.id.luuSua);

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
                                        Toast.makeText(TeacherHome.this, "Save", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                alertDialog.show();
                                break;
                            case R.id.logout:
                                Intent intent = new Intent(TeacherHome.this, Login.class);
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

}