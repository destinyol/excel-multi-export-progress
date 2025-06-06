package com.example.excelmultiexportprogress.model;

import lombok.Data;

@Data
public class TravelSearchDto {

    private String userName;
    private String createTime;
    private Integer pageSize;
    private Integer pageNum;

    public Integer getOffset() {
        return (getPageNum() - 1) * getPageSize();
    }


}
