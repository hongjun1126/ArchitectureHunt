package com.example.hongjunjin.architecturehunt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.favorites.FavoritesInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by hongjunjin on 8/1/15.
 */
public class addFavorite extends Service{


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Kick off new work to do

        final String photoId = intent.getStringExtra("photoId");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OAuth auth = new OAuth();
                    auth.setToken(new OAuthToken(Flickr_login.getSharedPref().getString("access_token", null), Flickr_login.getSharedPref().getString("access_secret", null)));
                    RequestContext.getRequestContext().setOAuth(auth);


                    FavoritesInterface favoritesInterface = new FavoritesInterface(Flickr_login.getFlickrKey(), Flickr_login.getFlickrSecret(), new REST());
                    favoritesInterface.add(photoId);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FlickrException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        return START_STICKY;

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
