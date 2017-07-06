package com.diyou.view;

import com.diyou.v5yibao.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

/**
 * 自定义进度条
 * 
 * @author Administrator
 *
 */
public class ProgressDialogBar extends Dialog
{
    public static ProgressDialogBar ProgressDialogBar = null;

    public ProgressDialogBar(Context context)
    {
        super(context);
    }

    public ProgressDialogBar(Context context, int theme)
    {
        super(context, theme);
    }

    public static ProgressDialogBar createDialog(Context context)
    {
        ProgressDialogBar = new ProgressDialogBar(context,
                R.style.CustomProgressDialog);
        ProgressDialogBar.setContentView(R.layout.progress_layout);
        return ProgressDialogBar;
    }

    /*
     * public void onWindowFocusChanged(boolean hasFocus){
     * 
     * if (ProgressDialogBar == null){ return; } // ImageView imageView =
     * (ImageView) ProgressDialogBar.findViewById(R.id.img);//////
     * //AnimationDrawable animationDrawable = (AnimationDrawable)
     * imageView.getBackground(); //animationDrawable.start(); }
     * 
     * /**
     * 
     * [Summary] setTitile 标题
     * 
     * @param strTitle
     * 
     * @return
     *
     */
    public ProgressDialogBar setTitile(String strTitle)
    {
        return ProgressDialogBar;
    }

    /**
     * 
     * [Summary] setMessage 提示内容
     * 
     * @param strMessage
     * @return
     *
     */
    public void setProgressMsg(String strMessage)
    {
        TextView tvMsg = (TextView) ProgressDialogBar
                .findViewById(R.id.text_progress);
        if (tvMsg != null)
            tvMsg.setText(strMessage);
    }
}
