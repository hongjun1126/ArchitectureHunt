package com.example.hongjunjin.architecturehunt;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FlickrApi;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import java.io.PrintStream;



public class Flickr_login extends Activity {

    private static final String FLICKR_KEY = "fc0baa54c996e02f0576193c6c4b313a";
    private static final String FLICKR_SECRET = "92b4fb82980e27be";
    protected static final String restURL = "https://api.flickr.com/services/rest/";
    private OAuthRequest request;
    private OAuthService service;
    private Token requestToken;
    private static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.getString("access_token", null) != null && sharedPref.getString("access_secret", null) != null) {
            startNewService();
            finish();
        }
        setContentView(R.layout.activity_flickr_login);
        Button launch_login_button = (Button) findViewById(R.id.launch_login);
        request = new OAuthRequest(Verb.GET, restURL);

        launch_login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        service = new ServiceBuilder()
                                .provider(FlickrApi.class)
                                .apiKey(FLICKR_KEY)
                                .apiSecret(FLICKR_SECRET)
                                .callback("my-activity://mywebsite.com/")
                                .build();

                        requestToken = service.getRequestToken();
                        String authorizationUrl = service.getAuthorizationUrl(requestToken);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl + "&perms=write"));
                        startActivity(browserIntent);
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onResume() {
        Uri uri = this.getIntent().getData();
        super.onResume();
        if (uri == null || !uri.toString().startsWith("my-activity://mywebsite.com")) {
            return;
        }

        final Verifier verifier = new Verifier(uri.getQueryParameter("oauth_verifier"));
        new Thread(new Runnable() {
            @Override
            public void run() {

                Token accessToken;
                try {

                    accessToken = service.getAccessToken(requestToken, verifier);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("access_token", accessToken.getToken());
                    editor.putString("access_secret", accessToken.getSecret());
                    editor.commit();
                } catch (OAuthException exception) {
                    exception.printStackTrace(new PrintStream(System.out));
                    Toast failed = Toast.makeText(getApplicationContext(), "Authorization failed.", Toast.LENGTH_SHORT);
                    failed.show();
                    return;
                }
                request = new OAuthRequest(Verb.GET, restURL);
                request.addQuerystringParameter("method", "flickr.test.login");
                service.signRequest(accessToken, request);
                Response response = request.send();
                System.out.println(response.getBody());

                Log.d("ADebugTag", "Value: " + "done log in");

                startNewService();

            }
        }).start();
    }

    public void startNewService(){
        Intent GPSService = new Intent(this, locationActivity.class);
        startActivity(GPSService);
        finish();
    }


    public static String getFlickrKey(){
        return FLICKR_KEY;
    }

    public static String getFlickrSecret(){
        return FLICKR_SECRET;
    }

    public static SharedPreferences getSharedPref(){
        return sharedPref;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flickr_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
