package com.sugarcube.todo.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sugarcube.todo.fragments.DatePickerFragment;
import com.sugarcube.todo.R;
import com.sugarcube.todo.models.Todo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TodoEditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public final static String DUE_DATE_EXTRA = "com.sugarcube.todo.DUE_DATE_EXTRA";

    private EditText subject;
    private TextView dueDate;
    private EditText note;

    private Spinner priority;
    private Spinner category;

    private CheckBox completed;

    private Button clearDueDate;

    private Todo pojo;

    private void grabControls() {
        // grab the controls
        subject = (EditText) findViewById(R.id.todo_edit_subject);
        dueDate = (TextView) findViewById(R.id.todo_edit_due_date);

        dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        note = (EditText) findViewById(R.id.todo_edit_note);

        priority = (Spinner) findViewById(R.id.todo_edit_priority);
        category = (Spinner) findViewById(R.id.todo_edit_category);
        //status = (Spinner) findViewById(R.id.todo_edit_status);

        completed = (CheckBox) findViewById(R.id.todo_edit_completed);

        clearDueDate = (Button) findViewById(R.id.clear_due_date);

        clearDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dueDate.setText("");
                clearDueDate.setVisibility(View.GONE);
            }
        });

        // before we clear the small check box, make sure we're not editing a
        // task that has already a due date populated
        clearDueDate.setVisibility(View.GONE);
    }

    private void setControlsContent() {
        subject.setText(pojo.getSubject());
        note.setText(pojo.getNote());

        dueDate.setText(pojo.getDueDate());

        priority.setSelection(pojo.getPriority().ordinal());
        category.setSelection(pojo.getCategory());

        if ((pojo.getDueDate() != null)  && (pojo.getDueDate().isEmpty() == false) ) {
            clearDueDate.setVisibility(View.VISIBLE);
        }

        completed.setChecked(pojo.isDone());
    }

    private void updatePojo() {
        // grab the content of the controls and populate the pojo
        pojo.setSubject(subject.getText().toString());
        pojo.setNote(note.getText().toString());
        pojo.setDueDate(dueDate.getText().toString());
        pojo.setPriority(Todo.Priority.valueOf(priority.getSelectedItem().toString()));
        pojo.setCategory(category.getSelectedItemPosition());
        pojo.setDone(completed.isChecked());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);
        pojo = (Todo) getIntent().getSerializableExtra(MainActivity.TODO_POJO);

        grabControls();

        if (pojo != null) {
            setControlsContent();
        }

        setTitle((pojo==null)?"New ToDo":"Edit ToDo");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_todo, menu);
        return true;
    }

    private boolean performActionSave() {

        // we will be passing a pojo back to the caller
        if (pojo == null) {
            pojo = new Todo();
        }

        updatePojo();

        // validate the input first. we need at least a task's subject
        String subjectText = pojo.getSubject();

        if (subjectText == null || subjectText.isEmpty()) {
            View v = findViewById(android.R.id.content);
            if (v != null) {
                Snackbar.make(v, R.string.subject_is_empty, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            return false;
        }

        Intent data = new Intent();
        data.putExtra(MainActivity.TODO_POJO, pojo);

        setResult(RESULT_OK, data);
        finish();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
               return performActionSave();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putString(TodoEditActivity.DUE_DATE_EXTRA, dueDate.getText().toString());
        newFragment.setArguments(args);

        newFragment.show(getFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.due_date_format));
        String formatted = sdf.format(c.getTime());

        // populate the date field
        dueDate.setText(formatted);

        // show the clear date button
        clearDueDate.setVisibility(View.VISIBLE);
    }

}
