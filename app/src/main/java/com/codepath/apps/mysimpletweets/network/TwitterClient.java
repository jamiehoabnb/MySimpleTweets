package com.codepath.apps.mysimpletweets.network;

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
	public static final String REST_CONSUMER_KEY = "yAcneeL7ryK9DSRxMDe0bUIYe";
	public static final String REST_CONSUMER_SECRET = "QWNAwxNrgGHyzuxhvNu24ptb4B73SmLDqKvb8ZYYawrLMMUJ4n";
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

	public void getMessagesReceived(long minId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("direct_messages.json");
		RequestParams params = new RequestParams();
		params.put("count", PAGE_SIZE);
		params.put("since_id", minId);
		getClient().get(apiUrl, params, handler);
	}

    public void getMessagesSent(long minId, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("direct_messages/sent.json");
        RequestParams params = new RequestParams();
        params.put("count", PAGE_SIZE);
        params.put("since_id", minId);
        getClient().get(apiUrl, params, handler);
    }

	public void getFriendsList(String screenName, Long cursor, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("friends/list.json");
		RequestParams params = new RequestParams();
		params.put("count", PAGE_SIZE);
		params.put("screen_name", screenName);

		if (cursor != null) {
			params.put("cursor", cursor);
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getFollowersList(String screenName, Long cursor, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("followers/list.json");
		RequestParams params = new RequestParams();
		params.put("count", PAGE_SIZE);
		params.put("screen_name", screenName);

		if (cursor != null) {
			params.put("cursor", cursor);
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

	public void postStatusUpdate(String status, Long replyUid, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", status);

		if (replyUid != null) {
			params.put("in_reply_to_status_id", replyUid);
		}

		getClient().post(apiUrl, params, handler);
	}

	public void postDirectMessage(String screenName, String text, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("direct_messages/new.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		params.put("text", text);
		getClient().post(apiUrl, params, handler);
	}

	public void postStatusRetweet(Tweet tweet, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/" + (tweet.isRetweeted() ? "unretweet/" : "retweet/")
                + tweet.getUid() + ".json");
		getClient().post(apiUrl, null, handler);
	}

	public void postFavoritesCreate(Tweet tweet, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/" + (tweet.isFavorited() ? "destroy" : "create") + ".json");
		RequestParams params = new RequestParams();
		params.put("id", tweet.getUid());
		getClient().post(apiUrl, params, handler);
	}

	public void getUserInfo(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);
	}

	public void getUserProfileBanner(String screenName, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/profile_banner.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}
}