package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.googlecode.flickrjandroid.uploader.Uploader;

import org.json.JSONException;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class cameraActivity extends Activity {

    private Uri uriSavedImage;
    static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    static int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    protected final static String UPLOAD_URL = "https://up.flickr.com/services/upload/";
    protected static  Bitmap bitmap = null;
    protected static ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_flickr_login);

        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        imagesFolder.mkdirs(); // <----
        File image = new File(imagesFolder, "image_001.jpg");
        uriSavedImage = Uri.fromFile(image);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {


            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent

                Log.d("ADebugTag", "test: " + "picture is taken!");

                setContentView(R.layout.uploader);

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriSavedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageView img = (ImageView) findViewById(R.id.image);
                img.setImageBitmap(bitmap);

                final EditText title = (EditText) findViewById(R.id.title);
                final EditText description = (EditText) findViewById(R.id.desEdit);

                progress = new ProgressDialog(this);
                progress.setMessage("Uploading...");
                progress.setIndeterminate(true);
                progress.setCancelable(false);


                Button button = (Button) findViewById(R.id.postButton);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click

                        progress.show();
                        Log.d("ADebugTag", "test: " + "post button is clicked");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    OAuth auth = new OAuth();
                                    auth.setToken(new OAuthToken(Flickr_login.sharedPref.getString("access_token", null), Flickr_login.sharedPref.getString("access_secret", null)));

                                    RequestContext.getRequestContext().setOAuth(auth);

                                    Uploader up = new Uploader(Flickr_login.FLICKR_KEY, Flickr_login.FLICKR_SECRET);
                                    UploadMetaData uploadMetaData = new UploadMetaData();
                                    uploadMetaData.setTitle(title.getText().toString());
                                    uploadMetaData.setDescription(description.getText().toString());

                                    List<String> list = new ArrayList<String>();
                                    list.add("architecture");
                                    uploadMetaData.setTags(list);

                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    byte[] bitmapdata = stream.toByteArray();

                                    Log.d("ADebugTag", "OAuth:: " + RequestContext.getRequestContext().getOAuth());

                                    up.upload("A photo taken using the Building Scavenger", bitmapdata, uploadMetaData);
                                    progress.dismiss();

                                    startSearch();

                                } catch (FlickrException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (SAXException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                });



            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture


            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }


    }


    public void startSearch(){
        Intent postUploadActivity = new Intent(this, postUpload.class);
        startActivity(postUploadActivity);
    }


}
