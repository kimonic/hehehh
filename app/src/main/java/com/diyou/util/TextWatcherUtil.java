package com.diyou.util;

import com.diyou.config.Constants;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class TextWatcherUtil
{

    public static TextWatcher getVerifyCode(final Button button){
        return new TextWatcher()
        {
            
            @Override
            public void onTextChanged(CharSequence s, int paramInt1,
                    int paramInt2, int paramInt3)
            {
                    button.setEnabled(s.toString().length() == 6);
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int paramInt1,
                    int paramInt2, int paramInt3)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable paramEditable)
            {
                // TODO Auto-generated method stub
                
            }
        };
    }
    
    public static TextWatcher getLoginPwd(final Button button){
        return new TextWatcher()
        {
            
            @Override
            public void onTextChanged(CharSequence s, int paramInt1,
                    int paramInt2, int paramInt3)
            {
                if(Constants.PAY_PASSWORD_MIN_LENGTH>s.toString().length() || 
                        Constants.PAY_PASSWORD_MAX_LENGTH<s.toString().length()){
                    button.setEnabled(false);
                }else{
                    button.setEnabled(true);
                }
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int paramInt1,
                    int paramInt2, int paramInt3)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable paramEditable)
            {
                // TODO Auto-generated method stub
                
            }
        };
    }
    
    public static TextWatcher getEmpty(final Button button){
        return new TextWatcher()
        {
            
            @Override
            public void onTextChanged(CharSequence s, int paramInt1,
                    int paramInt2, int paramInt3)
            {
                    button.setEnabled(!StringUtils.isEmpty(s.toString()));
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int paramInt1,
                    int paramInt2, int paramInt3)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable paramEditable)
            {
                // TODO Auto-generated method stub
                
            }
        };
    }

    /**
     * 限制EditText输入的为两位小数
     * @param money
     * @param editText
     * @return
     */
    public static String getMoneyFormat(String money,EditText editText) {
        if (money.startsWith(".")) {
            money="";
            editText.setText(money);
        }
        else if (money.startsWith("0")
                && money.trim().length() > 1) {
            if (!money.substring(1, 2).equals(".")) {//0开头但非0.xx则过滤
                money = money.substring(1);
                editText.setText(money);
                editText.setSelection(money.length());

            }
        } else if (money.contains(".") && money.length() - 1 - money.indexOf(".") > 2) {//截取小数点后两位
            money = money.substring(0,
                    money.indexOf(".") + 3);
            editText.setText(money);
            editText.setSelection(money.length());
        }
        return money;
    }
}
