package com.course.udacity.android.mytweaksdemo;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    public static final int MY_PERMISSIONS_EXTERNAL_STORAGE = 2;
    private static final String AUTHORITY = "com.course.udacity.android.mytweaksdemo.provider";
    private static final String TAG = MainActivity.class.getCanonicalName() ;


    private ImageView mImageView;
    private Uri contentUri;
    private String currentPhotoPath;

    private DrawerLayout mDrawer;
    private Bitmap thumbNail;
    private MyBitmapModel mMyBitmapModel;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mImageView = findViewById(R.id.imageView);


        mMyBitmapModel = ViewModelProviders.of(this).get(MyBitmapModel.class);


        try {
            mMyBitmapModel.getImagesForLiveData().observe(this, new Observer<List<MyImage>>() {
                @Override
                public void onChanged(@Nullable List<MyImage> myImages) {

                    if(myImages!= null && myImages.size() > 0)

                            mImageView.setImageBitmap(myImages.get(myImages.size()-1).getBitmap());

                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
                if (getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                   // Permission Granted , Start Camera preview
                    Snackbar.make(mDrawer, R.string.camera_permission_g, Snackbar.LENGTH_SHORT).show();
                    requestMediaStoragePermission();
                    startCamera();
                } else {

                    // permission request was denied
                    Snackbar.make(mDrawer, "Permission Denied", Snackbar.LENGTH_SHORT).show();
                    requestCameraPermission();
                    requestMediaStoragePermission();

                }


            }
        } else if (id == R.id.nav_gallery) {


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void requestMediaStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(this, "Write to Storage permission required", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                  MY_PERMISSIONS_EXTERNAL_STORAGE);

        } else {
            Snackbar.make(mDrawer, "Storage unavailable until permission granted", Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_EXTERNAL_STORAGE);
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.CAMERA)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            Snackbar.make(mDrawer, "Camera access required", Snackbar.LENGTH_INDEFINITE).setAction(
                    "OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //No explanation needed
                            // request the permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    }
            ).show();

        } else {
            Snackbar.make(mDrawer, "Camera Unavailable until permission granted", Snackbar.LENGTH_SHORT).show();
            // request the permission
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode== MY_PERMISSIONS_REQUEST_CAMERA){

                if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Permission has been granted , Start the Camera Activity
                    Snackbar.make(mDrawer, "Camera Permission Granted", Snackbar.LENGTH_SHORT).show();
                    startCamera();

                } else {
                    // Permission request was denied
                    Snackbar.make(mDrawer, "Camera Permission Denied", Snackbar.LENGTH_SHORT).show();

                }
            } else if(requestCode == MY_PERMISSIONS_EXTERNAL_STORAGE){

                if (PermissionUtil.verifyPermissions(grantResults)){
                    Snackbar.make(mDrawer, "Write permission granted", Snackbar.LENGTH_SHORT).show();

                } else {
                    // Permission request was denied
                    Snackbar.make(mDrawer, "Write permission denied", Snackbar.LENGTH_SHORT).show();

                }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 7 && resultCode == RESULT_OK){
            assert data != null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                thumbNail = null;
                try {
                    requestCameraPermission();
                    requestMediaStoragePermission();
                    thumbNail = MediaStore.Images.Media.getBitmap(getContentResolver(), contentUri);
                    thumbNail = decodeSampleBitmapFromFile(currentPhotoPath, 500, 200);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mImageView.setImageBitmap(thumbNail);
                mMyBitmapModel.saveImage(thumbNail);

            }
        }
    }


    public static Bitmap decodeSampleBitmapFromFile(String path,
                                                    int reqWidth, int reqHeight){
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        //Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig  = Bitmap.Config.RGB_565;
        int inSampleSize =1;

        if(height > reqHeight){
            inSampleSize = Math.round((float)height/ (float) reqHeight);

        }
        int expectedWidth = width/inSampleSize;
        if (expectedWidth > reqWidth) {
            inSampleSize = Math.round((float)width/ (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }



    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startCamera(){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that a camera activity is there to handle this
        if(cameraIntent.resolveActivity(getPackageManager()) != null){
            requestMediaStoragePermission();
            // Create the File where the photo should go

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex){
                ex.printStackTrace();
                //Error occured during file creation
            }

            // Continue only if the File was successfully created
            if(photoFile != null){
                contentUri = FileProvider.getUriForFile(this,
                        AUTHORITY, photoFile);
                Log.i(TAG, contentUri+"");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                startActivityForResult(cameraIntent, 7);

//                SharedPreferences shd = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                SharedPreferences.Editor editor = shd.edit();
//                editor.putString("ContentUri", contentUri.toString());
//                editor.apply();

                galleryAddPic();
            }
        }
    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public File createImageFile() throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory(); // storage directory
        Log.i(TAG, storageDir+"");
        File subDir = new File(storageDir, "/pictures");
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                subDir);



//         Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d("MainActivity", currentPhotoPath);
        return image;

    }

    /**
     * To add picture to the Gallery/ Media Provider's database
     *
     */

    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
