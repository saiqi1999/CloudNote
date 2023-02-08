package edu.neu.khoury.madsea.saiqihe.todolistversion2.firebaseComponents;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import edu.neu.khoury.madsea.saiqihe.todolistversion2.roomDatabaseComponents.TodoNote;
import edu.neu.khoury.madsea.saiqihe.todolistversion2.roomDatabaseComponents.TodoRepo;

public class AfterQueryListener implements OnCompleteListener<QuerySnapshot> {
    private final ArrayList<TodoNote> saved;
    protected Thread thread;
    protected RunnableQuery runnableQuery;
    private TodoRepo localRepo;

    public ArrayList<TodoNote> getSaved() {
        return saved;
    }

    public TodoRepo getLocalRepo() {
        return localRepo;
    }

    public void setLocalRepo(TodoRepo localRepo) {
        this.localRepo = localRepo;
    }

    public AfterQueryListener() {
        saved = new ArrayList<>();
        runnableQuery = new RunnableQuery();
        runnableQuery.setSaved(saved);
        thread = new Thread(runnableQuery);
    }

    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        //only execute if task success
        if(!task.isSuccessful())return;
        runnableQuery.setTask(task);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //no race condition, try to save id
        localRepo.deleteAndInsert(saved);

        //sync
        localRepo.cleanCache();
        //has uploaded, label as synced
        //FirebaseSyncFlag.getInstance().setFlag(true);
    }
}
