package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

import play.db.ebean.Model;

/**
 * Twitter APIのaccess token等を管理する。
 * 
 * @author rkaneko
 *
 */
@Entity
@Table(name = "twitter_api_info")
public class TwitterApiInfo extends Model {
	
	/** serial ID */
	private static final long serialVersionUID = 1L;

	/** PK */
	@Id
	public Long id;
	
	/** twitter screen name */
	@NotNull
	@Column(name = "screen_name", unique = true)
	public String screenName;
	
	/** twitter token */
	@NotNull
	@Column(name = "token")
	public String token;
	
	/** twitter token secret */
	@NotNull
	@Column(name = "tokenSecret")
	public String tokenSecret;
	
	/** default info */
	private static final TwitterApiInfo DEFAULT = new TwitterApiInfo();
	public boolean isDefault() {
		if (this == DEFAULT) return true;
		else return false;
	}
	
	// -------------------------
	
	/** finder */
	private static Finder<Long, TwitterApiInfo> find = new Finder<Long, TwitterApiInfo>(Long.class, TwitterApiInfo.class);
	
	/**
	 * Search by screen_name .
	 * @param screenName
	 * @return
	 */
	public static TwitterApiInfo findByScreenName(String screenName) {
		TwitterApiInfo ret = find.where()
				.eq("screenName", screenName)
				.findUnique();
		if (ret != null) return ret;
		else return DEFAULT;
	}

}
