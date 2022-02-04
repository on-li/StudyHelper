package com.zybooks.studyhelper;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT_ID = "com.zybooks.studyhelper.subject_id";

    private StudyDatabase mStudyDb;
    private long mSubjectId;
    private List<Question> mQuestionList;
    private TextView mAnswerLabel;
    private TextView mAnswerText;
    private Button mAnswerButton;
    private TextView mQuestionText;
    private int mCurrentQuestionIndex;
    private ViewGroup mShowQuestionLayout;
    private ViewGroup mNoQuestionLayout;
    private EditText mSearchField;
    private Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // SubjectActivity should provide the subject ID of the questions to display
        Intent intent = getIntent();
        mSubjectId = intent.getLongExtra(EXTRA_SUBJECT_ID, 0);

        // Get all questions for this subject
        mStudyDb = StudyDatabase.getInstance();
        mQuestionList = mStudyDb.getQuestions(mSubjectId);

        mQuestionText = findViewById(R.id.question_text_view);
        mAnswerLabel = findViewById(R.id.answer_label_text_view);
        mAnswerText = findViewById(R.id.answer_text_view);
        mAnswerButton = findViewById(R.id.answer_button);
        mShowQuestionLayout = findViewById(R.id.show_question_layout);
        mNoQuestionLayout = findViewById(R.id.no_question_layout);
        mSearchField = findViewById(R.id.search_field);
        mSearchButton = findViewById(R.id.search_button);

        // Show first question
        showQuestion(0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mQuestionList.size() == 0) {
            updateAppBarTitle();
            displayQuestion(false);
        }
        else {
            displayQuestion(true);
            toggleAnswerVisibility();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //  Determine which app bar item was chosen
        if (item.getItemId() == R.id.previous) {
            showQuestion(mCurrentQuestionIndex - 1);
            return true;
        }
        else if (item.getItemId() == R.id.next) {
            showQuestion(mCurrentQuestionIndex + 1);
            return true;
        }
        else if (item.getItemId() == R.id.add) {
            addQuestion();
            return true;
        }
        else if (item.getItemId() == R.id.edit) {
            editQuestion();
            return true;
        }
        else if (item.getItemId() == R.id.delete) {
            deleteQuestion();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addQuestionButtonClick(View view) {
        addQuestion();
    }

    public void answerButtonClick(View view) {
        toggleAnswerVisibility();
    }

    private void displayQuestion(boolean display) {
        if (display) {
            mShowQuestionLayout.setVisibility(View.VISIBLE);
            mNoQuestionLayout.setVisibility(View.GONE);
        }
        else {
            mShowQuestionLayout.setVisibility(View.GONE);
            mNoQuestionLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updateAppBarTitle() {

        // Display subject and number of questions in app bar
        Subject subject = mStudyDb.getSubject(mSubjectId);
        String title = getResources().getString(R.string.question_number,
                subject.getText(), mCurrentQuestionIndex + 1, mQuestionList.size());
        setTitle(title);
    }

    private void addQuestion() {
        // TODO: Add question
    }

    private void editQuestion() {
        // TODO: Edit question
    }

    private void deleteQuestion() {
        // TODO: Delete question
    }

    private void showQuestion(int questionIndex) {

        // Show question at the given index
        if (mQuestionList.size() > 0) {
            if (questionIndex < 0) {
                questionIndex = mQuestionList.size() - 1;
            }
            else if (questionIndex >= mQuestionList.size()) {
                questionIndex = 0;
            }

            mCurrentQuestionIndex = questionIndex;
            updateAppBarTitle();

            Question question = mQuestionList.get(mCurrentQuestionIndex);
            mQuestionText.setText(question.getText());
            mAnswerText.setText(question.getAnswer());

            mSearchButton.setOnClickListener(view -> {
                webSearch();
            });

        }
        else {
            // No questions yet
            mCurrentQuestionIndex = -1;
        }
    }

    private void toggleAnswerVisibility() {
        if (mAnswerText.getVisibility() == View.VISIBLE) {
            mAnswerButton.setText(R.string.show_answer);
            mAnswerText.setVisibility(View.INVISIBLE);
            mAnswerLabel.setVisibility(View.INVISIBLE);
        }
        else {
            mAnswerButton.setText(R.string.hide_answer);
            mAnswerText.setVisibility(View.VISIBLE);
            mAnswerLabel.setVisibility(View.VISIBLE);
        }
    }

    private void webSearch() {
        String query = mSearchField.getText().toString();

        if (validateField(mSearchField)) {
//            Uri uri = Uri.parse("http://www.google.com/#q=" + query);
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, query); // query contains search string
            startActivity(intent);
        }

        mSearchField.setText("");
    }

    private boolean validateField(EditText field) {
        if (field.length() == 0) {
            field.setError("Field cannot be empty");
            return false;
        }
        else {
            field.setError(null);
            return true;
        }
    }
}