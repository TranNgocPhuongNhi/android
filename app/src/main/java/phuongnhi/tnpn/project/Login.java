package phuongnhi.tnpn.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button btnLogin;
    EditText username, password;
    CheckBox remember;
    RadioButton student, teacher;
    TextView register;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AnhXa();
        // Khởi tạo để lưu dữ liệu
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);  // Lưu trữ tạm thời
        // Lấy giá trị sharedPreferences
        username.setText(sharedPreferences.getString("taikhoan",""));
        password.setText(sharedPreferences.getString("matkau",""));
        remember.setChecked(sharedPreferences.getBoolean("checked", false));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getUsername = username.getText().toString().trim();
                String getPassword = password.getText().toString().trim();
                if(getUsername.equals("") || getPassword.equals("")) {
                    Toast.makeText(Login.this, "Vui lòng điền thông tin", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(student.isChecked()) {
                        StudentLogin(getUsername, getPassword);
                    }
                    else if (teacher.isChecked()) {
                        TeacherLogin(getUsername, getPassword);
                    }
                    else {
                        Toast.makeText(Login.this, "Hãy chọn tư cách đăng nhập", Toast.LENGTH_SHORT).show();
                    }
                }

                if(remember.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();  // Chỉnh sửa
                    editor.putString("taikhoan", getUsername); // lưu tài khoản
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

    private void StudentLogin(String username, String password) {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://192.168.56.1:81/quanlysinhvien/studentLogin.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            Boolean status = jsonObject.getBoolean("status");
                            if(status) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                if(data.length() > 0) {
                                    Intent intent = new Intent(Login.this, StudentHome.class);
                                    startActivity(intent);
                                }
                                else {
                                    message = "Không tìm thấy tài khoản, xin hãy nhập chính xác tài khoản";
                                    failHandling(message);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Login.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("tkSV", username);
                    jsonObj.put("mkSV", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("params", jsonObj.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void TeacherLogin(String username, String password) {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://192.168.56.1:81/quanlysinhvien/teacherLogin.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            Boolean status = jsonObject.getBoolean("status");
                            if(status) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                if(data.length() > 0) {
                                    Intent intent = new Intent(Login.this, TeacherHome.class);
                                    startActivity(intent);
                                }
                                else {
                                    message = "Không tìm thấy tài khoản, xin hãy nhập chính xác tài khoản";
                                    failHandling(message);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Login.this, "Error " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "Error " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObj = new JSONObject();
                try {
                    jsonObj.put("tkGV", username);
                    jsonObj.put("mkGV", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("params", jsonObj.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void failHandling(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }

    private void AnhXa() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        register = (TextView) findViewById(R.id.dangKy);
        remember = (CheckBox) findViewById(R.id.remember);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        student = (RadioButton) findViewById(R.id.loginStudent);
        teacher = (RadioButton) findViewById(R.id.loginTeacher);
    }
}