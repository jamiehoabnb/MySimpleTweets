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
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;

public class HomeActivity extends BaseTimeLineActivity {

    private static final int ACTIVITY_SEARCH_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_timeline;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        //Set up the search manager.
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_twitter));

        //Set up the query text listener.
        final HomeActivity homeActivity = this;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                searchView.clearFocus();

                //Switch to search result activity and pass in query.
                homeActivity.executeSearch(query);
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

    private void executeSearch(String query) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.ARG_QUERY, query);
        startActivityForResult(intent, ACTIVITY_SEARCH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String query = data.getStringExtra(SearchActivity.ARG_QUERY);

            if (query != null) {
                executeSearch(query);
            }
        }
    }

    public String[] getTabTitles() {
        return new String[] {getString(R.string.home), getString(R.string.mentions)};
    }

    public OnFragmentCreateListener getOnFragmentCreateListener() {
        final TweetsArrayAdapter.OnProfileImageClickListener listener = this;
        return new OnFragmentCreateListener() {

            @Override
            public Fragment getItem(int position) {
                return position == 0 ?
                        HomeTimelineFragment.newInstance(listener, progressBar) :
                        MentionsTimelineFragment.newInstance(listener, progressBar);
            }
        };
    }
}
