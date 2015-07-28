package com.example.hongjunjin.architecturehunt;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class Flickr_login extends ActionBarActivity {

    protected static final String FLICKR_KEY = "fc0baa54c996e02f0576193c6c4b313a";
    private static final String FLICKR_SECRET = "92b4fb82980e27be";
    private static final String PROTECTED_RESOURCE_URL = "https://api.flickr.com/services/rest/";
    private Token accessToken;
    private OAuthService service;

    private Button launch_login_button;
    private Button authorize_button;
    private EditText edit;
    private Token requestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_login);
        launch_login_button = (Button) findViewById(R.id.launch_login);
        launch_login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        service = new ServiceBuilder()
                                .provider(FlickrApi.class)
                                .apiKey(FLICKR_KEY)
                                .apiSecret(FLICKR_SECRET)
                                .build();
                        // Obtain the Request Token
                        //Log.d("ADebugTag", "Value: " + "cool");
                        requestToken = service.getRequestToken();
                        String authorizationUrl = service.getAuthorizationUrl(requestToken);
                        //System.out.println(authorizationUrl + "&perms=write");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl + "&perms=write"));
                        startActivity(browserIntent);
                    }
                }).start();
            }
        });
        edit = (EditText)findViewById(R.id.verifier_code);
        authorize_button = (Button) findViewById(R.id.login_button);
        authorize_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("ADebugTag", "Value: " + "button is clicked");
                Toast null_code = Toast.makeText(getApplicationContext(), "Please enter a code", Toast.LENGTH_SHORT);
                final Toast bad_code = Toast.makeText(getApplicationContext(), "Could not authorize. Please check your code.", Toast.LENGTH_SHORT);
                final Toast success = Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT);

                String code = edit.getText().toString();
                if (code.length() == 0) {
                    null_code.show();
                    return;
                }
                if (requestToken == null) {
                    bad_code.show();
                    return;
                }
                final Verifier verifier = new Verifier(code);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            accessToken = service.getAccessToken(requestToken, verifier);
                        }
                        catch (OAuthException exception) {
                            bad_code.show();
                            return;
                        }
                        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
                        request.addQuerystringParameter("method", "flickr.test.login");
                        service.signRequest(accessToken, request);
                        Response response = request.send();
                        System.out.println(response.getBody());

                        Log.d("ADebugTag", "Value: " + "done log in");
                        success.show();
                        startNewService();
                    }
                }).start();
            }
        });
    }

    public void startNewService(){
        Intent GPSService = new Intent(this, locationActivity.class);
        startActivity(GPSService);
    }

    public String getFlickrKey(){
        return this.FLICKR_KEY;
    }

    public String getFlickrSecret(){
        return this.FLICKR_SECRET;
    }

    public Token getAccessToken(){
        return this.accessToken;
    }

    public String getProtectedResourceUrl(){
        return this.PROTECTED_RESOURCE_URL;
    }

    public OAuthService getService(){
        return this.service;
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
