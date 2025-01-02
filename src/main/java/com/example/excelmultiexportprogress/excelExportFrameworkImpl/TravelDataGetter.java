package com.example.excelmultiexportprogress.excelExportFrameworkImpl;

import com.example.excelmultiexportprogress.dao.TravelMapper;
import com.example.excelmultiexportprogress.excelExportFramework.DataGetter;
import com.example.excelmultiexportprogress.model.TravelSearchDto;

import java.util.ArrayList;
import java.util.List;

public class TravelDataGetter implements DataGetter {

    private TravelMapper travelMapper;

    public TravelDataGetter(TravelMapper travelMapper) {
        this.travelMapper = travelMapper;
    }

    @Override
    public List<Object> readData(Integer pageSize, Integer pageNum, Object sqlFilterClass) {
        TravelSearchDto travelSearchDto = (TravelSearchDto) sqlFilterClass;
        travelSearchDto.setPageNum(pageNum);
        travelSearchDto.setPageSize(pageSize);

        List<TravelExpenseExtraInfo> travelExpenseExtraInfo = travelMapper.getTravelList(travelSearchDto);
        return new ArrayList<>(travelExpenseExtraInfo);
    }

    @Override
    public Long countDataTotal(Object sqlFilterClass) {
        TravelSearchDto travelSearchDto = (TravelSearchDto) sqlFilterClass;
        return travelMapper.getTravelListCount(travelSearchDto);
    }
}
