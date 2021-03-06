package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class exportFile extends AppCompatActivity {

    DatabaseReference mData, myRef;
    ListView listView;
    Button btnExport, btnPDF;
    ArrayList<String> dsCamThi;
    ArrayList<SinhVien> dsCT = new ArrayList<>();

    ArrayAdapter adapter;
    File filePath ;

    List<String> date= new ArrayList<String>();
    String classID;
    String maxApsent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        btnExport = findViewById(R.id.btnExport);
        listView = findViewById(R.id.listView);
        btnPDF = findViewById(R.id.btnPDF);

        Intent intent = getIntent();
        classID = intent.getStringExtra("classID");
        maxApsent = intent.getStringExtra("maxAbsent");
//        temp = intent.getIntExtra("DuocPhepVang",temp);

        filePath = new File(Environment.getExternalStorageDirectory() + "/Danh S??ch C???m Thi L???p "+classID+".xls");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        dsCamThi = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dsCamThi);
        listView.setAdapter(adapter);

        mData = FirebaseDatabase.getInstance().getReference("Attendance").child(classID);
        myRef = FirebaseDatabase.getInstance().getReference("Class").child(classID);
        myRef.child("list_student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Map map = (Map) dataSnapshot.getValue();
                    String idUser = map.get("idUser").toString();
                    mData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            date.removeAll(date);
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                    if(dataSnapshot1.getKey().equals(idUser)) {
                                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                            if(dataSnapshot2.getKey().equals(idUser) && dataSnapshot2.getValue().equals("A")){
                                                date.add(dataSnapshot.getKey());
                                            }
                                        }
                                    }
                                }
                            }
                            SinhVien sinhVien = new SinhVien(map.get("fullname").toString(),map.get("idUser").toString(),classID,date.size());
                            if(date.size() > Integer.parseInt(maxApsent) * 0.2) {
                                dsCT.add(sinhVien);
                                dsCamThi.add("T??n: " + sinhVien.getName() + "\n" +
                                        "MSSV: " + sinhVien.getId() + "\n" +
                                        "S??? Ng??y V???ng: " + sinhVien.getnApsent() + "\n" +
                                        "-------------" + "\n");
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
                HSSFSheet hssfSheet = hssfWorkbook.createSheet("Danh Sach Cam Thi");

                hssfSheet.setColumnWidth(0, (5000));
                hssfSheet.setColumnWidth(1, (3000));
                hssfSheet.setColumnWidth(2, (3000));
                hssfSheet.setColumnWidth(3, (3000));

                HSSFRow hssfRow = hssfSheet.createRow(0);
                HSSFCell hssfCell = hssfRow.createCell(0);
                hssfCell.setCellValue("H??? v?? T??n");
                hssfCell = hssfRow.createCell(1);
                hssfCell.setCellValue("MSSV");
                hssfCell = hssfRow.createCell(2);
                hssfCell.setCellValue("M??n h???c");
                hssfCell = hssfRow.createCell(3);
                hssfCell.setCellValue("S??? ng??y v???ng");

                // Add d??? li???u
                int numRow = 1;
                for (SinhVien sv : dsCT) {
                    hssfRow = hssfSheet.createRow(numRow);
                    int numCol = 0;
                    while (numCol < 4) {
                        hssfCell = hssfRow.createCell(numCol);
                        if (numCol == 0)
                            hssfCell.setCellValue(sv.getName());
                        if (numCol == 1)
                            hssfCell.setCellValue(sv.getId());
                        if (numCol == 2)
                            hssfCell.setCellValue(sv.getMonHoc());
                        if (numCol == 3)
                            hssfCell.setCellValue(sv.getnApsent());
                        numCol++;
                    }
                    numRow += 1;
                }

                // Xu???t File
                try {
                    if (!filePath.exists()) {
                        filePath.createNewFile();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                    hssfWorkbook.write(fileOutputStream);
                    Toast.makeText(exportFile.this, "Export Success", Toast.LENGTH_SHORT).show();
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (Exception e) {
                    Toast.makeText(exportFile.this, "Export Failed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        btnPDF.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300,600,3).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                Paint paint = new Paint();
                int x = 10, y = 25;
                for(String line : dsCamThi){
                    for(String line1 : line.split("\n")) {
                        page.getCanvas().drawText(line1 , x, y, paint);
                        y += paint.descent() - paint.ascent();
                    }
                }



                pdfDocument.finishPage(page);

                String myFilePath = Environment.getExternalStorageDirectory().getPath() + "/Danh S??ch C???m Thi L???p "+classID+".pdf";

                File myFile = new File(myFilePath);

                try {
                    if (!myFile.exists()) {
                        myFile.createNewFile();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(myFile);

                    pdfDocument.writeTo(fileOutputStream);

                    Toast.makeText(exportFile.this, "Export Success", Toast.LENGTH_SHORT).show();
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (Exception e) {
                    Toast.makeText(exportFile.this, "Export Failed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }
    public static class SinhVien {
        public String name;
        public String id;
        public String monHoc;
        public Integer nApsent;
        public String getName() {
            return name;
        }
        public String getId() {
            return id;
        }
        public String getMonHoc() {
            return monHoc;
        }
        public Integer getnApsent() {
            return nApsent;
        }
        public SinhVien() {
            // default
        }
        public SinhVien(String name, String id, String monHoc, Integer nApsent) {
            this.name = name;
            this.id = id;
            this.monHoc = monHoc;
            this.nApsent = nApsent;
        }
    }
}