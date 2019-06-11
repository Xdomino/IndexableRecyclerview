package me.elvis.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.SectionIndexer;

public class IndexScroller extends RecyclerView.AdapterDataObserver {

    private RecyclerView mRv;
    private SectionIndexer mIndexer = null;
    private String[] mSections = null;
    private RectF mIndexbarRect;
    private Paint mIndexPaint;

    private float mDensity;
    private float mScaledDensity;
    private float mSingle;
    private float mHeight, mWidth, mGap, mBaseLineToTop, mMaxSingleWidth, mPadding;

    private int mListViewWidth;
    private int mListViewHeight;
    private int mCurrentSection = -1;

    private boolean mIsIndexing = false;

    IndexScroller(Context context, AttributeSet attrs, RecyclerView rv) {
        mRv = rv;
        mDensity = context.getResources().getDisplayMetrics().density;
        mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IndexableRecyclerView);
        int textColor = ta.getColor(R.styleable.IndexableRecyclerView_textColor, Color.BLACK);
        mPadding = ta.getDimensionPixelSize(R.styleable.IndexableRecyclerView_padding, (int) (10 * mDensity + 0.5));
        mGap = ta.getDimensionPixelSize(R.styleable.IndexableRecyclerView_padding, (int) (3 * mDensity + 0.5));
        int defTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                12f, context.getResources().getDisplayMetrics());
        int textSize = ta.getDimensionPixelSize(R.styleable.IndexableRecyclerView_textSize, defTextSize);
        ta.recycle();
        mIndexPaint = new Paint();
        mIndexPaint.setColor(textColor);
        mIndexPaint.setAntiAlias(true);
        mIndexPaint.setTextSize(textSize);
        mIndexPaint.setTextAlign(Paint.Align.CENTER);
    }


    void draw(Canvas canvas) {
        if (mSections != null && mSections.length > 0) {
            if (mCurrentSection >= 0) {
                Paint previewPaint = new Paint();
                previewPaint.setColor(Color.BLACK);
                previewPaint.setAlpha(96);
                previewPaint.setAntiAlias(true);
                previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

                Paint previewTextPaint = new Paint();
                previewTextPaint.setColor(Color.WHITE);
                previewTextPaint.setAntiAlias(true);
                previewTextPaint.setTextSize(50 * mScaledDensity);

                float previewTextWidth = previewTextPaint.measureText(mSections[mCurrentSection]);
                float previewSize = previewTextPaint.descent() - previewTextPaint.ascent();
                RectF previewRect = new RectF((mListViewWidth - previewSize) / 2
                        , (mListViewHeight - previewSize) / 2
                        , (mListViewWidth - previewSize) / 2 + previewSize
                        , (mListViewHeight - previewSize) / 2 + previewSize);

                canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint);
                canvas.drawText(mSections[mCurrentSection], previewRect.left + (previewSize - previewTextWidth) / 2 - 1
                        , previewRect.top - previewTextPaint.ascent() + 1, previewTextPaint);
            }
            for (int i = 0; i < mSections.length; i++) {
                canvas.drawText(mSections[i], mIndexbarRect.left + mMaxSingleWidth / 2 + mPadding,
                        mIndexbarRect.top + i * (mSingle + mGap) + mBaseLineToTop, mIndexPaint);
            }
        }
    }


    boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // If down event occurs inside index bar region, start indexing
                if (contains(ev.getX(), ev.getY())) {
                    // It demonstrates that the motion event started from index bar
                    mIsIndexing = true;
                    // Determine which section the point is in, and move the list to that section
                    mCurrentSection = getSectionByPoint(ev.getY());
                    scrollToPosition(mIndexer.getPositionForSection(mCurrentSection));
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsIndexing) {
                    // If this event moves inside index bar
                    if (contains(ev.getX(), ev.getY())) {
                        // Determine which section the point is in, and move the list to that section
                        mCurrentSection = getSectionByPoint(ev.getY());
                        scrollToPosition(mIndexer.getPositionForSection(mCurrentSection));

                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsIndexing) {
                    mIsIndexing = false;
                    mCurrentSection = -1;
                    mRv.invalidate();
                }
                break;
        }

        return false;
    }

    boolean contains(float x, float y) {
        // Determine if the point is in index bar region, which includes the right margin of the bar
        return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.bottom);
    }

    void onSizeChanged(int w, int h, int oldw, int oldh) {
        mListViewWidth = w;
        mListViewHeight = h;
        initRect();
    }

    void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof SectionIndexer) {
            mIndexer = (SectionIndexer) adapter;
            mSections = (String[]) mIndexer.getSections();
            adapter.registerAdapterDataObserver(this);
            final Paint.FontMetrics fm = mIndexPaint.getFontMetrics();
            mSingle = fm.bottom - fm.top + fm.leading;
            mBaseLineToTop = mSingle - fm.bottom;
            mHeight = mSingle * mSections.length + mGap * (mSections.length - 1);
            for (String s : mSections) {
                final float w = mIndexPaint.measureText(s);
                if (mMaxSingleWidth < w) {
                    mMaxSingleWidth = w;
                }
            }
            mWidth = mMaxSingleWidth + mPadding * 2;
            initRect();
        }
    }

    private void initRect() {
        final float margin = (mListViewHeight - mHeight) / 2;
        mIndexbarRect = new RectF(mListViewWidth - mWidth
                , margin
                , mListViewWidth
                , mListViewHeight - margin);
    }

    private void scrollToPosition(int position) {
        ((LinearLayoutManager) mRv.getLayoutManager()).scrollToPositionWithOffset(position, 0);
    }

    private int getSectionByPoint(float y) {
        if (mSections == null || mSections.length == 0)
            return 0;
        return (int) Math.floor((y - mIndexbarRect.top) / (mSingle + mGap));
    }


    @Override
    public void onChanged() {
        mSections = (String[]) mIndexer.getSections();
        mRv.invalidate();
    }
}
