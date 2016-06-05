package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.fragments.AllSearchedTweetsFragment;
import com.codepath.apps.mysimpletweets.fragments.TopSearchedTweetsFragment;

public class SearchActivity extends BaseTimeLineActivity {

    private String query;

    public static final String ARG_QUERY = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.query = getIntent().getStringExtra(ARG_QUERY);
        getSupportActionBar().setTitle("");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        //Set up the search manager.
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Put query in search box and set up the query text listener.
        final BaseTimeLineActivity searchActivity = this;
        searchView.setQueryHint(getString(R.string.search_twitter));
        searchView.setQuery(this.query, false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {

                //End this search activity and start a new one for a new query.
                Intent output = new Intent();
                output.putExtra(ARG_QUERY, query);
                setResult(RESULT_OK, output);
                finish();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
        changeSearchViewTextColor(searchView);

        return true;
    }

    public String[] getTabTitles() {
        return new String[] {getString(R.string.top_tweets), getString(R.string.all_tweets)};
    }

    public OnFragmentCreateListener getOnFragmentCreateListener() {
        final TweetsArrayAdapter.TweetListener listener = this;
        return new OnFragmentCreateListener() {

            @Override
            public Fragment getItem(int position) {
                return position == 0 ?
                        TopSearchedTweetsFragment.newInstance(query, listener, progressBar, user) :
                        AllSearchedTweetsFragment.newInstance(query, listener, progressBar, user);
            }
        };
    }
}
