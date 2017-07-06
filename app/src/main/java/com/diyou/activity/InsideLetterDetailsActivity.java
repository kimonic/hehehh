package com.diyou.activity;

import com.diyou.base.BaseActivity;
import com.diyou.util.DateUtils;
import com.diyou.util.StringUtils;
import com.diyou.v5yibao.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InsideLetterDetailsActivity extends BaseActivity implements OnClickListener
{
    private View title_bar;
    private RelativeLayout title_bar_back;
    private TextView title_name;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.activity_inside_details);
        intiView();
    }

    private void intiView()
    {
        title_bar = findViewById(R.id.inside_details_titlebar);
        title_bar_back = (RelativeLayout) title_bar
                .findViewById(R.id.title_bar_back);
        title_name = (TextView) title_bar.findViewById(R.id.title_name);
        title_name.setText(R.string.inside_letter_detail_title);
        TextView title = (TextView) findViewById(R.id.inside_details_name);
        TextView time = (TextView) findViewById(R.id.inside_details_time);
        TextView contents = (TextView) findViewById(
                R.id.inside_details_contents);
        title.setText(getIntent().getStringExtra("title"));
        time.setText(DateUtils.getDateTimeFormat(getIntent().getStringExtra("time")));

        contents.setText(getIntent().getStringExtra("contents"));
        title_bar_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.title_bar_back:
            finish();
            break;

        default:
            break;
        }

    }
}
