package com.hgj.universal.pocket.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.hanks.htextview.rainbow.RainbowTextView;
import com.hgj.universal.pocket.R;


public class FullScreenTextActivity extends AppCompatActivity {
    private RelativeLayout rootLayout;
    private RainbowTextView rainbowText;
    private TextView normalText;

    private Dialog mDialog;
    private View dialogView;
    private ImageView closeDialogBtn;
    private EditText editText;
    private Button btBackColor;
    private Button btTextColor;
    private RadioGroup radioGroup;
    private RadioButton radioRainbow;
    private RadioButton radioNormal;
    private LinearLayout textColorSet;
    private SeekBar seekBar;
    private TextView tvFontSize;
    private Button submit;

    //是否使用彩虹字体，默认使用
    private boolean isCheckRainbow = true;
    //最终设置的三个属性：文本颜色、背景颜色、文本大小
    int textColor;
    int backColor;
    int textSize;
    //设置对话框中选择的文本颜色、背景颜色、文本大小
    int selectedTextColor;
    int selectedBackColor;
    int selectedTextSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen_text);
        initView();
    }

    private void initView() {
        rootLayout = findViewById(R.id.layout_root);
        rainbowText = findViewById(R.id.text_rainbow);
        normalText = findViewById(R.id.text_normal);
        //初始化backColor、textColor、textSize
        textColor = 0xffff0000;
        backColor = 0xffffffff;
        textSize = 100;
        setBackColor(backColor);
        setTextColor(textColor);
        setTextSize(textSize);
        setTextContent("右上角设置文字");
        initDialogView();
        checkIsSelectRainbow();
    }

    //设置背景颜色
    private void setBackColor(int color) {
        rootLayout.setBackgroundColor(color);
    }

    //设置字体颜色
    private void setTextColor(int color) {
        normalText.setTextColor(color);
    }

    //设置字体大小
    private void setTextSize(int size) {
        rainbowText.setTextSize(size);
        normalText.setTextSize(size);
    }

    //设置显示内容
    private void setTextContent(String content) {
        rainbowText.setText(content);
        normalText.setText(content);
    }

    /**
     * 对选择的是彩虹字体还是普通字体进行处理
     * 1 展示的字体发生变化   2 设置对话框发生变化
     */
    private void checkIsSelectRainbow() {
        if (isCheckRainbow) {
            showRainbowText();
            radioRainbow.setChecked(true);
            radioNormal.setChecked(false);
            textColorSet.setVisibility(View.GONE);
        } else {
            showNormalText();
            radioRainbow.setChecked(false);
            radioNormal.setChecked(true);
            textColorSet.setVisibility(View.VISIBLE);
        }
    }

    //显示彩虹字体
    private void showRainbowText() {
        rainbowText.setVisibility(View.VISIBLE);
        normalText.setVisibility(View.GONE);
    }

    //显示正常字体
    private void showNormalText() {
        rainbowText.setVisibility(View.GONE);
        normalText.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化底部设置弹窗
     */
    private void initDialogView() {
        mDialog = new Dialog(this);
        dialogView = LayoutInflater.from(this).inflate(R.layout.layout_setting_dialog, null);
        closeDialogBtn = dialogView.findViewById(R.id.img_close_dialog);
        editText = dialogView.findViewById(R.id.edit_text);
        btBackColor = dialogView.findViewById(R.id.bt_back_color);
        btTextColor = dialogView.findViewById(R.id.bt_text_color);
        radioGroup = dialogView.findViewById(R.id.radio_group);
        radioRainbow = dialogView.findViewById(R.id.radio_rainbow);
        radioNormal = dialogView.findViewById(R.id.radio_normal);
        textColorSet = dialogView.findViewById(R.id.text_color_set);
        seekBar = dialogView.findViewById(R.id.seek_bar);
        tvFontSize = dialogView.findViewById(R.id.tv_font_size);
        submit = dialogView.findViewById(R.id.bt_submit);
        mDialog.setContentView(dialogView);
        initDialogListener();
    }


    //更新选择的颜色
    private void updateSelectedColor() {
        ((GradientDrawable) btBackColor.getBackground()).setColor(selectedBackColor);
        ((GradientDrawable) btTextColor.getBackground()).setColor(selectedTextColor);
    }

    //点击设置，显示底部弹窗
    public void showBottomDialog(View v) {
        //设置EditText初始值
        if (isCheckRainbow) {
            editText.setText(rainbowText.getText());
        } else {
            editText.setText(normalText.getText());
        }
        //初始化颜色的值
        selectedBackColor = backColor;
        selectedTextColor = textColor;
        selectedTextSize = textSize;
        updateSelectedColor();
        //初始化字体大小进度条
        seekBar.setProgress(selectedTextSize);
        tvFontSize.setText("" + selectedTextSize);
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_dialog_background));
        window.setWindowAnimations(R.style.DialogWindowAnim);
        mDialog.show();
        //设置对话框大小，和底部margin。设置宽高要放在show()方法后面才生效
        WindowManager.LayoutParams lps = window.getAttributes();
        WindowManager windowManager = window.getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        lps.width = (int) (defaultDisplay.getWidth() * 0.5);
        lps.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lps.verticalMargin = 0.0f;
        window.setAttributes(lps);
    }

    /**
     * 监听设置弹窗上的事件
     */
    private void initDialogListener() {
        //关闭弹窗事件
        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        //radioGroup发生改变，选择彩虹字体还是正常字体
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_normal:
                        isCheckRainbow = false;
                        textColorSet.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radio_rainbow:
                        isCheckRainbow = true;
                        textColorSet.setVisibility(View.GONE);
                        break;
                }
            }
        });
        //选择背景色
        btBackColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPicker(selectedBackColor, true);
            }
        });
        //选择字体颜色
        btTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPicker(selectedTextColor, false);
            }
        });
        //选择字体大小
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                selectedTextSize = progress;
                tvFontSize.setText("" + selectedTextSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //点击确定，保存更改
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editText.getText())) {
                    Toast.makeText(getApplicationContext(), "显示的文本内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    String textContent = editText.getText().toString();
                    setTextContent(textContent);
                    checkIsSelectRainbow();
                    backColor = selectedBackColor;
                    textColor = selectedTextColor;
                    textSize = selectedTextSize;
                    setBackColor(backColor);
                    setTextColor(textColor);
                    setTextSize(textSize);
                    mDialog.dismiss();
                }
            }
        });
    }

    /**
     * 颜色选择器弹窗
     * @param initColor  初始化颜色
     * @param isChooseBackColor 是否为选择背景色
     */
    private void showColorPicker(int initColor, final boolean isChooseBackColor) {
        ColorPickerDialogBuilder
                .with(this)
                .initialColor(initColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .showBorder(false)
                .setPositiveButton("选择", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if (isChooseBackColor) {
                            selectedBackColor = selectedColor;
                        } else {
                            selectedTextColor = selectedColor;
                        }
                        updateSelectedColor();
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
}
