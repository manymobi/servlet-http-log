package com.manymobi.servlet.http.util;

import java.util.Set;

/**
 * @author 梁建军
 * 创建日期： 2022/5/21
 * 创建时间： 下午10:23
 * @version 1.0
 * @since 1.0
 */
public class ContentTypeUtil {
    private ContentTypeUtil() {
    }

    /**
     * 检查当前数据类型与列表中的是否匹配
     *
     * @param contentTypeSet 类型列表
     * @param contentType    需要匹配的类型
     * @return 在类型列表中
     */
    public static boolean isCompatibleWith(Set<String> contentTypeSet, String contentType) {
        if (contentType == null) {
            return false;
        }
        String[] split = contentType.split(";");
        return contentTypeSet.contains(split[0]);
    }
}
