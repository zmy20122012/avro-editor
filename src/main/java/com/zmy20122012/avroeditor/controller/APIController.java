package com.zmy20122012.avroeditor.controller;

import com.zmy20122012.avroeditor.dto.SubmitFormPayload;
import com.zmy20122012.avroeditor.service.AvroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class APIController {

    @Autowired
    private AvroService avroService;

    @PostMapping("/save")
    public byte[] submitForm(@RequestBody SubmitFormPayload submitFormPayload) throws IOException {
        return avroService.writeAvro(submitFormPayload);
    }

}
