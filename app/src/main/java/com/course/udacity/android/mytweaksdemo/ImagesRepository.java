package com.course.udacity.android.mytweaksdemo;

import android.graphics.Bitmap;

import java.io.FileNotFoundException;
import java.util.List;

public interface ImagesRepository {

    List<MyImage> saveImage(Bitmap image);

    void deleteImage(String path);

    List<MyImage> getImages() throws FileNotFoundException;

    Bitmap getImage(String path);



}
