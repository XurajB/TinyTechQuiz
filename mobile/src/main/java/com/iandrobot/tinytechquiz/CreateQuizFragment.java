package com.iandrobot.tinytechquiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iandrobot.tinytechquiz.data.Question;
import com.iandrobot.tinytechquiz.data.QuestionList;
import com.iandrobot.tinytechquiz.data.QuestionsAdapter;
import com.iandrobot.tinytechquiz.data.QuestionsDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by surajbhattarai on 9/16/15.
 */
public class CreateQuizFragment extends Fragment {

    @Bind(R.id.questions_recycle_view)
    RecyclerView questionList;

    List<Question> questions = null;
    List<Question> questionsToSend = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_quiz, container, false);
        getActivity().setTitle("Select Questions (0/10)");
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        QuestionsDataSource questionsDataSource = new QuestionsDataSource(getActivity());

        try {
            questionsDataSource.open();
            questions = questionsDataSource.getAllQuestions();
            questionsDataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity());
        questionList.setLayoutManager(layout);
        questionList.setHasFixedSize(true);

        QuestionsAdapter adapter = new QuestionsAdapter(getActivity(), questions);
        questionList.setAdapter(adapter);

    }

    @OnClick(R.id.send_quiz_button)
    public void sendQuiz() {
        questionsToSend = new ArrayList<>();
        for (Question q:questions) {
            if (q.isSelected) {
                questionsToSend.add(q);
            }
        }
        if (questionsToSend.size() > 0) {
            Bundle bundle = new Bundle();
            QuestionList questionList = new QuestionList(questionsToSend);
            bundle.putSerializable(QuizStatusFragment.serializableKey, questionList);

            QuizStatusFragment quizStatus = new QuizStatusFragment();
            quizStatus.setArguments(bundle);


            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.popBackStack();
            FragmentTransaction trans = manager.beginTransaction();
            trans.addToBackStack(null);
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            trans.replace(R.id.fragment_container, quizStatus);
            trans.commit();


        } else {
            Toast.makeText(getContext(), "Please select at least on question to send!", Toast.LENGTH_LONG).show();
        }
    }
}
