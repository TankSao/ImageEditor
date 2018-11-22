package com.example.administrator.imageeditor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.security.acl.Group;

/**
 * Created by Administrator on 2018/11/22.
 */

public class ImageEditActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{
    private ImageView wrong,right,img;
    private RadioGroup group;
    private String[] colors = new String[]{"#ff2d2d","#ff0080","#3a006f","#6c6c6c","#ffd306","#0000e3","#9393ff","#00ec00","#80ffff","#9aff02","#bb3d00"};
    private Paint paint;
    private Canvas canvas;
    private Path path;
    private Bitmap bitmap;
    private double currentX, currentY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        wrong = findViewById(R.id.wrong);
        right = findViewById(R.id.right);
        group = findViewById(R.id.group);
        img = findViewById(R.id.img);
        img.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                img.setDrawingCacheEnabled(true);
                bitmap = img.getDrawingCache();
                canvas = new Canvas();
                canvas.setBitmap(bitmap);
                img.setImageBitmap(bitmap);
                img.setOnTouchListener(ImageEditActivity.this);
                //防止多次触发
                img.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });
        wrong.setOnClickListener(this);
        right.setOnClickListener(this);
        setPaint(0);
        path = new Path();
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.e("iii",i+"s");
                setPaint(i%colors.length-1);
            }
        });
    }

    //设置画笔
    private void setPaint(int i) {
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.parseColor(colors[i]));
        // 设置画笔风格
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        // 反锯齿
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.wrong:
                finish();
                break;
            case R.id.right:
                savePic();
                break;
        }
    }

    private void savePic() {
        img.setDrawingCacheEnabled(true);
        Bitmap bm = img.getDrawingCache();
        ImgUtils.saveBitmap(bm,"img2.png",ImageEditActivity.this);
        img.setDrawingCacheEnabled(false);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        double x = motionEvent.getX();
        double y = motionEvent.getY();
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo((float) x, (float) y);
                currentX = x;
                currentY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                path.quadTo((float) currentX, (float) currentY,(float) x, (float) y);
                currentX = x;
                currentY = y;
                break;

            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, paint);
                path.reset();
                break;
        }
        canvas.drawPath(path, paint);
        img.setImageBitmap(bitmap);
        return true;
    }
}
