package com.ruoyi.manage.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/** Keeps ownership and audit metadata unchanged when an administrator edits a record. */
public final class AdminAuditUpdateUtils {

    private static final String[] PRESERVED_PROPERTIES = {
            "userId", "userName", "status", "remark", "createTime", "updateTime"
    };

    private AdminAuditUpdateUtils() {
    }

    public static void preserve(Object original, Object edited, boolean auditEdit) {
        if (!auditEdit || original == null || edited == null) {
            return;
        }

        BeanWrapper source = new BeanWrapperImpl(original);
        BeanWrapper target = new BeanWrapperImpl(edited);
        for (String property : PRESERVED_PROPERTIES) {
            if (source.isReadableProperty(property) && target.isWritableProperty(property)) {
                target.setPropertyValue(property, source.getPropertyValue(property));
            }
        }
    }
}
