package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button btnLogin;
    EditText email, password;
    CheckBox remember;
    TextView register;
    SharedPreferences sharedPreferences;

    String getEmail, getPassword;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    FirebaseUser user;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AnhXa();

        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo để lưu dữ liệu
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);  // Lưu trữ tạm thời
        // Lấy giá trị sharedPreferences
        email.setText(sharedPreferences.getString("taikhoan",""));
        password.setText(sharedPreferences.getString("matkau",""));
        remember.setChecked(sharedPreferences.getBoolean("checked", false));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmail = email.getText().toString();
                getPassword = password.getText().toString();
                if(getEmail.isEmpty()) {
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
                else {
                    Login();
                }

                if(remember.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();  // Chỉnh sửa
                    editor.putString("taikhoan", getEmail); // lưu tài khoản
                    editor.putString("matkhau", getPassword);   // lưu mật khẩu
                    editor.putBoolean("checked", true);
                    editor.commit(); // Xác nhận giá trị
                }
                else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();  // Chỉnh sửa
                    editor.remove("taikhoan"); // Xóa lưu tài khoản
                    editor.remove("matkhau");
                    editor.remove("checked");
                    editor.commit(); // Xác nhận giá trị
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
    }

    private void Login() {
        mAuth.signInWithEmailAndPassword(getEmail, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    reference = FirebaseDatabase.getInstance().getReference("Users");
                    userID  = user.getUid();

                    reference.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users userProfile = snapshot.getValue(Users.class);
                            if(userProfile != null) {
                                if(userProfile.authority.equals("Sinh viên")) {
                                    Intent intent = new Intent(Login.this, StudentHome.class);
                                    startActivity(intent);
                                }
                                if(userProfile.authority.equals("Giảng viên")) {
                                    Intent intent = new Intent(Login.this, TeacherHome.class);
                                    startActivity(intent);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Login.this, "Error " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(Login.this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void AnhXa() {
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        register = (TextView) findViewById(R.id.dangKy);
        remember = (CheckBox) findViewById(R.id.remember);
        btnLogin = (Button) findViewById(R.id.btnLogin);

    }
}