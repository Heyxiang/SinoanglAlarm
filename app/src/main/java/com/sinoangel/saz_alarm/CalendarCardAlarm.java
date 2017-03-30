package com.sinoangel.saz_alarm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.sinoangel.saz_alarm.bean.CustomDate;

import java.util.Calendar;


/**
 * Created by pc on 2016/8/18.
 */
public class CalendarCardAlarm extends View {

    private static final int TOTAL_COL = 7; // 7列
    private static final int TOTAL_ROW = 6; // 6行

    private Paint mCirclePaint; // 绘制圆形的画笔
    private Paint mTextPaint; // 绘制文本的画笔
    private int mViewWidth; // 视图的宽度
    private int mViewHeight; // 视图的高度
    private int mCellSpace; // 单元格间距
    private Row rows[] = new Row[TOTAL_ROW]; // 行数组，每个元素代表一行
    private CustomDate mShowDate; // 自定义的日期，包括year,month,day
    private OnCellClickListener mCellClickListener; // 单元格点击回调事件
    private int touchSlop; //
    private boolean callBackCellSpace;
    private int upDay = -1;// 点击的日期
    private CustomDate upDate;//最新选择的月
    private Cell mClickCell;
    private float mDownX;
    private float mDownY;

    /**
     * 单元格点击的回调接口
     *
     * @author wuwenjie
     */
    public interface OnCellClickListener {
        void clickDate(CustomDate date); // 回调点击的日期

        void changeDate(CustomDate date); // 回调滑动ViewPager改变的日期
    }

    public CalendarCardAlarm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CalendarCardAlarm(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarCardAlarm(Context context) {
        super(context);
        init(context);
    }

    public CalendarCardAlarm(Context context, CustomDate cd, OnCellClickListener listener) {
        super(context);
        if (cd != null)
            upDate = cd;
        mShowDate = new CustomDate();
        this.mCellClickListener = listener;
        init(context);
    }

    private void init(Context context) {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        initDate();
    }

    private void initDate() {
        if (mShowDate == null)
            mShowDate = new CustomDate();
        //fillDate();

        //初始化传入 点击传入的日期
        upDay = upDate.day + DateUtil.getWeekDayFromDate(upDate.year, upDate.month) - 1;
        mCellClickListener.clickDate(upDate);
        // 刷新界面
        update();
    }

    private void fillDate() {
        int monthDay = DateUtil.getCurrentMonthDay(); // 今天
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.year,
                mShowDate.month); // 当前月的天数
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year,
                mShowDate.month);
        boolean isCurrentMonth = false;
        if (DateUtil.isCurrentMonth(mShowDate)) {
            isCurrentMonth = true;
        }
        int day = 0;
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL; // 单元格位置
                Log.i("UP", position + "--" + upDay);
                // 这个月的
                if (position >= firstDayWeek
                        && position < firstDayWeek + currentMonthDays) {
                    day++;
                    rows[j].cells[i] = new Cell(CustomDate.modifiDayForObject(
                            mShowDate, day), State.CURRENT_MONTH_DAY, i, j);
                    // 今天
                    if (isCurrentMonth && day == monthDay) {
                        CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
                        rows[j].cells[i] = new Cell(date, State.TODAY, i, j);
                    }

                    if (isCurrentMonth && day < monthDay) { // 已经过去的日子
                        rows[j].cells[i] = new Cell(
                                CustomDate.modifiDayForObject(mShowDate, day),
                                State.UNREACH_DAY, i, j);
                    }

                    // 过去一个月
                }

                if (position == upDay && upDate.month == mShowDate.month && upDate.year == mShowDate.year) {
                    rows[j].cells[i] = new Cell(
                            CustomDate.modifiDayForObject(mShowDate, day),
                            State.UP_DAY, i, j);
                }
            }
        }
        mCellClickListener.changeDate(mShowDate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null) {
                rows[i].drawCells(canvas);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mCellSpace = Math.min(mViewHeight / TOTAL_ROW, mViewWidth / TOTAL_COL);
        if (!callBackCellSpace) {
            callBackCellSpace = true;
        }
        mTextPaint.setTextSize(mCellSpace / 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (mDownX / mCellSpace);
                    int row = (int) (mDownY / mCellSpace);
                    measureClickCell(col, row);
                }
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 计算点击的单元格
     *
     * @param col
     * @param row
     */
    private void measureClickCell(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW)
            return;
        if (mClickCell != null) {
            rows[mClickCell.j].cells[mClickCell.i] = mClickCell;
        }
        if (rows[row] != null && rows[row].cells[col] != null) {
            mClickCell = new Cell(rows[row].cells[col].date,
                    rows[row].cells[col].state, rows[row].cells[col].i,
                    rows[row].cells[col].j);

            CustomDate date = rows[row].cells[col].date;
            date.week = col;
            CustomDate cd = new CustomDate();
            if (date.year <= cd.year && date.month <= cd.month && date.day < cd.day)
                AlarmUtils.showToast(R.string.wuxiaoriqi);
            else {
                upDay = col + row * TOTAL_COL;
                mCellClickListener.clickDate(date);
                upDate = new CustomDate(mShowDate.year, mShowDate.month, mShowDate.day);
                update();
            }
        }
    }

    /**
     * 组元素
     *
     * @author wuwenjie
     */
    class Row {
        public int j;

        Row(int j) {
            this.j = j;
        }

        public Cell[] cells = new Cell[TOTAL_COL];

        // 绘制单元格
        public void drawCells(Canvas canvas) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null) {
                    cells[i].drawSelf(canvas);
                }
            }
        }

    }

    /**
     * 单元格元素
     *
     * @author wuwenjie
     */
    class Cell {
        public CustomDate date;
        public State state;
        public int i;
        public int j;

        public Cell(CustomDate date, State state, int i, int j) {
            super();
            this.date = date;
            this.state = state;
            this.i = i;
            this.j = j;
        }

        public void drawSelf(Canvas canvas) {
            switch (state) {
                case TODAY: // 今天
                    mTextPaint.setColor(Color.parseColor("#fffffe"));
                    mCirclePaint.setColor(Color.parseColor("#a5a19e")); // 蓝色圆形
                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
                            (float) ((j + 0.4) * mCellSpace), (float) (mCellSpace / 2.5),
                            mCirclePaint);
                    break;
                case CURRENT_MONTH_DAY: // 当前月日期
                    mTextPaint.setColor(Color.parseColor("#fffffe"));
                    break;
                case UNREACH_DAY: // 已过去的天数
                    mTextPaint.setColor(Color.parseColor("#55fffffe"));
                    break;
                case UP_DAY: // 点击的日期
                    mTextPaint.setColor(Color.parseColor("#fffffe"));
                    mCirclePaint.setColor(Color.parseColor("#6fad00")); // 绿色圆形
                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)),
                            (float) ((j + 0.4) * mCellSpace), (float) (mCellSpace / 2.5),
                            mCirclePaint);
                    break;
                default:
                    break;
            }
            // 绘制文字
            String content = date.day + "";
            canvas.drawText(content,
                    (float) ((i + 0.5) * mCellSpace - mTextPaint
                            .measureText(content) / 2), (float) ((j + 0.7)
                            * mCellSpace - mTextPaint
                            .measureText(content, 0, 1) / 2), mTextPaint);
        }
    }

    /**
     * @author wuwenjie 单元格的状态 当前月日期，过去的月的日期，下个月的日期
     */
    enum State {
        TODAY, CURRENT_MONTH_DAY, UNREACH_DAY, UP_DAY;
    }

    // 从左往右划，上一个月
    public void leftSlide() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        if (mShowDate.month > month + 1) {
            if (mShowDate.month == 1) {
                mShowDate.month = 12;
                mShowDate.year -= 1;
            } else {
                mShowDate.month -= 1;
            }
            update();
        }
    }

    // 从右往左划，下一个月
    public void rightSlide() {
        if (mShowDate.month == 12) {
            mShowDate.month = 1;
            mShowDate.year += 1;
        } else {
            mShowDate.month += 1;
        }
        update();
    }

    public void update() {
        fillDate();
        invalidate();
    }
}
