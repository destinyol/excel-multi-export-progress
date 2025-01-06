package com.example.excelmultiexportprogress.excelExportFramework;

import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static com.example.excelmultiexportprogress.excelExportFramework.ExcelExportRunner.ensureEndsWithFileSeparator;

public class ExcelExportMainTool {

    // BATCH_COUNT 和 BATCH_COUNT_QUERY其中小的那个决定了进度条的粒度

    public static final int BATCH_COUNT = 5000; // 批量处理，每批插入Excel中的数据行数，可根据情况自行优化更改，建议5000或10000行

    public static final int BATCH_COUNT_QUERY = 1000; // 多线程分页查询，每页的数据行数，可自行优化更改（若 BATCH_COUNT_QUERY > BATCH_COUNT，则不是多线程，因为EasyExcel分批多次插入不能多线程）

    public static final int SHEET_CUNT_NUM = 100000; // 控制大约多少条数据分一个sheet，建议10万行

    public static final String FILE_SAVE_PATH = ensureEndsWithFileSeparator(System.getProperty("java.io.tmpdir")); // 临时excel文件存放位置，可自定义，默认是系统临时文件夹

    public static final Boolean DEBUG_LOG_RUNNING_TIMES = false; // 打印导出运行时间，true：打印， false：不打印

    /**
     * 主要构造方法
     * @param dataClass 参考easyExcel最简单的写的对象
     * @param dataGetter 数据分页获取器，需实现并实例化传入
     * @param redisTemplate
     * @return
     */

    public static ExcelExportMainTool build(Class dataClass, DataGetter dataGetter, RedisTemplate redisTemplate){
        ExcelExportMainTool exportMainTool = new ExcelExportMainTool();
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ExcelExportRunner runner = new ExcelExportRunner(redisTemplate,dataGetter,dataClass,executorService);
        exportMainTool.setExcelExportRunner(runner);
        return exportMainTool;
    }

    /**
     * 主要的run方法
     * @return 返回存有进度key的ExportProgress，可返回给前端
     */
    public ExportProgress runAsync() {
        return excelExportRunner.runAsync();
    }

    /**
     * 主要的run方法
     * @param sqlFilterClass 执行sql筛选项，会传入DataGetter方法中
     * @return 返回存有进度key的ExportProgress，可返回给前端
     */
    public ExportProgress runAsync(Object sqlFilterClass) {
        return excelExportRunner.runAsync(sqlFilterClass);
    }

    /**
     * 获取导入进度（静态方法）
     * @param redisTemplate 导入时候的那个redisTemplate
     * @param processKey 进度key
     * @return ImportProgress
     */
    public static ExportProgress getProgress(RedisTemplate redisTemplate, String processKey){
        processKey = ExcelExportRunner.EXPORT_EXCEL_REDIS_KEY + processKey;
        ExportProgress progress = (ExportProgress) redisTemplate.opsForValue().get(processKey);
        return progress;
    }

    /**
     * 下载excel文件
     * @param fileName 返回给前端的文件名
     * @return
     */
    public static void downloadExcel(HttpServletResponse response, String fileName){
        ExcelExportRunner.downloadExcel(response, fileName);
    }

    /**
     * 设置sheet名
     *      不设置则默认 ”sheet1“
     * @param sheetName
     */
    public ExcelExportMainTool setSheetName(String sheetName){
        excelExportRunner.setSheetName(sheetName);
        return this;
    }

    @Setter
    private ExcelExportRunner excelExportRunner;

}
