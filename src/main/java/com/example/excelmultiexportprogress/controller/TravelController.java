package com.example.excelmultiexportprogress.controller;
import com.example.excelmultiexportprogress.model.Response;
import com.example.excelmultiexportprogress.model.TravelSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

/**
 * 示例controller
 */
@RestController
@RequestMapping("/test")
public class TravelController {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 导出费用明细统计excel
     */
    @PostMapping("exportExpenseExtraInfo")
    public Response startExportTravelExpenseExtraInfo(@RequestBody TravelSearchDto travelSearchDto){
        Response response = new Response();
        response.ret(0,"成功了");
        try {

        } catch (Exception e) {
            e.printStackTrace();
            response.ret(111000,"出错了");
        }
        return response;
    }

    /**
     * 获取导出费用明细统计excel进度
     */
    @GetMapping("getExportExpenseExtraInfoProgress")
    public Response getExportTravelExpenseExtraInfoProgress(String processKey){
        Response response = new Response();
        response.ret(0,"成功了");
        try {

        } catch (Exception e) {
            e.printStackTrace();
            response.ret(111000,"出错了");
        }
        return response;
    }

    /**
     * 下载导出的excel文件
     */
    @GetMapping("downloadExportExcel")
    public Response downloadExportTravelExpenseExtraInfoExcel(HttpServletResponse httpResponse, String fileName){
        Response response = new Response();
        response.ret(0,"成功了");
        try {

        } catch (Exception e) {
            e.printStackTrace();
            response.ret(111000,"出错了");
        }
        return response;
    }

}
