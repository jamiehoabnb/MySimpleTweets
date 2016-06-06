package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.adapters.MessagesArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Message;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.util.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.util.InternetCheckUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MessageListFragment extends Fragment {

    private User loggedInUser;

    List<Message> list;
    MessagesArrayAdapter adapter;

    List<String> followersScreenNames;
    ArrayAdapter<String> spinnerArrayAdapter;

    @BindView(R.id.spRecipient)
    Spinner spRecipient;

    @BindView(R.id.etMessage)
    EditText etMessage;

    @BindView(R.id.progressBar)
    SmoothProgressBar progressBar;

    @BindView(R.id.lvMessages)
    ListView lvMessages;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    private TwitterClient twitterClient = TwitterApplication.getRestClient();

    private long maxSendId = 1;
    private long maxReceivedId = 1;

    private boolean listenersInitialized = false;

    public static MessageListFragment newInstance(User loggedInUser) {
        MessageListFragment fragment = new MessageListFragment();
        fragment.loggedInUser = loggedInUser;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<>();
        adapter = new MessagesArrayAdapter(getActivity(), list, loggedInUser);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message_list, parent, false);
        ButterKnife.bind(this, v);
        lvMessages.setAdapter(adapter);

        initListeners();
        populateMessageList();

        followersScreenNames = new ArrayList<>();
        spinnerArrayAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_layout, followersScreenNames);
        spRecipient.setAdapter(spinnerArrayAdapter);
        populateFollowersList();
        return v;
    }

    private void populateFollowersList() {
        progressBar.setVisibility(View.VISIBLE);
        twitterClient.getFollowersList(loggedInUser.getScreenName(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressBar.setVisibility(View.INVISIBLE);
                swipeContainer.setRefreshing(false);

                if (response.length() > 0) {
                    List<User> friends = User.fromJSONArray(response.optJSONArray("users"));
                    for(User friend: friends) {
                        followersScreenNames.add("@" + friend.getScreenName());
                    }
                    spinnerArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeContainer.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
                Log.e("ERROR", errorResponse == null ? "" : errorResponse.toString(), throwable);

                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(lvMessages, errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(getContext().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                populateFollowersList();
                            }
                        })
                        .show();

            }
        });
    }

    private void populateMessageList() {
        twitterClient.getMessagesReceived(maxReceivedId, getResponseHandler(maxReceivedId, false));
        twitterClient.getMessagesSent(maxSendId, getResponseHandler(maxSendId, true));
    }

    private JsonHttpResponseHandler getResponseHandler(final long foo, final boolean isSentMsgs) {
        progressBar.setVisibility(View.VISIBLE);

        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                progressBar.setVisibility(View.INVISIBLE);
                swipeContainer.setRefreshing(false);

                if (response.length() > 0) {
                    List<Message> newMsgs = Message.fromJSONArray(response);
                    adapter.addMessages(newMsgs);

                    if (isSentMsgs) {
                        maxSendId = newMsgs.get(0).getId();
                    } else {
                        maxReceivedId = newMsgs.get(0).getId();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeContainer.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
                Log.e("ERROR", errorResponse == null ? "" : errorResponse.toString(), throwable);

                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(lvMessages, errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(getContext().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                populateMessageList();
                            }
                        })
                        .show();

            }
        };
    }

    private void initListeners() {
        if (listenersInitialized) {
            return;
        }
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateMessageList();
            }
        });

        lvMessages.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                populateMessageList();
                return true;
            }

            @Override
            public int getFooterViewType() {
                return -1;
            }
        });
        listenersInitialized = true;
    }

    @OnClick(R.id.tvSend)
    public void send() {
        String text = etMessage.getText().toString();
        String screenName = spRecipient.getSelectedItem().toString();

        progressBar.setVisibility(View.VISIBLE);
        twitterClient.postDirectMessage(screenName, text, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressBar.setVisibility(View.INVISIBLE);

                Message message = Message.fromJSON(response);
                adapter.add(message);
                maxSendId = message.getId();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeContainer.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
                Log.e("ERROR", errorResponse == null ? "" : errorResponse.toString(), throwable);

                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(lvMessages, errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(getContext().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                send();
                            }
                        })
                        .show();

            }
        });
    }
}
