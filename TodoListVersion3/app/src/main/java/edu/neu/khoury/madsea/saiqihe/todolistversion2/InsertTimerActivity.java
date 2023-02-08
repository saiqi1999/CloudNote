package edu.neu.khoury.madsea.saiqihe.todolistversion2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.time.LocalTime;

import edu.neu.khoury.madsea.saiqihe.todolistversion2.R;

public class InsertTimerActivity extends AppCompatActivity {

    private Button button;
    private Button picker;
    private EditText title;
    private EditText detail;
    private EditText timerSec;
    private EditText timerMin;
    private EditText timerHrs;
    private TextView idv;
    private TextView fIdv;
    private TextView cTv;
    private TodoModelView modelView;
    private Switch aSwitch;

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.d("insert Act","insert frag resume");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("insert Act","insert resume");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_timer);
        button = findViewById(R.id.insert_submit_button);
        picker = findViewById(R.id.insert_picker_button);
        title = findViewById(R.id.text_title);
        detail = findViewById(R.id.text_detail);
        idv = findViewById(R.id.text_id_hidden);
        fIdv = findViewById(R.id.text_firebaseId_hidden);
        cTv = findViewById(R.id.text_createTime_hidden);
        timerSec = findViewById(R.id.text_timerSec);
        timerMin = findViewById(R.id.text_timerMin);
        timerHrs = findViewById(R.id.text_timerHr);
        aSwitch = findViewById(R.id.insert_switch);
        modelView = new ViewModelProvider(this).get(TodoModelView.class);
        modelView.getMinute().observe(this, item->{
            if(item!=null){
                picker.setText("notice after "+modelView.getHour().getValue().toString()+" hours "+item.toString()+" minutes");
            }
        });
        Intent inputIntent = getIntent();
        if (inputIntent.getStringExtra("title") != null) {
            title.setText(inputIntent.getStringExtra("title"));
            detail.setText(inputIntent.getStringExtra("detail"));
            idv.setText(inputIntent.getStringExtra("id"));
            fIdv.setText(inputIntent.getStringExtra("firebaseId"));
            cTv.setText(inputIntent.getStringExtra("createTime"));
            if(inputIntent.getStringExtra("checked").equals("checked"))aSwitch.toggle();
        }
        button.setOnClickListener(view -> {
            Intent intent = new Intent();
            if (idv.getText() != null && idv.getText() != "")
                intent.putExtra("id", Integer.parseInt(idv.getText().toString()));
            intent.putExtra("title", title.getText().toString());
            intent.putExtra("detail", detail.getText().toString());
            intent.putExtra("firebaseId", fIdv.getText().toString());
            intent.putExtra("createTime", cTv.getText().toString());
            intent.putExtra("alarm_time",modelView.getHour().getValue()+"-"+modelView.getMinute().getValue());
            intent.putExtra("checked",aSwitch.isChecked()?"checked":"unchecked");
            String sec = timerSec.getText().toString();
            String min = timerMin.getText().toString();
            String hrs = timerHrs.getText().toString();
            try{
                int totalSec;
                if(sec.equals(""))sec="0";
                if(min.equals(""))min="0";
                if(hrs.equals(""))hrs="0";
                totalSec = Integer.parseInt(sec);
                totalSec += Integer.parseInt(min) * 60;
                totalSec += Integer.parseInt(hrs) * 3600;
                if(totalSec<0)totalSec=1;
                intent.putExtra("timer", totalSec + "");
            }catch (Exception ignored){
            }
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(modelView.getMinute().getValue()!=null) {
                    sec += modelView.getMinute().getValue() * 60;
                    sec += modelView.getHour().getValue() * 3600;
                }
            }*/
            setResult(RESULT_OK, intent);
            finish();
        });
    }

}