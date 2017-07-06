package com.diyou.view;

import com.diyou.bean.Reimbursement;
import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <p>
 * Title: CalculatorDialog
 * </p>
 * <p>
 * Description:自定义Dialog（参数传入Dialog样式文件，Dialog布局文件）
 * </p>
 * <p>
 * Copyright (c): 20150716
 * </p>
 * 
 * @author 林佳荣
 * @version 1.2
 */
public class CalculatorIncomeDialog extends Dialog
        implements android.view.View.OnClickListener
{
    Activity context;
    private ImageView spinner_performclick;
    private Window window = null;
    private EditText investment_money;
    private EditText investment_mouth;
    private TextView product_expected_income;
    private TextView bank_expected_income;
    private Reimbursement type = Reimbursement.Dengebenxi;
    private String rate;
    private String mouth;
    private CustomKeyboard mCustomKeyboard;

    public CalculatorIncomeDialog(Activity context)
    {
        super(context, R.style.MyDialog);
        this.context = context;

    }

    public CalculatorIncomeDialog(Activity context, String rate, String mouth)
    {
        super(context, R.style.MyDialog);
        this.context = context;
        this.rate = rate;
        this.mouth = mouth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.calculator_income_dialog);
        this.setCanceledOnTouchOutside(false);
        mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview,
                R.xml.hexkbd);
        initWindowView();
        findViewById(R.id.calculator_dialog_false).setOnClickListener(this);
        // initView();
        // initListener();
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

    private void initListener()
    {
        spinner_performclick.setOnClickListener(this);
        investment_money
                .addTextChangedListener(new MyTextListener("investment_money"));
        investment_mouth
                .addTextChangedListener(new MyTextListener("investment_mouth"));

    }

    private void initView()
    {

        investment_money = (EditText) findViewById(R.id.investment_money);
        mCustomKeyboard.registerEditText(R.id.investment_money);
        mCustomKeyboard.registerEditText(R.id.annual_rate);
        investment_mouth = (EditText) findViewById(R.id.investment_mouth);
        mCustomKeyboard.registerEditText(R.id.investment_mouth);
        product_expected_income = (TextView) findViewById(
                R.id.product_expected_income);
        bank_expected_income = (TextView) findViewById(
                R.id.bank_expected_income);
        investment_mouth.setText(mouth);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.calculator_dialog_false:
            dismiss();
            break;

        default:
            break;
        }
    }

    class MyTextListener implements TextWatcher
    {
        private String inputEditView = null;

        public MyTextListener(String inputEditView)
        {
            this.inputEditView = inputEditView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            if (s.toString().startsWith("."))
            {
                if ("investment_money".equals(inputEditView))
                {
                    investment_money.setText("");
                    return;
                }
                if ("investment_mouth".equals(inputEditView))
                {
                    investment_mouth.setText("");
                    return;
                }
            }
            else
            {
                if (isCanPost())
                {
                    // product_expected_income
                    // .setText(Intercept(BorrowCalculateManagerImpl.Bill(
                    // DoubleUtils.toDouble(investment_money.getText()
                    // .toString()), DoubleUtils
                    // .toDouble(investment_mouth.getText()
                    // .toString()), DoubleUtils
                    // .toDouble(annual_rate.getText()
                    // .toString()), type)));
                    //
                    // bank_expected_income
                    // .setText(Intercept(BorrowCalculateManagerImpl.Bill(
                    // DoubleUtils.toDouble(investment_money.getText()
                    // .toString()), DoubleUtils
                    // .toDouble(investment_mouth.getText()
                    // .toString()), 3.0, type)));
                }
                else
                {
                    product_expected_income.setText("0");
                    bank_expected_income.setText("0");
                }

            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }

    }

    private String Intercept(String account)
    {
        String number = account.substring(0, account.indexOf("."));
        return number + "元";
    }

    public boolean isCanPost()
    {
        return (!StringUtils.isEmpty2(investment_money.getText().toString())
                && !StringUtils
                        .isEmpty2(investment_mouth.getText().toString()));
    }
}