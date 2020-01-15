package com.hearing.calltest.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * @author liujiadong
 * @since 2020/1/15
 */
public class CallAccessibilityService extends AccessibilityService {

    private static final String TAG = "CallAccessibility";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent: " + event.toString());
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo != null) {
                List<AccessibilityNodeInfo> infos = nodeInfo.findAccessibilityNodeInfosByText("选择来电秀视频");
                for (AccessibilityNodeInfo info : infos) {
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }
}
