package edu.neu.khoury.madsea.saiqihe.todolistversion2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

public class TodoNoteViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView detail;
    private TextView idv;
    private TextView tdv;
    private Switch aSwitch;
    public TodoNoteViewHolder(@NonNull View itemView) {
        super(itemView);
        //mv = new ViewModelProvider((ViewModelStoreOwner) this).get(TodoModelView.class);
        itemView.setOnClickListener(view -> {

        });
        title = itemView.findViewById(R.id.recycle_title);
        detail = itemView.findViewById(R.id.recycle_detail);
        idv = itemView.findViewById(R.id.hidden_id);
        tdv = itemView.findViewById(R.id.hidden_createTime);
        aSwitch = itemView.findViewById(R.id.recycle_switch);
    }
    public void bind(String id, String titles, String details, String checked, String createTime){
        idv.setText(id);
        title.setText(titles);
        detail.setText(details);
        if(checked.equals("checked")&&!aSwitch.isChecked())aSwitch.toggle();
        else if(checked.equals("unchecked")&&aSwitch.isChecked())aSwitch.toggle();
        //use create time cuz firebase not available offline and noteId could change online
        tdv.setText(createTime);
    }
    public static TodoNoteViewHolder create(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view,parent,false);
        return new TodoNoteViewHolder(view);
    }
}
