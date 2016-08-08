package com.example;

import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;

public class SQLDemoActivity extends Activity
{
    EventDataSQLHelper eventsData;
    private static final String DB_PASSWORD = "foo123";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // You must set Context on SQLiteDatabase first.
        SQLiteDatabase.loadLibs(this);

        // Then you can open the writeable database using a password.
        eventsData = new EventDataSQLHelper(this);
        SQLiteDatabase db = eventsData.getWritableDatabase(DB_PASSWORD);

        for (int i = 1; i < 100; i++)
        {
            addEvent("Hello Android Event: " + i, db);
        }
        db.close();

        // Read from the database.
        db = eventsData.getReadableDatabase(DB_PASSWORD);
        Cursor cursor = getEvents(db);
        showEvents(cursor);
        db.close();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        eventsData.close();
    }

    private void addEvent(String title, SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();
        values.put(EventDataSQLHelper.TIME, System.currentTimeMillis());
        values.put(EventDataSQLHelper.TITLE, title);
        db.insert(EventDataSQLHelper.TABLE, null, values);
    }

    private Cursor getEvents(SQLiteDatabase db)
    {
        Cursor cursor = db.query(EventDataSQLHelper.TABLE, null, null, null, null,
                null, null);

        startManagingCursor(cursor);
        return cursor;
    }

    private void showEvents(Cursor cursor)
    {
        StringBuilder ret = new StringBuilder("Saved Events:\n\n");
        while (cursor.moveToNext())
        {
            long id = cursor.getLong(0);
            long time = cursor.getLong(1);
            String title = cursor.getString(2);
            ret.append(id + ": " + time + ": " + title + "\n");
        }

        Log.i("sqldemo",ret.toString());
    }
}
