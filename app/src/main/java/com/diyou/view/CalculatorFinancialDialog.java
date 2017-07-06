package com.diyou.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.diyou.base.BaseActivity;
import com.diyou.bean.Reimbursement;
import com.diyou.bean.RepayTypeInfo;
import com.diyou.config.UrlConstants;
import com.diyou.config.UserConfig;
import com.diyou.http.HttpUtil;
import com.diyou.util.StringUtils;
import com.diyou.util.TextWatcherUtil;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 理财计算器
 */
public class CalculatorFinancialDialog extends Dialog
        implements View.OnClickListener {
    private Activity context;
    private Spinner spinner1;
    private ImageView spinner_performclick;
    private Window window = null;
    private SimpleAdapter simpleAdapter;
    private EditText investment_money;
    private EditText annual_rate;
    private EditText investment_mouth;
    private TextView product_expected_income;
    private TextView bank_expected_income;
    private Reimbursement type = Reimbursement.Dengebenxi;
    private String typename;
    private String rate;
    private String mouth;
    private CustomKeyboard mCustomKeyboard;
    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    public CalculatorFinancialDialog(Activity context) {
        super(context, R.style.MyDialog);
        this.context = context;

    }

    public CalculatorFinancialDialog(BaseActivity context, String rate,
                                     String mouth) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.rate = rate;
        this.mouth = mouth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.calculator_financial_dialog);
        this.setCanceledOnTouchOutside(false);
        mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview,
                R.xml.hexkbd);

        simpleAdapter = new SimpleAdapter(context, list,
                R.layout.items_spinner, new String[]
                {"name"}, new int[]
                {R.id.textview});
        initWindowView();
        initView();
        initListener();
        requesetRepayType();
    }

    private void initWindowView() {
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

    private void initListener() {
        findViewById(R.id.calculator_dialog_false).setOnClickListener(this);
        spinner_performclick.setOnClickListener(this);
        spinner1.setAdapter(simpleAdapter);
        spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                typename = mTypeList.get(position).getId();
//                switch (position)
//                {
//
//                case 0:
//                    type = Reimbursement.Dengebenxi;
//                    break;
//                case 1:
//                    type = Reimbursement.Daoqihuanbenhuanxi;
//                    break;
//                case 2:
//                    type = Reimbursement.Anyuefuxi;
//                    break;
//                case 3:
//                    type = Reimbursement.Anyuefuxidaoqihuanben;
//                    break;
//                default:
//                    type = Reimbursement.Dengebenxi;
//                    break;
//                }
                if (isCanPost()) {
//                    product_expected_income
//                            .setText(Intercept(BorrowCalculateManagerImpl.Bill(
//                                    DoubleUtils.toDouble(investment_money
//                                            .getText().toString()),
//                                    DoubleUtils.toDouble(investment_mouth
//                                            .getText().toString()),
//                                    DoubleUtils.toDouble(
//                                            annual_rate.getText().toString()),
//                                    type)));
//
//                    bank_expected_income
//                            .setText(Intercept(BorrowCalculateManagerImpl.Bill(
//                                    DoubleUtils.toDouble(investment_money
//                                            .getText().toString()),
//                                    DoubleUtils.toDouble(investment_mouth
//                                            .getText().toString()),
//                                    3.0, type)));
                    calculatorResult();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        investment_money
                .addTextChangedListener(new MyTextListener("investment_money"));
        annual_rate.addTextChangedListener(new MyTextListener("annual_rate"));
        investment_mouth
                .addTextChangedListener(new MyTextListener("investment_mouth"));

    }

    private void initView() {
        spinner1 = (Spinner) findViewById(R.id.spinnerBase1);
        spinner_performclick = (ImageView) findViewById(
                R.id.spinner_performclick);
        investment_money = (EditText) findViewById(R.id.investment_money);
        mCustomKeyboard.registerEditText(R.id.investment_money);
        annual_rate = (EditText) findViewById(R.id.annual_rate);
        mCustomKeyboard.registerEditText(R.id.annual_rate);
        investment_mouth = (EditText) findViewById(R.id.investment_mouth);
        mCustomKeyboard.registerEditText(R.id.investment_mouth);
        product_expected_income = (TextView) findViewById(
                R.id.product_expected_income);
        bank_expected_income = (TextView) findViewById(
                R.id.bank_expected_income);
        investment_mouth.setText(mouth);
        annual_rate.setText(rate);
    }

    private List<RepayTypeInfo> mTypeList = new ArrayList<>();

    private void requesetRepayType() {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("login_token", UserConfig.getInstance().getLoginToken(context));
        HttpUtil.post(UrlConstants.GET_REPAY_TYPE_LIST, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                mTypeList.clear();
                                JSONObject data = StringUtils
                                        .parseContent(response);
                                if (data.optString("result").equals("success")) {
                                    JSONArray ja = data.optJSONArray("data");
                                    for (int i = 0; i < ja.length(); i++) {
                                        JSONObject j = ja.optJSONObject(i);
                                        RepayTypeInfo info = new RepayTypeInfo();
                                        info.setId(j.optString("id"));
                                        info.setName(j.optString("name"));
                                        info.setStatus(j.optString("status"));
                                        mTypeList.add(info);

                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("name", j.optString("name"));
                                        list.add(map);
                                    }
                                    simpleAdapter.notifyDataSetChanged();


                                } else {

                                    ToastUtil.show(data.getString("description"));
                                }
                            }
                        } catch (Exception e) {
                            ToastUtil.show(R.string.remind_data_parse_exception);
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
//                            hideProgressDialog();
                        super.onFinish();
                    }

                    @Override
                    public void onStart() {
//                            context.showProgressDialog();
                        super.onStart();
                    }

                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.spinner_performclick:
                spinner1.performClick();
                break;
            case R.id.calculator_dialog_false:
                CalculatorFinancialDialog.this.dismiss();
                break;

            default:
                break;
        }
    }

//    public List<Map<String, Object>> getData()
//    {
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("TypeName", "等额本息");
//        list.add(map);
//        Map<String, Object> map2 = new HashMap<String, Object>();
//        map2.put("TypeName", "到期还本还息");
//        list.add(map2);
//        Map<String, Object> map3 = new HashMap<String, Object>();
//        map3.put("TypeName", "按月付息");
//        list.add(map3);
//        Map<String, Object> map4 = new HashMap<String, Object>();
//        map4.put("TypeName", "按月付息到期还本");
//        list.add(map4);
//        return list;
//    }

    class MyTextListener implements TextWatcher {
        private String inputEditView = null;

        public MyTextListener(String inputEditView) {
            this.inputEditView = inputEditView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

            if ("investment_money".equals(inputEditView)) {
                TextWatcherUtil.getMoneyFormat(s.toString(), investment_money);
            } else if ("annual_rate".equals(inputEditView)) {
                TextWatcherUtil.getMoneyFormat(s.toString(), annual_rate);
            }
            //投资月份为整数，无需限制保留两位小数
//            else if ("investment_mouth".equals(inputEditView)) {
//                TextWatcherUtil.getMoneyFormat(s.toString(), investment_mouth);
//            }
            if (isCanPost()) {
                calculatorResult();
//                    product_expected_income
//                            .setText(Intercept(BorrowCalculateManagerImpl.Bill(
//                                    DoubleUtils.toDouble(investment_money
//                                            .getText().toString()),
//                                    DoubleUtils.toDouble(investment_mouth
//                                            .getText().toString()),
//                                    DoubleUtils.toDouble(
//                                            annual_rate.getText().toString()),
//                                    type)));
//
//                    bank_expected_income
//                            .setText(Intercept(BorrowCalculateManagerImpl.Bill(
//                                    DoubleUtils.toDouble(investment_money
//                                            .getText().toString()),
//                                    DoubleUtils.toDouble(investment_mouth
//                                            .getText().toString()),
//                                    3.0, type)));
            } else {
                product_expected_income.setText("0元");
                bank_expected_income.setText("0元");
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    private String Intercept(String account) {
        // String number = account.substring(0, account.indexOf("."));
        String number = StringUtils.getDoubleFormat(account);
        return number + "元";
    }

    public boolean isCanPost() {
        return (!StringUtils.isEmpty2(investment_money.getText().toString())
                && !StringUtils.isEmpty2(investment_mouth.getText().toString())
                && !StringUtils.isEmpty2(annual_rate.getText().toString()));
    }

    private void calculatorResult() {
        if (Double.valueOf(investment_mouth.getText().toString()) > 36) {
            ToastUtil.show("借款期限不能大于36个月");
            return;
        }
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("amount", investment_money.getText().toString());
        map.put("apr", annual_rate.getText().toString());
        map.put("period", investment_mouth.getText().toString());
        map.put("repay_type", typename);
        HttpUtil.post(UrlConstants.CALCULATOR_RESULT, map,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        ToastUtil.show(R.string.generic_error);
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {
                            if (CreateCode.AuthentInfo(response)) {
                                JSONObject data = StringUtils
                                        .parseContent(response);
                                if (data.optString("result").equals("success")) {
                                    JSONObject data1 = data.optJSONObject("data");
                                    product_expected_income.setText(StringUtils.getDoubleFormat(data1.optString("interest_total")) + "元");

                                } else {

                                    ToastUtil.show(data.getString("description"));
                                }
                            }
                        } catch (Exception e) {
                            ToastUtil.show(R.string.remind_data_parse_exception);
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                });
    }
}