package com.hgj.universal.pocket.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 嵌套在RecyclerView外层
 * 使RecyclerView滑动到顶部或底部时具有弹簧阻尼效果
 */
public class OverScrollLayout extends LinearLayout {

    private static final int ANIM_TIME = 400;

    private RecyclerView childView;

    private Rect original = new Rect();

    private boolean isMoved = false;

    private float startYpos;

    /**
     * 阻尼系数
     */
    private static final float DAMPING_COEFFICIENT = 0.3f;

    private boolean isSuccess = false;

    private ScrollListener mScrollListener;

    public OverScrollLayout(Context context) {
        this(context, null);
    }

    public OverScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        childView = (RecyclerView) getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        original.set(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
    }

    public void setScrollListener(ScrollListener listener) {
        mScrollListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float touchYpos = ev.getY();
        if (touchYpos >= original.bottom || touchYpos <= original.top) {
            if (isMoved) {
                recoverLayout();
            }
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startYpos = ev.getY();
            case MotionEvent.ACTION_MOVE:
                int scrollYpos = (int) (ev.getY() - startYpos);
                boolean pullDown = scrollYpos > 0 && canPullDown();
                boolean pullUp = scrollYpos < 0 && canPullUp();
                if (pullDown || pullUp) {
                    cancelChild(ev);
                    int offset = (int) (scrollYpos * DAMPING_COEFFICIENT);
                    childView.layout(original.left, original.top + offset, original.right, original.bottom + offset);
                    if (mScrollListener != null) {
                        mScrollListener.onScroll();
                    }
                    isMoved = true;
                    isSuccess = false;
                    return true;
                } else {
                    startYpos = ev.getY();
                    isMoved = false;
                    isSuccess = true;
                    return super.dispatchTouchEvent(ev);
                }
            case MotionEvent.ACTION_UP:
                if (isMoved) {
                    recoverLayout();
                }
                return !isSuccess || super.dispatchTouchEvent(ev);
            default:
                return true;
        }
    }

    /**
     * 取消子view已经处理的事件
     *
     * @param ev event
     */
    private void cancelChild(MotionEvent ev) {
        ev.setAction(MotionEvent.ACTION_CANCEL);
        super.dispatchTouchEvent(ev);
    }

    /**
     * 位置还原
     */
    private void recoverLayout() {
        TranslateAnimation anim = new TranslateAnimation(0, 0, childView.getTop() - original.top, 0);
        anim.setDuration(ANIM_TIME);
        childView.startAnimation(anim);
        childView.layout(original.left, original.top, original.right, original.bottom);
        isMoved = false;
    }

    /**
     * 判断是否可以下拉
     *
     * @return true：可以，false:不可以
     */
    private boolean canPullDown() {
        final int firstVisiblePosition = ((LinearLayoutManager) childView.getLayoutManager()).findFirstVisibleItemPosition();
        if (firstVisiblePosition != 0 && childView.getAdapter().getItemCount() != 0) {
            return false;
        }
        int mostTop = (childView.getChildCount() > 0) ? childView.getChildAt(0).getTop() : 0;
        return mostTop >= 0;
    }

    /**
     * 判断是否可以上拉
     *
     * @return true：可以，false:不可以
     */
    private boolean canPullUp() {
        final int lastItemPosition = childView.getAdapter().getItemCount() - 1;
        final int lastVisiblePosition = ((LinearLayoutManager) childView.getLayoutManager()).findLastVisibleItemPosition();
        if (lastVisiblePosition >= lastItemPosition) {
            final int childIndex = lastVisiblePosition - ((LinearLayoutManager) childView.getLayoutManager()).findFirstVisibleItemPosition();
            final int childCount = childView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = childView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= childView.getBottom() - childView.getTop();
            }
        }
        return false;
    }


    public interface ScrollListener {
        /**
         * 滚动事件回调
         */
        void onScroll();
    }
}

