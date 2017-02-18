package com.sugarcube.todo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sugarcube.todo.R;
import com.sugarcube.todo.models.Todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Abed on 2/14/2017.
 */

public class TodoAdapter extends ArrayAdapter<Todo> {

    private final String DUE_DATE_PREFIX = "Due date : ";

    public static class ViewHolder {
        TextView subject;
        TextView priority;
        TextView dueDate;
        CheckBox completed;
    }

    public TodoAdapter(Context context, ArrayList<Todo> todos) {
        super(context, 0, todos);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Todo todo = getItem(position);

        ViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_row, parent, false);

            viewHolder.subject = (TextView) convertView.findViewById(R.id.listItemTodoSubject);
            viewHolder.priority = (TextView) convertView.findViewById(R.id.listItemTodoPriority);
            viewHolder.dueDate = (TextView) convertView.findViewById(R.id.listItemTodoDueDate);
            viewHolder.completed = (CheckBox) convertView.findViewById(R.id.listItemTodoCompleted);


            // save the view holder with the convert view
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // format the date into Month day, Year
        String pojoDueDate = todo.getDueDate();
        String formatted = null;


        if (pojoDueDate.isEmpty()== false) {
            try {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Date d = null;
                d = sf.parse(pojoDueDate);
                SimpleDateFormat sf2 = new SimpleDateFormat("MMM d, y");
                formatted = sf2.format(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String dueDateText = pojoDueDate;

        if (formatted != null) {
            dueDateText = DUE_DATE_PREFIX + formatted;
        }

        viewHolder.completed.setChecked(todo.isDone());
        viewHolder.dueDate.setText(dueDateText);
        viewHolder.subject.setText(todo.getSubject());
        viewHolder.priority.setText(todo.getPriority().name());
        // set the color based on the priority
        viewHolder.priority.setTextColor(getContext().getResources().getColor(todo.getPriority().getColor()));
        return  convertView;
    }
} // eoc

