package com.zmy20122012.avroeditor.service;

import com.zmy20122012.avroeditor.dto.UploadFilesResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
public class FileService {

    @Value("${upload_root}")
    public String UPLOAD_DIR;

    public UploadFilesResult uploadFiles(MultipartFile file){
        prepareUploadFiles();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileNameInServer = generateFileNameInServer(fileName);
        try {
            Path path = Paths.get(UPLOAD_DIR + fileNameInServer);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return new UploadFilesResult("upload files error");
        }
        return new UploadFilesResult("successfully uploaded", fileNameInServer);
    }

    private String generateFileNameInServer(String fileName){
        String[] separetedNameParts = fileName.split("\\.");
        String[] separetedRealNameParts = Arrays.copyOfRange(separetedNameParts, 0, separetedNameParts.length-1);
        String realName = String.join(".",separetedRealNameParts);
        return realName + "_" + getCurrentDatetimeString() + ".avro";
    }

    private void prepareUploadFiles() {
        File theDir = new File(UPLOAD_DIR);
        if (!theDir.exists()){
            theDir.mkdirs();
        }
    }

    private String getCurrentDatetimeString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss_SSS");
        return formatter.format(LocalDateTime.now());
    }
}