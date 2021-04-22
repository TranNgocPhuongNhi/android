package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

public class TeacherHome extends AppCompatActivity {

    CardView classroom, chatWithStudent;
    ImageView optionMenu, imgView, imgEdit;
    TextView nameofTeacher, btnEditImg;
    DatabaseReference reference;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;
    String userID;
    int REQUEST_CODE_IMAGE = 1;
    Uri imageUri;
    String myUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        AnhXa();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID  = user.getUid();

        classroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherHome.this, Classroom.class);
                startActivity(intent);
            }
        });

        chatWithStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherHome.this, TeacherChat.class);
                startActivity(intent);
            }
        });

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);
                if(userProfile != null) {
                    String fullname = userProfile.fullname;
                    String img = userProfile.image;
                    nameofTeacher.setText(fullname);
                    // Lấy hình gán vào dùng thư viện Picasso
                    Picasso.with(TeacherHome.this).load(img).into(imgView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeacherHome.this, "Error " + error, Toast.LENGTH_SHORT).show();
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
                                View mView = getLayoutInflater().inflate(R.layout.edit_info_user, null);

                                btnEditImg = (TextView) mView.findViewById(R.id.changeImage);
                                imgEdit = (ImageView) mView.findViewById(R.id.imgUser);
                                EditText nameGV = (EditText) mView.findViewById(R.id.tenUser);
                                Button btnCancel = (Button) mView.findViewById(R.id.thoatSuaUser);
                                Button btnSave = (Button) mView.findViewById(R.id.luuSuaUser);

                                alert.setView(mView);

                                AlertDialog alertDialog = alert.create();
                                alertDialog.setCanceledOnTouchOutside(false);

                                nameGV.setText(nameofTeacher.getText().toString());

                                btnEditImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setType("image/*");
                                        intent.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(intent, REQUEST_CODE_IMAGE);
                                    }
                                });
                                getImgInfo();

                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                btnSave.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(btnEditImg.isClickable()) {
                                            reference.child(userID).child("image").setValue(myUri);
                                        }
                                        if(nameGV.isClickable()) {
                                            reference.child(userID).child("image").setValue(myUri);
                                            reference.child(userID).child("fullname").setValue(nameGV.getText().toString());
                                        }
                                    }
                                });

                                alertDialog.show();
                                break;
                            case R.id.changePassword:
                                EditText changePassword = new EditText(TeacherHome.this);
                                AlertDialog.Builder changePasswordDialog = new AlertDialog.Builder(TeacherHome.this);
                                changePasswordDialog.setTitle("Đổi mật khẩu");
                                changePasswordDialog.setMessage("Nhập mật khẩu mới ít nhất 6 ký tự");
                                changePasswordDialog.setView(changePassword);

                                changePasswordDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String newPassword = changePassword.getText().toString();
                                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                reference.child(userID).child("password").setValue(newPassword);
                                                Toast.makeText(TeacherHome.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(TeacherHome.this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                changePasswordDialog.setNegativeButton("No", null);
                                changePasswordDialog.show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgEdit.setImageURI(imageUri);
            uploadPicture();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getImgInfo() {
        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users userProfile = snapshot.getValue(Users.class);
                if(userProfile != null) {
                    String image = userProfile.image;
                    // Lấy hình gán vào dùng thư viện Picasso
                    Picasso.with(TeacherHome.this).load(image).into(imgEdit);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeacherHome.this, "Error " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadPicture() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image...");
        pd.show();

        String randomKey = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("image/"+randomKey);
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(TeacherHome.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        myUri = imageUri.toString();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(TeacherHome.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

    private void AnhXa() {
        optionMenu = (ImageView) findViewById(R.id.optionMenu);
        classroom = (CardView) findViewById(R.id.classroom);
        chatWithStudent = (CardView) findViewById(R.id.chatWithStudent);
        nameofTeacher = (TextView) findViewById(R.id.nameofTeacher);
        imgView = (ImageView) findViewById(R.id.imgPersonTeacher);
    }
}