package com.sugarcube.todo.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sugarcube.todo.models.Todo;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Abed on 2/15/2017.
 */

public class TodoDbAdapter {

    public static final String DATABASE_NAME = "todo.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TODO_TABLE = "todos";

    public static final String COLUMN_ID  = "id";
    public static final String COLUMN_SUBJECT  = "subject"; // required
    public static final String COLUMN_NOTE  = "note"; // might be null
    public static final String COLUMN_CATEGORY  = "category"; // integer will map to index of categories 0 default, 1 personal, etc.. 
    public static final String COLUMN_PRIORITY = "priority";  // integer 0 low, 1 medium, 2 high
    public static final String COLUMN_DUE_DATE = "due_date";  // might be null
    public static final String COLUMN_STATUS   = "status";   // integer -- for now, 0 = in progress | 1 = done
    public static final String COLUMN_CREATED_ON     = "created_on";  // might be null

    //priority desc, subject asc
    public static final String ORDER_BY_PRIORTY  = COLUMN_PRIORITY + " desc,  " + COLUMN_SUBJECT  + " asc";
    public static final String ORDER_BY_DUE_DATE = COLUMN_DUE_DATE+ " asc,  " + COLUMN_SUBJECT  + " asc";


    private String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_SUBJECT ,COLUMN_NOTE, COLUMN_CATEGORY, COLUMN_PRIORITY, COLUMN_STATUS, COLUMN_DUE_DATE,COLUMN_CREATED_ON};

    public static final String  CREATE_TABLE_TODO_STATEMENT =
            "create table " + TODO_TABLE + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_SUBJECT + " text not null, "
                    + COLUMN_NOTE + " text, "
                    + COLUMN_CATEGORY + " integer not null, "
                    + COLUMN_PRIORITY + " integer not null, "
                    + COLUMN_STATUS   + " integer not null, "
                    + COLUMN_DUE_DATE + " text, "
                    + COLUMN_CREATED_ON
                    + ");" ;

    private SQLiteDatabase sqlDB;
    private Context context;
    private TodoDbHelper todoDbHelper;

    public TodoDbAdapter(Context ctx) {
        context = ctx;
    }

    public TodoDbAdapter open() throws android.database.SQLException {
        todoDbHelper = new TodoDbHelper(context);
        sqlDB = todoDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        todoDbHelper.close();
    }


    public Todo createTodo(String subject, String note, Todo.Priority priority, boolean done, int category, String dueDate) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_SUBJECT, subject);
        if (note != null) {
            values.put(COLUMN_NOTE, note);
        }
        else {
            values.putNull(COLUMN_NOTE);
        }

        values.put(COLUMN_PRIORITY, priority.ordinal());
        int status = 0;
        if (done) {
            status = 1;
        }

        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_CATEGORY, category);

        if (dueDate!= null) {
            values.put(COLUMN_DUE_DATE, dueDate);
        }
        else {
            values.putNull(COLUMN_DUE_DATE);
        }

        values.put(COLUMN_CREATED_ON, Calendar.getInstance().getTimeInMillis());
        //values.put(COLUMN_CREATED_ON, Long.toString(Calendar.getInstance().getTimeInMillis()));

        long insertId = sqlDB.insert(TODO_TABLE, null, values);

        Cursor cursor = sqlDB.query(TODO_TABLE, ALL_COLUMNS, COLUMN_ID + " = " +insertId, null,null,null,null);
        cursor.moveToFirst();
        Todo todo = cursorToTodo(cursor);
        cursor.close();

        return todo;
    }


    public long updateTodo (long id, String subject, String note, Todo.Priority priority, boolean done, int category, String dueDate) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_SUBJECT, subject);
        if (note != null) {
            values.put(COLUMN_NOTE, note);
        }
        else {
            values.putNull(COLUMN_NOTE);
        }

        values.put(COLUMN_PRIORITY, priority.ordinal());
        int status = 0;
        if (done) {
            status = 1;
        }
        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_CATEGORY, category);

        if (dueDate!= null) {
            values.put(COLUMN_DUE_DATE, dueDate);
        }
        else {
            values.putNull(COLUMN_DUE_DATE);
        }

        values.put(COLUMN_CREATED_ON, Calendar.getInstance().getTimeInMillis());
        //values.put(COLUMN_CREATED_ON, Long.toString(Calendar.getInstance().getTimeInMillis()));


        return sqlDB.update(TODO_TABLE, values, COLUMN_ID + " = " + id, null);
    }

    public long deleteTodo (long id) {
        return sqlDB.delete(TODO_TABLE,  COLUMN_ID + " = " + id, null);
    }

    public ArrayList<Todo> getAllTodos(int sortOrder, int category) {

        String orderBy;
        String whereClause = null;
        String[] whereArgs = null;

        if (sortOrder == 1) {
            orderBy = ORDER_BY_PRIORTY;
        }
        else {
            orderBy = ORDER_BY_DUE_DATE;
        }

        if (category != 0) {
            whereClause = COLUMN_CATEGORY + " = ?";

            whereArgs = new String[1];
            whereArgs[0] = Integer.toString(category);

        }

        ArrayList<Todo> notes = new ArrayList<>();
        Cursor cursor = sqlDB.query(TODO_TABLE, ALL_COLUMNS, whereClause, whereArgs ,null,null, orderBy);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        //for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious())
        {
            Todo note = cursorToTodo(cursor);
            notes.add(note);
        }
        cursor.close();
        return  notes;
    }

    private Todo cursorToTodo(Cursor cursor) {

        long id = cursor.getLong(0);
        String subject = cursor.getString(1);
        String note = cursor.getString(2);
        int category = cursor.getInt(3);
        Todo.Priority priority = Todo.Priority.values()[cursor.getInt(4)];
        boolean done = false;
        int status = cursor.getInt(5);
        if (status == 1) {
            done = true;
        }
        String dueDate = cursor.getString(6);
        long createdOn = cursor.getLong(7);

        Todo todo = new Todo(subject, note, priority, done,category, dueDate, id, createdOn);

        return  todo;
    }

    private static class TodoDbHelper extends SQLiteOpenHelper {

        public TodoDbHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_TODO_STATEMENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // drop table and recreate it
            db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
            onCreate(db);
        }
    }
}
