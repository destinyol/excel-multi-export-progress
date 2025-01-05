package com.example.excelmultiexportprogress;

import com.example.excelmultiexportprogress.model.Travel;
import com.example.excelmultiexportprogress.service.TravelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class ExcelMultiExportProgressApplicationTests {

    @Autowired
    private TravelService travelService; // 假设您有一个 TravelService

    @Test
    void contextLoads() {
    }

    /**
     * 给 td_travel 插入15万条模拟数据
     * @throws InterruptedException
     */
    @Test
    public void insertTravelDataWithMultithreading() throws InterruptedException {
        // 随机生成数据的辅助工具
        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 每批插入 1000 条数据
        int batchSize = 1000;
        int totalRecords = 150000;

        // 创建一个固定大小的线程池
        int threadPoolSize = 10; // 可以根据实际情况调整线程池大小
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        // 用于记录已插入的数据条数
        AtomicInteger insertedCount = new AtomicInteger(0);

        // 提交任务
        for (int i = 0; i < totalRecords; i += batchSize) {
            int finalI = i;
            executorService.submit(() -> {
                List<Travel> travels = new ArrayList<>();

                // 生成一批数据
                for (int j = 0; j < batchSize; j++) {
                    Travel travel = new Travel();
                    travel.setUserId("user" + random.nextInt(10000));
                    travel.setRemarks("remarks" + (finalI + j));
                    travel.setUserName("user" + random.nextInt(1000));
                    travel.setDepartId("depart" + random.nextInt(100));
                    travel.setDepartName("department" + random.nextInt(100));
                    travel.setSerialNumber("SN" + (finalI + j));
                    travel.setProjectCode("PC" + random.nextInt(1000));
                    travel.setProjectName("project" + random.nextInt(100));
                    travel.setAmount(new BigDecimal(random.nextDouble() * 10000));
                    travel.setTravelStatus(random.nextInt(5));
                    travel.setOccurTime(LocalDateTime.now().minusDays(random.nextInt(365)).format(formatter));
                    travel.setCreateTime(LocalDateTime.now().format(formatter));

                    travels.add(travel);
                }

                // 批量插入
                travelService.saveBatch(travels);

                // 更新已插入的数据条数
                insertedCount.addAndGet(batchSize);
            });
        }

        // 定期检查并打印进度
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            int currentCount = insertedCount.get();
            double progress = (double) currentCount / totalRecords * 100;
            System.out.printf("Progress: %.2f%% (%d / %d)\n", progress, currentCount, totalRecords);
        }, 0, 1, TimeUnit.SECONDS);

        // 等待所有任务完成
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // 关闭进度检查器
        scheduler.shutdown();
    }

}
