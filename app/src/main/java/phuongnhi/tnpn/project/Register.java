package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText name, email, password, rePassword;
    Button btnRegister;
    RadioButton student, teacher;

    private FirebaseAuth mAuth;
    String getName, getEmail, getPassword, getReenterPassword, getAuthority, getImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AnhXa();
        getImage = "https://freeiconshop.com/wp-content/uploads/edd/person-solid.png";
        mAuth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAuthority = "";
                getName = name.getText().toString();
                getEmail = email.getText().toString();
                getPassword = password.getText().toString();
                getReenterPassword = rePassword.getText().toString();
                if (student.isChecked()) {
                    getAuthority = student.getText().toString().trim();
                }
                if(teacher.isChecked()) {
                    getAuthority = teacher.getText().toString().trim();
                }
                if(getName.isEmpty()) {
                    name.setError("Vui lòng nhập tên");
                    name.requestFocus();
                    return;
                }
                else if(getEmail.isEmpty()) {
                    email.setError("Vui lòng nhập email");
                    email.requestFocus();
                    return;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
                    email.setError("Không đúng định dạng email");
                    email.requestFocus();
                    return;
                }
                else if(getPassword.isEmpty()) {
                    password.setError("Vui lòng nhập mật khẩu");
                    password.requestFocus();
                    return;
                }
                else if(getPassword.length() < 6) {
                    password.setError("Mật khẩu có tối thiểu 6 ký tự.");
                    password.requestFocus();
                    return;
                }
                else if(!getPassword.equals(getReenterPassword)) {
                    rePassword.setError("Mật khẩu nhập lại không khớp!");
                    rePassword.requestFocus();
                    return;
                }
                else if(getAuthority.isEmpty()) {
                    Toast.makeText(Register.this, "Chọn tư cách đăng ký", Toast.LENGTH_SHORT).show();
                }
                else {
                    Register();
                }
            }
        });
    }

    private void Register() {
        mAuth.createUserWithEmailAndPassword(getEmail, getPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Users users = new Users(getName, getEmail, getPassword, getAuthority, getImage);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                        Toast.makeText(Register.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(Register.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(Register.this, "Đăng ký thất bại, email đã có người đăng ký", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void AnhXa() {
        name = (EditText) findViewById(R.id.nameRegister);
        email = (EditText) findViewById(R.id.accountRegister);
        password = (EditText) findViewById(R.id.passwordRegister);
        rePassword = (EditText) findViewById(R.id.rePasswordRegister);
        student = (RadioButton) findViewById(R.id.registerStudent);
        teacher = (RadioButton) findViewById(R.id.registerTeacher);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

}
