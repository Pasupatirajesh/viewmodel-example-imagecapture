package com.course.udacity.android.mytweaksdemo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

class MyBitmapModel extends
        ViewModel implements ImagesRepository {
    private static final String TAG = "MyBitmapModel";
    private static final String PATH = "/pictures";
    private File mStorage;
    private static  ArrayList<MyImage> list = new ArrayList<>();


    private MutableLiveData<List<MyImage>> mMutableLiveData;

    public MyBitmapModel(){


        File externalStorage = Environment.getExternalStorageDirectory();
        Log.i(TAG, externalStorage.toString());
        mStorage = new File(externalStorage, PATH);
        if(!mStorage.exists()){
            if(!mStorage.mkdirs()){
                Log.e(TAG, "Could not create storage directory: "+mStorage.getAbsolutePath());
            }
        }

    }

    @Override
    public List<MyImage> saveImage(Bitmap image) {
        list.add(new MyImage(image));
        mMutableLiveData.postValue(list);
        return list;
    }

    @Override
    public void deleteImage(String path) {

    }

    @Override
    public List<MyImage> getImages()  {
        File[] files = mStorage.listFiles();
        if(files == null){
            Log.e(TAG, "Could not list files");
            return null;
        }

        for(File f : files){
            Log.i(TAG, "The files are "+ f);

            Bitmap bitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(f.getPath());

            list.add(new MyImage(bitmap));


        }
        return list;
    }

    LiveData<List<MyImage>> getImagesForLiveData() throws FileNotFoundException {
        if (mMutableLiveData == null){
            mMutableLiveData = new MutableLiveData<List<MyImage>>();
            mMutableLiveData.postValue(getImages());
//            mMutableLiveData.setValue(getImages());
//
        }
        return mMutableLiveData;
    }

    @Override
    public Bitmap getImage(String path) {
        try {
            byte[] encodedByte = Base64.decode(path, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodedByte,0, encodedByte.length);

            return bitmap;
        } catch (Exception e){
            e.getMessage();
            return null;
        }
    }




}
