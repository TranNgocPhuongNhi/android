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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListStudent extends AppCompatActivity {

    ImageView btnBack, btnAdd;
    ImageView img;
    RecyclerView recyclerView;
    TextView diemDanh;
    studentAdapter adapter;
    ArrayList<String> data;

    int SELECT_PHOTO = 1;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_student);

        getData();
        btnBack = (ImageView) findViewById(R.id.imageView5);
        btnAdd = (ImageView) findViewById(R.id.imageView6);
        diemDanh = (TextView) findViewById(R.id.diemDanh);
        recyclerView = (RecyclerView) findViewById(R.id.listStudent);
        adapter = new studentAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        diemDanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListStudent.this, RollCall.class);
                startActivity(intent);
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

                img = (ImageView) mView.findViewById(R.id.img);
                Button btnCancel = (Button) mView.findViewById(R.id.thoatThem);
                Button btnSave = (Button) mView.findViewById(R.id.luuThem);

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
                        Toast.makeText(ListStudent.this, "Save", Toast.LENGTH_SHORT).show();
                    }
                });

//                img.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent gallery = new Intent(Intent.ACTION_PICK);
//                        gallery.setType("image/*");
//                        startActivityForResult(gallery, SELECT_PHOTO);
//                    }
//                });

                alertDialog.show();
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

    private void getData() {
        data = new ArrayList<>();
        for(int i=1; i<=50; i++) {
            data.add("Item " + i);
        }
    }

    private class studentAdapter extends RecyclerView.Adapter<myStudentViewHoler> {
        @NonNull
        @Override
        public myStudentViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ListStudent.this);
            View itemView = inflater.inflate(R.layout.list_student, parent, false);
            return new myStudentViewHoler(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull myStudentViewHoler holder, int position) {
            holder.nameStudent.setText(data.get(position));
            holder.btnOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(ListStudent.this, holder.btnOptions);
                    popupMenu.getMenuInflater().inflate(R.menu.options_menu_student, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()) {
                                case R.id.editStudent:
                                    AlertDialog.Builder alert = new AlertDialog.Builder(ListStudent.this);
                                    View mView = getLayoutInflater().inflate(R.layout.edit_information_student, null);

//                                    EditText editTenPercent = (EditText) mView.findViewById(R.id.editTenPercent);
//                                    EditText editTwentyPercent = (EditText) mView.findViewById(R.id.editTwentyPercent);
//                                    EditText editMidterm = (EditText) mView.findViewById(R.id.editMidterm);
//                                    EditText editFinalExam = (EditText) mView.findViewById(R.id.editFinalExam);
                                    Button btnCancel = (Button) mView.findViewById(R.id.btnCancel);
                                    Button btnSave = (Button) mView.findViewById(R.id.btnSave);

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
                                            Toast.makeText(ListStudent.this, "Save", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    alertDialog.show();
//                                    Toast.makeText(ListStudent.this, "Edit Student", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.deleteStudent:
                                    new AlertDialog.Builder(ListStudent.this)
                                            .setTitle("Bạn có chắc muốn xóa " + data.get(position) + "?")
                                            .setMessage("Xóa " + data.get(position))
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    data.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                    break;
                                case R.id.scoreStudent:
                                    Intent intent = new Intent(ListStudent.this, StudentGrade.class);
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

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class myStudentViewHoler extends RecyclerView.ViewHolder {
        TextView nameStudent, btnOptions;
        ImageView img;
        public myStudentViewHoler(View itemView) {
            super(itemView);
            nameStudent = itemView.findViewById(R.id.nameSV);
            img = itemView.findViewById(R.id.imgSV);
            btnOptions = itemView.findViewById(R.id.textViewOptions);
        }
    }
}