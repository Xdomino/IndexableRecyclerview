package elvis.view.indexablerecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class IndexableRecyclerView extends RecyclerView {
    private IndexScroller mIndexScroller;
    private boolean mEnableIndex = true;

    public IndexableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mIndexScroller = new IndexScroller(context, attrs, this);
    }

    public boolean isEnableIndex() {
        return mEnableIndex;
    }

    public void setEnableIndex(boolean mEnableIndex) {
        this.mEnableIndex = mEnableIndex;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mEnableIndex) {
            mIndexScroller.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Intercept ListView's touch event
        if (mEnableIndex && mIndexScroller.onTouchEvent(ev)) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mEnableIndex && mIndexScroller.contains(ev.getX(), ev.getY()))
            return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        mIndexScroller.setAdapter(adapter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mIndexScroller.onSizeChanged(w, h, oldw, oldh);
    }
}
