package com.sinoangel.sazalarm;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

/**
 * Created by tangyangkai on 16/6/12.
 */
public class ZRecyclerView extends RecyclerView {


    private float maxLength;
    private int mStartX;
    private View itemLayout;
    private int postionNow;
    private int xDown, xMove, yDown, yMove, mTouchSlop;
    private Scroller mScroller;
    private boolean isEdit, inclick;//编辑模式, 是否接受点击事件
    private OnItenOnClickIF oCIF;

    public ZRecyclerView(Context context) {
        this(context, null);
    }

    public ZRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //滑动到最小距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() + 5;
        //滑动的最大距离
        maxLength = context.getResources().getDimension(R.dimen.sw600_70dp);
        //初始化Scroller
        mScroller = new Scroller(context, new LinearInterpolator(context, null));
    }

    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                xDown = x;
                yDown = y;
                mStartX = x;
                //通过点击的坐标计算当前的position
                int mFirstPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                Rect frame = new Rect();
                int count = getChildCount();
                postionNow = -1;
                for (int i = count - 1; i >= 0; i--) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() == View.VISIBLE) {
                        child.getHitRect(frame);
                        if (frame.contains(x, y)) {
                            postionNow = mFirstPosition + i;
                            break;
                        }
                    }
                }

                inclick = true;
                View view = getChildAt(postionNow - mFirstPosition);

                if (isEdit && view != itemLayout) {
                    itemLayout.scrollTo(0, 0);
                    isEdit = false;
                    inclick = false;
                }

                if (postionNow == -1)//没有选中任何项
                    break;
                itemLayout = view;

            }
            break;
            case MotionEvent.ACTION_MOVE:
                if (postionNow == -1)//没有滑动任何项
                    break;

                xMove = x;
                yMove = y;
                int dx = xMove - xDown;
                int dy = yMove - yDown;
                if (Math.abs(dy) < mTouchSlop * 2 && Math.abs(dx) > mTouchSlop) {

                    int scrollX = itemLayout.getScrollX();

                    int newScrollX = mStartX - x;
                    mStartX = x;

                    if (newScrollX < 0) {//右滑动
                        if (scrollX <= 0) {
                            newScrollX = 0;
                        } else if (Math.abs(newScrollX) - scrollX > 0) {
                            newScrollX = -scrollX;
                        }
                    } else if (newScrollX > 0) {//左滑动
                        if (scrollX >= maxLength) {
                            newScrollX = 0;
                        } else if (newScrollX + scrollX >= maxLength) {
                            newScrollX = (int) maxLength - scrollX;
                        }
                    }

                    itemLayout.scrollBy(newScrollX, 0);
                    isEdit = true;
                }

                break;
            case MotionEvent.ACTION_UP: {
                if (Math.abs(xDown - x) < 2 && Math.abs(yDown - y) < 2) {//算点击
                    if (oCIF != null) {
                        if (inclick && postionNow != -1) {
                            if (isEdit) {
                                int i = (int) itemLayout.findViewById(R.id.iv_delete).getX();
                                if (x + 70 >= i)
                                    oCIF.onDelete(postionNow);
                            } else {
                                View view = itemLayout.findViewById(R.id.rl_btn);
                                if (view != null) {
                                    int i = (int) view.getX();
                                    if (x >= i)
                                        oCIF.onHead(postionNow);
                                    else
                                        oCIF.onClickItem(postionNow);
                                } else
                                    oCIF.onClickItem(postionNow);
                            }

                        }
                    }

                } else {
                    if (itemLayout != null) {
                        int scrollX = itemLayout.getScrollX();
                        if (scrollX > maxLength / 2) {
                            mScroller.startScroll(scrollX, 0, (int) maxLength - scrollX, 0);
                            invalidate();
                        } else {
                            mScroller.startScroll(scrollX, 0, -scrollX, 0);
                            invalidate();
                            isEdit = false;
                        }
                    } else
                        isEdit = false;
                }
            }
            break;
        }

        if (isEdit)
            return true;
        else
            return super.onTouchEvent(event);

    }

    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            itemLayout.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    public void setOnItemClick(OnItenOnClickIF lin) {
        this.oCIF = lin;
    }

    public interface OnItenOnClickIF {
        void onClickItem(int postion);

        void onDelete(int postion);

        void onHead(int postion);
    }

    public void close() {
        itemLayout.scrollTo(0, 0);
        isEdit = false;
        inclick = false;
    }
}
