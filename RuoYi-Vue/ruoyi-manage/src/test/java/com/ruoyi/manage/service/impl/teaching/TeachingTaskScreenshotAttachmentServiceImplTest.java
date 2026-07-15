package com.ruoyi.manage.service.impl.teaching;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.EvidenceFilePathUtils;
import com.ruoyi.manage.domain.teaching.TeachingTaskScreenshotAttachment;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class TeachingTaskScreenshotAttachmentServiceImplTest extends TestCase {

    private Path profileRoot;
    private String originalProfile;

    @Override
    protected void setUp() throws Exception {
        originalProfile = RuoYiConfig.getProfile();
        profileRoot = Files.createTempDirectory("teaching-screenshot-delete-");
        new RuoYiConfig().setProfile(profileRoot.toString());
    }

    @Override
    protected void tearDown() throws Exception {
        new RuoYiConfig().setProfile(originalProfile);
        deleteRecursively(profileRoot);
    }

    public void testRemoveOwnedAttachmentDeletesDatabaseRowAndPhysicalFile() throws Exception {
        Path evidenceFile = profileRoot.resolve("profile/pdf/2026/07/15/evidence.png");
        Files.createDirectories(evidenceFile.getParent());
        Files.write(evidenceFile, new byte[]{1, 2, 3});

        TeachingTaskScreenshotAttachment attachment = new TeachingTaskScreenshotAttachment();
        attachment.setId(10L);
        attachment.setUserId(1001L);
        attachment.setFileUrl("http://localhost:5883/profile/pdf/2026/07/15/evidence.png");

        TestService service = new TestService(attachment);

        assertTrue(service.removeOwnedAttachment(10L, 1001L));
        assertTrue(service.databaseRowRemoved);
        assertFalse("删除附件记录后，服务器物理文件也应被删除", Files.exists(evidenceFile));
    }

    public void testStoragePathCannotEscapeEvidenceDirectory() {
        assertNull(EvidenceFilePathUtils.resolveStorageFile(
                profileRoot.toString(),
                "http://localhost:5883/profile/pdf/../../avatar/other-user.png"));
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (root == null || !Files.exists(root)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
            });
        }
    }

    private static class TestService extends TeachingTaskScreenshotAttachmentServiceImpl {
        private final TeachingTaskScreenshotAttachment attachment;
        private boolean databaseRowRemoved;

        private TestService(TeachingTaskScreenshotAttachment attachment) {
            this.attachment = attachment;
        }

        @Override
        public TeachingTaskScreenshotAttachment getOne(Wrapper<TeachingTaskScreenshotAttachment> queryWrapper) {
            return attachment;
        }

        @Override
        public boolean removeById(Serializable id) {
            databaseRowRemoved = true;
            return true;
        }
    }
}
