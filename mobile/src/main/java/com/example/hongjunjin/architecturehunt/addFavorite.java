package com.example.hongjunjin.architecturehunt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.Transport;
import com.googlecode.flickrjandroid.favorites.FavoritesInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import static com.googlecode.flickrjandroid.Transport.*;

/**
 * Created by hongjunjin on 8/1/15.
 */
public class addFavorite extends Service{


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Kick off new work to do

        Log.d("ADebugTag", "test: " + "in addFavorite");

        final String photoId = intent.getStringExtra("photoId");

        Log.d("ADebugTag", "in addFav: " + photoId);


        //final REST rest = new REST();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OAuth auth = new OAuth();
                    auth.setToken(new OAuthToken(Flickr_login.sharedPref.getString("access_token", null), Flickr_login.sharedPref.getString("access_secret", null)));
                    RequestContext.getRequestContext().setOAuth(auth);


                    final FavoritesInterface favoritesInterface = new FavoritesInterface(Flickr_login.FLICKR_KEY, Flickr_login.FLICKR_SECRET, new REST());
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
