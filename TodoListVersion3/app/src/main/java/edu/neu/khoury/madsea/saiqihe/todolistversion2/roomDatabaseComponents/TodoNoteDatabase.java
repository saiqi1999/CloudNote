package edu.neu.khoury.madsea.saiqihe.todolistversion2.roomDatabaseComponents;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TodoNote.class}, version = 1, exportSchema = false)
public abstract class TodoNoteDatabase extends RoomDatabase {
    public abstract TodoNoteDao dao();
    private static volatile TodoNoteDatabase instance;
    private static final int THREAD_NUM = 3;
    static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);

    public static TodoNoteDatabase getInstance(final Context context){
        if(instance==null){
            synchronized (TodoNoteDatabase.class){
                if(instance==null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            TodoNoteDatabase.class,
                            "todo_database_2_4")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return instance;
    }


}
