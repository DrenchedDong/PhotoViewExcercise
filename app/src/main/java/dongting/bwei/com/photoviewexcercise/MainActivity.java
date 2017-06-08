package dongting.bwei.com.photoviewexcercise;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener{

    private PhotoView photoView;
    private PopupWindow popupWindow;

    private static final String SAVE_REAL_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath();//保存的确切位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoView = (PhotoView) findViewById(R.id.photoview);

        photoView.setImageResource(R.drawable.a);

        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //Toast.makeText(MainActivity.this, "长按事件", Toast.LENGTH_SHORT).show();
                alert();

                return true;
            }
        });photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //Toast.makeText(MainActivity.this, "长按事件", Toast.LENGTH_SHORT).show();
                alert();

                return true;
            }
        });
    }

    private void alert() {

        final View view = View.inflate(this, R.layout.popwindow, null);

        popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);// 取得焦点
        //进入退出的动画
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);

        view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.pop_layout).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        popupWindow.dismiss();
                    }
                }
                return true;
            }
        });

        // 按下android回退物理键 PopipWindow消失解决
        photoView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });

        Button btn_save=(Button) view.findViewById(R.id.btn_save);
        Button btn_cancel=(Button) view.findViewById(R.id.btn_cancel);
        Button btn_look=(Button) view.findViewById(R.id.btn_look);

        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_look.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_save:
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a);

                try {
                    save(bitmap, "photoview.jpg","/photoview");

                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_look:

                look();

                break;
            case R.id.btn_cancel:
                //Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                break;
        }
    }

    private void look() {
        Toast.makeText(this, "打开相册", Toast.LENGTH_SHORT).show();

        Intent picture = new Intent
                (Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, 1);
    }

    private void save(Bitmap bm, String fileName, String path) throws IOException {
            String subForder = SAVE_REAL_PATH + path;
            File foder = new File(subForder);
            if (!foder.exists()) {
                foder.mkdirs();
            }
            File myCaptureFile = new File(subForder, fileName);
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();

        //扫描sd卡
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File("photoview.jpg"));
        intent.setData(uri);
        sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            c.close();
        }
    }

    //加载图片
    private void showImage(String imaePath){
        Bitmap bm = BitmapFactory.decodeFile(imaePath);
        ((ImageView)findViewById(R.id.photoview)).setImageBitmap(bm);
    }
}