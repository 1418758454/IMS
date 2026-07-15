package com.ruoyi.manage.utils;

import com.ruoyi.common.utils.file.EvidenceFilePathUtils;
import junit.framework.TestCase;

public class EvidenceFilePathUtilsTest extends TestCase {

    public void testDuplicateProfileSegmentIsRemoved() {
        String path = "/profile/profile/pdf/2026/07/15/example.png";

        assertEquals(
                "/profile/pdf/2026/07/15/example.png",
                EvidenceFilePathUtils.normalizePublicPath(path));
    }

    public void testCanonicalProfilePathIsUnchanged() {
        String path = "/profile/pdf/2026/07/15/example.pdf";

        assertEquals(path, EvidenceFilePathUtils.normalizePublicPath(path));
    }
}
