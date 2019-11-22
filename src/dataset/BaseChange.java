package dataset;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

public class BaseChange {
	final Logger logger = LoggerFactory.getLogger(getClass());
	private String[] csv_headers;
	private List<String[]> csv_rows;
public String[] getCsv_headers() {
//		logger.info("csv_headers{}",csv_headers);
		return csv_headers;
	}

	public List<String[]> getCsv_rows() {
//		logger.info("csv_rows{}",csv_rows);
		return csv_rows;
	}

	//	filename：指定带解析的文件名
	@SuppressWarnings("deprecation")
	public void parseCsv(String filename) {
		logger.debug("正在把csv文件转换成java内部格式");
//	    创建一个配置对象，该对象提供多种配置选项
	    CsvParserSettings parserSettings = new CsvParserSettings();
//		自动检测行分隔符
	    parserSettings.setLineSeparatorDetectionEnabled(true);
//	    用于把每个解析的行存储在列表
	    RowListProcessor rowProcessor = new RowListProcessor();	    
	    parserSettings.setRowProcessor(rowProcessor);
//	    如包含标题头
	    parserSettings.setHeaderExtractionEnabled(true);
//	    指定实例
	    CsvParser parser = new CsvParser(parserSettings);
	    parser.parse(new File(filename));
//	    如包含标题头
	    String[] headers = rowProcessor.getHeaders();
	    csv_headers=headers;	    
	    for (String header : headers) {
//	    	debug比info低级
			logger.trace(header);
		}
	    List<String[]> rows = rowProcessor.getRows();
	    csv_rows=rows;
	    for (int i = 0; i < rows.size(); i++){
	    	logger.trace(Arrays.asList(rows.get(i)).toString());
	    }
	    logger.debug("转换完成");
	}
}
