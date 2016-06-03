package com.codepath.apps.mysimpletweets;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Request;

import android.content.Context;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "ga2BV8XfvSgfZsQtWDLjLqoxn";
	public static final String REST_CONSUMER_SECRET = "q6jwEKEYkwBTnElz7s2QG53E5zIoMgCW8Km7PiknTfnXK9hnX4";
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets";

	private int PAGE_SIZE = 25;

    private int POPULARITY_SEARCH_PAGE_SIZE = 100;

	public enum SearchResultType {
		mixed, recent, popular;
	}

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getSearchResults(AsyncHttpResponseHandler handler,
								 String query,
								 SearchResultType resultType,
								 long maxId,
								 long minId) {
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		params.put("count", resultType == SearchResultType.popular ?
                POPULARITY_SEARCH_PAGE_SIZE : PAGE_SIZE);
		params.put("q", query);
		params.put("result_type", resultType.name());
		params.put("include_entities", "true");

		if (maxId < Long.MAX_VALUE) {
			params.put("max_id", maxId);
		} else {
			params.put("since_id", minId);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getHomeTimeLine(AsyncHttpResponseHandler handler, long maxId, long minId) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", PAGE_SIZE);
		params.put("include_entities", "true");

        if (maxId < Long.MAX_VALUE) {
            params.put("max_id", maxId);
        } else {
            params.put("since_id", minId);
        }
		getClient().get(apiUrl, params, handler);
	}

	public void getMentionsTimeline(JsonHttpResponseHandler handler, long maxId, long minId) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", PAGE_SIZE);
		params.put("include_entities", "true");

        if (maxId < Long.MAX_VALUE) {
            params.put("max_id", maxId);
        } else {
            params.put("since_id", minId);
        }
		getClient().get(apiUrl, params, handler);
	}

	public void getUserTimeline(String screenName, JsonHttpResponseHandler handler, long maxId, long minId) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", PAGE_SIZE);
		params.put("screen_name", screenName);
		params.put("include_entities", "true");
		params.put("include_rts", true);
		params.put("exclude_replies", false);

		if (maxId < Long.MAX_VALUE) {
			params.put("max_id", maxId);
		} else {
            params.put("since_id", minId);
        }
		getClient().get(apiUrl, params, handler);
	}

	public void postStatusUpdate(String status, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);
		getClient().post(apiUrl, params, handler);
	}

	public void getUserInfo(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);
	}
}