package com.example.excelmultiexportprogress.excelExportFramework;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ExcelExportRunner {

    public ExcelExportRunner(RedisTemplate redisTemplate, DataGetter dataGetter, Class dataClass, ExecutorService executorService) {
        this.redisTemplate = redisTemplate;
        this.dataGetter = dataGetter;
        this.dataClass = dataClass;
        this.executorService = executorService;
    }

    private static final String fileSavePath = ExcelExportMainTool.FILE_SAVE_PATH;
    @Setter
    private String sheetName;
    private final String defaultSheetName = "sheet1";
    private RedisTemplate redisTemplate;
    private DataGetter dataGetter;
    private static final int BATCH_COUNT = ExcelExportMainTool.BATCH_COUNT;
    private static final int BATCH_COUNT_QUERY = ExcelExportMainTool.BATCH_COUNT_QUERY;
    private static final int SHEET_CUNT_NUM = ExcelExportMainTool.SHEET_CUNT_NUM;
    private Class dataClass;
    private ExecutorService executorService;
    private String processKey;
    private String preKey;

    public void run(String fileName, Object sqlFilterClass){
        if (sheetName == null || sheetName.isEmpty()){
            sheetName = defaultSheetName;
        }

        final AtomicLong currentCount = new AtomicLong(0);
        final AtomicLong sheetCutCount = new AtomicLong(0);
        Integer sheetNum = 0;
        Long totalCount = dataGetter.countDataTotal(sqlFilterClass);

        String fileNamePath = fileSavePath + fileName;

        if (BATCH_COUNT > BATCH_COUNT_QUERY){
            runBatchByEasyExcel(fileNamePath,fileName,totalCount,sqlFilterClass,sheetNum,currentCount,sheetCutCount);
        }else{
            runBatchBySql(fileNamePath,fileName,totalCount,sqlFilterClass,sheetNum,currentCount,sheetCutCount);
        }

        ExportProgress progressObj1 = new ExportProgress(preKey, ((double)currentCount.get()/(double)totalCount), 2,fileName);
        redisTemplate.opsForValue().set(processKey, progressObj1);

    }

    private void runBatchByEasyExcel(String fileNamePath,String fileName,Long totalCount,Object sqlFilterClass,Integer sheetNum,AtomicLong currentCount,AtomicLong sheetCutCount){
        final List<Future<Object>> futures = new ArrayList<>();
        final List<Object> dataList = Collections.synchronizedList(new ArrayList<>());
        ExcelWriter excelWriter = EasyExcel.write(fileNamePath, dataClass).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

        while (currentCount.get() < totalCount){
            final AtomicLong oneBatchCount = new AtomicLong(0);

            while (oneBatchCount.get() < BATCH_COUNT) {
                Integer pageNum = Math.toIntExact((currentCount.get() / BATCH_COUNT_QUERY) + 1);
                Object sqlFilterClassCopy = BeanUtil.copyProperties(sqlFilterClass, sqlFilterClass.getClass());     // copy一份，避免多线程查询过程中修改该类，避免多线程问题
                Future<Object> future = executorService.submit(() -> dataGetter.readData(BATCH_COUNT_QUERY, pageNum, sqlFilterClassCopy));
                futures.add(future);
            }

            for (Future<Object> future : futures) {
                try {
                    List<Object> resDto = (List<Object>)future.get();
                    if (resDto != null && !resDto.isEmpty()){
                        if (!dataClass.isInstance(resDto.get(0))){
                            throw new RuntimeException("类型错误，返回数据类不是期望实体类");
                        }
                        currentCount.addAndGet(resDto.size());
                        sheetCutCount.addAndGet(resDto.size());
                        dataList.addAll(resDto);
                        ExportProgress progressObj = new ExportProgress(preKey, ((double)currentCount.get()/(double)totalCount), 1,fileName);
                        redisTemplate.opsForValue().set(processKey, progressObj);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    ExportProgress progressObj = new ExportProgress(preKey, ((double)currentCount.get()/(double)totalCount), 3,fileName);
                    progressObj.setWrongCode("111000"); // 错误码，可自定义
                    redisTemplate.opsForValue().set(processKey, progressObj);
                }
            }

            futures.clear();

            excelWriter.write(dataList, writeSheet);
            if (sheetCutCount.get() >= SHEET_CUNT_NUM){ // 超过每页sheet数据上限
                sheetCutCount.set(0);
                sheetNum++;
                writeSheet = EasyExcel.writerSheet(sheetName+sheetNum).build();
            }
            dataList.clear();
            excelWriter.finish();   // 不finish文件会报错

        }
    }

    private void runBatchBySql(String fileNamePath,String fileName,Long totalCount,Object sqlFilterClass,Integer sheetNum,AtomicLong currentCount,AtomicLong sheetCutCount){
        ExcelWriter excelWriter = EasyExcel.write(fileNamePath, dataClass).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();

        while (currentCount.get() < totalCount){
            Integer pageNum = Math.toIntExact((currentCount.get() / BATCH_COUNT_QUERY) + 1);
            Object sqlFilterClassCopy = BeanUtil.copyProperties(sqlFilterClass, sqlFilterClass.getClass());
            List<Object> objects = dataGetter.readData(BATCH_COUNT_QUERY, pageNum, sqlFilterClassCopy);

            if (objects != null && !objects.isEmpty()){
                if (!dataClass.isInstance(objects.get(0))){
                    ExportProgress progressObj = new ExportProgress(preKey, ((double)currentCount.get()/(double)totalCount), 3,fileName);
                    progressObj.setWrongCode("111000"); // 错误码，可自定义
                    redisTemplate.opsForValue().set(processKey, progressObj);
                    throw new RuntimeException("类型错误，返回数据类不是期望实体类");
                }
            }

            for (int start = 0; start < objects.size(); start += BATCH_COUNT) {
                int end = Math.min(start + BATCH_COUNT, objects.size());
                List<Object> batch = objects.subList(start,end);

                excelWriter.write(batch, writeSheet);
                currentCount.addAndGet(objects.size());
                sheetCutCount.addAndGet(objects.size());

                ExportProgress progressObj = new ExportProgress(preKey, ((double)currentCount.get()/(double)totalCount), 1,fileName);
                redisTemplate.opsForValue().set(processKey, progressObj);

                if (sheetCutCount.get() >= SHEET_CUNT_NUM){ // 超过每页sheet数据上限
                    sheetCutCount.set(0);
                    sheetNum++;
                    writeSheet = EasyExcel.writerSheet(sheetName+sheetNum).build();
                }
            }

            excelWriter.finish();   // 不finish文件会报错

        }
    }

    public ExportProgress runAsync(Object sqlFilterClass){
        String fileName = UUID.randomUUID()+".xlsx";
        ExecutorService executor = Executors.newSingleThreadExecutor();
        String key = generateConcurrentUUID(EXPORT_EXCEL_REDIS_KEY);
        String keyStr = EXPORT_EXCEL_REDIS_KEY + key;
        this.processKey = keyStr;
        this.preKey = key;
        ExportProgress progressObj1 = new ExportProgress(preKey, 0.0, 1,fileName);
        redisTemplate.opsForValue().set(processKey, progressObj1);
        executor.submit(() -> {
            try {
                this.run(fileName,sqlFilterClass);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        return progressObj1;
    }

    public ExportProgress runAsync(){
        return this.runAsync(null);
    }

    public static void downloadExcel(HttpServletResponse response, String fileName){
        // 假设你有一个文件路径
        File file = new File(fileSavePath + fileName);

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentLengthLong(file.length());
        // 读取文件并写入响应
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // 处理异常，例如记录日志
            e.printStackTrace();
        }
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("-------------- 导出excel临时文件删除失败 -----------------");
        }
    }

    private static String generateConcurrentUUID(String ident) {
        String result = ident +
                System.currentTimeMillis() +
                COUNT.incrementAndGet();
        try {
            result = UUID.nameUUIDFromBytes(result.getBytes(StandardCharsets.UTF_8)).toString().replace("-", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String ensureEndsWithFileSeparator(String path) {
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        return path;
    }
    private static final AtomicInteger COUNT = new AtomicInteger(0);
    public static final String EXPORT_EXCEL_REDIS_KEY = "multi_export_excel:";

}
