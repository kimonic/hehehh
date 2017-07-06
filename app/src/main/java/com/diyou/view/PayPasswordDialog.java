package com.diyou.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.diyou.config.Constants;
import com.diyou.util.StringUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;

/**
 * 支付密码dialog
 * 
 * @author wonder
 * 
 */
public class PayPasswordDialog extends Dialog
        implements android.view.View.OnClickListener
{
    private Window window = null;
    // public TextView txt_Title = null;
    // public TextView txt_Content = null;
    // public Button btn_Ok = null;
    // public Button btn_Close = null;

    private PayListener mPayListener;
    private EditText mEtPayment;

    public PayPasswordDialog(Context context)
    {
        super(context);
        setOwnerActivity((Activity) context);
    }

    public PayPasswordDialog(Context context, int theme)
    {
        super(context, theme);

    }

    public PayPasswordDialog(Context context, PayListener payListener)
    {
        super(context);
        this.mPayListener = payListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDialog(R.layout.dialog_payment);
        findViewById(R.id.dialog_close).setOnClickListener(this);
        findViewById(R.id.sure_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
        mEtPayment = (EditText) findViewById(R.id.payment);
        // InputMethodManager imm = (InputMethodManager)
        // getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setDialog(int layoutResID)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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

    @Override
    public void show() {
        if(mEtPayment != null){
            mEtPayment.setText("");
        }
        super.show();
    }

    /**
     * 关闭dialog
     */
    public void closeDialog()
    {
        dismiss();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.dialog_close:
            dismiss();
            break;
        case R.id.cancel_button:
            dismiss();
            break;
        case R.id.sure_button:
            if (mPayListener != null)
            {
                String password = StringUtils.getString(mEtPayment);
                int length = password.length();
                if (length < Constants.PAY_PASSWORD_MIN_LENGTH || length > Constants.PAY_PASSWORD_MAX_LENGTH)
                {
                    ToastUtil.show(R.string.remind_input_pay_password);
                }else{
                    mPayListener.payHandle(mEtPayment,password);
                }
            }
            break;
        default:
            break;
        }
    }

    public interface PayListener
    {
        void payHandle(EditText editText, String password);
    }

    public PayListener getmPayListener()
    {
        return mPayListener;
    }

    public void setmPayListener(PayListener mPayListener)
    {
        this.mPayListener = mPayListener;
    }
}
