package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by hongjunjin on 7/22/15.
 */
public class locationActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, Serializable, AdapterView.OnItemClickListener {


    protected static final String searchMethod = "flickr.photos.search";
    private static final int numberOfThreads = 20;
    private final int defaultPage = 1;


    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates;

    private static String lon;
    private static String lat;
    private static String radius;
    private static String sort;

    private ListView lv;
    private static List<RowItem> rowItems;
    private List<RowItem> rowItemList;
    private static NodeList nodeList;
    private static long threadId;
    private List<Thread> threadList;
    private static RowItem item;
    private static FrameLayout flayout;
    private static ImageView frag_img;
    private static TextView frag_name;
    private Button backButton;
    private LinearLayout curr_nav_container;
    private boolean navigating = false;
    private Location nav_loc;
    private Button stop_nav;
    private CustomList adapter;
    private int currentPage;
    private Handler handler;
    private ProgressDialog progressDialog;
    private int fragPosition;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        Log.d("ADebugTag", "Value: " + "onCreat");


        backButton = (Button)findViewById(R.id.backButton);
        frag_img = (ImageView)findViewById(R.id.frag_img);
        frag_name = (TextView) findViewById(R.id.frag_name);
        flayout = (FrameLayout)findViewById(R.id.overlay_fragment_container);

        curr_nav_container = (LinearLayout) findViewById(R.id.curr_nav_container);
        stop_nav = (Button) findViewById(R.id.stop_nav);
        stop_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigating = false;
                item.navigating = false;
                adapter.notifyDataSetChanged();
                stop_nav.setVisibility(View.INVISIBLE);
                curr_nav_container.setVisibility(View.INVISIBLE);
                sendMessageToWear_fin();

            }
        });

        buildGoogleApiClient();
        createLocationRequest();
        this.mGoogleApiClient.connect();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

    }

    public void spinnerHelper(){

        Log.d("ADebugTag", "Value: " + "in Spinner");

        TextView radiusText = (TextView) findViewById(R.id.radiusText);
        TextView sortText = (TextView) findViewById(R.id.sortText);
        radiusText.setText("Radius");
        sortText.setText("Sort by");


        Spinner radius_spinner = (Spinner) findViewById(R.id.radius_spinner);
        ArrayAdapter<CharSequence> radius_adapter = ArrayAdapter.createFromResource(this,
                R.array.radius, R.layout.spinner_item);
        radius_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radius_spinner.setAdapter(radius_adapter);

        MySpinner sorting_spinner = (MySpinner) findViewById(R.id.sorting_spinner);
        final ArrayAdapter<CharSequence> sorting_adapter = ArrayAdapter.createFromResource(this,
                R.array.sorting, R.layout.spinner_item);
        sorting_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sorting_spinner.setAdapter(sorting_adapter);

        Log.d("ADebugTag", "Value: " + "done spinner");



        radius_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int pos, long id) {
                Log.d("ADebugTag", "Value: " + "spinner selected");

                String radius_selected = parent.getItemAtPosition(pos).toString();
                String radiusNum = radius_selected.substring(0, 1);
                setRadius(radiusNum);

                progressDialog.show();

                rowItemList = new ArrayList<RowItem>();
                currentPage = 0;
                searchPhotos(getRadius(), getSort(), getLat(), getLon(), defaultPage, "radius");

                handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {

                        if (message.obj.equals("radius") || message.obj.equals("onLocationChange")) {
                            updateItemList();
                            sortingItems(getSort());
                            showList();
                            loadExtraData();
                        }else if (message.obj.equals("loadPages")){
                            //Log.d("ADebugTag", "in loadPages handler: ");
                            updateItemList();
                            adapter.notifyDataSetChanged();
                            loadExtraData();
                            showToast();
                        }else if (message.obj.equals("fragLoad")){
                            updateItemList();
                            adapter.notifyDataSetChanged();
                            loadExtraData();
                            showToast();
                            showFragments(fragPosition + 1);
                            fragPosition += 1;
                        }

                    }
                };

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // your code here

            }

        });



        sorting_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean firstTime = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int pos, long id) {
                Log.d("ADebugTag", "Value: " + "spinner selected");

                String sorting_selected = parent.getItemAtPosition(pos).toString();
                setSort(sorting_selected);

                if (firstTime) {
                    firstTime = false;

                } else {
                    sortingItems(getSort());
                    showList();
                    //searchPhotos(getRadius(), getSort(), getLat(), getLon(), defaultPage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Log.d("ADebugTag", "Value: " + "nothing selected");
            }

        });

    }

    protected void createLocationRequest() {
        Log.d("ADebugTag", "Value: " + "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d("ADebugTag", "Value: " + "building google api client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    protected void startLocationUpdates() {
        mRequestingLocationUpdates = true;
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    void set_new_curr_location(Location location) {
        mCurrentLocation = location;
        String latitude = Double.toString(mCurrentLocation.getLatitude());
        String longitude = Double.toString(mCurrentLocation.getLongitude());
        setLat(latitude);
        setLon(longitude);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (navigating) {
            get_dist_rot(location, nav_loc);
        }
        Location prev_location = null;
        if (mCurrentLocation != null) {
            prev_location = new Location(mCurrentLocation);
        }
        //Log.d("ADebugTag", "onLcationChange: " + Thread.currentThread().getName());
        if (prev_location == null) {
            Log.e("START_SEARCH", ">>>>>>>>>>>>>>CALLING SEARCH PHOTOS");
            set_new_curr_location(location);
            spinnerHelper();
        }
        else if (location.distanceTo(prev_location) > 150) {
            Log.e("LOCATION From", Double.toString(prev_location.getLatitude()) + ", " + Double.toString(prev_location.getLongitude()));
            Log.e("LOCATION CHANGED", Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude()));
            Log.e("LOCATION CHANGED", Float.toString(location.distanceTo(prev_location)));

            Log.e("LOCATION CHANGED", ">>>>>>>>>>>>>>CALLING SEARCH PHOTOS");

            progressDialog.show();
            set_new_curr_location(location);
            rowItemList = new ArrayList<RowItem>();
            currentPage = 0;
            searchPhotos(getRadius(), getSort(), getLat(), getLon(), defaultPage, "onLocationChange");

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("ADebugTag", "Value: " + "onConnected");
        startLocationUpdates();
    }

    private void searchPhotos(final String radius, String sorting, final String lat, final String lon, final int page, final String sender){

        rowItems = new ArrayList<RowItem>();
        threadList = new ArrayList<Thread>();

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                Log.d("ADebugTag", "Value: " + "in t thread");

                System.out.println("t theadId: " + Thread.currentThread().getId());
                //threadId = Thread.currentThread().getId();

                String perPage = "10";

                StringBuffer searchBuffer = new StringBuffer(Flickr_login.restURL);
                searchBuffer.append("?method=");
                searchBuffer.append(searchMethod);
                searchBuffer.append("&api_key=");
                searchBuffer.append(Flickr_login.getFlickrKey());
                searchBuffer.append("&tags=architecture");
                searchBuffer.append("&radius_units=mi");
                searchBuffer.append("&radius=");
                searchBuffer.append(radius);
                searchBuffer.append("&lat=");
                searchBuffer.append(lat);
                searchBuffer.append("&lon=");
                searchBuffer.append(lon);
                searchBuffer.append("&media=photo&per_page=");
                searchBuffer.append(perPage);
                searchBuffer.append("&page=");
                searchBuffer.append(page);
//                searchBuffer.append("&sort=interestingness-desc");
                String searchURL = searchBuffer.toString();

                try {
                    URL url = new URL(searchURL);
                    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
                    DocumentBuilder b = null;
                    try {
                        b = f.newDocumentBuilder();
                        try {
                            Document doc = b.parse(url.openStream());
                            doc.getDocumentElement ().normalize();
                            NodeList nodes = doc.getDocumentElement().getChildNodes();
                            Node node = nodes.item(1); //get photos
                            nodeList = node.getChildNodes(); //photo list

                            MyThread firstThread = new MyThread();
                            threadId = firstThread.getId();
                            firstThread.start();
                            threadList.add(firstThread);


                            for (int i = 0; i < numberOfThreads - 1; i++){
                                MyThread t = new MyThread();
                                t.start();
                                threadList.add(t);
                            }


                            for (int i = 0; i < threadList.size(); i++){
                                try {
                                    threadList.get(i).join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            progressDialog.dismiss();

                            Log.d("ADebugTag", "done thread work!");

                            Message message = Message.obtain();
                            message.obj = sender;
                            handler.sendMessage(message);


                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                //show list of pictures

            }

        });
        t.start();

    }

    public void updateItemList(){

        for (int i = 0; i < rowItems.size(); i++) {
            rowItemList.add(rowItems.get(i));
        }
    }


    public void sortingItems(String sorting) {

        Log.d("ADebugTag", "title: " + "in sortingItems");

        if (sorting == null || sorting.equals("Distance")) {
            sortByDistance();
        } else {
            sortByFavorite();
        }
    }

    public void showList() {


        lv = (ListView) findViewById(R.id.list);

        adapter = new CustomList(this,
                R.layout.listitem, rowItemList);

        lv.setAdapter(adapter);

    }

    public void loadExtraData(){


        lv.setOnItemClickListener(this);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int visibleThreshold = 1;
            private int previousTotal = 0;
            private boolean loading = true;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (curr_nav_container.getVisibility() == View.VISIBLE) {
                    if (scrollState == SCROLL_STATE_IDLE) {
                        TranslateAnimation anim = new TranslateAnimation(0, 0, 0, curr_nav_container.getMeasuredHeight());
                        anim.setDuration(250);
                        anim.setFillAfter(true);
                        curr_nav_container.startAnimation(anim);
                    } else {
                        TranslateAnimation anim = new TranslateAnimation(0, 0, curr_nav_container.getMeasuredHeight(), 0);
                        anim.setDuration(250);
                        anim.setFillAfter(true);
                        curr_nav_container.startAnimation(anim);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                        currentPage++;

                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // I load the next page of gigs using a background task,
                    // but you can call any function here.


                    //callSearchPhotos(getRadius(), getSort(), getLat(), getLon(), currentPage + 1);
                    //progressDialog.show();

                    searchPhotos(getRadius(), getSort(), getLat(), getLon(), currentPage + 1, "loadPages");

                    //updateItemList();
                    //sortingItems(getSort());
                    //showList();
                    //adapter.notifyDataSetChanged();
                    //loadExtraData();
                    //showToast();

                    Log.d("ADebugTag", "currentPage: " + currentPage);

                    loading = true;
                }


            }
        });

        //ButtonsOnFragment();

    }

    public void showToast(){
        Toast.makeText(this, "Loaded 10 new pictures!", Toast.LENGTH_SHORT).show();
    }

    public void ButtonsOnFragment(){

        Button compassButton = (Button)findViewById(R.id.compassButton);
        Button GPSbutton = (Button)findViewById(R.id.GPSbutton);
        Button frag_upButton = (Button) findViewById(R.id.frag_upButton);
        Button frag_downButton = (Button) findViewById(R.id.frag_downButton);

        GPSbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("ADebugTag", "test: " + "GPS is clicked");
                backButton.performClick();
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + item.getLoc()[0] + ", " + item.getLoc()[1] + "&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        compassButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("ADebugTag", "test: " + "Compass is clicked");
                TextView nav_title = (TextView) findViewById(R.id.nav_title);
                ImageView nav_img = (ImageView) findViewById(R.id.nav_img);

                navigating = true;
                nav_loc = new Location("");
                nav_loc.setLatitude(item.getLoc()[0]);
                nav_loc.setLongitude(item.getLoc()[1]);
                sendMessageToWear();
                get_dist_rot(mCurrentLocation, nav_loc);
                for (RowItem r_item : rowItemList) {
                    if (r_item.navigating) {
                        r_item.navigating = false;
                    }
                }
                item.navigating = true;
                adapter.notifyDataSetChanged();
                Bitmap img = item.getBmp();
                Bitmap icon;
                if (img.getWidth() >= img.getHeight()) {
                    icon = Bitmap.createBitmap(
                            img, img.getWidth() / 2 - img.getHeight() / 2,
                            0,
                            img.getHeight(), img.getHeight()
                    );
                } else {
                    icon = Bitmap.createBitmap(
                            img, 0,
                            img.getHeight() / 2 - img.getWidth() / 2,
                            img.getWidth(), img.getWidth()
                    );
                }
                icon = Bitmap.createScaledBitmap(icon, curr_nav_container.getMeasuredHeight(), curr_nav_container.getMeasuredHeight(), true);
                nav_img.setImageBitmap(icon);
                nav_title.setText(item.getTitle());
                TranslateAnimation anim = new TranslateAnimation(0, 0, curr_nav_container.getMeasuredHeight(), 0);
                anim.setDuration(250);
//                anim.setFillAfter(true);

                curr_nav_container.startAnimation(anim);
                curr_nav_container.setVisibility(View.VISIBLE);
                stop_nav.setVisibility(View.VISIBLE);
                //showList(getSort());
                backButton.performClick();


            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.d("ADebugTag", "test: " + "Back is clicked");
                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayer);
                flayout.setVisibility(View.INVISIBLE);
                linearLayout.setAlpha(1.0f);

            }
        });

        frag_upButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                if (fragPosition > 0) {
                    showFragments(fragPosition - 1);
                    fragPosition -= 1;
                }


            }
        });

        frag_downButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                if ((rowItemList.size() - fragPosition) <= 1) {

                    // updateItemList();
                    //sortingItems(getSort());
                    //showList();
                    //loadExtraData();
                    //showToast();

                    //progressDialog.show();

                    searchPhotos(getRadius(), getSort(), getLat(), getLon(), currentPage + 1, "fragLoad");


                    //fragPosition = -1;
                    Log.d("ADebugTag", "currentPage: " + currentPage);
                } else {
                    showFragments(fragPosition + 1);
                    fragPosition += 1;
                }

                // showFragments(fragPosition + 1);
                // fragPosition += 1;


            }
        });
    }

    public void get_dist_rot(Location curr, Location dest) {
        float distance_to = curr.distanceTo(dest);
        float bearing = curr.bearingTo(dest);
        float heading = curr.getBearing();
        float compass_rotation = (360+((bearing + 360) % 360)-heading) % 360;
        sendMessageToWear2(distance_to, compass_rotation);
    }

    public void sendMessageToWear(){

        //Log.d("ADebugTag", "test: " + "in Message Servce");

        Intent sendMsgIntent = new Intent(this, sendMessage.class);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        item.getBmp().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] pic = stream.toByteArray();
        sendMsgIntent.putExtra("pic", pic);
        ByteBuffer loc = ByteBuffer.allocate(8);
        loc.putFloat(0, item.getLoc()[0]);
        loc.putFloat(4, item.getLoc()[1]);
        sendMsgIntent.putExtra("loc", loc.array());
        byte[] idByte = item.getPhotoId().getBytes(StandardCharsets.US_ASCII);

        // Log.d("ADebugTag", "sendMessage photoId: " + item.getPhotoId());
        sendMsgIntent.putExtra("photoId", idByte);
        startService(sendMsgIntent);
    }

    public void sendMessageToWear2(float distance, float compass_rotation){

        //Log.d("ADebugTag", "test: " + "in Message Servce 2");

        Intent sendMsgIntent = new Intent(this, sendMessage2.class);
        sendMsgIntent.putExtra("distance", distance);
        sendMsgIntent.putExtra("rot", compass_rotation);
        startService(sendMsgIntent);
    }

    public void sendMessageToWear_fin(){
        Log.d("ADebugTag", "test: " + "in Message Servce Fin");
        Intent sendMsgIntent = new Intent(this, sendMessage_fin.class);
        startService(sendMsgIntent);
    }


    public void sortByDistance() {

        Collections.sort(rowItemList, new Comparator<RowItem>() {
            @Override
            public int compare(RowItem p1, RowItem p2) {
                return Float.compare(p1.getDist(), p2.getDist()); // Ascending
            }

        });

    }

    public void sortByFavorite(){
        Collections.sort(rowItemList, new Comparator<RowItem>() {
            @Override
            public int compare(RowItem p1, RowItem p2) {
                return Integer.compare(p1.getFavoriteNum(), p2.getFavoriteNum()); // Ascending
            }

        });
        Collections.reverse(rowItemList);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ButtonsOnFragment();

        fragPosition = position;

        Log.d("ADebugTag", "showList: " + "im in on click");
        //item = rowItemList.get(position);
        showFragments(position);

    }

    public void showFragments(int position){

        item = rowItemList.get(position);
        Fragment newFragment = new MyFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.overlay_fragment_container, newFragment).commit();

    }

    public static String getLat(){
        return lat;
    }

    public void setLat(String lat){
        this.lat = lat;
    }

    public static String getLon(){
        return lon;
    }

    public void setLon(String lon){
        this.lon = lon;
    }

    public void setRadius(String radius){
        this.radius = radius;
    }

    public static String getRadius(){
        return radius;
    }

    public static String getSort(){
        return sort;
    }

    public void setSort(String sort){
        this.sort = sort;
    }

    public static RowItem getItem(){
        return item;
    }

    public static ImageView getFrag_img(){
        return frag_img;
    }

    public static TextView getFrag_name(){
        return frag_name;
    }

    public static FrameLayout getFlayout(){
        return flayout;
    }

    public static List<RowItem> getRowItems(){
        return rowItems;
    }

    public static int getNumberOfThreads(){
        return numberOfThreads;
    }

    public static NodeList getNodeList(){
        return nodeList;
    }

    public static long getThreadId(){
        return threadId;
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mGoogleApiClient.connect();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates && !navigating) {
            stopLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("ADebugTag", "test: " + "in Destroy in locationActivity");
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
        sendMessageToWear_fin();
        mGoogleApiClient.disconnect();
    }

}