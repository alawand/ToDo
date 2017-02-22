package com.sugarcube.todo.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.sugarcube.todo.models.ToDoApplication;
import com.sugarcube.todo.models.Todo;
import com.sugarcube.todo.adapters.TodoAdapter;
import com.sugarcube.todo.adapters.TodoDbAdapter;
import com.sugarcube.todo.activities.MainActivity;
import com.sugarcube.todo.activities.TodoViewActivity;

import java.util.ArrayList;

/**
 * Created by Abed on 2/14/2017.
 */
public class MainActivityListFragment extends ListFragment {

    private final static String TAG = "MainActivityFrag";
    private ArrayList<Todo> todoList;
    private TodoAdapter todoAdapter;

    public TodoAdapter getTodoAdapter() {
        return todoAdapter;
    }

    public ArrayList<Todo> getTodoList() {
        return todoList;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        SharedPreferences sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = sharedPreferences.getString("sort_order", "1");

        ToDoApplication toDoApplication = (ToDoApplication) getActivity().getApplicationContext();

        int category = toDoApplication.getDefaultCategory();

        // wire code to the db
        TodoDbAdapter dbAdapter = new TodoDbAdapter(getActivity().getBaseContext());
        dbAdapter.open();
        todoList = dbAdapter.getAllTodos(Integer.parseInt(sortOrder), category);
        dbAdapter.close();
        todoAdapter = new TodoAdapter(getActivity(), todoList);
        setListAdapter(todoAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // get note
        Todo todo = (Todo) getListAdapter().getItem(position);

        Intent intent = new Intent(getActivity(), TodoViewActivity.class);

        // instead of passing the properties one at a time, serialize the whole object
        intent.putExtra(MainActivity.TODO_POJO, todo);

        // start
        startActivity(intent);
    }
} // eoc

