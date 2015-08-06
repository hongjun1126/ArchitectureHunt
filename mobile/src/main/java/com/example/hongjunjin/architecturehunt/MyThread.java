package com.example.hongjunjin.architecturehunt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.Keyboard;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by hongjunjin on 7/23/15.
 */
public class MyThread extends Thread {


    protected static final String restURL = "https://api.flickr.com/services/rest/";
    protected static final String getSizeMethod = "flickr.photos.getSizes";
    protected static final String getInfoMethod = "flickr.photos.getInfo";
    protected static final String getFavMethod = "flickr.photos.getFavorites";
    protected static final String pictureWidth = "500";
    protected static final String pictureHeight = "500";

    // using multi-threads to retrieve data from Flickr. Default number of threads I set here is 20
    public void run() {

        long threadId = locationActivity.threadId;

        int numThreads = locationActivity.numberOfThreads;

        NodeList nodeList = locationActivity.nodeList;
        String lat = locationActivity.lat;
        String lon = locationActivity.lon;
        List<RowItem> rowItems = locationActivity.rowItems;
        int len = nodeList.getLength();
        long currentThreadId = Thread.currentThread().getId();

        //System.out.println("id: " + Thread.currentThread().getId());
        String photoId;
        String photoSecret;
        String tempURL;
        String title;
        float distance;
        int start = 0;
        int length = 0;


        if (threadId == currentThreadId){
            //System.out.println("MyThread 1st running");
            start = 0;
            length = len/numThreads;
        }else if ((threadId + 1) == currentThreadId){
            //System.out.println("MyThread 2nd running");
            start = len/numThreads;
            length = (len * 2)/numThreads;
        }else if ((threadId + 2) == currentThreadId){
            //System.out.println("MyThread 3rd running");
            start = (len * 2)/numThreads;
            length = (len * 3)/numThreads;
        }else if ((threadId + 3) == currentThreadId){
            //System.out.println("MyThread 4th running");
            start = (len * 3)/numThreads;
            length = (len * 4)/numThreads;
        }else if ((threadId + 4) == currentThreadId) {
            //System.out.println("MyThread 5th running");
            start = (len * 4)/numThreads;
            length = (len * 5)/numThreads;
        }else if ((threadId + 5) == currentThreadId) {
            //System.out.println("MyThread 6th running");
            start = (len * 5)/numThreads;
            length = (len * 6)/numThreads;
        }else if ((threadId + 6) == currentThreadId) {
            //System.out.println("MyThread 7th running");
            start = (len * 6)/numThreads;
            length = (len * 7)/numThreads;
        }else if ((threadId + 7) == currentThreadId) {
            //System.out.println("MyThread 8th running");
            start = (len * 7)/numThreads;
            length = (len * 8)/numThreads;
        }else if ((threadId + 8) == currentThreadId) {
            //System.out.println("MyThread 9th running");
            start = (len * 8)/numThreads;
            length = (len * 9)/numThreads;
        }else if ((threadId + 9) == currentThreadId) {
            //System.out.println("MyThread 10th running");
            start = (len * 9)/numThreads;
            length = (len * 10)/numThreads;
        }else if ((threadId + 10) == currentThreadId) {
            //System.out.println("MyThread 11th running");
            start = (len * 10)/numThreads;
            length = (len * 11)/numThreads;
        }else if ((threadId + 11) == currentThreadId) {
            //System.out.println("MyThread 12th running");
            start = (len * 11)/numThreads;
            length = (len * 12)/numThreads;
        }else if ((threadId + 12) == currentThreadId) {
            //System.out.println("MyThread 13th running");
            start = (len * 12)/numThreads;
            length = (len * 13)/numThreads;
        }else if ((threadId + 13) == currentThreadId) {
            //System.out.println("MyThread 14th running");
            start = (len * 13)/numThreads;
            length = (len * 14)/numThreads;
        }else if ((threadId + 14) == currentThreadId) {
            //System.out.println("MyThread 15th running");
            start = (len * 14)/numThreads;
            length = (len * 15)/numThreads;
        }else if ((threadId + 15) == currentThreadId) {
            //System.out.println("MyThread 16th running");
            start = (len * 15)/numThreads;
            length = (len * 16)/numThreads;
        }else if ((threadId + 16) == currentThreadId) {
            //System.out.println("MyThread 17th running");
            start = (len * 16)/numThreads;
            length = (len * 17)/numThreads;
        }else if ((threadId + 17) == currentThreadId) {
            //System.out.println("MyThread 18th running");
            start = (len * 17)/numThreads;
            length = (len * 18)/numThreads;
        }else if ((threadId + 18) == currentThreadId) {
            //System.out.println("MyThread 19th running");
            start = (len * 18)/numThreads;
            length = (len * 19)/numThreads;
        }else if ((threadId + 19) == currentThreadId) {
            //System.out.println("MyThread 20th running");
            start = (len * 19)/numThreads;
            length = len;
        }



        for (int i = start; i < length; i++) {
            if (nodeList.item(i).getNodeName().equals("photo")) {

                photoId = nodeList.item(i).getAttributes().item(0).getNodeValue();
                photoSecret = nodeList.item(i).getAttributes().item(2).getNodeValue();

                StringBuffer photoSizesURL = new StringBuffer(restURL);
                photoSizesURL.append("?method=");
                photoSizesURL.append(getSizeMethod);
                photoSizesURL.append("&api_key=");
                photoSizesURL.append(Flickr_login.FLICKR_KEY);
                photoSizesURL.append("&photo_id=");
                photoSizesURL.append(photoId);
                tempURL = photoSizesURL.toString();
                //Log.d("ADebugTag", "url: " + tempURL);

                try {

                    URL url = new URL(tempURL); //get photoinfo
                    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
                    DocumentBuilder b = null;
                    b = f.newDocumentBuilder();
                    Document photoSizeDoc = b.parse(url.openStream());
                    photoSizeDoc.getDocumentElement().normalize();

                    StringBuffer getInfoBuffer = new StringBuffer(restURL);
                    getInfoBuffer.append("?method=");
                    getInfoBuffer.append(getInfoMethod);
                    getInfoBuffer.append("&api_key=");
                    getInfoBuffer.append(Flickr_login.FLICKR_KEY);
                    getInfoBuffer.append("&photo_id=");
                    getInfoBuffer.append(photoId);
                    getInfoBuffer.append("&secret=");
                    getInfoBuffer.append(photoSecret);
                    tempURL = getInfoBuffer.toString();

                    url = new URL(tempURL); //get photoinfo
                    //Log.d("ADebugTag", "url: " + photoURL);
                    Document photoDoc = b.parse(url.openStream());
                    photoDoc.getDocumentElement().normalize();

                    StringBuffer getFavButter = new StringBuffer(restURL);
                    getFavButter.append("?method=");
                    getFavButter.append(getFavMethod);
                    getFavButter.append("&api_key=");
                    getFavButter.append(Flickr_login.FLICKR_KEY);
                    getFavButter.append("&photo_id=");
                    getFavButter.append(photoId);
                    tempURL = getFavButter.toString();

                    url = new URL(tempURL);
                    Document favDoc = b.parse(url.openStream());
                    favDoc.getDocumentElement().normalize();

                    //Log.d("ADebugTag", "faverites: " + favDoc.getDocumentElement().getChildNodes().item(1).getAttributes().item(7).getNodeValue());


                   // Log.d("ADebugTag", "Value: " + photoSizeDoc.getDocumentElement().getChildNodes().item(1).getChildNodes().item(11).getAttributes().item(2).getNodeValue());
                   // Log.d("ADebugTag", "Value: " + photoSizeDoc.getDocumentElement().getChildNodes().item(1).getChildNodes().item(11).getAttributes().item(3).getNodeValue());

                    Node titleNode = photoDoc.getDocumentElement().getChildNodes().item(1).getChildNodes().item(3).getChildNodes().item(0);

                    String picWidth = photoSizeDoc.getDocumentElement().getChildNodes().item(1).getChildNodes().item(11).getAttributes().item(1).getNodeValue();
                    String picHeight = photoSizeDoc.getDocumentElement().getChildNodes().item(1).getChildNodes().item(11).getAttributes().item(2).getNodeValue();
                    if (picWidth.equals(pictureWidth) && !(picHeight.equals(pictureHeight)) && titleNode != null){

                        String picURL = photoSizeDoc.getDocumentElement().getChildNodes().item(1).getChildNodes().item(11).getAttributes().item(3).getNodeValue();
                        Bitmap bmp = getBitMap(picURL);

                        title = titleNode.getNodeValue();

                        Node locationNode = photoDoc.getDocumentElement().getChildNodes().item(1).getChildNodes().item(25);
                        float lat2 = Float.parseFloat(locationNode.getAttributes().item(0).getNodeValue());
                        float lng2 = Float.parseFloat(locationNode.getAttributes().item(1).getNodeValue());
                        float lat1 = Float.parseFloat(lat);
                        float lng1 = Float.parseFloat(lon);
                        distance = distFrom(lat1, lng1, lat2, lng2);
                        float loc[] = new float[]{lat2, lng2};


                        int favNum = Integer.parseInt(favDoc.getDocumentElement().getChildNodes().item(1).getAttributes().item(7).getNodeValue());


                        RowItem item = new RowItem(bmp, title, distance, favNum, loc, photoId);
                        rowItems.add(item);

                    }






                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);
        float distInMile = (float)(dist * 0.000621371192);


        return distInMile;
    }


    public Bitmap getBitMap(String picUrl){

        URL url = null;
        try {
            url = new URL(picUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            Bitmap bmp = BitmapFactory.decodeStream(url != null ? url.openConnection().getInputStream() : null);
            return bmp;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}