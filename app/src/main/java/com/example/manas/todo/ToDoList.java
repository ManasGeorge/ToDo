package com.example.manas.todo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import db.TaskContract;
import db.TaskDBHelper;


public class ToDoList extends ActionBarActivity {

    ListView listView;
    private SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        sqlDB = new TaskDBHelper(this).getWritableDatabase();
        listView = (ListView) findViewById(R.id.listView);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_to_do_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_item:
                Log.d("MainActivity", "Add new task");
                addItem();
                return true;
            default:
                return false;
        }
    }

    private void addItem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.add_task_dialog, null);
        builder.setView(view);
        builder.setTitle(R.string.add_task_title);

        final TextView inputField = (TextView)view.findViewById(R.id.add_task_text);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String task = inputField.getText().toString();
                Log.d("MainActivity", task);

                ContentValues values = new ContentValues();

                values.clear();
                values.put(TaskContract.Columns.TASK, task);

                sqlDB.insertWithOnConflict(TaskContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                updateUI();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void updateUI() {
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns.TASK, TaskContract.Columns._ID},
                null,null,null,null,null);

        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item,
                cursor,
                new String[] {TaskContract.Columns.TASK},
                new int[] {R.id.label},
                0
        );

        listView.setAdapter(adapter);
    }

    public void onDoneButtonClick(View view){
        View v = (View) view.getParent();
        TextView tv = (TextView) v.findViewById(R.id.label);
        String task = tv.getText().toString();

        String sql = String.format("DELETE FROM %s WHERE %s == '%s'",
                TaskContract.TABLE,
                TaskContract.Columns.TASK,
                task);

        sqlDB.execSQL(sql);
        updateUI();
    }
}
