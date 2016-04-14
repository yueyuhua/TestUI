package com.jxlims.gary.testui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jxlims.gary.myprinter.Global;
import com.jxlims.gary.myprinter.WorkService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class PrintActivity extends AppCompatActivity {

    ImageView barview=null;
    ImageView fullimgview=null;
   int   QR_WIDTH=100;
   int  QR_HEIGHT=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        barview=(ImageView)findViewById(R.id.barcodeview);
     //   fullimgview=(ImageView)findViewById(R.id.fullimgview);


        createQRImage("这个编码是中文");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem m=  menu.add(0,1,1,"打印");
        m.setOnMenuItemClickListener( new MenuItemClickListener());
        return super.onCreateOptionsMenu(menu);
    }
    private final class MenuItemClickListener implements  MenuItem.OnMenuItemClickListener
    {


        @Override
        public boolean onMenuItemClick(MenuItem item) {

            Bitmap mBitmap = takeScreenShot(PrintActivity.this) ; // ((BitmapDrawable) imageViewPicture.getDrawable()).getBitmap();
         //   fullimgview.setImageBitmap(mBitmap);
         //   String filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
         //   ImageTools.savePhotoToSDCard(mBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(),filename);
         //   Intent intent=getIntent();
         //   intent.putExtra("filepath",filename);
         //   setResult(1000,intent);
         //   finish();;
     //       return false;
            int nPaperWidth = 576;
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
                    //Toast.makeText(this, Global.toast_notconnect,Toast.LENGTH_SHORT).show();
                }
            }

            return false;
        }
    }


    public void createQRImage(String url)
    {

            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1)
            {
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++)
            {
                for (int x = 0; x < QR_WIDTH; x++)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    }
                    else
                    {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面
            barview.setImageBitmap(bitmap);
        }

    public static Bitmap takeScreenShot(Activity activity) {

        // View是你需要截图的View

        View view = activity.getWindow().getDecorView();

        view.setDrawingCacheEnabled(true);

        view.buildDrawingCache();

        Bitmap b1 = view.getDrawingCache();



        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = 600 ;// activity.getWindowManager().getDefaultDisplay().getHeight();

        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, 160, width, height);
        view.destroyDrawingCache();
       // savePic(b, "/sdcard/screen_test.png");
        return b;

    }
}
