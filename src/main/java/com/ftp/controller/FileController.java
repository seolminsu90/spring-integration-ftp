package com.ftp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ftp.service.FileService;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService issueFileService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return issueFileService.uploadFile(file);
    }

    @GetMapping("/download/{randomkey}/{filename}")
    public HttpEntity<byte[]> download(@PathVariable("randomkey") String randomKey,
            @PathVariable("filename") String filename) {
        return issueFileService.download(randomKey, filename);
    }
}
