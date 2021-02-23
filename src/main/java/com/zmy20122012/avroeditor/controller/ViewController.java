package com.zmy20122012.avroeditor.controller;

import com.zmy20122012.avroeditor.dto.ReadAvroResult;
import com.zmy20122012.avroeditor.dto.UploadFilesResult;
import com.zmy20122012.avroeditor.service.AvroService;
import com.zmy20122012.avroeditor.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ViewController {

    @Autowired
    private FileService fileService;

    @Autowired
    private AvroService avroService;

    @GetMapping("/")
    public String index(String name, Model model) {
        model.addAttribute("name", name);
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {

        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }

        UploadFilesResult uploadFilesResult = fileService.uploadFiles(file);

        // return success response
        attributes.addFlashAttribute("message", uploadFilesResult.getMessage());
        attributes.addFlashAttribute("filename", uploadFilesResult.getFileNameInServer());

        ReadAvroResult avroData = avroService.readAvro(uploadFilesResult.getFileNameInServer());
        attributes.addFlashAttribute("titles", avroData.getFiledNames());
        attributes.addFlashAttribute("datas", avroData.getValues());
        return "redirect:/";
    }

}
