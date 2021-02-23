package com.zmy20122012.avroeditor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFilesResult {
    private String message;
    private String fileNameInServer;

    public UploadFilesResult(String message){
        this.message = message;
    }
}
