package com.rzsd.wechat.form;

import org.springframework.web.multipart.MultipartFile;

public class ImportDataForm {

    private MultipartFile uploadFile;

    public MultipartFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(MultipartFile uploadFile) {
        this.uploadFile = uploadFile;
    }

}
