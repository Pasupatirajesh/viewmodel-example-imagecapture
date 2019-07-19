package com.course.udacity.android.mytweaksdemo;

import android.graphics.Bitmap;

import org.parceler.Parcel;

@Parcel
public class MyImage {
    public Bitmap bitmap;
    public String filePath;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getFilePath() {
        return filePath;
    }

    public MyImage(String filePath, Bitmap bitmap){
        this.filePath = filePath;
        this.bitmap =bitmap;
    }


    public MyImage(){}

    public MyImage(Bitmap bitmap){
        this.bitmap = bitmap;
    }



}
