package com.diyou.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;

/**
 * 确认与取消选项的自定义dialog
 * @version 1.0
 */
public class ConfirmCreditorCancelDialog extends Dialog
        implements View.OnClickListener
{
    Activity context;
    private Button confirmBtn;
    private Button cancelBtn;
    private String title;
    private String subtitle;
    private onClickListener mOnClickListener;
    private String confirm;
    private String cancel;

    public ConfirmCreditorCancelDialog(Activity context, String title, String subtitle)
    {
        this(context, title, subtitle, null, null);
    }

    public ConfirmCreditorCancelDialog(Activity context, String title, String subtitle,
                                       String confirm, String cancel)
    {
        super(context, R.style.mystyle);
        this.context = context;
        this.title = title;
        this.subtitle = subtitle;
        this.confirm = confirm;
        this.cancel = cancel;

    }

    public ConfirmCreditorCancelDialog(Activity context, int titleId, String subtitleId){
        this(context, context.getString(titleId), subtitleId);
    }
    public onClickListener getOnClickListener()
    {
        return mOnClickListener;
    }

    public void setOnClickListener(onClickListener onClickListener)
    {
        this.mOnClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.custom_dialog);
        this.setCanceledOnTouchOutside(false);
        TextView title = (TextView) findViewById(R.id.customdialog_title);
        title.setText(this.title);
        TextView subtitle = (TextView) findViewById(R.id.customdialog_subtitle);
        subtitle.setText(this.subtitle);
        // 根据id在布局中找到控件对象
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        if (!StringUtils.isEmpty(confirm))
        {
            confirmBtn.setText(confirm);
        }
        if (!StringUtils.isEmpty(cancel))
        {
            cancelBtn.setText(cancel);
        }
        // 设置按钮的文本颜色
        confirmBtn.setTextColor(0xff1E90FF);
        cancelBtn.setTextColor(0xff1E90FF);

        // 为按钮绑定点击事件监听器
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        dismiss();
        switch (v.getId())
        {
        case R.id.confirm_btn:
            if (mOnClickListener != null)
            {
                mOnClickListener.confirmClick();
            }
            break;
        case R.id.cancel_btn:
            if (mOnClickListener != null)
            {
                mOnClickListener.cancelClick();
            }
            break;
        default:
            break;
        }
    }



    public interface onClickListener
    {
        void confirmClick();

        void cancelClick();
    }
}