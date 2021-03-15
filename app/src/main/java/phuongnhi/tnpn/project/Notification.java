package phuongnhi.tnpn.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Notification extends AppCompatActivity {

    ImageView btnBack;
    RecyclerView recyclerView;
    notificationAdapter adapter;
//    List<InfoNotification> data;
    ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getData();
        btnBack = (ImageView) findViewById(R.id.imageView12);
        recyclerView = (RecyclerView) findViewById(R.id.listNotification);
        adapter = new notificationAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification.this, StudentHome.class);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        data = new ArrayList<>();
        for(int i=1; i<=50; i++) {
            data.add("Item " + i);
        }
    }

    private class notificationAdapter extends RecyclerView.Adapter<notificationViewHolder> {
        @NonNull
        @Override
        public notificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(Notification.this);
            View itemView = inflater.inflate(R.layout.list_notification, parent, false);
            return new notificationViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull notificationViewHolder holder, int position) {
            holder.noticeNotify.setText(data.get(position));
            holder.btnOptionDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(Notification.this, holder.btnOptionDelete);
                    popupMenu.getMenuInflater().inflate(R.menu.notification_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.deleteNotification:
                                    new AlertDialog.Builder(Notification.this)
                                        .setTitle("Bạn có chắc muốn xóa thông báo này?")
                                        .setMessage("Xóa thông báo")
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

    private class notificationViewHolder extends RecyclerView.ViewHolder {
        TextView noticeNotify, nameObject, shift, place, dateAbsent, reasonAbsent, btnOptionDelete;
        public notificationViewHolder(View itemView) {
            super(itemView);
            noticeNotify = itemView.findViewById(R.id.noticeNotify);
            nameObject = itemView.findViewById(R.id.nameObject);
            shift = itemView.findViewById(R.id.shift);
            place = itemView.findViewById(R.id.place);
            dateAbsent = itemView.findViewById(R.id.dateAbsent);
            reasonAbsent = itemView.findViewById(R.id.reasonAbsent);
            btnOptionDelete = itemView.findViewById(R.id.textView10);
        }
    }
}