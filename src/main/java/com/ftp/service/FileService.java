package com.ftp.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.integration.file.remote.InputStreamCallback;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ftp.domain.Extension;

@Service
public class FileService {
    @Autowired
    @Qualifier("sftpTemplate")
    private SftpRemoteFileTemplate sftpTemplate;

    public String uploadFile(MultipartFile mFile) {
        String extension = FilenameUtils.getExtension(mFile.getOriginalFilename());

        File file = new File("/tmp", mFile.getOriginalFilename());
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        try {
            FileUtils.writeByteArrayToFile(file, mFile.getBytes());

            sftpTemplate.setUseTemporaryFileName(false);
            sftpTemplate.setAutoCreateDirectory(true);
            sftpTemplate.send(MessageBuilder.withPayload(file).build(), key, FileExistsMode.APPEND);
            return key;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HttpEntity<byte[]> download(String randomKey, String filename) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            boolean success = sftpTemplate.get("data/" + randomKey + "/" + filename, new InputStreamCallback() {
                @Override
                public void doWithInputStream(InputStream stream) throws IOException {
                    FileCopyUtils.copy(stream, baos);
                }
            });

            if (success) {
                String tail = filename.split("\\.")[1].toUpperCase();
                byte[] pData = baos.toByteArray();
                HttpHeaders header = new HttpHeaders();
                ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                        .filename(filename, StandardCharsets.UTF_8).build();
                header.add("Content-Type", mediaType(tail));
                header.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
                header.setContentLength(pData.length);
                return new HttpEntity<>(pData, header);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String mediaType(String extension) {
        Extension ext = Extension.valueOf(extension);
        switch (ext) {
        case JPG:
        case BMP:
        case JPEG: {
            return "image/jpeg";
        }
        case PNG: {
            return "image/png";
        }
        case GIF: {
            return "image/gif";
        }
        case PPTX: {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        }
        case CSV: {
            return "application/vnd.ms-excel";
        }
        case XLSX: {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        case PDF: {
            return "application/pdf";
        }
        }
        return "text/plain";
    }

}
