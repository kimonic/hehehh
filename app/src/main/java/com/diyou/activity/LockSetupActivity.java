package com.diyou.activity;

import java.util.ArrayList;
import java.util.List;

import com.diyou.base.BaseMainActivity;
import com.diyou.config.Constants;
import com.diyou.util.LockPatternUtils;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.diyou.view.LockPatternView;
import com.diyou.view.LockPatternView.Cell;
import com.diyou.view.LockPatternView.DisplayMode;
import com.diyou.view.LockPatternView.OnPatternListener;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LockSetupActivity extends BaseMainActivity
        implements OnPatternListener, OnClickListener
{

    /**
     * 绘制手势密码
     */
    private static final int ID_EMPTY_MESSAGE = -1;
    private static final String KEY_UI_STAGE = "uiStage";
    private static final String KEY_PATTERN_CHOICE = "chosenPattern";
    private LockPatternView lockPatternView;
    private Stage mUiStage = Stage.Introduction;
    private static final int STEP_1 = 1; // 开始
    private static final int STEP_2 = 2; // 第一次设置手势完成
    private static final int STEP_3 = 3; // 按下继续按钮
    private static final int STEP_4 = 4; // 第二次设置手势完成
    private View mPreviewViews[][] = new View[3][3];
    private Button mFooterRightButton;
    private Button mFooterLeftButton;
    protected TextView mHeaderText;
    private int step;

    private List<Cell> choosePattern;

    private boolean confirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setup);
        findViewById(R.id.gesturepwd_back).setOnClickListener(this);
        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern1);
        lockPatternView.setOnPatternListener(this);
        mHeaderText = (TextView) findViewById(R.id.gesturepwd_create_text);
        mFooterRightButton = (Button) this.findViewById(R.id.right_btn);
        mFooterLeftButton = (Button) this.findViewById(R.id.reset_btn);
        mFooterRightButton.setOnClickListener(this);
        mFooterLeftButton.setOnClickListener(this);
        initPreviewViews();

        step = STEP_1;
        updateView();
        if (savedInstanceState == null)
        {
            // updateStage(Stage.Introduction);
            // updateStage(Stage.HelpScreen);
        }
        else
        {
            // restore from previous state
            final String patternString = savedInstanceState
                    .getString(KEY_PATTERN_CHOICE);
            if (patternString != null)
            {
                choosePattern = LockPatternUtils.stringToPattern(patternString);
            }
            updateStage(
                    Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_UI_STAGE, mUiStage.ordinal());
        if (choosePattern != null)
        {
            outState.putString(KEY_PATTERN_CHOICE,
                    LockPatternUtils.patternToString(choosePattern));
        }
    }

    private void initPreviewViews()
    {
        mPreviewViews = new View[3][3];
        mPreviewViews[0][0] = findViewById(R.id.gesturepwd_setting_preview_0);
        mPreviewViews[0][1] = findViewById(R.id.gesturepwd_setting_preview_1);
        mPreviewViews[0][2] = findViewById(R.id.gesturepwd_setting_preview_2);
        mPreviewViews[1][0] = findViewById(R.id.gesturepwd_setting_preview_3);
        mPreviewViews[1][1] = findViewById(R.id.gesturepwd_setting_preview_4);
        mPreviewViews[1][2] = findViewById(R.id.gesturepwd_setting_preview_5);
        mPreviewViews[2][0] = findViewById(R.id.gesturepwd_setting_preview_6);
        mPreviewViews[2][1] = findViewById(R.id.gesturepwd_setting_preview_7);
        mPreviewViews[2][2] = findViewById(R.id.gesturepwd_setting_preview_8);
    }

    private void updateView()
    {
        switch (step)
        {
        case STEP_1:
            mHeaderText.setText("绘制解锁图案");
            mFooterLeftButton.setVisibility(View.GONE);
            mFooterLeftButton.setText(R.string.cancel);
            mFooterRightButton.setText("继续");
            mFooterRightButton.setEnabled(false);
            choosePattern = null;
            confirm = false;
            lockPatternView.clearPattern();
            lockPatternView.enableInput();
            break;
        case STEP_2:
            mHeaderText.setText("请再绘制一次");
            mFooterLeftButton.setVisibility(View.VISIBLE);
            mFooterLeftButton.setText(R.string.try_again);
            mFooterRightButton.setText(R.string.goon);
            mFooterRightButton.setEnabled(true);
            lockPatternView.disableInput();
            if (step == STEP_2)
            {
                lockPatternView.postDelayed(mClearPatternRunnable, 500);
                step = STEP_3;
                updateView();
            }

            break;
        case STEP_3:

            mFooterLeftButton.setText(R.string.cancel);
            mFooterRightButton.setText("继续");
            mFooterRightButton.setEnabled(false);
            updatePreviewViews();
            lockPatternView.clearPattern();
            lockPatternView.enableInput();

            break;
        case STEP_4:
            mFooterLeftButton.setText(R.string.cancel);
            if (confirm)
            {
                mFooterRightButton.setText(R.string.confirm);
                mFooterRightButton.setEnabled(true);
                lockPatternView.disableInput();
            }
            else
            {
                mFooterRightButton.setText("");
                lockPatternView.setDisplayMode(DisplayMode.Wrong);
                lockPatternView.enableInput();

            }
            if (step == STEP_4 && confirm)
            {

                SharedPreferences preferences = getSharedPreferences(
                        Constants.LOCK, MODE_PRIVATE);
                preferences.edit()
                        .putString(Constants.LOCK_KEY,
                                LockPatternUtils.patternToString(choosePattern))
                        .commit();
                setResult(Constants.SETGESTUREPASSWORD);
                ToastUtil.show("手势密码设置成功！");
                finish();
            }

            break;

        default:
            break;
        }
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
        case R.id.gesturepwd_back:
            finish();
            break;
        case R.id.reset_btn:
            // if (step == STEP_1 || step == STEP_3 || step == STEP_4)
            // {
            // finish();
            // }
            // else
            if (step == STEP_3)
            {
                for (LockPatternView.Cell cell : choosePattern)
                {
                    mPreviewViews[cell.getRow()][cell.getColumn()]
                            .setBackgroundResource(R.drawable.circle_bg);
                }
                step = STEP_1;
                updateView();
            }
            break;

        case R.id.right_btn:
            if (step == STEP_2)
            {
                step = STEP_3;
                updateView();
            }
            else if (step == STEP_4)
            {

                SharedPreferences preferences = getSharedPreferences(
                        Constants.LOCK, MODE_PRIVATE);
                preferences.edit()
                        .putString(Constants.LOCK_KEY,
                                LockPatternUtils.patternToString(choosePattern))
                        .commit();
                finish();
            }

            break;
        default:
            break;
        }

    }

    @Override
    public void onPatternStart()
    {
    }

    @Override
    public void onPatternCleared()
    {
    }

    @Override
    public void onPatternCellAdded(List<Cell> pattern)
    {
    }

    @Override
    public void onPatternDetected(List<Cell> pattern)
    {

        if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE)
        {

            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            ToastUtil.show("至少连接4个点，请重试");
            lockPatternView.postDelayed(mClearPatternRunnable, 500);

            return;
        }

        if (choosePattern == null)
        {
            choosePattern = new ArrayList<Cell>(pattern);
            // Log.d(TAG, "choosePattern = "+choosePattern.toString());
            // Log.d(TAG, "choosePattern.size() = "+choosePattern.size());

            step = STEP_2;
            updateView();
            return;
        }
        // [(row=1,clmn=0), (row=2,clmn=0), (row=1,clmn=1), (row=0,clmn=2)]
        // [(row=1,clmn=0), (row=2,clmn=0), (row=1,clmn=1), (row=0,clmn=2)]

        if (choosePattern.equals(pattern))
        {
            // Log.d(TAG, "pattern = "+pattern.toString());
            // Log.d(TAG, "pattern.size() = "+pattern.size());
            confirm = true;
        }
        else
        {

            confirm = false;
            mFooterRightButton.setEnabled(false);
            ToastUtil.show("与上次输入不一致，请重试");
            lockPatternView.postDelayed(mClearPatternRunnable, 500);
            for (LockPatternView.Cell cell : choosePattern)
            {
                mPreviewViews[cell.getRow()][cell.getColumn()]
                        .setBackgroundResource(R.drawable.circle_bg);
            }
            step = STEP_1;
            updateView();
            return;

        }

        step = STEP_4;
        updateView();

    }

    protected enum Stage
    {

        Introduction(R.string.lockpattern_recording_intro_header,
                LeftButtonMode.Cancel, RightButtonMode.ContinueDisabled,
                ID_EMPTY_MESSAGE, true), HelpScreen(
                        R.string.lockpattern_settings_help_how_to_record,
                        LeftButtonMode.Gone, RightButtonMode.Ok,
                        ID_EMPTY_MESSAGE, false), ChoiceTooShort(
                                R.string.lockpattern_recording_incorrect_too_short,
                                LeftButtonMode.Retry,
                                RightButtonMode.ContinueDisabled,
                                ID_EMPTY_MESSAGE, true), FirstChoiceValid(
                                        R.string.lockpattern_pattern_entered_header,
                                        LeftButtonMode.Retry,
                                        RightButtonMode.Continue,
                                        ID_EMPTY_MESSAGE, false), NeedToConfirm(
                                                R.string.lockpattern_need_to_confirm,
                                                LeftButtonMode.Cancel,
                                                RightButtonMode.ConfirmDisabled,
                                                ID_EMPTY_MESSAGE,
                                                true), ConfirmWrong(
                                                        R.string.lockpattern_need_to_unlock_wrong,
                                                        LeftButtonMode.Cancel,
                                                        RightButtonMode.ConfirmDisabled,
                                                        ID_EMPTY_MESSAGE,
                                                        true), ChoiceConfirmed(
                                                                R.string.lockpattern_pattern_confirmed_header,
                                                                LeftButtonMode.Cancel,
                                                                RightButtonMode.Confirm,
                                                                ID_EMPTY_MESSAGE,
                                                                false);

        /**
         * @param headerMessage
         *            The message displayed at the top.
         * @param leftMode
         *            The mode of the left button.
         * @param rightMode
         *            The mode of the right button.
         * @param footerMessage
         *            The footer message.
         * @param patternEnabled
         *            Whether the pattern widget is enabled.
         */
        Stage(int headerMessage, LeftButtonMode leftMode,
                RightButtonMode rightMode, int footerMessage,
                boolean patternEnabled)
        {
            this.headerMessage = headerMessage;
            this.leftMode = leftMode;
            this.rightMode = rightMode;
            this.footerMessage = footerMessage;
            this.patternEnabled = patternEnabled;
        }

        final int headerMessage;
        final LeftButtonMode leftMode;
        final RightButtonMode rightMode;
        final int footerMessage;
        final boolean patternEnabled;
    }

    enum LeftButtonMode
    {
        Cancel(android.R.string.cancel, true), CancelDisabled(
                android.R.string.cancel,
                false), Retry(R.string.lockpattern_retry_button_text,
                        true), RetryDisabled(
                                R.string.lockpattern_retry_button_text,
                                false), Gone(ID_EMPTY_MESSAGE, false);

        /**
         * @param text
         *            The displayed text for this mode.
         * @param enabled
         *            Whether the button should be enabled.
         */
        LeftButtonMode(int text, boolean enabled)
        {
            this.text = text;
            this.enabled = enabled;
        }

        final int text;
        final boolean enabled;
    }

    enum RightButtonMode
    {
        Continue(R.string.lockpattern_continue_button_text,
                true), ContinueDisabled(
                        R.string.lockpattern_continue_button_text,
                        false), Confirm(
                                R.string.lockpattern_confirm_button_text,
                                true), ConfirmDisabled(
                                        R.string.lockpattern_confirm_button_text,
                                        false), Ok(android.R.string.ok, true);

        /**
         * @param text
         *            The displayed text for this mode.
         * @param enabled
         *            Whether the button should be enabled.
         */
        RightButtonMode(int text, boolean enabled)
        {
            this.text = text;
            this.enabled = enabled;
        }

        final int text;
        final boolean enabled;
    }

    private void updateStage(Stage stage)
    {
        mUiStage = stage;
        if (stage == Stage.ChoiceTooShort)
        {
            mHeaderText.setText(getResources().getString(stage.headerMessage,
                    LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
        }
        else
        {
            mHeaderText.setText(stage.headerMessage);
        }

        if (stage.leftMode == LeftButtonMode.Gone)
        {
            mFooterLeftButton.setVisibility(View.GONE);
        }
        else
        {
            mFooterLeftButton.setVisibility(View.VISIBLE);
            mFooterLeftButton.setText(stage.leftMode.text);
            mFooterLeftButton.setEnabled(stage.leftMode.enabled);
        }

        mFooterRightButton.setText(stage.rightMode.text);
        mFooterRightButton.setEnabled(stage.rightMode.enabled);

        // same for whether the patten is enabled
        if (stage.patternEnabled)
        {
            lockPatternView.enableInput();
        }
        else
        {
            lockPatternView.disableInput();
        }

        lockPatternView.setDisplayMode(DisplayMode.Correct);

        switch (mUiStage)
        {
        case Introduction:
            lockPatternView.clearPattern();
            break;
        case ChoiceTooShort:
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            break;
        case FirstChoiceValid:
            break;
        case NeedToConfirm:
            lockPatternView.clearPattern();
            break;
        case ConfirmWrong:
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            break;
        case ChoiceConfirmed:
            break;
        default:
            break;
        }

    }

    private void updatePreviewViews()
    {
        if (choosePattern == null)
            return;
        for (LockPatternView.Cell cell : choosePattern)
        {
            mPreviewViews[cell.getRow()][cell.getColumn()]
                    .setBackgroundResource(
                            R.drawable.gesture_create_grid_selected);
        }
    }

    private Runnable mClearPatternRunnable = new Runnable()
    {
        public void run()
        {
            lockPatternView.clearPattern();
        }
    };
}
