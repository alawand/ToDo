package com.sugarcube.todo.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sugarcube.todo.R;
import com.sugarcube.todo.models.Todo;
import com.sugarcube.todo.adapters.TodoDbAdapter;

public class TodoViewActivity extends AppCompatActivity {

    private TextView subject;
    private TextView dueDate;
    private TextView priority;
    private TextView category;
    private TextView status;
    private TextView note;

    private Todo pojo;

    private AlertDialog confirmDeleteDialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_todo, menu);
        return true;
    }

    private void grabControls() {
        subject = (TextView) findViewById(R.id.todo_view_subject);
        dueDate = (TextView) findViewById(R.id.todo_view_due_date);
        priority = (TextView) findViewById(R.id.todo_view_priority);
        category = (TextView) findViewById(R.id.todo_view_category);
        status = (TextView) findViewById(R.id.todo_view_status);
        note = (TextView) findViewById(R.id.todo_view_note);

    }

    private void updateControlsContent() {
        // populate the values
        subject.setText(pojo.getSubject());
        String dueDateText = pojo.getDueDate();

        if (dueDateText.isEmpty()) {
            dueDateText = "Due date not set";
        }

        dueDate.setText(dueDateText);
        priority.setText(pojo.getPriority().name());
        priority.setTextColor(getApplicationContext().getResources().getColor(pojo.getPriority().getColor()));

        // grab the category text
        String[] cat_array = getResources().getStringArray(R.array.category_items);
        category.setText(cat_array[pojo.getCategory()  - 1]);
        status.setText(pojo.isDone()?getString(R.string.task_completed):getString(R.string.task_in_progress));
        note.setText(pojo.getNote());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_view);

        Intent intent = getIntent();
        pojo = (Todo) intent.getSerializableExtra(MainActivity.TODO_POJO);

        grabControls();
        updateControlsContent();

        // build confirm delete dialog
        buildConfirmDialog();

        setTitle("View ToDo");
    }
    private void buildConfirmDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);

        ab.setTitle(R.string.delete_dialog_title);
        ab.setMessage(R.string.delete_dialog_message);
        ab.setPositiveButton(R.string.delete_dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TodoDbAdapter dbAdapter = new TodoDbAdapter(getBaseContext());
                dbAdapter.open();
                dbAdapter.deleteTodo(pojo.getTodoId());
                dbAdapter.close();

                // pass control back to the main activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        ab.setNegativeButton(R.string.delete_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });

        confirmDeleteDialog = ab.create();
    }

    private final int REQUEST_CODE = 80;

    private void launchEditTodo() {
        Intent intent = new Intent(getApplicationContext(), TodoEditActivity.class);

        // pass in the pojo that came in
        intent.putExtra(MainActivity.TODO_POJO, pojo);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                confirmDeleteDialog.show();
                return true;
            case R.id.action_edit:
                launchEditTodo();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == this.REQUEST_CODE && resultCode == RESULT_OK) {

            pojo = (Todo) data.getSerializableExtra(MainActivity.TODO_POJO);

            // this pojo should have an id -- it's an edit of a existing item
            TodoDbAdapter dbAdapter = new TodoDbAdapter(getBaseContext());
            dbAdapter.open();
            dbAdapter.updateTodo(   pojo.getTodoId(),
                                    pojo.getSubject(),
                                    pojo.getNote(),
                                    pojo.getPriority(),
                                    pojo.isDone(),
                                    pojo.getCategory(),
                                    pojo.getDueDate());
            dbAdapter.close();

            // reflect the changes of the db with these controls
            updateControlsContent();
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }
}
