package edu.neu.khoury.madsea.saiqihe.todolistversion2.firebaseComponents;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;

public class AfterInputFailureListener implements OnFailureListener {
    @Override
    public void onFailure(@NonNull Exception e) {
        Log.i("input failure listener","failure " + e.getMessage());
    }
}
