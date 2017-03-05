package com.joy.tweetit.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.joy.tweetit.R;
import com.joy.tweetit.model.Tweet;

import org.parceler.Parcels;

/**
 * Created by joy0520 on 2017/3/5.
 */

public class DetailDialog extends DialogFragment {
    private ImageView mImage;
    private TextView mName, mUserName, mBody;
    private Tweet mTweet;

    public static DetailDialog newInstance(Tweet tweet) {
        DetailDialog frag = new DetailDialog();
        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingDialog);
        Bundle args = new Bundle();
        args.putParcelable("tweet", Parcels.wrap(tweet));
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

                    }
                });
        // Unwrap tweet object
        mTweet = Parcels.unwrap(getArguments().getParcelable("tweet"));

        // Load the custom dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_tweet_detail, null, false);
        updateViews(customView);
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private void updateViews(View customView) {
        // Init views
        mImage = (ImageView) customView.findViewById(R.id.image);
        mName = (TextView) customView.findViewById(R.id.name);
        mUserName = (TextView) customView.findViewById(R.id.user_name);
        mBody = (TextView) customView.findViewById(R.id.body);
        // Setup views content
        Glide.with(getActivity())
                .load(mTweet.getProfileImageUrl())
                .fitCenter()
                .into(mImage);
        mName.setText(mTweet.getName());
        mUserName.setText(String.format("@%s", mTweet.getScreenName()));
        mBody.setText(mTweet.getText());
    }
}
