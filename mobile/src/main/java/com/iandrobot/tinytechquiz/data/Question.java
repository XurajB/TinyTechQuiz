package com.iandrobot.tinytechquiz.data;

import android.content.Context;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.iandrobot.tinytechquiz.Constants;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by surajbhattarai on 9/16/15.
 */
public class Question implements Serializable, Comparable<Question>{

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    private String question;
    private String[] options;
    private int correctAnswerIndex;
    private String category;
    private String difficulty;

    public int questionIndex;

    public boolean isSelected = false;

    public Question(String question, String[] options, String category, String difficulty, int correctAnswerIndex) {
        this.question = question;
        this.options = options;
        this.category = category;
        this.difficulty = difficulty;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public Question(String question, int questionIndex, String[] options, int correctAnswerIndex) {
        this.question = question;
        this.options = options;
        this.questionIndex = questionIndex;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public PutDataRequest toPutDataMapRequest() {
        PutDataMapRequest request = PutDataMapRequest.create("/question/" + questionIndex);
        DataMap dataMap = request.getDataMap();
        dataMap.putString(Constants.QUESTION, question);
        dataMap.putInt(Constants.QUESTION_INDEX, questionIndex);
        dataMap.putStringArray(Constants.ANSWERS, options);
        dataMap.putInt(Constants.CORRECT_ANSWER_INDEX, correctAnswerIndex);

        return request.asPutDataRequest();
    }

    /*
    public static void addSeedQuestions(Context context) {
        List<Question> list = new ArrayList<>();

        String qes1 = "What is the name of the flashing symbol that appear on a computer screen when typing?";
        String[] options1 = {"Chip","Circuit","Cursor","Mouse"};
        String cat1 = "Basic";
        String diff = "Easy";
        int answer = 2;
        Question q1 = new Question(qes1, options1, cat1, diff, answer);
        list.add(q1);
        //
        String qes2 = "What is the area inside the computer that allows the computer to store and/or act on information coded in binary code?";
        String[] options2 = {"Microprocessor","Memory","Circuit","Byte"};
        String cat2 = "Memory";
        String diff2 = "Easy";
        int answer2 = 1;
        Question q2 = new Question(qes2, options2, cat2, diff2, answer2);
        list.add(q2);
        //
        String qes3 = "What is the primary input device used to enter information and instructions into the computer?";
        String[] options3 = {"Scanner","Printer","Keyboard","Monitor"};
        String cat3 = "Basic";
        String diff3 = "Easy";
        int answer3 = 2;
        Question q3 = new Question(qes3, options3, cat3, diff3, answer3);
        list.add(q3);
        //
        qes2 = "What is the command given which stores information on a disk, tape, or other device?";
        String[] options4 = {"Print","Save","Add","Format"};
        cat2 = "Basic";
        diff2 = "Easy";
        answer2 = 1;
        q2 = new Question(qes2, options4, cat2, diff2, answer2);
        list.add(q2);
        //
        qes2 = "What term means to modify or change a document or file?";
        String[] options5 = {"Edit","Import","Justify","Close"};
        cat2 = "Document";
        diff2 = "Easy";
        answer2 = 0;
        q2 = new Question(qes2, options5, cat2, diff2, answer2);
        list.add(q2);
        //
        qes2 = "What is a single binary digit, 0 or 1? It is the smallest unit of data that a computer can process.";
        String[] options6 = {"Byte","Single","Bit","Boot"};
        cat2 = "Basic";
        diff2 = "Easy";
        answer2 = 2;
        q2 = new Question(qes2, options6, cat2, diff2, answer2);
        list.add(q2);
        //
        qes2 = "What is a tool that finds Web pages online based on terms and criteria specified by the user?";
        String[] options7 = {"Server","Network","Database","Search Engine"};
        cat2 = "Basic";
        diff2 = "Medium";
        answer2 = 3;
        q2 = new Question(qes2, options7, cat2, diff2, answer2);
        list.add(q2);
        //
        qes2 = "What is any hardware device that is attached to the computer, usually with a cable?";
        String[] options8 = {"Output Device","Input Device","Peripheral","Printer"};
        cat2 = "Hardware";
        diff2 = "Medium";
        answer2 = 2;
        q2 = new Question(qes2, options8, cat2, diff2, answer2);
        list.add(q2);
        //
        qes2 = "What word means to copy or move files from another computer system to your local computer system over a network?";
        String[] options9 = {"Upload","Format","Download","Function"};
        cat2 = "Internet";
        diff2 = "Easy";
        answer2 = 0;
        q2 = new Question(qes2, options9, cat2, diff2, answer2);
        list.add(q2);
        //
        qes2 = "What do we call two or more computers physically connected by cables to share information or hardware?";
        String[] options11 = {"Modem","Server","Network","Computer"};
        cat2 = "Network";
        diff2 = "Easy";
        answer2 = 2;
        q2 = new Question(qes2, options11, cat2, diff2, answer2);
        list.add(q2);
        //
        qes2 = "What is an on-screen display listing available options or functions?";
        String[] options12 = {"Menu","View","Mouse","Tool"};
        cat2 = "Basic";
        diff2 = "Easy";
        answer2 = 0;
        q2 = new Question(qes2, options12, cat2, diff2, answer2);
        list.add(q2);
        //
        qes2 = "What is part of a database that holds only one type of information?";
        String[] options13 = {"Report","Field","Table","Query"};
        cat2 = "Database";
        diff2 = "Medium";
        answer2 = 1;
        q2 = new Question(qes2, options13, cat2, diff2, answer2);
        list.add(q2);
        //
        qes2 = "What is a networked computer that is shared by multiple users?";
        String[] options14 = {"WAN","Server","WWW","Modem"};
        cat2 = "Network";
        diff2 = "Easy";
        answer2 = 1;
        q2 = new Question(qes2, options14, cat2, diff2, answer2);
        list.add(q2);

        //add questions
        QuestionsDataSource qd = new QuestionsDataSource(context);
        try {
            qd.open();
            for (Question q:list) {
                qd.addQuestion(q);
            }
            qd.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    */

    @Override
    public int compareTo(Question another) {
        return this.questionIndex - another.questionIndex;
    }
}
