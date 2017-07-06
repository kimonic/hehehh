package com.diyou.fragment;

import java.util.TreeMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diyou.activity.CreditorRightsDetailsActivity;
import com.diyou.activity.InvestmentDetailsActivity;
import com.diyou.http.HttpUtil;
import com.diyou.util.ToastUtil;
import com.diyou.v5yibao.R;
import com.example.encryptionpackages.CreateCode;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class CreditorRightsDetailsFragment extends Fragment
        implements OnClickListener
{
    private TextView mCreditors;
    private TextView mDebtors;
    private TextView mSafeguardWay;
    private TextView mTransferTheTotalPeriods;
    private TextView mInterestToBeCollected;
    private TextView mCreditType;
    private TextView mBorrowingDeadline;
    private TextView mMethodOfPayment;
    private TextView mOverdueSituation;
    private TextView mSecurity;
    private String mBorrow_nid;
    private View mLoadlayout;
    private TextView mTitle;

    /**
     * 债权转让详情->债权详情
     * 
     * @author harve
     * 
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        CreditorRightsDetailsActivity activity = (CreditorRightsDetailsActivity) getActivity();
        container = (ViewGroup) inflater
                .inflate(R.layout.fragment_creditorrightsdetails, null);
        // mBorrow_nid = activity.getBorrow_nid();
        getCreditorRightsDetails(mBorrow_nid);
        initView(container);
        getEnsureThatWay(mBorrow_nid);
        return container;
    }

    private void initView(ViewGroup container)
    {
        mLoadlayout = container
                .findViewById(R.id.creditorrightsdetailsfragment_loadlayout);
        mTitle = (TextView) container
                .findViewById(R.id.creditorrightsdetailsfragment_markDetails);
        mTitle.setOnClickListener(this);
        mCreditors = (TextView) container
                .findViewById(R.id.creditorrightsdetailsfragment_creditors);
        mDebtors = (TextView) container
                .findViewById(R.id.creditorrightsdetailsfragment_debtors);
        mSafeguardWay = (TextView) container
                .findViewById(R.id.creditorrightsdetailsfragment_safeguardWay);
        mTransferTheTotalPeriods = (TextView) container.findViewById(
                R.id.creditorrightsdetailsfragment_transferTheTotalPeriods);
        mInterestToBeCollected = (TextView) container.findViewById(
                R.id.creditorrightsdetailsfragment_interestToBeCollected);
        mCreditType = (TextView) container
                .findViewById(R.id.creditorrightsdetailsfragment_creditType);
        mBorrowingDeadline = (TextView) container.findViewById(
                R.id.creditorrightsdetailsfragment_borrowingDeadline);
        mMethodOfPayment = (TextView) container.findViewById(
                R.id.creditorrightsdetailsfragment_methodOfPayment);
        mOverdueSituation = (TextView) container.findViewById(
                R.id.creditorrightsdetailsfragment_overdueSituation);
        mSecurity = (TextView) container
                .findViewById(R.id.creditorrightsdetailsfragment_security);

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.creditorrightsdetailsfragment_markDetails:
            Intent intent = new Intent(getActivity(),
                    InvestmentDetailsActivity.class);
            intent.putExtra("borrow_nid", mBorrow_nid);
            startActivity(intent);
            break;
        default:
            break;
        }
    }

    private void getCreditorRightsDetails(String borrow_nid)
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "borrow");
        map.put("q", "change_get_list");
        map.put("borrow_nid", borrow_nid);
        map.put("method", "get");
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {
            @Override
            public void onStart()
            {

                super.onStart();
            }

            @Override
            public void onFinish()
            {

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (response.getString("result").contains("success"))
                    {

                        JSONArray jsonArray = response.getJSONArray("list");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        mCreditors.setText(
                                jsonObject.getString("change_username"));
                        mDebtors.setText(
                                jsonObject.getString("borrow_username"));
                        mTransferTheTotalPeriods.setText(
                                jsonObject.getString("change_period") + "期/"
                                        + jsonObject.getString("borrow_period")
                                        + "期");
                        mInterestToBeCollected.setText(jsonObject
                                .getString("recover_account_capital_wait") + "/"
                                + jsonObject.getString(
                                        "recover_account_interest_wait"));
                        mCreditType.setText(
                                jsonObject.getString("borrow_type_name"));
                        mBorrowingDeadline.setText(
                                jsonObject.getString("borrow_period_name"));
                        if (jsonObject.getString("borrow_style")
                                .equals("endmonth"))
                        {
                            mMethodOfPayment.setText("按月付息 ");
                        }
                        else if (jsonObject.getString("borrow_style")
                                .equals("month"))
                        {
                            mMethodOfPayment.setText("等额本息");
                        }
                        if (jsonObject.getInt("repay_is_late") == 1)
                        {
                            mOverdueSituation.setText("逾期");

                        }
                        else if (jsonObject.getInt("repay_is_late") == 0)
                        {
                            mOverdueSituation.setText("未逾期");

                        }
                        mTitle.setText(jsonObject.getString("borrow_name")
                                + "<点击查看标详情>");

                        mLoadlayout.setVisibility(View.GONE);

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show("网络请求失败,请稍后在试！");
            }

        });

    }

    private void getEnsureThatWay(String borrow_nid)
    {
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("module", "borrow");
        map.put("q", "get_view_one");
        map.put("borrow_nid", borrow_nid);
        map.put("method", "get");
        HttpUtil.get(CreateCode.getUrl(map), new JsonHttpResponseHandler()
        {
            @Override
            public void onStart()
            {

                super.onStart();
            }

            @Override
            public void onFinish()
            {

                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                    JSONObject response)
            {
                try
                {
                    if (response.getString("result").contains("success"))
                    {

                        mSecurity.setText(response.getString("style_title"));
                        mSafeguardWay
                                .setText(response.getString("style_title"));
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                    Throwable throwable, JSONObject errorResponse)
            {
                ToastUtil.show("网络请求失败,请稍后在试！");
            }

        });

    }

}
