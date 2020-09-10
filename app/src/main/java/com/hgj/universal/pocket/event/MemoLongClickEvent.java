package com.hgj.universal.pocket.event;

/**
 * 便签item长按事件
 */
public class MemoLongClickEvent {
    public int position;
    public MemoLongClickEvent(int position) {
        this.position = position;
    }
}
