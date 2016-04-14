package com.jxlims.gary.testui;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jxlims.gary.myprinter.Global;
import com.jxlims.gary.myprinter.WorkService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SampleingEditActivity extends AppCompatActivity  implements View.OnClickListener {

    private    ImageView imageViewPicture=null;
    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;

    private static final int SCALE = 5;//照片缩小比例
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sampleing_edit);
        Button btnprint=(Button)findViewById(R.id.btnprint);

          imageViewPicture=(ImageView)findViewById(R.id.imageViewPicture);
        Bitmap bm = getImageFromAssetsFile("yellowmen.png");
        if (null != bm) {
            imageViewPicture.setImageBitmap(bm);
        }
        btnprint.setOnClickListener(this);
        imageViewPicture.setOnClickListener(this);


        Button buttonFeedAndCut=(Button)findViewById(R.id.buttonFeedAndCut);
        buttonFeedAndCut.setOnClickListener(this);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000 && resultCode == 1000)
        {
            String filename = data.getStringExtra("filepath");
            Bitmap bitmap=  ImageTools.getPhotoFromSDCard( Environment.getExternalStorageDirectory().getAbsolutePath(),filename);
            imageViewPicture.setImageBitmap(bitmap);

        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    //将保存在本地的图片取出并缩小后显示在界面上
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/image.jpg");
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    //将处理过的图片显示在界面上，并保存到本地
                    imageViewPicture.setImageBitmap(newBitmap);
                    ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
                    break;

                case CHOOSE_PICTURE:
                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();

                            imageViewPicture.setImageBitmap(smallBitmap);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    }
    public void showPicturePicker(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("图片来源");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照","相册"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;

                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    private  void Print(   Bitmap mBitmap)
    {

        int nPaperWidth = 384;
        if (mBitmap != null) {
            if (WorkService.workThread.isConnected()) {
                Bundle data = new Bundle();
                // data.putParcelable(Global.OBJECT1, mBitmap);
                data.putParcelable(Global.PARCE1, mBitmap);
                data.putInt(Global.INTPARA1, nPaperWidth);
                data.putInt(Global.INTPARA2, 0);
                WorkService.workThread.handleCmd(
                        Global.CMD_POS_PRINTPICTURE, data);
            } else {
                Toast.makeText(this, Global.toast_notconnect,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnprint: {

                Intent intent =new Intent(SampleingEditActivity.this,PrintActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, 1000);


//                "/mnt/sdcard/Pictures"
                break;
            }
            case R.id.imageViewPicture: {


                showPicturePicker(SampleingEditActivity.this);

                break;
            }
            case R.id.buttonFeedAndCut: {

                // 走纸到切刀位置并切纸
                byte[] buf = new byte[]{0x1d, 0x56, 0x42, 0x00};

                if (WorkService.workThread.isConnected()) {
                    Bundle data = new Bundle();
                    data.putByteArray(Global.BYTESPARA1, buf);
                    data.putInt(Global.INTPARA1, 0);
                    data.putInt(Global.INTPARA2, buf.length);
                    WorkService.workThread.handleCmd(Global.CMD_POS_WRITE, data);
                } else {
                    Toast.makeText(this, Global.toast_notconnect, Toast.LENGTH_SHORT).show();
                }

                break;
            }

        }
    }
    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }
}
