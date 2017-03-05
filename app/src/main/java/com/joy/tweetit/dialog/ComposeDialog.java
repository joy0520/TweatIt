package com.joy.tweetit.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.joy.tweetit.R;

import java.util.Locale;

/**
 * Created by joy0520 on 2017/3/4.
 */

public class ComposeDialog extends DialogFragment {

    private EditText etNewTweet;
    private TextView tvLengthMeasure;
    private Callback mCallback;

    private String mDraft;

    public static ComposeDialog newInstance(String title, String tweetDraft) {
        ComposeDialog frag = new ComposeDialog();
        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingDialog);
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("draft", tweetDraft);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(getString(R.string.compose_dialog_post),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCallback.onPostNewTweet(etNewTweet.getText().toString());
            }
        }).setTitle(getArguments().getString("title", "Filter"));

        mDraft = getArguments().getString("draft", "");

        // Load the custom dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_compose, null, false);
        updateViews(customView);
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mCallback.onCancelNewTweet(etNewTweet.getText().toString());
    }

    private void updateViews(View container) {
        etNewTweet = (EditText) container.findViewById(R.id.etNewTweet);
        etNewTweet.setText(mDraft);
        // To monitor text change and measure text length instantly.
        etNewTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tvLengthMeasure != null) {
                    tvLengthMeasure.setText(String.format(Locale.getDefault(),
                            "%d / 140", s.length()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvLengthMeasure = (TextView) container.findViewById(R.id.tvLengthMeasure);
        tvLengthMeasure.setText("0 / 140");
    }

    public interface Callback {
        void onPostNewTweet(String newTweet);
        void onCancelNewTweet(String newTweet);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }
}
