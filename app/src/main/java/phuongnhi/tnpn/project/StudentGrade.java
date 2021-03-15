package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class StudentGrade extends AppCompatActivity {

    ImageView btnBack, btnOption;
    TextView gradeOfTen, gradeOfTwenty, gradeOfMidterm, gradeOfFinalExam, totalGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_grade);

        gradeOfTen = (TextView) findViewById(R.id.gradeOfTen);
        gradeOfTwenty = (TextView) findViewById(R.id.gradeOfTwenty);
        gradeOfMidterm = (TextView) findViewById(R.id.gradeOfMidterm);
        gradeOfFinalExam = (TextView) findViewById(R.id.gradeOfFinalExam);
        totalGrade = (TextView) findViewById(R.id.totalGrade);
        btnBack = (ImageView) findViewById(R.id.imageView7);
        btnOption = (ImageView) findViewById(R.id.imageView8);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentGrade.this, ListStudent.class);
                startActivity(intent);
            }
        });

        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(StudentGrade.this, btnOption);
                popupMenu.getMenuInflater().inflate(R.menu.grade_option_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editGrade:
                                AlertDialog.Builder alert = new AlertDialog.Builder(StudentGrade.this);
                                View mView = getLayoutInflater().inflate(R.layout.edit_grade_student, null);

                                EditText editTenPercent = (EditText) mView.findViewById(R.id.editTenPercent);
                                EditText editTwentyPercent = (EditText) mView.findViewById(R.id.editTwentyPercent);
                                EditText editMidterm = (EditText) mView.findViewById(R.id.editMidterm);
                                EditText editFinalExam = (EditText) mView.findViewById(R.id.editFinalExam);
                                Button btnCancel = (Button) mView.findViewById(R.id.button);
                                Button btnSave = (Button) mView.findViewById(R.id.button2);

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
                                        Toast.makeText(StudentGrade.this, "Save", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                alertDialog.show();

//                                Toast.makeText(StudentGrade.this, "Edit grade", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.deleteAllGrade:
                                gradeOfTen.setText("");
                                gradeOfTwenty.setText("");
                                gradeOfMidterm.setText("");
                                gradeOfFinalExam.setText("");
                                totalGrade.setText("");
                                totalGrade.setBackground(null);
                                Toast.makeText(StudentGrade.this, "Delete all grade", Toast.LENGTH_SHORT).show();
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