package com.example.excelmultiexportprogress.excelExportFramework;

import lombok.Data;

@Data
public class ExportProgress {
    private String key; // 唯一标识
    private Double progress; // 进度百分比
    private Integer status; // 处理状态  0是未开始   1是处理中   2是处理完毕     3是报错了
    private Object wrongCode; // 报错code
    private String fileName; // 文件名

    public ExportProgress(String key, Double progress, Integer status) {
        this.key = key;
        this.progress = progress;
        this.status = status;
    }

    public ExportProgress(String key, Double progress, Integer status, String fileName) {
        this.key = key;
        this.progress = progress;
        this.status = status;
        this.fileName = fileName;
    }

    public ExportProgress() {
    }
}
