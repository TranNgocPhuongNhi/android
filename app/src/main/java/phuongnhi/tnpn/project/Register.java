package phuongnhi.tnpn.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText name, username, password, rePassword;
    Button btnRegister;
    RadioButton student, teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AnhXa();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getName = name.getText().toString().trim();
                String getUsername = username.getText().toString().trim();
                String getPassword = password.getText().toString().trim();
                String getReenterPassword = rePassword.getText().toString().trim();

                if (getName.equals("") || getUsername.equals("") || getPassword.equals("")) {
                    Toast.makeText(Register.this, "Vui lòng điền thông tin", Toast.LENGTH_SHORT).show();
                } else if (getPassword.length() < 6) {
                    Toast.makeText(Register.this, "Mật khẩu phải tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show();
                } else if (!getPassword.equals(getReenterPassword)) {
                    Toast.makeText(Register.this, "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show();
                } else {
                    if (student.isChecked()) {
                        StudentRegister();
                    } else if (teacher.isChecked()) {
                        TeacherRegister();
                    } else {
                        Toast.makeText(Register.this, "Hãy chọn tư cách đăng ký", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void TeacherRegister() {
        String getName = name.getText().toString().trim();
        String getUsername = username.getText().toString().trim();
        String getPassword = password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://192.168.56.1:81/quanlysinhvien/teacherRegister.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean status = jsonObject.getBoolean("status");
                            String message = jsonObject.getString("message");
                            if (status) {
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Register.this, "Register Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Register.this, "Register Error! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tenGV", getName);
                params.put("tkGV", getUsername);
                params.put("mkGV", getPassword);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void StudentRegister() {
        String getName = name.getText().toString().trim();
        String getUsername = username.getText().toString().trim();
        String getPassword = password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://192.168.56.1:81/quanlysinhvien/studentRegister.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean status = jsonObject.getBoolean("status");
                            String message = jsonObject.getString("message");
                            if (status) {
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Register.this, "Register Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Register.this, "Register Error! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tenSV", getName);
                params.put("tkSV", getUsername);
                params.put("mkSV", getPassword);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void AnhXa() {
        name = (EditText) findViewById(R.id.nameRegister);
        username = (EditText) findViewById(R.id.accountRegister);
        password = (EditText) findViewById(R.id.passwordRegister);
        rePassword = (EditText) findViewById(R.id.rePasswordRegister);
        student = (RadioButton) findViewById(R.id.registerStudent);
        teacher = (RadioButton) findViewById(R.id.registerTeacher);
        btnRegister = (Button) findViewById(R.id.btnRegister);
    }

}
