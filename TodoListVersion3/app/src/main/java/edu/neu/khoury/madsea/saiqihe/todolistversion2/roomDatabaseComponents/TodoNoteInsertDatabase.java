package edu.neu.khoury.madsea.saiqihe.todolistversion2.roomDatabaseComponents;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TodoNote.class}, version = 1, exportSchema = false)
public abstract class TodoNoteInsertDatabase extends RoomDatabase {
    public abstract TodoNoteDao dao();
    private static volatile TodoNoteInsertDatabase instance;
    private static final int THREAD_NUM = 3;
    static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);

    public static TodoNoteInsertDatabase getInstance(final Context context){
        if(instance==null){
            synchronized (TodoNoteInsertDatabase.class){
                if(instance==null){
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            TodoNoteInsertDatabase.class,
                            "todo_database_insert_2_4")
                            .allowMainThreadQueries()
                            .build();

                }
            }
        }
        return instance;
    }


}
