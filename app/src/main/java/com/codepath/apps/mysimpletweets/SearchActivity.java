package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.codepath.apps.mysimpletweets.fragments.AllSearchedTweetsFragment;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.TopSearchedTweetsFragment;

public class SearchActivity extends BaseTimeLineActivity {

    private String query;

    public static final String ARG_QUERY = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.query = getIntent().getStringExtra(ARG_QUERY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        //Set up the search manager.
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Set up the query text listener.
        final BaseTimeLineActivity searchActivity = this;
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

        return true;
    }

    public String[] getTabTitles() {
        return new String[] {getString(R.string.top_tweets), getString(R.string.all_tweets)};
    }

    public OnFragmentCreateListener getOnFragmentCreateListener() {
        final TweetsArrayAdapter.OnProfileImageClickListener listener = this;
        return new OnFragmentCreateListener() {

            @Override
            public Fragment getItem(int position) {
                return position == 0 ?
                        TopSearchedTweetsFragment.newInstance(query, listener) :
                        AllSearchedTweetsFragment.newInstance(query, listener);
            }
        };
    }
}