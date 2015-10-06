package com.iandrobot.tinytechquiz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by surajbhattarai on 9/16/15.
 */
public class QuestionsDataSource {

    //db fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_QUESTION,
        MySQLiteHelper.COLUMN_OPTION_1, MySQLiteHelper.COLUMN_OPTION_2,
        MySQLiteHelper.COLUMN_OPTION_3, MySQLiteHelper.COLUMN_OPTION_4,
        MySQLiteHelper.COLUMN_CATEGORY, MySQLiteHelper.COLUMN_DIFFICULTY,
        MySQLiteHelper.COLUMN_ANSWER_INDEX};

    public QuestionsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void addQuestion(Question question) {

        String[] options = question.getOptions();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_QUESTION, question.getQuestion());
        values.put(MySQLiteHelper.COLUMN_OPTION_1, options[0]);
        values.put(MySQLiteHelper.COLUMN_OPTION_2, options[1]);
        values.put(MySQLiteHelper.COLUMN_OPTION_3, options[2]);
        values.put(MySQLiteHelper.COLUMN_OPTION_4, options[3]);
        values.put(MySQLiteHelper.COLUMN_CATEGORY, question.getCategory());
        values.put(MySQLiteHelper.COLUMN_DIFFICULTY, question.getDifficulty());
        values.put(MySQLiteHelper.COLUMN_ANSWER_INDEX, question.getCorrectAnswerIndex());

        long id = database.insert(MySQLiteHelper.TABLE_QUESTIONS, null, values);
    }

    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_QUESTIONS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            questions.add(cursorToQuestion(cursor));
            cursor.moveToNext();
        }

        return questions;
    }

    private Question cursorToQuestion(Cursor cursor) {
        String question = cursor.getString(1);
        String[] options = new String[4];
        options[0] = cursor.getString(2);
        options[1] = cursor.getString(3);
        options[2] = cursor.getString(4);
        options[3] = cursor.getString(5);

        String category = cursor.getString(6);
        String difficulty = cursor.getString(7);
        int answerIndex = cursor.getInt(8);

        return new Question(question, options, category, difficulty, answerIndex);
    }

}