package org.skillbox.socnet.controller;

import org.skillbox.socnet.model.entity.FileInfo;
import org.skillbox.socnet.service.files.FileService;
import org.skillbox.socnet.service.files.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class ApiStorageController {
    private final FileService fileService;
    private final StorageService storageService;

    public ApiStorageController(FileService fileService,
                                StorageService storageService) {
        this.fileService = fileService;
        this.storageService = storageService;
    }

    @PostMapping(value = "/storage", consumes = {"multipart/form-data"})
    public ResponseEntity<?> toStoreFile(@RequestParam String type, @RequestPart("file") MultipartFile imageAvatar) throws IOException {
        FileInfo fileInfo = fileService.upload(imageAvatar);
        fileInfo.setFileType(type);

        return storageService.response(fileInfo);
    }

}
