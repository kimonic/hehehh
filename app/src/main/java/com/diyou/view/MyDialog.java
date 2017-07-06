package com.diyou.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.diyou.activity.LockActivity;
import com.diyou.activity.LockSetupActivity;
import com.diyou.v5yibao.R;

public class MyDialog extends Dialog
        implements android.view.View.OnClickListener
{

    private Window window;
    private boolean isDelete;
    private boolean isUpdate;
    private boolean isCreate;
    private boolean isEditMode;
    private Activity context;
    private Intent intent;

    public MyDialog(Activity context, boolean isCreate, boolean isUpdate,
            boolean isDelete, boolean isEditMode)
    {
        super(context, R.style.MyDialog);
        this.context = context;
        this.isCreate = isCreate;
        this.isUpdate = isUpdate;
        this.isDelete = isDelete;
        this.isEditMode = isEditMode;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.my_dialog);
        this.setCanceledOnTouchOutside(false);
        initWindowView();
        initView();
    }

    private void initView()
    {
        Button create = (Button) findViewById(R.id.my_dialog_create);
        create.setEnabled(isCreate);
        create.setOnClickListener(this);
        Button update = (Button) findViewById(R.id.my_dialog_update);
        update.setEnabled(isUpdate);
        update.setOnClickListener(this);
        Button delete = (Button) findViewById(R.id.my_dialog_delete);
        delete.setEnabled(isDelete);
        delete.setOnClickListener(this);
        findViewById(R.id.my_dialog_cancel).setOnClickListener(this);
    }

    private void initWindowView()
    {
        window = getWindow();
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
        switch (v.getId())
        {
        case R.id.my_dialog_create:
            intent = new Intent(context, LockSetupActivity.class);
            context.startActivityForResult(intent, 0);
            this.dismiss();
            break;
        case R.id.my_dialog_update:
            intent = new Intent(context, LockActivity.class);
            intent.putExtra("updatePassword", true);
            intent.putExtra("ISEDITMODE", isEditMode);
            context.startActivityForResult(intent, 0);
            this.dismiss();

            break;
        case R.id.my_dialog_delete:
            intent = new Intent(context, LockActivity.class);
            intent.putExtra("deletePassword", true);
            intent.putExtra("ISEDITMODE", isEditMode);
            context.startActivityForResult(intent, 0);

            this.dismiss();
            break;
        case R.id.my_dialog_cancel:
            this.dismiss();
            break;

        default:
            break;
        }
    }
}
