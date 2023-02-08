package edu.neu.khoury.madsea.saiqihe.todolistversion2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.util.StringUtil;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button loginButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        if(getIntent().getStringExtra("type").equals("signIn")){
            loginButton.setText("sign in");
            loginButton.setOnClickListener(view -> {
                login();
            });
        }
        if(getIntent().getStringExtra("type").equals("signUp")){
            loginButton.setText("sign up");
            loginButton.setOnClickListener(view -> {
                signUp();
            });
        }


    }

    private void signUp() {
        String name = username.getText().toString();
        String pwd = password.getText().toString();
        if (!name.equals("") && !pwd.equals("")) {
            mAuth.createUserWithEmailAndPassword(name,pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser() != null){
                                    mAuth.getCurrentUser().updateProfile(
                                            new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(name)
                                                    .build()
                                    );
                                    finish();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "sign up failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void login() {
        String name = username.getText().toString();
        String pwd = password.getText().toString();
        if (!name.equals("") && !pwd.equals("")) {
            mAuth.signInWithEmailAndPassword(name, pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (mAuth.getCurrentUser() != null) finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "login failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
        }
    }


}