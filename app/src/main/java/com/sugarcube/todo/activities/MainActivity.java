package com.sugarcube.todo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.sugarcube.todo.fragments.MainActivityListFragment;
import com.sugarcube.todo.R;
import com.sugarcube.todo.models.Todo;
import com.sugarcube.todo.adapters.TodoDbAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // to pass extras between activities
    public static final String TODO_POJO     =  "com.sugarcube.todo.POJO";
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TodoEditActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
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

            // tell the fragment that data is changed
            FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.Fragment frag = fm.findFragmentById(R.id.mainActivityFrag);

            if (frag instanceof MainActivityListFragment) {

                SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
                String sortOrder = sharedPreferences.getString("sort_order", "1");
                MainActivityListFragment mf = (MainActivityListFragment) frag;

                ArrayList<Todo> todoList = mf.getTodoList();
                todoList.clear();
                todoList.addAll(dbAdapter.getAllTodos(Integer.parseInt(sortOrder)));
                mf.getTodoAdapter().notifyDataSetChanged();
            }
            dbAdapter.close();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
