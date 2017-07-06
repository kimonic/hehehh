package com.diyou.view;

import java.io.File;

import com.diyou.util.ImageUtil;
import com.diyou.v5yibao.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class PhotoDialog extends Dialog
        implements android.view.View.OnClickListener
{

    private Activity context;
    private File photoFile;

    public PhotoDialog(Activity context, File photoFile)
    {
        super(context, R.style.MyDialog);
        this.context = context;
        this.photoFile = photoFile;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.photo_dialog);
        this.setCanceledOnTouchOutside(false);
        initWindowView();
        initView();
    }

    private void initView()
    {
        Button btnPick = (Button) findViewById(R.id.btn_photo_dialog_pick);
        btnPick.setOnClickListener(this);
        Button btnTake = (Button) findViewById(R.id.btn_photo_dialog_take);
        btnTake.setOnClickListener(this);
        findViewById(R.id.btn_photo_dialog_cancel).setOnClickListener(this);
    }

    private void initWindowView()
    {
        Window window = getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.getDecorView().setPadding(0, 0, 0, 10);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    @Override
    public void onClick(View v)
    {
        this.dismiss();
        switch (v.getId())
        {
        case R.id.btn_photo_dialog_pick:
            ImageUtil.pickPhoto(context);
            break;
        case R.id.btn_photo_dialog_take:
            ImageUtil.takePhoto(context, photoFile);
            break;
        case R.id.btn_photo_dialog_cancel:

            break;

        default:
            break;
        }
    }
}
