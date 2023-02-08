package edu.neu.khoury.madsea.saiqihe.todolistversion2.firebaseComponents;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

public class AfterInputSuccessListener implements OnSuccessListener<DocumentReference>{

    @Override
    public void onSuccess(DocumentReference documentReference) {
        Log.d("after input listener","insert id="+documentReference.getId());
    }


}
