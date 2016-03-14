package org.wxc.opensources.retrofit;

import org.wxc.opensources.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by black on 2016/3/10.
 */
public class UserModel {
    /** Not-null value. */
    public String login;
    /** Not-null value. */
    public int id;
    /** Not-null value. */
    public String avatar_url;
    /** Not-null value. */
    public String url;
    /** Not-null value. */
    public String html_url;
    /** Not-null value. */
    public String followers_url;
    /** Not-null value. */
    public String following_url;
    /** Not-null value. */
    public String gists_url;
    /** Not-null value. */
    public String starred_url;
    /** Not-null value. */
    public String subscriptions_url;
    /** Not-null value. */
    public String organizations_url;
    /** Not-null value. */
    public String repos_url;
    /** Not-null value. */
    public String events_url;
    /** Not-null value. */
    public String received_events_url;
    /** Not-null value. */
    public String type;
    public boolean site_admin;
/*

    public UserModel(String login, int userId, String avatarUrl, String url, String htmlUrl, String followersUrl, String followingUrl, String gistsUrl, String starredUrl, String subscriptionsUrl, String organizationsUrl, String reposUrl, String eventsUrl, String receivedEventsUrl, String type, boolean site_admin) {
        this.login = login;
        this.id = userId;
        this.avatar_url = avatarUrl;
        this.url = url;
        this.html_url = htmlUrl;
        this.followers_url = followersUrl;
        this.following_url = followingUrl;
        this.gists_url = gistsUrl;
        this.starred_url = starredUrl;
        this.subscriptions_url = subscriptionsUrl;
        this.organizations_url = organizationsUrl;
        this.repos_url = reposUrl;
        this.events_url = eventsUrl;
        this.received_events_url = receivedEventsUrl;
        this.type = type;
        this.site_admin = site_admin;
    }
*/
    public interface ICreator {
        @GET("/repos/{owner}/{repo}/contributors")
        Call<List<UserModel>> contributors(
                @Path("owner") String owner,
                @Path("repo") String repo);
    }
    
    public User toUserEntity(){
        return new User(
                null,
                login,
                id,
                avatar_url,
                url,
                html_url,
                followers_url,
                following_url,
                gists_url,
                starred_url,
                subscriptions_url,
                organizations_url,
                repos_url,
                events_url,
                received_events_url,
                type,
                site_admin);
    }
}
