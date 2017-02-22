package com.sugarcube.todo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.sugarcube.todo.fragments.MainActivityListFragment;
import com.sugarcube.todo.R;
import com.sugarcube.todo.models.ToDoApplication;
import com.sugarcube.todo.models.Todo;
import com.sugarcube.todo.adapters.TodoDbAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // to pass extras between activities
    public static final String TODO_POJO     =  "com.sugarcube.todo.POJO";
    private final static String TAG = "MainActivity";

    private final int REQUEST_CODE = 20;
    private int currentSelection;
    private boolean initialSelection;
    private Spinner navSpinner;


    @Override
    protected void onStart() {
        super.onStart();

        navSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (initialSelection) {
                    initialSelection = false;
                }
                else {
                    // manual selection
                    if (currentSelection != position) {
                        currentSelection = position;
                        ToDoApplication toDoApplication = (ToDoApplication) getApplicationContext();
                        toDoApplication.setDefaultCategory(currentSelection);
                        refreshToDoList();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        loadPreferences();

        // grab the spinner
        navSpinner  = (Spinner) findViewById(R.id.spinner_nav);

        // set the spinner selection based on the shared prefs
        ToDoApplication toDoApplication = (ToDoApplication) getApplicationContext();
        int cat = toDoApplication.getDefaultCategory();
        navSpinner.setSelection(cat);
        currentSelection = cat;

        // will keep track of the first time the nav spinner is called
        // might need to reset it more often
        initialSelection = true;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TodoEditActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isDark = sharedPreferences.getBoolean("background_color", false);
        if (isDark) {
            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.content_main);
            mainLayout.setBackgroundColor(Color.parseColor("#3c3f41"));
        }

    }

    private void refreshToDoList() {
        // tell the fragment that data is changed
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment frag = fm.findFragmentById(R.id.mainActivityFrag);

        if (frag instanceof MainActivityListFragment) {
            TodoDbAdapter dbAdapter = new TodoDbAdapter(getBaseContext());
            dbAdapter.open();

            SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
            String sortOrder = sharedPreferences.getString("sort_order", "1");

            MainActivityListFragment mf = (MainActivityListFragment) frag;
            ArrayList<Todo> todoList = mf.getTodoList();
            todoList.clear();
            todoList.addAll(dbAdapter.getAllTodos(Integer.parseInt(sortOrder), currentSelection));
            mf.getTodoAdapter().notifyDataSetChanged();
            dbAdapter.close();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
          // we got back something from the edit activity

            Todo pojo = (Todo) data.getSerializableExtra(MainActivity.TODO_POJO);

            if (pojo == null) {
                return;
            }

            // make a call to persist this new task
            TodoDbAdapter dbAdapter = new TodoDbAdapter(getBaseContext());
            dbAdapter.open();
            dbAdapter.createTodo(pojo.getSubject(),
                                 pojo.getNote(),
                                 pojo.getPriority(),
                                 pojo.isDone(),
                                 pojo.getCategory(),
                                 pojo.getDueDate());
            dbAdapter.close();

            // update list content
            refreshToDoList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AppPreferences.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
