package com.sinoangel.saz_alarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinoangel.saz_alarm.base.MyBaseActivity;


/**
 * Created by Z on 2016/12/19.
 */

public class AlarmSelectAnmiActivity extends MyBaseActivity implements View.OnClickListener {
    private ViewPager vp_list;
    private final int[] piclist = new int[]{R.drawable.pic_alarm_head1, R.drawable.pic_alarm_head3, R.drawable.pic_alarm_head4, R.drawable.pic_alarm_head5, R.drawable.pic_alarm_head6};
    private TextView iv_details, iv_choose;
    private int currID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_selectani);
        vp_list = (ViewPager) findViewById(R.id.vp_list);
        iv_details = (TextView) findViewById(R.id.iv_details);
        iv_choose = (TextView) findViewById(R.id.iv_choose);

        // 1.设置幕后item的缓存数目
        vp_list.setOffscreenPageLimit(5);

        vp_list.setPageTransformer(true, new ViewPager.PageTransformer() {
            final float MIN_SCALE1 = 0.85f;
            final float MIN_SCALE2 = 0.6f;
            final float MIN_SCALE3 = 0.5f;

            final float MIN_ALPHA1 = 0.8f;
            final float MIN_ALPHA2 = 0.6f;
            final float MIN_ALPHA3 = 0.4f;

            @Override
            public void transformPage(View view, float position) {

                int hei = view.getHeight();
                float scaleFactor;
                float tranYFactor;
                float apFactor;
                if (position <= -2) {
                    float cha = MIN_SCALE2 - MIN_SCALE3;
                    float baifenbi = (-position) - (int) (-position);
                    scaleFactor = MIN_SCALE2 - cha * baifenbi;

                    float apcha = MIN_ALPHA2 - MIN_ALPHA3;
                    apFactor = MIN_ALPHA2 - apcha * baifenbi;

                    tranYFactor = (hei - scaleFactor * hei) / 4;
                    view.setSelected(false);
                } else if (position <= -1) {
                    float cha = MIN_SCALE1 - MIN_SCALE2;
                    float baifenbi = (-position) - (int) (-position);
                    scaleFactor = MIN_SCALE1 - cha * baifenbi;

                    float apcha = MIN_ALPHA1 - MIN_ALPHA2;
                    apFactor = MIN_ALPHA1 - apcha * baifenbi;

                    tranYFactor = (hei - scaleFactor * hei) / 4;
                    view.setSelected(false);
                } else if (position > -1f && position < 1f) {//[-1,1]
                    float cha = 1 - MIN_SCALE1;
                    scaleFactor = 1 - cha * Math.abs(position);

                    float apcha = 1 - MIN_ALPHA1;
                    apFactor = 1 - apcha * Math.abs(position);

                    tranYFactor = (hei - scaleFactor * hei) / 4;
                    view.setSelected(true);

                } else if (position >= 1 && position < 2) {
                    float cha = MIN_SCALE1 - MIN_SCALE2;
                    float baifenbi = position - (int) position;
                    scaleFactor = MIN_SCALE1 - cha * baifenbi;

                    float apcha = MIN_ALPHA1 - MIN_ALPHA2;
                    apFactor = MIN_ALPHA1 - apcha * baifenbi;

                    tranYFactor = (hei - scaleFactor * hei) / 4;
                    view.setSelected(false);
                } else {
                    float cha = MIN_SCALE2 - MIN_SCALE3;
                    float baifenbi = position - (int) position;
                    scaleFactor = MIN_SCALE2 - cha * baifenbi;

                    float apcha = MIN_ALPHA2 - MIN_ALPHA3;
                    apFactor = MIN_ALPHA2 - apcha * baifenbi;

                    tranYFactor = (hei - scaleFactor * hei) / 4;
                    view.setSelected(false);
                }
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                view.setTranslationY(tranYFactor);
                view.setAlpha(apFactor);

            }
        });

        vp_list.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView iv = new ImageView(container.getContext());
                iv.setLayoutParams(new ViewGroup.LayoutParams(getResources().getDimensionPixelSize(R.dimen.sw600_300dp), getResources().getDimensionPixelSize(R.dimen.sw600_418dp)));
                iv.setImageResource(piclist[position]);
                container.addView(iv);
                return iv;
            }

            @Override
            public int getCount() {
                return piclist.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView((View) object);
            }
        });

        vp_list.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currID = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        currID = getIntent().getIntExtra("picId", 2);
        vp_list.setCurrentItem(currID);

        iv_choose.setOnClickListener(this);
        iv_details.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        vp_list.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_choose:
                Intent data = new Intent();
                data.putExtra("pic", currID);
                setResult(200, data);
                finish();
                break;
            case R.id.iv_details:
                Intent intent = new Intent(AlarmSelectAnmiActivity.this, AnmiActivity.class);
                switch (currID) {
                    case 0:
                        intent.putExtra("ZIYUAN", R.raw.alarm_yizhi_ve);
                        intent.putExtra("MUSIC", R.raw.alarm_07);
                        break;
                    case 1:
                        intent.putExtra("ZIYUAN", R.raw.alarm_kexue_ve);
                        intent.putExtra("MUSIC", R.raw.alarm_05);
                        break;
                    case 2:
                        intent.putExtra("ZIYUAN", R.raw.alarm_renzhi_ve);
                        intent.putExtra("MUSIC", R.raw.alarm_09);
                        break;
                    case 3:
                        intent.putExtra("ZIYUAN", R.raw.alarm_yishu_ve);
                        intent.putExtra("MUSIC", R.raw.alarm_11);
                        break;
                    default:
                        intent.putExtra("ZIYUAN", R.raw.alarm_shuxue_ve);
                        intent.putExtra("MUSIC", R.raw.alarm_06);
                        break;
                }
                startActivity(intent);
                break;
        }
    }
}