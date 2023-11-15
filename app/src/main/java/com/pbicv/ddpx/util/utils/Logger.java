package com.pbicv.ddpx.util.utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

import com.blankj.utilcode.util.LogUtils;

import java.util.Arrays;

public final class Logger {

    public static final Logger S_MAIN_LOGGER = new Logger();

    private Logger() {
    }

    private final ClipboardManager getClipboardManager(Context context) {
        Object systemService = context.getSystemService(Context.CLIPBOARD_SERVICE);
        return (ClipboardManager) systemService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void addPrimaryClipChangedListener(ClipboardManager clipboardManager) {
        S_MAIN_LOGGER.logClipBoard("addPrimaryClipChangedListener", clipboardManager.getPrimaryClip());
    }

    public final String getClipboardText(Context context) {
        ClipData primaryClip;
        try {
            final ClipboardManager clipboardManager = getClipboardManager(context);
            clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public final void onPrimaryClipChanged() {
                    Logger.addPrimaryClipChangedListener(clipboardManager);
                }
            });
            logClipBoard("get hasPrimaryClip", Boolean.valueOf(clipboardManager.hasPrimaryClip()));
            if (!clipboardManager.hasPrimaryClip()) {
                return "";
            }
            logClipBoard("get primaryClipDescription", clipboardManager.getPrimaryClipDescription());
            ClipDescription primaryClipDescription = clipboardManager.getPrimaryClipDescription();
            logClipBoard("get primaryClipDescription MIME", Boolean.valueOf(primaryClipDescription.hasMimeType("text/plain")));
            logClipBoard("get primaryClip", clipboardManager.getPrimaryClip());
            if (clipboardManager.getPrimaryClipDescription() == null) {
                return "";
            }
            ClipDescription primaryClipDescription2 = clipboardManager.getPrimaryClipDescription();
            if (!primaryClipDescription2.hasMimeType("text/plain") || (primaryClip = clipboardManager.getPrimaryClip()) == null) {
                return "";
            }
            ClipData.Item itemAt = primaryClip.getItemAt(0);
            logClipBoard("get primaryClip item", itemAt, itemAt.getText());
            if (itemAt.getText() == null) {
                return "";
            }
            String obj = itemAt.getText().toString();
            logClipBoard("get copyString", obj);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public final void logClipBoard(String str, Object... objArr) {
        LogUtils.dTag(getClass().getSimpleName(), "type : " + str, "contents : " + Arrays.toString(objArr));
    }

    public final void copyString(Context context, String str) {
        ClipboardManager clipboardManager = getClipboardManager(context);
        logClipBoard("copyString", str);
        if (str == null) {
            return;
        }
        clipboardManager.setText(str);
        logClipBoard("copyString success", str);
    }
}