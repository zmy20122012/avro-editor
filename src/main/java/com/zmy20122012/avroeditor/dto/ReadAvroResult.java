package com.zmy20122012.avroeditor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReadAvroResult {
    private List<String> filedNames;
    private List<List<AvroValue>> values;
}
