package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import play.Logger;

public class ConfigUtil {
	
	/** properties */
	private static Properties config = read();
	
	/**
	 * Read configuration file .
	 * @return
	 */
	private static Properties read() {
		Properties ret = new Properties();
		final String PATH = new File(".").getAbsoluteFile().getParent() + "/conf/extra/twitter.conf.properties";
		try {
			InputStream is = new FileInputStream(new File(PATH));
			ret.load(is);
		} catch (IOException e) {
			Logger.error(e.getMessage());
		}
		
		return ret;
	}
	
	/**
	 * Get a twitter consumer_key from configuration file .
	 * @return
	 */
	public static String twitterConsumerKey() {
		return config.getProperty("CONSUMER_KEY", "");	// TODO
	}
	
	/**
	 * Get a twitter consumer_secret from configuration file .
	 * @return
	 */
	public static String twitterCosumerSecret() {
		return config.getProperty("CONSUMER_SECRET", "");	// TODO
	}
	
	/**
	 * Get a twitter callback URL from configuration file .
	 * @return
	 */
	public static String twitterCallbackURL() {
		return config.getProperty("CALLBACK_URL", "");	// TODO
	}
	
	/**
	 * Get a twitter screen_name .
	 * @return
	 */
	public static String twitterScreenName() {
		return config.getProperty("SCREEN_NAME", "");    // TODO
	}
}
