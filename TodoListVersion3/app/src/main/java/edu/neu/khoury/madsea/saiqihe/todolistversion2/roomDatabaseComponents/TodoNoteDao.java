package edu.neu.khoury.madsea.saiqihe.todolistversion2.roomDatabaseComponents;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoNoteDao {
    @Insert
    public void insert(TodoNote note);
    @Query("SELECT * FROM todo_note_table")
    public LiveData<List<TodoNote>> selectAll();
    @Query("SELECT * FROM todo_note_table where note_alarmTime = 'null-null'")
    public LiveData<List<TodoNote>> selectAllTimer();
    @Query("SELECT * FROM todo_note_table where note_alarmTime != 'null-null' or note_alarmTime is NULL")
    public LiveData<List<TodoNote>> selectAllAlarm();
    @Query("SELECT * FROM todo_note_table")
    public List<TodoNote> staticSelectAll();
    @Query("DELETE FROM todo_note_table")
    public void deleteAll();
    @Update
    void update(TodoNote note);
    @Delete
    public void delete(TodoNote note);
    @Query("DELETE FROM todo_note_table where firebase_id = :firebaseId")
    void deleteByFirebaseId(String firebaseId);
    @Query("DELETE FROM todo_note_table where note_id = :noteId")
    void deleteById(Integer noteId);
    @Query("DELETE FROM todo_note_table where create_time = :createTime")
    void deleteByCreateTime(String createTime);
}
