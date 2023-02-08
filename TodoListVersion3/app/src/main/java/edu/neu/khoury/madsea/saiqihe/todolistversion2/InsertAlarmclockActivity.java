package edu.neu.khoury.madsea.saiqihe.todolistversion2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalTime;

public class InsertAlarmclockActivity extends AppCompatActivity {
    private Button button;
    private Button picker;
    private EditText title;
    private EditText detail;
    private EditText timer;
    private TextView idv;
    private TextView fIdv;
    private TextView cTv;
    private TodoModelView modelView;
    private Switch aSwitch;

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.d("insert Act", "insert frag resume");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("insert Act", "insert resume");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        button = findViewById(R.id.insert_submit_button);
        picker = findViewById(R.id.insert_picker_button);
        title = findViewById(R.id.text_title);
        detail = findViewById(R.id.text_detail);
        idv = findViewById(R.id.text_id_hidden);
        fIdv = findViewById(R.id.text_firebaseId_hidden);
        cTv = findViewById(R.id.text_createTime_hidden);
        timer = findViewById(R.id.text_timer);
        aSwitch = findViewById(R.id.insert_switch);
        modelView = new ViewModelProvider(this).get(TodoModelView.class);
        modelView.getMinute().observe(this, item -> {
            if (item != null) {
                picker.setText("alarm at " + modelView.getHour().getValue().toString() + ":" + item.toString());
            }
        });
        Intent inputIntent = getIntent();
        if (inputIntent.getStringExtra("title") != null) {
            title.setText(inputIntent.getStringExtra("title"));
            detail.setText(inputIntent.getStringExtra("detail"));
            idv.setText(inputIntent.getStringExtra("id"));
            fIdv.setText(inputIntent.getStringExtra("firebaseId"));
            cTv.setText(inputIntent.getStringExtra("createTime"));
            if (inputIntent.getStringExtra("alarm_time") != null) {
                String s = inputIntent.getStringExtra("alarm_time");
                String[] sp = s.split("-");
                LocalTime time = LocalTime.now();
                picker.setText("alarm at " + sp[0] + ":" + sp[1]);
            }
            if (inputIntent.getStringExtra("checked").equals("checked")) aSwitch.toggle();
        }
        button.setOnClickListener(view -> {
            Intent intent = new Intent();
            if (idv.getText() != null && idv.getText() != "")
                intent.putExtra("id", Integer.parseInt(idv.getText().toString()));
            intent.putExtra("title", title.getText().toString());
            intent.putExtra("detail", detail.getText().toString());
            intent.putExtra("firebaseId", fIdv.getText().toString());
            intent.putExtra("createTime", cTv.getText().toString());
            String alarm = modelView.getHour().getValue() + "-" + modelView.getMinute().getValue();
            if (!alarm.equals("null-null")) intent.putExtra("alarm_time", alarm);
            intent.putExtra("checked", aSwitch.isChecked() ? "checked" : "unchecked");
            String s = timer.getText().toString();
            if (s.equals("")) s = "0";
            int sec = Integer.parseInt(s);
            Log.d("my set sec", sec + "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (modelView.getMinute().getValue() != null) {
                    sec += modelView.getMinute().getValue() * 60;
                    sec += modelView.getHour().getValue() * 3600;
                }
            }
            if (sec < 0) sec = 1;
            intent.putExtra("timer", sec + "");
            setResult(RESULT_OK, intent);
            /*if(modelView.getMinute()!=0){
                modelView.startTimer("auto","",modelView.getMinute());
            }*/
            finish();
        });
    }

    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimerPickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}