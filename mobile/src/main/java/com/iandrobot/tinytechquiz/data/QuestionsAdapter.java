package com.iandrobot.tinytechquiz.data;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.iandrobot.tinytechquiz.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by surajbhattarai on 9/17/15.
 */
public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {

    //to keep context
    static Activity mContext;
    static List<Question> mQuestions;

    static int selectedQuestionCount = 0;

    private static final Map<Integer, Integer> textIdToAnswerIndex;

    static {
        Map<Integer, Integer> temp = new HashMap<>(4);
        temp.put(0, R.id.option_layout_a_text);
        temp.put(1, R.id.option_layout_b_text);
        temp.put(2, R.id.option_layout_c_text);
        temp.put(3, R.id.option_layout_d_text);
        textIdToAnswerIndex = Collections.unmodifiableMap(temp);
    }

    public QuestionsAdapter(Activity context, List<Question> questions) {
        mContext = context;
        mQuestions = questions;
        selectedQuestionCount = 0;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.question_single, viewGroup, false);
        ButterKnife.bind(this, view);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QuestionViewHolder questionViewHolder, int i) {
        final Question question = mQuestions.get(i);
        questionViewHolder.questionSelectCheck.setChecked(question.isSelected);
        questionViewHolder.questionText.setText(question.getQuestion());
        questionViewHolder.categoryText.setText(question.getCategory());
        questionViewHolder.difficultyText.setText(question.getDifficulty());

        if (questionViewHolder.questionSelectCheck.isChecked()) {
            questionViewHolder.questionContainer.setBackgroundColor(Color.LTGRAY);
        } else questionViewHolder.questionContainer.setBackgroundColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.create_quiz_question_textview)
        TextView questionText;
        @Bind(R.id.create_quiz_category_textview)
        TextView categoryText;
        @Bind(R.id.create_quiz_difficulty_textview)
        TextView difficultyText;
        @Bind(R.id.question_select_check_box)
        CheckBox questionSelectCheck;
        @Bind(R.id.question_container)
        LinearLayout questionContainer;

        private static final Map<Boolean, Integer> checkedToColorIndex;

        static {
            Map<Boolean, Integer> temp = new HashMap<>(2);
            temp.put(true, Color.LTGRAY);
            temp.put(false, Color.WHITE);
            checkedToColorIndex = Collections.unmodifiableMap(temp);
        }

        @OnClick(R.id.question_container)
        public void onClickQuestionContainer() {
            if (questionSelectCheck.isChecked()) {
                questionSelectCheck.setChecked(false);
                changeBackground(Color.WHITE);
            }
            else {
                questionSelectCheck.setChecked(true);
                changeBackground(Color.LTGRAY);
            }
            mQuestions.get(getAdapterPosition()).isSelected = questionSelectCheck.isChecked();
        }

        @OnCheckedChanged(R.id.question_select_check_box)
        public void onChecked(CheckBox c) {
            mQuestions.get(getAdapterPosition()).isSelected = c.isChecked();
            questionContainer.setBackgroundColor(checkedToColorIndex.get(c.isChecked()));
            if (c.isShown()) {
            if (c.isChecked()) {
                selectedQuestionCount++;
            } else selectedQuestionCount--;
            mContext.setTitle("Select Questions (" + selectedQuestionCount + "/10)"); }
        }

        public void changeBackground(int color) {
            questionContainer.setBackgroundColor(color);
        }

        @OnClick(R.id.create_quiz_view_options_button)
        public void showOptions() {
            // custom dialog
            final Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.options_layout);
            dialog.setTitle("Options");

            // set the custom dialog components - text, image and button
            TextView optionAText = (TextView)dialog.findViewById(R.id.option_layout_a_text);
            TextView optionBText = (TextView)dialog.findViewById(R.id.option_layout_b_text);
            TextView optionCText = (TextView)dialog.findViewById(R.id.option_layout_c_text);
            TextView optionDText = (TextView)dialog.findViewById(R.id.option_layout_d_text);
            TextView optionTitle = (TextView)dialog.findViewById(R.id.option_layout_title_text);

            Question question = mQuestions.get(getAdapterPosition());
            String[] options = question.getOptions();
            int editTextId = textIdToAnswerIndex.get(question.getCorrectAnswerIndex());

            TextView answer = (TextView)dialog.findViewById(editTextId);
            answer.setTextColor(Color.GREEN);

            optionAText.setText(options[0]);
            optionBText.setText(options[1]);
            optionCText.setText(options[2]);
            optionDText.setText(options[3]);
            optionTitle.setText(question.getQuestion());

            dialog.show();
        }

        public QuestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}