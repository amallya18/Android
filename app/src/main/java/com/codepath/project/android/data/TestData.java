package com.codepath.project.android.data;

import com.jjoe64.graphview.series.DataPoint;

import java.util.Random;

public class TestData {

    public static final String [] itemName = {"HP Notebook 14-ax010nr", "32GB USB Flash Drive", "32GB USB Flash Drive"};

    public static final String [] itemUrl = {"http://ssl-product-images.www8-hp.com/digmedialib/prodimg/lowres/c05218075.png",
            "http://ssl-product-images.www8-hp.com/digmedialib/prodimg/lowres/c04381013.png",
            "http://ssl-product-images.www8-hp.com/digmedialib/prodimg/lowres/c04478617.png"};

    public static final String [] reviewItemName = {"Original Ink Cartridges", "HP Premium Plus Glossy Photo Paper", "HP Sprocket Photo Printer"};

    public static final String [] reviewItemUrl = {"http://ssl-product-images.www8-hp.com/digmedialib/prodimg/lowres/c02436844.png",
            "http://ssl-product-images.www8-hp.com/digmedialib/prodimg/lowres/c02702779.png",
            "http://ssl-product-images.www8-hp.com/digmedialib/prodimg/lowres/c05266447.png"};

    public static final String [] catItemName = {"Phones", "Tablets", "Television", "Camera"};

    public static final String [] catItemUrl = {"https://images-na.ssl-images-amazon.com/images/I/A1hkP61l4UL._SL1500_.jpg",
            "https://images-na.ssl-images-amazon.com/images/I/61XZBdl6CtL._SL1000_.jpg",
            "https://images-na.ssl-images-amazon.com/images/I/71ZFHJ6u9dL._SL1500_.jpg",
            "https://images-na.ssl-images-amazon.com/images/I/81Ds3GA1DvL._SL1500_.jpg"};


    public static final String MORE = "MORE";

    public static final String MORE_URL = "https://pbs.twimg.com/profile_images/558750489152458752/pBBTFL0j.png";

    public static final String[] tabNameList = {"Samsung","Apple","Sony","LG"};

    public static  final String USER_PROFILE_RANDOM = "https://pbs.twimg.com/profile_images/792911867563749376/R3_7PZg_.jpg";

    public static  final String USER_PROFILE_PLACEHOLDER = "http://trackback.net/wp-content/uploads/2015/02/Dummy-profile-picture.png";

    private static final int DATA_SIZE = 100;

    public static DataPoint[] getDataPoint(){
        DataPoint[] dataPoints = new DataPoint[DATA_SIZE];

        for(int i = 0; i < DATA_SIZE; i++){
            DataPoint d = new DataPoint(i, getRandom(i));
            dataPoints[i] = d;
        }
        return dataPoints;
    }

    private static int getRandom(int n){
        int max = (120 - n/2) + 1 + n/24;
        int min = (120 - n/2) + 1 - n/24;

        Random ran = new Random();
        return ran.nextInt(max) + min;
    }

    public static String getMaxMinPrice(String currPrice, String type){
        Double price = new Double(currPrice);
        Random rand = new Random();
        int randomNum = rand.nextInt(6) + 1;
        if(type.equals("MAX")){
            price = price * (1+randomNum*(0.01));
        } else{
            price = price * (1-randomNum*(0.01));
        }
        price = Math.floor(price * 100) / 100;
        return (new Double(price)).toString();
    }

}
