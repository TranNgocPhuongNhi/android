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
    String getID, getName, getEmail, getPassword;
    String getReenterPassword, getAuthority, getImage, getIdUser;

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
                    name.setError("Vui l??ng nh???p t??n");
                    name.requestFocus();
                    return;
                }
                else if(getEmail.isEmpty()) {
                    email.setError("Vui l??ng nh???p email");
                    email.requestFocus();
                    return;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
                    email.setError("Kh??ng ????ng ?????nh d???ng email");
                    email.requestFocus();
                    return;
                }
                else if(getPassword.isEmpty()) {
                    password.setError("Vui l??ng nh???p m???t kh???u");
                    password.requestFocus();
                    return;
                }
                else if(getPassword.length() < 6) {
                    password.setError("M???t kh???u c?? t???i thi???u 6 k?? t???.");
                    password.requestFocus();
                    return;
                }
                else if(!getPassword.equals(getReenterPassword)) {
                    rePassword.setError("M???t kh???u nh???p l???i kh??ng kh???p!");
                    rePassword.requestFocus();
                    return;
                }
                else if(getAuthority.isEmpty()) {
                    Toast.makeText(Register.this, "Ch???n t?? c??ch ????ng k??", Toast.LENGTH_SHORT).show();
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
                            // T???o m?? User ng???u nhi??n
                            getIdUser = String.valueOf((int)(Math.random() * 100001) + 1000000);
                            getID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            Users users = new Users(getID,getName, getEmail, getPassword, getAuthority, getImage, getIdUser);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())    // T???o t??n con l???y t??? User UID trong authentication
                                    .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);
                                        Toast.makeText(Register.this, "????ng k?? th??nh c??ng", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(Register.this, "????ng k?? th???t b???i", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(Register.this, "????ng k?? th???t b???i, email ???? c?? ng?????i ????ng k??", Toast.LENGTH_SHORT).show();
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
