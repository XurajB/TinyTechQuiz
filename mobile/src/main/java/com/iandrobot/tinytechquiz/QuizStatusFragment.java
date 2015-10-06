package com.iandrobot.tinytechquiz;

import static com.iandrobot.tinytechquiz.Constants.*;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.iandrobot.tinytechquiz.R;
import com.iandrobot.tinytechquiz.data.Question;
import com.iandrobot.tinytechquiz.data.QuestionList;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by surajbhattarai on 9/19/15.
 */
public class QuizStatusFragment extends Fragment implements DataApi.DataListener,
        MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static String serializableKey = "QUESTIONS";
    private List<Question> questionList;

    private GoogleApiClient mGoogleApiClient;
    private Activity mContext;

    private PriorityQueue<Question> mFutureQuestions;
    private int mQuestionIndex = 0;
    private boolean mHasQuestionBeenAsked = false;

    //data for end report
    private int mNumCorrect = 0;
    private int mNumIncorrect = 0;
    private int mNumSkipped = 0;

    private final String TAG = "TinyTechQuiz";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_status_fragment, container, false);
        getActivity().setTitle("Quiz Status");
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        mContext = getActivity();

        QuestionList list = (QuestionList)bundle.getSerializable(serializableKey);
        questionList = list.questions;

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mFutureQuestions = new PriorityQueue<>(10);
    }

    @Bind(R.id.question_status_container)
    LinearLayout questionsContainer;

    @Override
    public void onStart() {
        super.onStart();

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();


    }

    @Override
    public void onStop() {
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);

        //Tell wearable to end the quiz
        DataMap dataMap = new DataMap();
        dataMap.putInt(NUM_CORRECT, mNumCorrect);
        dataMap.putInt(NUM_INCORRECT, mNumIncorrect);
        if (mHasQuestionBeenAsked) {
            mNumSkipped++;
        }
        mNumSkipped += mFutureQuestions.size();
        dataMap.putInt(NUM_SKIPPED, mNumSkipped);
        if (mNumCorrect + mNumIncorrect + mNumSkipped > 0) {
            sendMessageToWearable(QUIZ_EXITED_PATH, dataMap.toByteArray());
        }

        clearQuizStatus();
        super.onStop();
    }

    private void addQuestionToContainer(Question question) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View questionElement = inflater.inflate(R.layout.question_status_single, null, false);
        ((TextView)questionElement.findViewById(R.id.question_status_question))
                .setText(question.getQuestion());
        ((TextView)questionElement.findViewById(R.id.question_status_status))
                .setText("This questions has not been answered yet");
        questionsContainer.addView(questionElement);
    }

    //adds the question the the wearable's stream that will create corresponding notification
    private void addQuestionDataItem(Question question) {
        if (!mHasQuestionBeenAsked) {
            Wearable.DataApi.putDataItem(mGoogleApiClient, question.toPutDataMapRequest());
            setHasQuestionBeenAsked(true);
            Log.d(TAG, "addQuestionDataItem - sent to device");
        } else {
            mFutureQuestions.add(question);
            Log.d(TAG, "addQuestionDataItem - add to Future list");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d(TAG, "onConnected");

        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);

        newQuiz();

        for (Question question:questionList) {
            question.questionIndex = mQuestionIndex++;
            addQuestionToContainer(question);

            //send questions
            addQuestionDataItem(question);
            //setNewQuestionStatus(question.getQuestion());
        }

        askNextQuestionIfExists();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        // freeze dataevents so they will exist later on the UI thread
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DataEvent event : events) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem())
                                .getDataMap();
                        boolean questionWasAnswered = dataMap.getBoolean(QUESTION_WAS_ANSWERED);
                        boolean questionWasDeleted = dataMap.getBoolean(QUESTION_WAS_DELETED);
                        if (questionWasAnswered) {
                            //update status
                            int questionIndex = dataMap.getInt(QUESTION_INDEX);
                            boolean questionCorrect = dataMap.getBoolean(CHOSEN_ANSWER_CORRECT);
                            updateQuestionStatus(questionIndex, questionCorrect);
                            askNextQuestionIfExists();
                        } else if (questionWasDeleted) {
                            int questionIndex = dataMap.getInt(QUESTION_INDEX);
                            markQuestionLeftBlank(questionIndex);
                            askNextQuestionIfExists();
                        }
                    }
                }
            }
        });
    }

    //updates the given question based on whether it was answered correctly or not
    //it also changes text colors of the question text and status text
    public void updateQuestionStatus(int questionIndex, boolean questionCorrect) {
        LinearLayout questionElement = (LinearLayout)questionsContainer.getChildAt(questionIndex);
        TextView question = (TextView)questionElement.findViewById(R.id.question_status_question);
        TextView status = (TextView)questionElement.findViewById(R.id.question_status_status);
        if (questionCorrect) {
            question.setTextColor(Color.GREEN);
            status.setText("This question has been answered correctly!");
            mNumCorrect++;
        } else {
            question.setTextColor(Color.RED);
            status.setText("This question has been answered incorrectly");
            mNumIncorrect++;
        }
    }

    //marks a question as left blank when its question notification is deleted
    private void markQuestionLeftBlank(int index) {
        LinearLayout questionElement = (LinearLayout)questionsContainer.getChildAt(index);
        if (questionElement!=null) {
            TextView question = (TextView)questionElement.findViewById(R.id.question_status_question);
            TextView status = (TextView)questionElement.findViewById(R.id.question_status_status);
            if (question.getText().equals("This questions has not been answered yet")) {
                question.setTextColor(Color.YELLOW);
                status.setText("This question was left blank");
                mNumSkipped++;
            }
        }
    }

    //asks the next enqueued question if exists or end the quiz
    private void askNextQuestionIfExists() {
        if (mFutureQuestions.isEmpty()) {
            DataMap dataMap = new DataMap();
            dataMap.putInt(NUM_CORRECT, mNumCorrect);
            dataMap.putInt(NUM_INCORRECT, mNumIncorrect);
            dataMap.putInt(NUM_SKIPPED, mNumSkipped);
            sendMessageToWearable(QUIZ_ENDED_PATH, dataMap.toByteArray());
            setHasQuestionBeenAsked(false);
        } else {
            Wearable.DataApi.putDataItem(mGoogleApiClient,
                    mFutureQuestions.remove().toPutDataMapRequest());
            setHasQuestionBeenAsked(true);
        }
    }

    private void sendMessageToWearable(final String path, final byte[] data) {
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                        for (Node node:nodes.getNodes()) {
                            Wearable.MessageApi
                                    .sendMessage(mGoogleApiClient, node.getId(), path, data);
                            Log.d(TAG, "message sent");
                        }

                        if (path.equals(QUIZ_EXITED_PATH) && mGoogleApiClient.isConnected()) {
                            mGoogleApiClient.disconnect();
                        }
                    }
                }
        );
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(RESET_QUIZ_PATH)) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resetQuiz();
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //reset quiz status in phone
    public void resetQuiz() {
        for (int i=0; i<questionsContainer.getChildCount(); i++) {
            LinearLayout questionElement = (LinearLayout)questionsContainer.getChildAt(i);
            TextView question = (TextView)questionElement.findViewById(R.id.question_status_question);
            TextView questionStatus = (TextView)questionElement.findViewById(R.id.question_status_status);

            question.setTextColor(Color.WHITE);
            questionStatus.setText("This question has not been answered yet");
        }

        if (mGoogleApiClient.isConnected()) {
            Wearable.DataApi.getDataItems(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DataItemBuffer>() {
                        @Override
                        public void onResult(DataItemBuffer dataItems) {
                            try {
                                if (dataItems.getStatus().isSuccess()) {
                                    resetDataItems(dataItems);
                                } else {
                                    //failed to get data items to reset
                                }
                            } finally {
                                dataItems.release();
                            }
                        }
                    });
        } else {
            //client disconnected
        }
        setHasQuestionBeenAsked(false);
        mNumCorrect = 0;
        mNumIncorrect = 0;
        mNumSkipped = 0;

    }

    private void setHasQuestionBeenAsked(boolean b) {
        mHasQuestionBeenAsked = b;

        //use this to update ui.. like reset button or new quiz
    }

    private void resetDataItems(DataItemBuffer dataItemBuffer) {
        if (mGoogleApiClient.isConnected()) {
            for (final DataItem dataItem : dataItemBuffer) {
                final Uri dataItemUri = dataItem.getUri();
                Wearable.DataApi.getDataItem(mGoogleApiClient, dataItemUri)
                        .setResultCallback(new ResetDataItemCallback());
            }
        } else {
            Log.d(TAG, "ResetDataItem: GoogleApiClient is not connected");
        }
    }

    //removes quiz status views
    private void clearQuizStatus() {
        questionsContainer.removeAllViews();

        setHasQuestionBeenAsked(false);
        mFutureQuestions.clear();
        mQuestionIndex = 0;
        mNumCorrect = 0;
        mNumIncorrect = 0;
        mNumSkipped = 0;
    }

    private class ResetDataItemCallback implements ResultCallback<DataApi.DataItemResult> {

        @Override
        public void onResult(DataApi.DataItemResult dataItemResult) {
            if (dataItemResult.getStatus().isSuccess()) {
                PutDataMapRequest request = PutDataMapRequest.createFromDataMapItem(
                        DataMapItem.fromDataItem(dataItemResult.getDataItem()));
                DataMap dataMap = request.getDataMap();
                dataMap.putBoolean(QUESTION_WAS_ANSWERED, false);
                dataMap.putBoolean(QUESTION_WAS_DELETED, false);

                if (!mHasQuestionBeenAsked && dataMap.getInt(QUESTION_INDEX) == 0) {
                    // ask first question
                    Wearable.DataApi.putDataItem(mGoogleApiClient, request.asPutDataRequest());
                    setHasQuestionBeenAsked(true);
                } else {
                    // enqueue future question
                    mFutureQuestions.add(new Question(dataMap.getString(QUESTION),
                            dataMap.getInt(QUESTION_INDEX), dataMap.getStringArray(ANSWERS),
                            dataMap.getInt(CORRECT_ANSWER_INDEX)));
                }
            }
        }
    }

    //clears current quiz. clear the quiz status layout and delete all dataitems. the wearable
    //will remove any outstanding question notifications
    public void newQuiz() {
        clearQuizStatus();
        if (mGoogleApiClient.isConnected()) {
            Wearable.DataApi.getDataItems(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DataItemBuffer>() {
                        @Override
                        public void onResult(DataItemBuffer result) {
                            try {
                                if (result.getStatus().isSuccess()) {
                                    List<Uri> dataItemUriList = new ArrayList<>();
                                    for (final DataItem dataItem:result) {
                                        dataItemUriList.add(dataItem.getUri());
                                    }
                                    deleteDataItems(dataItemUriList);
                                 } else {
                                    //clear quiz failed
                                }
                            } finally {
                                result.release();
                            }
                        }
                    });
        } else {
            Log.d(TAG, "newQuiz: GoogleApiClient is not connected");
        }
    }

    private void deleteDataItems(List<Uri> dataItemUriList) {
        if (mGoogleApiClient.isConnected()) {
            for (final Uri dataItemUri:dataItemUriList) {
                Wearable.DataApi.deleteDataItems(mGoogleApiClient, dataItemUri)
                        .setResultCallback(new ResultCallback<DataApi.DeleteDataItemsResult>() {
                            @Override
                            public void onResult(DataApi.DeleteDataItemsResult deleteResult) {
                                if (Log.isLoggable(TAG, Log.DEBUG)) {
                                    if (deleteResult.getStatus().isSuccess()) {
                                        Log.d(TAG, "Successfully delete data item " + dataItemUri);
                                    } else {
                                        Log.d(TAG, "deleteDataItem: Failed to delete data item " +dataItemUri);
                                    }
                                }
                            }
                        });
            }
        } else {
            Log.e(TAG, "Failed to delete data items. Client is disconnected");
        }
    }
}
