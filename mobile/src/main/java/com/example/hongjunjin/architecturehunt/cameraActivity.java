package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.googlecode.flickrjandroid.uploader.Uploader;

import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class cameraActivity extends Activity {

    private Uri uriSavedImage;
    private Bitmap bitmap;
    protected static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    protected static int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    ImageView img = (ImageView) findViewById(R.id.image);
                    img.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                final EditText title = (EditText) findViewById(R.id.title);
                final EditText description = (EditText) findViewById(R.id.desEdit);

                progress = new ProgressDialog(this);
                progress.setMessage("Loading...");
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
                                    auth.setToken(new OAuthToken(Flickr_login.getSharedPref().getString("access_token", null), Flickr_login.getSharedPref().getString("access_secret", null)));

                                    RequestContext.getRequestContext().setOAuth(auth);

                                    Uploader up = new Uploader(Flickr_login.getFlickrKey(), Flickr_login.getFlickrSecret());
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

                                    startPostActivity();

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


    public void startPostActivity(){
        Intent postUploadActivity = new Intent(this, postUpload.class);
        startActivity(postUploadActivity);
    }


}
