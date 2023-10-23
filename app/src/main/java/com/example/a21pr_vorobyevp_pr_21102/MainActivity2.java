package com.example.a21pr_vorobyevp_pr_21102;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity2 extends AppCompatActivity {

    EditText nameBox;
    EditText yearBox;
    Button delButton;
    Button saveButton;

    DB_helper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        nameBox = findViewById(R.id.name);
        yearBox = findViewById(R.id.year);
        delButton = findViewById(R.id.deleteButton);
        saveButton = findViewById(R.id.saveButton);

        sqlHelper = new DB_helper(this);
        db = sqlHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }
        // если 0, то добавление
        if (userId > 0) {
            // получаем элемент по id из бд
            userCursor = db.rawQuery("select * from " + DB_helper.TABLE + " where " +
                    DB_helper.COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
            userCursor.moveToFirst();
            nameBox.setText(userCursor.getString(1));
            yearBox.setText(String.valueOf(userCursor.getInt(2)));
            userCursor.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
        }
    }

    public void save(View view){
        ContentValues cv = new ContentValues();
        cv.put(DB_helper.COLUMN_NAME, nameBox.getText().toString());
        cv.put(DB_helper.COLUMN_YEAR, Integer.parseInt(yearBox.getText().toString()));

        if (userId > 0) {
            db.update(DB_helper.TABLE, cv, DB_helper.COLUMN_ID + "=" + userId, null);
        } else {
            db.insert(DB_helper.TABLE, null, cv);
        }
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        goHome();
    }
    public void delete(View view){
        db.delete(DB_helper.TABLE, "_id = ?", new String[]{String.valueOf(userId)});
        goHome();
    }
    private void goHome(){
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}