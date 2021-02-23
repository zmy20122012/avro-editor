package com.zmy20122012.avroeditor.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmitFormPayload {
    private String filename;
    private List<List<RecordItem>> data;
}
