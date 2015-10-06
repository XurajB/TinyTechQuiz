package com.iandrobot.tinytechquiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by surajbhattarai on 9/16/15.
 */
public class MainFragment extends Fragment {

    @Bind (R.id.add_question_button)
    Button addQuestionButton;
    @Bind (R.id.create_quiz_button)
    Button createQuizButton;
    @Bind (R.id.random_quiz_button)
    Button randomQuizButton;
    @Bind (R.id.help_button)
    Button helpButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Tiny Tech Quiz");
        animateButtons();
    }

    @OnClick(R.id.add_question_button)
    public void addQuestion() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .replace(R.id.fragment_container, new AddQuestionFragment())
                .commit();
    }

    @OnClick(R.id.create_quiz_button)
    public void createQuiz() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .replace(R.id.fragment_container, new CreateQuizFragment())
                .commit();
    }

    private void animateButtons() {

        int duration = 50;

        final Animation anim = new TranslateAnimation(-500f, 0f,0f,0f);
        anim.setDuration(duration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());

        final Animation anim2 = new TranslateAnimation(500f, 0f,0f,0f);
        anim2.setDuration(duration);
        anim2.setInterpolator(new AccelerateDecelerateInterpolator());

        final Animation anim3 = new TranslateAnimation(-500f, 0f,0f,0f);
        anim3.setDuration(duration);
        anim3.setInterpolator(new AccelerateDecelerateInterpolator());

        final Animation anim4 = new TranslateAnimation(500f, 0f,0f,0f);
        anim4.setDuration(duration);
        anim4.setInterpolator(new AccelerateDecelerateInterpolator());

        createQuizButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                createQuizButton.startAnimation(anim);
                createQuizButton.setVisibility(View.VISIBLE);
            }
        }, duration);

        addQuestionButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                addQuestionButton.startAnimation(anim2);
                addQuestionButton.setVisibility(View.VISIBLE);
            }
        }, duration*2);

        randomQuizButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                randomQuizButton.startAnimation(anim3);
                randomQuizButton.setVisibility(View.VISIBLE);
            }
        }, duration*3);

        helpButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                helpButton.startAnimation(anim4);
                helpButton.setVisibility(View.VISIBLE);
            }
        }, duration*4);
    }
}
