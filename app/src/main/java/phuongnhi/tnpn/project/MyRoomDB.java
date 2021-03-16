package phuongnhi.tnpn.project;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {}, version = 1)
public abstract class MyRoomDB extends RoomDatabase {
    private static MyRoomDB INSTANCE;


    static MyRoomDB getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (MyRoomDB.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        MyRoomDB.class, "roomdb.db")
                        .addCallback(new Callback() {
                        })
                        .fallbackToDestructiveMigration()
                        .build();
                }
            }
        }
        return INSTANCE;
    }
}
