package com.sugarcube.todo.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Abed on 2/14/2017.
 */

public class Todo implements Serializable{

    private static final long serialVersionUID = 5177222050535318999L;

    private String subject;
    private String note;
    private Priority priority;
    private boolean done;
    private int category;
    private String dueDate;

    // generated
    private long todoId;
    private long dateCreated;


    public enum Priority {
        LOW, MEDIUM, HIGH;

        public int getColor() {
            int color  = android.R.color.background_dark;

            switch (this) {
                case LOW:
                    color = android.R.color.holo_green_dark;
                    break;
                case MEDIUM:
                    color =  android.R.color.holo_orange_dark;
                    break;
                case HIGH:
                    color =  android.R.color.holo_red_dark;
                    break;
            }
            return  color;
        }

    }



    public Todo(String subject, String note, Priority priority, boolean done, int category, String dueDate, long todoId, long dateCreated) {
        this.subject = subject;
        this.note = note;
        this.priority = priority;
        this.done = done;
        this.category = category;
        this.dueDate = dueDate;
        this.todoId = todoId;
        this.dateCreated = dateCreated;
    }

    public Todo() {
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getSubject() {
        return subject;
    }

    public String getNote() {
        return note;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isDone() {
        return done;
    }

    public int getCategory() {
        return category;
    }

    public String getDueDate() {
        return dueDate;
//        Date today = Calendar.getInstance().getTime();
//        return today.toString();
    }

    public long getTodoId() {
        return todoId;
    }

    public long getDateCreated() {
        return dateCreated;
    }
}
