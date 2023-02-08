package edu.neu.khoury.madsea.saiqihe.todolistversion2;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.neu.khoury.madsea.saiqihe.todolistversion2.roomDatabaseComponents.TodoNote;

//TODO: UI design, make insert user friendly and beautiful
//TODO: add a confirm fragment before deleting all the notes
//todo: try separate alarming and timer by set alarm time "null", separate acts from layouts
public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 5;
    private static final int SIGN_UP = 4;
    private static final int SIGN_IN = 3;
    private static final String ALARMS = "alarms";
    private static final String TIMERS = "timers";
    private static final int NEW_NOTE = 1;
    private static final int UPDATE_NOTE = 2;
    private TodoModelView modelView;
    private TodoNoteAdapter myAdapter;
    private TodoNoteAdapter myAdapter2;
    RecyclerView viewInMain;
    RecyclerView viewInMain2;
    private String currentPage;
    private HashMap<String,TodoNote> updList;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        modelView = new ViewModelProvider(this).get(TodoModelView.class);
        updList = new HashMap<>();
        myAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.my_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //no sync

        //observe
        viewInMain = findViewById(R.id.recycle_hold);
        viewInMain2 = findViewById(R.id.recycle_hold2);
        myAdapter = new TodoNoteAdapter(new TodoNoteAdapter.NoteDiff(), this);
        myAdapter2 = new TodoNoteAdapter(new TodoNoteAdapter.NoteDiff(), this);
        viewInMain.setAdapter(myAdapter);
        viewInMain2.setAdapter(myAdapter2);
        viewInMain.setLayoutManager(new LinearLayoutManager(this));
        viewInMain2.setLayoutManager(new LinearLayoutManager(this));
        modelView.selectInsert().observe(this, items -> {
        });
        modelView.selectDelete().observe(this, items -> {
        });
        modelView.select().observe(this, items -> {
        });
        modelView.select(ALARMS).observe(this, items -> {
            myAdapter.submitList(sortByCreateTime(items));
        });
        modelView.select(TIMERS).observe(this, items -> {
            myAdapter2.submitList(sortByCreateTime(items));
        });
        switchUiToAlarm();

        //add
        FloatingActionButton button = findViewById(R.id.float_action_button);
        button.setOnClickListener(view -> {
            if (currentPage.equals(ALARMS)) {
                Intent intent = new Intent(MainActivity.this, InsertAlarmclockActivity.class);
                startActivityForResult(intent, NEW_NOTE);
            } else {
                Intent intent = new Intent(MainActivity.this, InsertTimerActivity.class);
                startActivityForResult(intent, NEW_NOTE);
            }
        });

        //del
        FloatingActionButton delButton = findViewById(R.id.float_del_button);
        delButton.setOnClickListener(view -> {
            ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
            confirmDialogFragment.setModelView(modelView);
            confirmDialogFragment.show(getSupportFragmentManager(), "confirm del");
        });

        //switch
        Button toTimersButton = findViewById(R.id.to_timers);
        Button toAlarmsButton = findViewById(R.id.to_alarm_clocks);
        toTimersButton.setOnClickListener((view) -> {
            switchUiToTimer();
        });
        toAlarmsButton.setOnClickListener((view) -> {
            switchUiToAlarm();
        });

        //sign in
        FloatingActionButton signInButton = findViewById(R.id.float_login_button);
        signInButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("type","signIn");
            startActivityForResult(intent,SIGN_IN);
        });

        //sign up
        FloatingActionButton signUpButton = findViewById(R.id.float_sign_up_button);
        signUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            intent.putExtra("type","signUp");
            startActivityForResult(intent,SIGN_UP);
        });

        //google sign in
        FloatingActionButton googleButton = findViewById(R.id.float_google_button);
        googleButton.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }



    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //check for current user
        if(myAuth.getCurrentUser()!=null){
            modelView.updateCurrentUser(myAuth.getCurrentUser().getEmail());
        }
        //silent update
        for(TodoNote note : updList.values()){
            modelView.mainThreadUpdate(note);
        }
        updList.clear();
        //sync
        //first upload delete/insert note to cloud,
        //empty local change database, then download all notes from cloud
        modelView.syncCloud();
        modelView.syncLocal();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN) {
            if(myAuth.getCurrentUser()!=null){
                String displayName = myAuth.getCurrentUser().getDisplayName();
                Toast.makeText(getApplicationContext(), "Welcome back, "+ displayName, Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == SIGN_UP) {
            if(myAuth.getCurrentUser()!=null){
                String displayName = myAuth.getCurrentUser().getDisplayName();
                if(displayName==null)displayName = "";
                Toast.makeText(getApplicationContext(), "Greetings "+ displayName, Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("login google", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("login gogle", "Google sign in failed", e);
            }
        }
        else if (data != null) {
            if (requestCode == NEW_NOTE) {
                TodoNote t = new TodoNote(
                        data.getStringExtra("title"),
                        data.getStringExtra("detail"));
                t.setAlarmTime(data.getStringExtra("alarm_time"));
                t.setChecked(data.getStringExtra("checked"));
                modelView.insert(t);
            } else if(requestCode == UPDATE_NOTE) {
                TodoNote t = new TodoNote(
                        data.getIntExtra("id", 0),
                        data.getStringExtra("title"),
                        data.getStringExtra("detail"));
                t.setAlarmTime(data.getStringExtra("alarm_time"));
                t.setChecked(data.getStringExtra("checked"));
                t.setFirebaseId(data.getStringExtra("firebaseId"));
                t.setCreateTime(data.getStringExtra("createTime"));
                modelView.update(t);
            }
            if (data.getStringExtra("alarm_time") != null &&
                    data.getStringExtra("alarm_time").equals("null-null")) {
                switchUiToTimer();
            } else {
                switchUiToAlarm();
            }
            try {
                String titleS = data.getStringExtra("title");
                String detailS = data.getStringExtra("detail");
                String timeS = data.getStringExtra("timer");
                int time = Integer.parseInt(timeS);
                modelView.startTimer(titleS, detailS, time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //listener recover
        Button toTimersButton = findViewById(R.id.to_timers);
        Button toAlarmsButton = findViewById(R.id.to_alarm_clocks);
        if (currentPage.equals(TIMERS)) {
            switchUiToTimer();
        } else {
            switchUiToAlarm();
        }
        toTimersButton.setOnClickListener((view) -> {
            switchUiToTimer();
        });
        toAlarmsButton.setOnClickListener((view) -> {
            switchUiToAlarm();
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        myAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("google exchange token", "signInWithCredential:success");
                            FirebaseUser user = myAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "google signed in", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("google exchange failed", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "google signed in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void delete(TodoNote note) {
        modelView.delete();
    }

    public void update(TodoNote note) {
        Intent intent = null;//note id could change
        for (TodoNote n : modelView.select().getValue()) {
            if (n.getCreateTime().equals(note.getCreateTime())) {
                if (n.getAlarmTime() == null) {
                    intent = new Intent(MainActivity.this, InsertAlarmclockActivity.class);
                } else if (n.getAlarmTime().equals("null-null")) {//timer
                    intent = new Intent(MainActivity.this, InsertTimerActivity.class);
                } else {//alarm clock
                    intent = new Intent(MainActivity.this, InsertAlarmclockActivity.class);
                }
                intent.putExtra("id", note.getNoteId().toString());
                intent.putExtra("title", n.getTitle());
                intent.putExtra("detail", n.getDetail());
                intent.putExtra("alarm_time", n.getAlarmTime());
                intent.putExtra("checked", n.getChecked());
                intent.putExtra("firebaseId", n.getFirebaseId());
                intent.putExtra("createTime", n.getCreateTime());
            }
        }
        startActivityForResult(intent, 2);
    }

    public void silentUpdate(TodoNote t) {
        TodoNote upd = null;
        for (TodoNote n : modelView.select().getValue()) {
            if (n.getCreateTime().equals(t.getCreateTime())) {
                n.setChecked(t.getChecked());
                upd = n;
            }
        }
        if(upd!=null){
            updList.put(upd.getCreateTime(),upd);
        }
    }

    private void switchUiToTimer() {
        Button toTimersButton = findViewById(R.id.to_timers);
        Button toAlarmsButton = findViewById(R.id.to_alarm_clocks);
        currentPage = TIMERS;
        toTimersButton.setBackgroundColor(Color.GRAY);
        toAlarmsButton.setBackgroundColor(Color.WHITE);
        viewInMain.setVisibility(View.INVISIBLE);
        viewInMain2.setVisibility(View.VISIBLE);
    }

    private void switchUiToAlarm() {
        Button toTimersButton = findViewById(R.id.to_timers);
        Button toAlarmsButton = findViewById(R.id.to_alarm_clocks);
        currentPage = ALARMS;
        toTimersButton.setBackgroundColor(Color.WHITE);
        toAlarmsButton.setBackgroundColor(Color.GRAY);
        viewInMain2.setVisibility(View.INVISIBLE);
        viewInMain.setVisibility(View.VISIBLE);
    }

    private List<TodoNote> sortByCreateTime(List<TodoNote> items) {
        HashMap<String,TodoNote> map = new HashMap<>();
        ArrayList<Long> times = new ArrayList<>();
        for(TodoNote note : items){
            times.add(Long.parseLong(note.getCreateTime()));
            map.put(note.getCreateTime(),note);
        }
        Collections.sort(times);
        ArrayList<TodoNote> rst = new ArrayList<>();
        for(Long l : times){
            rst.add(map.get(l.toString()));
        }
        return rst;
    }
}