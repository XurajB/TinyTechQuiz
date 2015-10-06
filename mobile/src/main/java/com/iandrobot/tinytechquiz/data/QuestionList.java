package com.iandrobot.tinytechquiz.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by surajbhattarai on 9/19/15.
 */
public class QuestionList implements Serializable {

    public List<Question> questions;

    public QuestionList(List<Question> questionList) {
        this.questions = questionList;
    }
}
