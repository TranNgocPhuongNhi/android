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

public class StudentHome extends AppCompatActivity {

    CardView notification, statusLearn;
    ImageView optionsMenu;
    TextView nameofStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        optionsMenu = (ImageView) findViewById(R.id.optionsMenu);
        notification = (CardView) findViewById(R.id.notification);
        statusLearn = (CardView) findViewById(R.id.statusLearn);
        nameofStudent = (TextView) findViewById(R.id.nameofStudent);

        Intent intent = getIntent();
        String tenSV = intent.getStringExtra("tenSV");
        nameofStudent.setText(tenSV);

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentHome.this, Notification.class);
                startActivity(intent);
            }
        });

        statusLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentHome.this, StudySituation.class);
                startActivity(intent);
            }
        });

        optionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(StudentHome.this, optionsMenu);
                popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sua:
                                AlertDialog.Builder alert = new AlertDialog.Builder(StudentHome.this);
                                View mView = getLayoutInflater().inflate(R.layout.edit_info_student, null);

                                Button btnCancel = (Button) mView.findViewById(R.id.thoatSuaSV);
                                Button btnSave = (Button) mView.findViewById(R.id.luuSuaSV);

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
                                        Toast.makeText(StudentHome.this, "Save", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                alertDialog.show();
                                break;
                            case R.id.logout:
                                Intent intent = new Intent(StudentHome.this, Login.class);
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