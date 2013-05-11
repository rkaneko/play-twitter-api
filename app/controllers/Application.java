package controllers;

import java.util.List;

import javax.persistence.PersistenceException;

import com.avaje.ebean.Ebean;

import enumeration.ETwitterAPI;

import models.TwitterApiInfo;
import play.*;
import play.libs.Json;
import play.mvc.*;

import twitter4j.Paging;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import utils.ConfigUtil;
import views.html.*;

public class Application extends Controller {
	
	/** twitter consumer_key */
	private static final String CONSUMER_KEY = ConfigUtil.twitterConsumerKey();
	/** twitter consumer_secret */
	private static final String CONSUMER_SECRET = ConfigUtil.twitterCosumerSecret();
	/** twitter OAuth callback URL */
	private static final String CALLBACK_URL = ConfigUtil.twitterCallbackURL();
	/** twitter screen name */
	private static final String SCREEN_NAME = ConfigUtil.twitterScreenName();
  
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    
    
    public static Result oauth() {
    	final String REDIRECT_URL = session().get("redirect");
    	Logger.info("oauth() redirecrt url : " + REDIRECT_URL);
    	twitter4j.Twitter twitter = TwitterFactory.getSingleton();
    	try {
    		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    	} catch (IllegalStateException e) {
    		Logger.error("oauth() Set OAuth consumer Error ! : " + e.getMessage());
    		twitter.setOAuthAccessToken(null);
    	}
    	
    	RequestToken requestToken;
		try {
			requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
			session("requestToken", requestToken.getToken());
			session("requestTokenSecret", requestToken.getTokenSecret());
			session("redirect", REDIRECT_URL);
			return redirect(requestToken.getAuthenticationURL());
		} catch (TwitterException e) {
			Logger.error("at oauth() : " + e.getMessage());
			Logger.info("Callback URL : " + CALLBACK_URL);
		}
		
		return badRequest("Bad Request .");
    }
    
    public static Result callback(String oauth_token, String oauth_verifier) {
    	final String TOKEN = session().get("requestToken");
    	final String TOKEN_SECRET = session().get("requestTokenSecret");
    	final String REDIRECT_URL = session().get("redirect");
    	Logger.info("callback() redirecrt url : " + REDIRECT_URL);
    	if ((TOKEN != null) && (TOKEN_SECRET != null)) {
    		try {
    			RequestToken requestToken = new RequestToken(TOKEN, TOKEN_SECRET);
    			twitter4j.Twitter twitter = TwitterFactory.getSingleton();
    			try {
    				twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    			} catch (IllegalStateException e) {
    				Logger.info("callback() Twitter OAuth : consumer key/secret pair aleready set .");
    			}
    			
    			twitter.getOAuthAccessToken(requestToken, oauth_verifier);
    			AccessToken accessToken = twitter.getOAuthAccessToken();
    			
    			Logger.info("token : " + accessToken.getToken());
        		Logger.info("tokenSecret : " + accessToken.getTokenSecret());
    			TwitterApiInfo info = TwitterApiInfo.findByScreenName(SCREEN_NAME);
    			info.token = accessToken.getToken();
        		info.tokenSecret = accessToken.getTokenSecret();
    			if (info.isDefault()) {
    				info.screenName = SCREEN_NAME;
            		Ebean.save(info);
    			} else {
    				Ebean.update(info);
    			}
    		} catch (PersistenceException e) {
    			Logger.info("twitter info already exists on database .");
    		} catch (TwitterException e) {
    			Logger.error("callback() TwitterException : " + e.getErrorMessage());
    		}
    	}
    	return redirect(REDIRECT_URL);
    }
    
    public static Result timeline(Integer page) {
    	return api(ETwitterAPI.HOME_TIMELINE, page);
    }
    
    public static Result follow(String screenName) {
    	return api(ETwitterAPI.FOLLOW, screenName);
    }
    
    public static Result unfollow(String screenName) {
    	return api(ETwitterAPI.UNFOLLOW, screenName);
    }
    
    private static Result api(ETwitterAPI command, Object option) {
    	TwitterApiInfo info = TwitterApiInfo.findByScreenName(SCREEN_NAME);
    	
    	String redirectUrl = "/twitter/";
    	switch (command) {
    	case HOME_TIMELINE:
    		if (option instanceof Integer) {
    			final int PAGE = new Integer(option.toString());
    			redirectUrl += "timeline/" + PAGE;
    		} else return badRequest("You must define page you want to get timeline of .");
    		break;
    	case FOLLOW:
    		if (option instanceof String) {
    			final String _SCREEN_NAME = option.toString();
    			redirectUrl += "follow/" + _SCREEN_NAME;
    		} else return badRequest("You must define target screen_name .");
    		break;
    	case UNFOLLOW:
    		if (option instanceof String) {
    			final String _SCREEN_NAME = option.toString();
    			redirectUrl += "unfollow/" + _SCREEN_NAME;
    		} else return badRequest("You must define target screen_name .");
    		break;
    	default:
    		return badRequest("API isn't defined !");	
    	}
    	
		session("redirect", redirectUrl);
		if (info.isDefault()) {
			return redirect("/twitter/oauth");
		}
	    
		twitter4j.Twitter twitter;
		if ((info.token != null) && (info.tokenSecret != null)) {
    		try {
    			twitter = TwitterFactory.getSingleton();
    			AccessToken accessToken = new AccessToken(info.token, info.tokenSecret);
    			
    			try {
    				twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
    			} catch (IllegalStateException e) {
    				//Logger.info("token : " + info.token);
    				//Logger.info("tokenSecret : " + info.tokenSecret);
    				//Logger.info("key : " + CONSUMER_KEY + " , secret : " + CONSUMER_SECRET);
    				Logger.info("api() Set OAuth Error ! : " + e.getMessage());
    			}
    			
    			try {
					twitter.setOAuthAccessToken(accessToken);
				} catch (IllegalStateException ex) {
					Logger.info("api() Set OAuth Error ! : " + ex.getMessage());
				}
    			
    			switch (command) {
				case HOME_TIMELINE:
					final int PAGE = new Integer(option.toString());
					List<twitter4j.Status> statuses = twitter.getHomeTimeline(new Paging(PAGE, 200));
					return ok(Json.toJson(statuses));
				case FOLLOW:
					final String FOLLOWED_SCREEN_NAME = option.toString();
					twitter4j.User theFollowed = twitter.createFriendship(FOLLOWED_SCREEN_NAME);
					return ok(Json.toJson(theFollowed));
				case UNFOLLOW:
					final String UNFOLLOWD_SCREEN_NAME = option.toString();
					twitter4j.User theUnfollowed = twitter.destroyFriendship(UNFOLLOWD_SCREEN_NAME);
					return ok(Json.toJson(theUnfollowed));
				}
    		} catch (TwitterException e) {
    			Logger.error("api() TwitterException :" + e.getErrorMessage());
    			return badRequest("API doesn't work .");
    		}
    	}
		
		return redirect("/twitter/oauth");
    }
}
