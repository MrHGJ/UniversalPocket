package com.hgj.universal.pocket.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.hgj.universal.pocket.R;
import com.hgj.universal.pocket.util.DimenUtil;
import com.hgj.universal.pocket.view.PaintView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PaintActivity extends AppCompatActivity implements PaintView.Callback, Handler.Callback {
    @BindView(R.id.paint_view)
    PaintView mPaintView;
    @BindView(R.id.undo)
    ImageView mUndoView;
    @BindView(R.id.redo)
    ImageView mRedoView;
    @BindView(R.id.pen)
    ImageView mPenView;
    @BindView(R.id.eraser)
    ImageView mEraserView;

    ImageView colorOne;
    ImageView colorTwo;
    ImageView colorThree;
    ImageView colorFour;
    ImageView colorFive;
    ImageView colorSix;
    ImageView colorSeven;
    ImageView colorEight;
    ImageView colorNine;

    private Handler mHandler;
    private ProgressDialog mSaveProgressDlg;
    private static final int MSG_SAVE_SUCCESS = 1;
    private static final int MSG_SAVE_FAILED = 2;
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 100;  //请求存储权限的code

    private int penSize = 2;
    private int penColor = 0xff2c2c2c;
    private int penColorIndex = 0;
    private int eraserSize = 50;

    private int multiColor = 0xffff4444;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_paint);
        ButterKnife.bind(this);
        mPaintView.setCallback(this);
        mPenView.setSelected(true);
        mUndoView.setEnabled(false);
        mRedoView.setEnabled(false);
        mHandler = new Handler(this);
    }

    /**
     * 重写返回逻辑
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);
            builder.setTitle("涂鸦不会保存，确定退出吗？");
            builder.setPositiveButton("退出",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //...To-do
                        }
                    });
            // 显示
            AlertDialog dialog = builder.create();
            Window window = dialog.getWindow();
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(getResources().getDrawable(R.drawable.paint_dialog_background));
            dialog.show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_SAVE_FAILED);
        mHandler.removeMessages(MSG_SAVE_SUCCESS);
    }

    private void initSaveProgressDlg() {
        mSaveProgressDlg = new ProgressDialog(this);
        mSaveProgressDlg.setMessage("正在保存，请稍后...");
        mSaveProgressDlg.setCancelable(false);
    }

    /**
     * 检查并申请存储权限
     */
    private void checkAndRequestPermission() {
        boolean isRequested = true;
        //如果Android版在6.0以上，申请运行时权限
        if (Build.VERSION.SDK_INT >= 19) {
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            for (String str : permissions) {
                //如果某一个权限未申请，则申请权限
                if (ContextCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED) {
                    isRequested = false;
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION_STORAGE);
                }
            }
        } else {
            beginSaveImage();
        }
        //判断权限是否已经申请过了，如果已经申请过，则直接开始保存图片
        if (isRequested) {
            beginSaveImage();
        }
    }

    /**
     * 权限发生变更时调用
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_STORAGE: {
                //若已经授权，则开始保存图片
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    beginSaveImage();
                } else {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    /**
     * 开始保存图片
     */
    private void beginSaveImage() {
        if (mSaveProgressDlg == null) {
            initSaveProgressDlg();
        }
        mSaveProgressDlg.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = mPaintView.buildBitmap();  //PaintView生成Bitmap
                String savedFile = saveImage(bm, 100); //保存图片并返回图片位置的绝对路径
                if (savedFile != null) {
                    scanFile(PaintActivity.this, savedFile);
                    mHandler.obtainMessage(MSG_SAVE_SUCCESS).sendToTarget();
                } else {
                    mHandler.obtainMessage(MSG_SAVE_FAILED).sendToTarget();
                }
            }
        }).start();
    }

    /**
     * 将bitmap转换成jpg存储到手机存储中
     */
    private String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        //获取手机存储Picture的位置
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (appDir == null) {
            return null;
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            //将数据写到文件中
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            //清空缓冲区数据
            fos.flush();
            //返回存储照片的绝对路径
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_SAVE_FAILED:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
                break;
            case MSG_SAVE_SUCCESS:
                mSaveProgressDlg.dismiss();
                Toast.makeText(this, "画板已保存", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    /**
     * 发送广播，通知手机图库发现该图片
     *
     * @param context  context
     * @param filePath 图片路径
     */
    private static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    /**
     * undo、redo状态发生改变时触发
     */
    @Override
    public void onUndoRedoStatusChanged() {
        mUndoView.setEnabled(mPaintView.canUndo());
        mRedoView.setEnabled(mPaintView.canRedo());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @OnClick({R.id.undo, R.id.redo, R.id.pen, R.id.eraser, R.id.clear, R.id.download})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.undo:
                mPaintView.undo();
                break;
            case R.id.redo:
                mPaintView.redo();
                break;
            case R.id.pen:
                mPaintView.setPenColor(penColor);
                mPaintView.setPenRawSize(DimenUtil.dp2pxInt(penSize));
                mPenView.setImageTintList(ColorStateList.valueOf(penColor));
                if (v.isSelected()) {
                    showPenSetDialog();  //弹出画笔选择框
                }
                v.setSelected(true);
                mEraserView.setSelected(false);
                mPaintView.setMode(PaintView.Mode.DRAW);
                break;
            case R.id.eraser:
                mPaintView.setEraserSize(DimenUtil.dp2pxInt(eraserSize));
                if (v.isSelected()) {
                    showEraserDialog();   //弹出橡皮擦大小选择框
                }
                v.setSelected(true);
                mPenView.setSelected(false);
                mPaintView.setMode(PaintView.Mode.ERASER);
                break;
            case R.id.clear:
                mPaintView.clear();
                break;
            case R.id.download:
                //点击保存图片，检查存储权限
                showSavePicDialog();
                break;
        }
    }

    /**
     * 是否保存图片对话框
     */
    public void showSavePicDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle("保存为图片到本地？");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkAndRequestPermission();
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.paint_dialog_background));
        dialog.show();
    }

    /**
     * 橡皮擦大小选择弹窗
     */
    public void showEraserDialog() {
        Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_eraser_dialog, null);
        SeekBar seekBar = dialogView.findViewById(R.id.seek_bar_eraser_size);
        final TextView tvEraserSize = dialogView.findViewById(R.id.tv_eraser_size);
        //根据橡皮擦大小初始化seekbar和textView
        tvEraserSize.setText("" + eraserSize);
        seekBar.setProgress(eraserSize);
        dialog.setContentView(dialogView);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.paint_dialog_background));
        dialog.show();
        //设置对话框大小，和底部margin。设置宽高要放在show()方法后面才生效
        WindowManager.LayoutParams lps = window.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        lps.width = (int) (screenWidth * 0.9);
        lps.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lps.verticalMargin = DimenUtil.dp2px(65) / screenHeight;
        window.setAttributes(lps);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                eraserSize = progress;
                tvEraserSize.setText("" + eraserSize);
                mPaintView.setEraserSize(DimenUtil.dp2pxInt(eraserSize));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 画笔大小和颜色选择弹窗
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void showPenSetDialog() {
        Dialog dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_pen_dialog, null);
        SeekBar seekBar = dialogView.findViewById(R.id.seek_bar_pen_size);
        final TextView tvPenSize = dialogView.findViewById(R.id.tv_pen_size);
        initColorView(dialogView);
        tvPenSize.setText("" + penSize);
        seekBar.setProgress(penSize);
        dialog.setContentView(dialogView);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.paint_dialog_background));
        dialog.show();
        //设置对话框大小，和底部margin。设置宽高要放在show()方法后面才生效
        WindowManager.LayoutParams lps = window.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        lps.width = (int) (screenWidth * 0.9);
        lps.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lps.verticalMargin = DimenUtil.dp2px(65) / screenHeight;
        window.setAttributes(lps);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                penSize = progress;
                tvPenSize.setText("" + penSize);
                mPaintView.setPenRawSize(DimenUtil.dp2pxInt(penSize));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void initColorView(View dialogView) {
        colorOne = dialogView.findViewById(R.id.color_one);
        colorTwo = dialogView.findViewById(R.id.color_two);
        colorThree = dialogView.findViewById(R.id.color_three);
        colorFour = dialogView.findViewById(R.id.color_four);
        colorFive = dialogView.findViewById(R.id.color_five);
        colorSix = dialogView.findViewById(R.id.color_six);
        colorSeven = dialogView.findViewById(R.id.color_seven);
        colorEight = dialogView.findViewById(R.id.color_eight);
        colorNine = dialogView.findViewById(R.id.color_nine);
        final ImageView[] colorViews = {colorOne, colorTwo, colorThree, colorFour, colorFive, colorSix, colorSeven, colorEight, colorNine};
        final int[] colors = {0xff2c2c2c, 0xffff1a3b, 0xffff9418, 0xfffffc19, 0xff00e676, 0xffb388fe, 0xff038dcc, 0xffe819ff};
        clearAllSelectedColor(colorViews);
        colorViews[penColorIndex].setSelected(true);
        adjustImgSize(colorViews[penColorIndex], 32);
        for (int i = 0; i < colorViews.length - 1; i++) {
            //动态设置每个颜色圆块的背景
            ((GradientDrawable) ((StateListDrawable) colorViews[i].getBackground()).getStateDrawable(0)).setColor(colors[i]);
            ((GradientDrawable) ((StateListDrawable) colorViews[i].getBackground()).getStateDrawable(1)).setColor(colors[i]);
            colorViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearAllSelectedColor(colorViews);
                    switch (view.getId()) {
                        case R.id.color_one:
                            penColorIndex = 0;
                            break;
                        case R.id.color_two:
                            penColorIndex = 1;
                            break;
                        case R.id.color_three:
                            penColorIndex = 2;
                            break;
                        case R.id.color_four:
                            penColorIndex = 3;
                            break;
                        case R.id.color_five:
                            penColorIndex = 4;
                            break;
                        case R.id.color_six:
                            penColorIndex = 5;
                            break;
                        case R.id.color_seven:
                            penColorIndex = 6;
                            break;
                        case R.id.color_eight:
                            penColorIndex = 7;
                            break;
                    }
                    colorViews[penColorIndex].setSelected(true);
                    adjustImgSize(colorViews[penColorIndex], 32);
                    penColor = colors[penColorIndex];
                    mPaintView.setPenColor(penColor);
                    mPenView.setImageTintList(ColorStateList.valueOf(penColor));
                }
            });
        }
        colorNine.setImageTintList(ColorStateList.valueOf(multiColor));
        colorNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllSelectedColor(colorViews);
                colorNine.setSelected(true);
                adjustImgSize(colorNine, 32);
                showColorPicker(multiColor);
                penColorIndex = 8;
                penColor = multiColor;
                mPaintView.setPenColor(penColor);
                colorNine.setImageTintList(ColorStateList.valueOf(multiColor));
                mPenView.setImageTintList(ColorStateList.valueOf(multiColor));
            }
        });
    }

    /**
     * 清除所有颜色块的选择状态，并重置大小为25dp
     */
    private void clearAllSelectedColor(ImageView[] colorViews) {
        for (int i = 0; i < colorViews.length; i++) {
            colorViews[i].setSelected(false);
            adjustImgSize(colorViews[i], 25);
        }
    }

    /**
     * 颜色选择器弹窗
     * @param initColor 初始化颜色
     */
    private void showColorPicker(int initColor) {
        ColorPickerDialogBuilder
                .with(this)
                .initialColor(initColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .showBorder(false)
                .setPositiveButton("选择", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        multiColor = selectedColor;
                        penColor = multiColor;
                        mPaintView.setPenColor(penColor);
                        colorNine.setImageTintList(ColorStateList.valueOf(multiColor));
                        mPenView.setImageTintList(ColorStateList.valueOf(multiColor));
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    /**
     * 重置ImageView的大小
     */
    private void adjustImgSize(ImageView imageView, int dp) {
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = DimenUtil.dp2pxInt(dp);
        layoutParams.height = DimenUtil.dp2pxInt(dp);
        imageView.setLayoutParams(layoutParams);
    }
}