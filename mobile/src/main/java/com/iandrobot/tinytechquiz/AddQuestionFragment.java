package com.iandrobot.tinytechquiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.iandrobot.tinytechquiz.data.Question;
import com.iandrobot.tinytechquiz.data.QuestionsDataSource;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by surajbhattarai on 9/17/15.
 */
public class AddQuestionFragment extends Fragment {

    @Bind(R.id.question_text)
    EditText questionTest;
    @Bind(R.id.option_a_text)
    EditText optionAText;
    @Bind(R.id.option_b_text)
    EditText optionBText;
    @Bind(R.id.option_c_text)
    EditText optionCText;
    @Bind(R.id.option_d_text)
    EditText optionDText;
    @Bind(R.id.options_radio_group)
    RadioGroup optionRadioGroup;

    private static final Map<Integer, Integer> radioIdToIndex;

    static {
        Map<Integer, Integer> temp = new HashMap<>(4);
        temp.put(R.id.option_a_radio, 0);
        temp.put(R.id.option_b_radio, 1);
        temp.put(R.id.option_c_radio, 2);
        temp.put(R.id.option_d_radio, 3);
        radioIdToIndex = Collections.unmodifiableMap(temp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_question, container, false);
        ButterKnife.bind(this, view);

        getActivity().setTitle("Create Question");

        return view;
    }

    @OnClick(R.id.add_question)
    public void addQuestion() {
        String questionText = questionTest.getText().toString();
        String[] options = new String[4];
        options[0] = optionAText.getText().toString();
        options[1] = optionBText.getText().toString();
        options[2] = optionCText.getText().toString();
        options[3] = optionDText.getText().toString();

        int correctOptionIndex = radioIdToIndex.get(optionRadioGroup.getCheckedRadioButtonId());

        //add category, difficulty
        String category = "Java";
        String difficulty = "Easy";

        Question question = new Question(questionText,options,category,difficulty, correctOptionIndex);

        QuestionsDataSource questionsDataSource = new QuestionsDataSource(getActivity());
        try {
            questionsDataSource.open();
            questionsDataSource.addQuestion(question);
            questionsDataSource.close();
            Toast.makeText(getActivity(), "Question Added", Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().popBackStack();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
