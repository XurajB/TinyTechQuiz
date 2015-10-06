package com.iandrobot.tinytechquiz;

/**
 * Created by suraj bhattarai on 9/20/15.
 *
 */
public final class Constants {
    private Constants() {}

    public static final String ANSWERS = "answers";
    public static final String CHOSEN_ANSWER_CORRECT = "chosen_answer_correct";
    public static final String CORRECT_ANSWER_INDEX = "correct_answer_index";
    public static final String QUESTION = "question";
    public static final String QUESTION_INDEX = "question_index";
    public static final String QUESTION_WAS_ANSWERED = "question_was_answered";
    public static final String QUESTION_WAS_DELETED = "question_was_deleted";

    public static final String NUM_CORRECT = "num_correct";
    public static final String NUM_INCORRECT = "num_incorrect";
    public static final String NUM_SKIPPED = "num_skipped";

    public static final String QUIZ_ENDED_PATH = "/quiz_ended";
    public static final String QUIZ_EXITED_PATH = "/quiz_exited";
    public static final String RESET_QUIZ_PATH = "/reset_quiz";

    public static final int CONNECT_TIMEOUT_MS = 100;
    public static final int GET_CAPABILITIES_TIMEOUT_MS = 5000;
}