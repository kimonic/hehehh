
package com.diyou.view;

import android.app.Dialog;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;

/**
 * @author 林佳荣 自定义键盘
 */
public class CustomKeyboard
{

    private KeyboardView mKeyboardView;
    private Dialog mHostActivity;

    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener()
    {

        public final static int CodeDelete = -5; // Keyboard.KEYCODE_DELETE
        public final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL

        @Override
        public void onKey(int primaryCode, int[] keyCodes)
        {
            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            if (focusCurrent == null
                    || focusCurrent.getClass() != EditText.class)
                return;
            EditText edittext = (EditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();
            if (primaryCode == CodeCancel)
            {
                hideCustomKeyboard();
            }
            else if (primaryCode == CodeDelete)
            {
                if (editable != null && start > 0)
                    editable.delete(start - 1, start);
            }
            else
            {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onPress(int arg0)
        {
        }

        @Override
        public void onRelease(int primaryCode)
        {
        }

        @Override
        public void onText(CharSequence text)
        {
        }

        @Override
        public void swipeDown()
        {
        }

        @Override
        public void swipeLeft()
        {
        }

        @Override
        public void swipeRight()
        {
        }

        @Override
        public void swipeUp()
        {
        }
    };

    public CustomKeyboard(Dialog calculatorDialog, int viewid, int layoutid)
    {
        mHostActivity = calculatorDialog;
        mKeyboardView = (KeyboardView) calculatorDialog.findViewById(viewid);
        mKeyboardView.setKeyboard(
                new Keyboard(mHostActivity.getContext(), layoutid));
//        mKeyboardView.setTextDirection(Gravity.CENTER);
        mKeyboardView.setPreviewEnabled(false); // 显示白板
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // // Hide the standard keyboard initially
        // mHostActivity.getWindow().setSoftInputMode(
        // WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public boolean isCustomKeyboardVisible()
    {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    // public void showCustomKeyboard(View v)
    // {
    // mKeyboardView.setVisibility(View.VISIBLE);
    // mKeyboardView.setEnabled(true);
    // if (v != null)
    // ((InputMethodManager) mHostActivity
    // .getSystemService(Activity.INPUT_METHOD_SERVICE))
    // .hideSoftInputFromWindow(v.getWindowToken(), 0);
    // }

    /** 隐藏. */
    public void hideCustomKeyboard()
    {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    public void registerEditText(int resid)
    {
        EditText edittext = (EditText) mHostActivity.findViewById(resid);
        edittext.setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                // if (hasFocus)
                // showCustomKeyboard(v);
                // else
                // hideCustomKeyboard();
            }
        });
        edittext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // showCustomKeyboard(v);
            }
        });
        edittext.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType(); // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard
                                                            // keyboard
                edittext.onTouchEvent(event); // Call native handler
                edittext.setInputType(inType); // Restore input type
                return true; // Consume touch event
            }
        });
        edittext.setInputType(edittext.getInputType()
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

}
