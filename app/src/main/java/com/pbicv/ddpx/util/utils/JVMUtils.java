package com.pbicv.ddpx.util.utils;

import kotlin.text.StringsKt;

public class JVMUtils {
//    判断一个字符串是否以特定的前缀开头，并根据参数z的值选择不同的方法来进行比较。
    public static final boolean m3983d(String str, String prefix, boolean z) {

        if (!z) {
            return str.startsWith(prefix);
        }
        return StringsKt.regionMatches(str, 0, prefix, 0, prefix.length(), z);
    }

    public static  boolean startWith(String str, String str2, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        return m3983d(str, str2, z);
    }

}