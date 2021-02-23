package com.zmy20122012.avroeditor.service;


import com.zmy20122012.avroeditor.dto.AvroValue;
import com.zmy20122012.avroeditor.dto.ReadAvroResult;
import com.zmy20122012.avroeditor.dto.RecordItem;
import com.zmy20122012.avroeditor.dto.SubmitFormPayload;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AvroService {

    @Value("${upload_root}")
    public String UPLOAD_DIR;

    public ReadAvroResult readAvro(String avroFileName) {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        DataFileReader<GenericRecord> dataFileReader = null;
        try {
            dataFileReader = new DataFileReader<>(new File(UPLOAD_DIR + avroFileName), datumReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Schema schema = dataFileReader.getSchema();
        final List<String> fieldNames = schema.getFields().stream().map(Schema.Field::name).collect(Collectors.toList());
        final Map<String, String> fieldTypeMap = getFieldTypeMap(schema);

        List<List<AvroValue>> values = new LinkedList<>();
        while (dataFileReader.hasNext()) {
            GenericRecord record = dataFileReader.next();
            List<AvroValue> value = new LinkedList<>();
            for (String fieldName : fieldNames) {
                value.add(new AvroValue(fieldName, record.get(fieldName), fieldTypeMap.get(fieldName)));
            }
            values.add(value);
        }
        return new ReadAvroResult(fieldNames, values);
    }

    private Map<String, String> getFieldTypeMap(Schema schema) {
        final Map<String, String> fieldTypeMap = new HashMap<>();
        schema.getFields().forEach(field -> {
            Schema.Type type = field.schema().getType();
            if (Schema.Type.UNION.equals(type)) {
                for(Schema subType : field.schema().getTypes()) {
                    if (!Schema.Type.NULL.equals(subType.getType())) {
                        type = subType.getType();
                    }
                }
            }
            fieldTypeMap.put(field.name(),type.getName());
        });
        return fieldTypeMap;
    }

    public byte[] writeAvro(SubmitFormPayload submitFormPayload) throws IOException {
        final DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        DataFileReader<GenericRecord> dataFileReader = null;
        try {
            dataFileReader = new DataFileReader<>(new File(UPLOAD_DIR + submitFormPayload.getFilename()), datumReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Schema schema = dataFileReader.getSchema();

        List<Map<String, Object>> records = new LinkedList<>();
        submitFormPayload.getData().forEach(record -> {
            Map<String, Object> recordMap = new HashMap<>(record.size());
            record.forEach(recordItem -> recordMap.put(recordItem.getName(), recordItem.getValue()));
            records.add(recordMap);
        });


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GenericDatumWriter<GenericData.Record> datum = new GenericDatumWriter<>(schema);
        DataFileWriter<GenericData.Record> writer = new DataFileWriter<>(datum);
        writer.create(schema, out);
        for (Map<String, Object> record : records) {
            GenericData.Record r = new GenericData.Record(schema);
            for (Map.Entry<String, Object> item : record.entrySet()) {
                r.put(item.getKey(), item.getValue());
            }
            writer.append(r);
        }
        writer.close();

        return out.toByteArray();
//        FileOutputStream fos = new FileOutputStream(UPLOAD_DIR + "result.avro");
//        fos.write(out.toByteArray());
//        fos.close();
    }

}
