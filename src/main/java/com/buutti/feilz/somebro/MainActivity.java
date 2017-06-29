package com.buutti.feilz.somebro;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static android.R.color.white;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.WHITE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static com.buutti.feilz.somebro.R.id.view;

public class MainActivity extends AppCompatActivity {
    String currentPicPath;
    File f;
    public Boolean send;
    private boolean pic;
    EditText textToSend;
    ImageView photo;
    static Bitmap bitmap;
    Switch fbSwitch;
    Switch twitterSwitch;
    Button postButton;
    Button clipButton;
    LinearLayout innerLayout;
    SharePhotoContent content;
    ShareButton shareButton;
    public boolean shareButtonEnabled;



    final static int PICK_IMAGE = 1;
    final static int IMAGE_CAPTURE = 2;
    final static int REQUEST_READWRITE = 3;
    View demofake;
    DatabaseOperations db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pic=false;
        photo = (ImageView) findViewById(R.id.imageView);
        textToSend = (EditText) findViewById(R.id.postContent);
        demofake = (View)findViewById(R.id.demofake);
        demofake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSharedPreferences();
            }
        });

        db=new DatabaseOperations(this);

        fbSwitch = (Switch) findViewById(R.id.fbSwitch);
        twitterSwitch = (Switch) findViewById(R.id.twitterSwitch);
        postButton = (Button) findViewById(R.id.postButton);
        clipButton = (Button) findViewById(R.id.clipboard);
        innerLayout = (LinearLayout) findViewById(R.id.layoutID);

        enableSwitches();
        shareButtonEnabled = true;


        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.imagepicker, null);
        builder.setView(mView);
        final FloatingActionButton cameraButton = (FloatingActionButton) mView.findViewById(R.id.selectCamera);
        final FloatingActionButton galleryButton = (FloatingActionButton) mView.findViewById(R.id.selectFromGallery);
        final FloatingActionButton clearButton = (FloatingActionButton) mView.findViewById(R.id.clearButton);
        dialog = builder.create();
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, setImageUri());
                    startActivityForResult(takePictureIntent, IMAGE_CAPTURE);
                    dialog.dismiss();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READWRITE);
                }

            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent getPictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getPictureIntent.setType("image/*");
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");
                    Intent chooserIntent = Intent.createChooser(getPictureIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                    startActivityForResult(chooserIntent, PICK_IMAGE);
                    dialog.dismiss();
                } else {

                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READWRITE);
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo.setImageResource(android.R.drawable.ic_menu_camera);
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
        clipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String text = textToSend.getText().toString();
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", text);
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        fbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (shareButtonEnabled) {
                    if (isChecked) {
                        shareButton = (ShareButton) findViewById(R.id.shareButton);
                        if(pic) {
                            shareButton.setShareContent(content);
                        }else if(!textToSend.getText().toString().isEmpty()){

                            Bitmap bitmap = textAsBitmap(textToSend.getText().toString(),100, WHITE);

                            SharePhoto photo = new SharePhoto.Builder()
                                    .setBitmap(bitmap)
                                    .build();
                            content = new SharePhotoContent.Builder().addPhoto(photo)
                                    .setRef("asdf")
                                    .build();

                            shareButton.setShareContent(content);

                        }


                    } else {
                        shareButton.setShareContent(null);
                    }
                } else {
                    return;
                }
            }
        });

    }
    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void post() {

        if (twitterSwitch.isChecked()) {
            SocialNetwork sn = LoginFragment.mSocialNetworkManager.getSocialNetwork(LoginFragment.TWITTER);
            if (sn.isConnected()) {
                if (pic) {
                    if (sn.isConnected()) {
                        db.putInfo(currentPicPath, textToSend.getText().toString());
                        sn.requestPostPhoto(f, textToSend.getText().toString(), postingComplete);
                    }
                } else {
                    if (sn.isConnected()) {
                        db.putText(textToSend.getText().toString());
                        sn.requestPostMessage(textToSend.getText().toString(), postingComplete);
                    }
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri pickedImage = data.getData();
            currentPicPath = getPath(pickedImage);
            pic=true;
            setPic(photo);
        }
        if (requestCode == IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            setPic(photo);
            pic=true;
        }
    }


    private void setPic(ImageView imgView) {
        int targetW = 512;
        int targetH = 512;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPicPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleF = Math.max(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleF;

        bitmap = BitmapFactory.decodeFile(currentPicPath, bmOptions);
        f = new File(getApplicationContext().getCacheDir(), "tmpimage.png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Convert bitmap to byte array

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imgView.setImageBitmap(bitmap);

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .build();
        content = new SharePhotoContent.Builder().addPhoto(photo)
                .setRef("asdf")
                .build();
        shareButtonEnabled = true;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        } else return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==R.id.smlogin){
          /*  //Create && add methods to check if logged in to the FB service
            Intent i=new Intent(MainActivity.this,SMLoginHandler.class);
            startActivity(i);
            item.setChecked(!item.isChecked());
            send=item.isChecked();
            return false; */
        }
        if (id==R.id.menuMessageHistory){
            openPostHistory();
        }
        if (id==R.id.postButton){
            if (send) {
                post();
            }else {
                Toast.makeText(getApplicationContext(), "Please log in to at least one of the posting services", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public Uri setImageUri() {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        this.currentPicPath = file.getAbsolutePath();
        return imgUri;
    }
    private void openPostHistory(){
        Intent i = new Intent(MainActivity.this,MessageHistory.class);
        startActivity(i);
    }
    private OnPostingCompleteListener postingComplete = new OnPostingCompleteListener() {
        @Override
        public void onPostSuccessfully(int socialNetworkID) {
            Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
            Toast.makeText(getApplicationContext(),"Error while sending" + errorMessage,Toast.LENGTH_LONG).show();
        }
    };
    private void removeSharedPreferences(){
        getApplicationContext().getSharedPreferences("myLogin",MODE_PRIVATE).edit().remove("userPW").apply();
    }

    public void enableSwitches(){

        Intent i = getIntent(); // gets the previously created intent
        String isFbLoggedIn = i.getStringExtra("isFbLoggedIn");
        String isTwitterLoggedIn = i.getStringExtra("isTwitterLoggedIn");

         if(isFbLoggedIn.equals("true")){
            fbSwitch.setEnabled(true);
        }else {
             fbSwitch.setEnabled(false);
         }
        if(isTwitterLoggedIn.equals("true")){
            twitterSwitch.setEnabled(true);
        }else{
            twitterSwitch.setEnabled(false);
        }
    }

   /* public static void share(final String message,
                             final ShareContent shareContent,
                             final FacebookCallback<Sharer.Result> callback) {
        new ShareApi(message, shareContent)
                .share(callback);
    }*/
}