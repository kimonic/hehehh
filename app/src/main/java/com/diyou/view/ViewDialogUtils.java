package com.diyou.view;

import com.diyou.v5yibao.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * 自定义dialog类
 * 
 * @author lijingjin
 * 
 */
public class ViewDialogUtils extends Dialog
{
    private Window window = null;
    public TextView txt_Title = null;
    public TextView txt_Content = null;
    public Button btn_Ok = null;
    public Button btn_Close = null;

    public ViewDialogUtils(Context context)
    {
        super(context);
        setOwnerActivity((Activity) context);
    }

    public ViewDialogUtils(Context context, int theme)
    {
        super(context, theme);

    }

    public void setDialog(int layoutResID)
    {
        setContentView(layoutResID);
        window = getWindow(); // 得到对话框
        window.setWindowAnimations(R.style.dialogWindowAnim); // 设置窗口弹出动画
        window.getDecorView().setPadding(0, 0, 0, 10);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        // 设置触摸对话框意外的地方不能取消对话框
        setCanceledOnTouchOutside(true);
        // 阻止返回键响应
        setCancelable(true);
    }

    // 如果自定义的Dialog声明为局部变量，就可以调用下面两个方法进行显示和消失，全局变量则无所谓
    /**
     * 显示dialog
     */
    public void showDialog()
    {
        show();
    }

    /**
     * 关闭dialog
     */
    public void Closedialog()
    {
        dismiss();
    }
}
