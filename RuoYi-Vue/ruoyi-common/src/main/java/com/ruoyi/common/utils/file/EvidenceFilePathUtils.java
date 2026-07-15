package com.ruoyi.common.utils.file;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 证明材料文件路径工具。
 */
public final class EvidenceFilePathUtils {

    private static final String DUPLICATE_PROFILE_PREFIX =
            Constants.RESOURCE_PREFIX + Constants.RESOURCE_PREFIX + "/";
    private static final String EVIDENCE_PUBLIC_PREFIX = Constants.RESOURCE_PREFIX + "/pdf/";

    private EvidenceFilePathUtils() {
    }

    /**
     * 将上传工具生成的双层 profile 地址恢复为系统原有的单层公开地址。
     */
    public static String normalizePublicPath(String filePath) {
        if (StringUtils.isEmpty(filePath) || !filePath.startsWith(DUPLICATE_PROFILE_PREFIX)) {
            return filePath;
        }
        return Constants.RESOURCE_PREFIX + "/" + filePath.substring(DUPLICATE_PROFILE_PREFIX.length());
    }

    /**
     * 将证明材料公开地址解析为磁盘文件，并确保目标没有越出证明材料目录。
     */
    public static File resolveStorageFile(String profileRoot, String fileUrl) {
        if (StringUtils.isEmpty(profileRoot) || StringUtils.isEmpty(fileUrl)) {
            return null;
        }

        String publicPath;
        try {
            publicPath = new URI(fileUrl).getPath();
        } catch (URISyntaxException | IllegalArgumentException e) {
            return null;
        }
        publicPath = normalizePublicPath(publicPath == null ? null : publicPath.replace('\\', '/'));
        if (StringUtils.isEmpty(publicPath) || !publicPath.startsWith(EVIDENCE_PUBLIC_PREFIX)) {
            return null;
        }

        String relativePath = publicPath.substring(EVIDENCE_PUBLIC_PREFIX.length());
        if (StringUtils.isEmpty(relativePath)) {
            return null;
        }

        Path evidenceRoot = Paths.get(profileRoot, "profile", "pdf").toAbsolutePath().normalize();
        Path target = evidenceRoot.resolve(relativePath).normalize();
        if (!target.startsWith(evidenceRoot)) {
            return null;
        }
        return target.toFile();
    }
}
