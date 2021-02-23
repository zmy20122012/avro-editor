package com.zmy20122012.avroeditor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvroValue {
    private String name;
    private Object value;
    private String type;
}
