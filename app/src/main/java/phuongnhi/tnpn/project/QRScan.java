package phuongnhi.tnpn.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScan extends AppCompatActivity{
    Button btn;
    TextView txt;

    DatabaseReference myRef, myRefAttendance;
    String takeID, takeClass, takeDate, code, takeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        txt = findViewById(R.id.text);
        btn = findViewById(R.id.scan);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(QRScan.this);
                intentIntegrator.setCaptureActivity(CaptureAct.class);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setPrompt("Scanning code");
                intentIntegrator.initiateScan();
            }
        });

        Intent intent = getIntent();
        takeID = intent.getStringExtra("takeID");
        takeClass = intent.getStringExtra("takeClass");
        takeName = intent.getStringExtra("name");
        takeDate = intent.getStringExtra("date");
        myRef = FirebaseDatabase.getInstance().getReference("Attendance").child(takeClass).child(takeDate);
        myRef.child("CodeNow").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                code = String.valueOf(snapshot.getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void  onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!= null){
            if(result.getContents() != null) {
                txt.setText("");
                String rs = result.getContents();
                if (rs.equals(code)) {
                    myRef.child(takeID).child(takeID).setValue("P");
                    myRef.child(takeID).child("fullName").setValue(takeName);
//                    txt.setText(takeDate+"\n"+takeClass+"\n"+takeID+"\n"+result.getContents());
                    finish();
                }
            }
            else {
                Toast.makeText(this, "No result", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
}