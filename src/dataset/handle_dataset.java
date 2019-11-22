package dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class handle_dataset {
	public static ArrayList<Double[]> continue_features = new ArrayList<Double[]>();
	public static ArrayList<String[]> nominal_features = new ArrayList<String[]>();
	public static ArrayList<String> continue_features_headers = new ArrayList<String>();
	public static ArrayList<String> nominal_features_headers = new ArrayList<String>();
	public static Map<String, Set<String>> features_value = new LinkedHashMap<>();
	
	//分别建立离散和连续特征矩阵
	/*
	 csv格式
	 adult_csv_headers:特征名字，包括类别，默认类别是最后一个
	 adult_csv_rows： sample列表
	 */
	public static void get_feature(String[] adult_csv_headers, List<String[]> adult_csv_rows) {
		final Logger logger = LoggerFactory.getLogger("get_continue_features");
		logger.info("正在提取出连续特征和离散特征");
		logger.debug("属性:{}",(Object)adult_csv_headers);
		int header_flag = 0;
	    for (String[] row : adult_csv_rows) {
	    	ArrayList<Double> temp = new ArrayList<Double>();
	    	ArrayList<String> tempn = new ArrayList<String>();
			List<String> row_list = Arrays.asList(row);
			
			int flag_0 = 0;
			//先检查一整行是否有缺失值缺失值，一整行都删除
			for (int j = 0; j < adult_csv_headers.length; j++){	
				if(row_list.get(j).equals("?")||row_list.get(j).equals("null")) {
					logger.warn("名称：{}，行：{}",adult_csv_headers[j],row_list.get(j)+"跳过此行");
					flag_0 = 1;
					break;
				}
			}
			if(flag_0 == 1) {
				continue;
			}
			
			for (int i = 0; i < adult_csv_headers.length-1; i++){	
				// 连续值加入temp
				boolean isNum=row_list.get(i).matches("[0-9]+");
		    	if (isNum) {
		    		temp.add(Double.parseDouble(row_list.get(i)));
		    		if(header_flag == 0) {
		    			continue_features_headers.add(adult_csv_headers[i]);
		    		}
		    	}else {
					//离散值加入tempn
		    		tempn.add(row_list.get(i));
		    		if(header_flag == 0) {
		    			Set<String> str = new HashSet<String>();
		    			str.add(row_list.get(i));
		    			features_value.put(adult_csv_headers[i], str);
		    			nominal_features_headers.add(adult_csv_headers[i]);
		    		}else {
		    			Set<String> str = features_value.get(adult_csv_headers[i]);
		    			str.add(row_list.get(i));
		    			features_value.put(adult_csv_headers[i], str);
		    		}
		    		
		    	}
		    }
			header_flag = 1;
		    if(temp.size()!=0) {
		    	Double[] strings = new Double[temp.size()];
		    	temp.toArray(strings);
		    	continue_features.add(strings);
		    }
//		    System.out.println(tempn[0]);
		    if(tempn.size()!=0){
		    	String[] strings = new String[tempn.size()];
		    	tempn.toArray(strings);
		    	nominal_features.add(strings);
		    }
	    }
	    System.out.println(continue_features.size()+"  "+nominal_features.size());
	    if(continue_features.size() == nominal_features.size()) {
	    	logger.info("离散属性和连续属性的样本点个数相同，检查通过");
	    	logger.debug("continue_features size:{},值{}\n",continue_features.size(),(Object)continue_features);
	    	logger.debug("nominal_features size:{},值{}\n",nominal_features.size(),(Object)nominal_features);
	    }else if(continue_features.size()==0) {
	    	logger.info("只有离散值");
	    }else if(nominal_features.size() == 0) {
	    	logger.info("只有连续值");
	    }
	    
		logger.info("提取完毕\n");
		return;
	}
	//
	
//	将类别转换成值aim_class
	/*
	 csv格式
	 adult_csv_headers:特征名字，包括类别，默认类别是最后一个
	 adult_csv_rows： sample列表
	 */
	public static Map<String, Integer> get_target_enum(String[] adult_csv_headers, List<String[]> adult_csv_rows) {
		final Logger logger = LoggerFactory.getLogger("get_aim_class");
		int cnt = 0;//用于计数
		Map<String, Integer> aim_class = new LinkedHashMap<>();
		//默认分类目标在最后一列
		int index_last = adult_csv_headers.length-1;
		for (String[] csv_rows : adult_csv_rows) {
			List<String> row = Arrays.asList(csv_rows);
			String cls = row.get(index_last);
			if(!aim_class.containsKey(cls)) {
				aim_class.put(cls, cnt);
				logger.debug("类：{}，值：{}\n",cls,cnt);
				cnt++;
			}
		}
		if (cnt==2) {
			logger.debug("这是二分类问题\n");
		}else {
			logger.debug("这是多分类问题\n");
		}
		return aim_class;
	}
	
	//获取分类数目个数target_size，得到少数类和多数类，可适用于多分类
	/*
	 csv格式
	 headers:特征名字，包括类别，默认类别是最后一个
	 rows： sample列表
	 */
	public static Map<String, Integer> get_target_size(String[] headers,List<String[]> rows) {
		final Logger logger = LoggerFactory.getLogger("get_target_size");
		Map<String, Integer> target_size = new LinkedHashMap<>();
		int index_last = headers.length-1;
		for (String[] arow : rows) {
			List<String> row = Arrays.asList(arow);
			String cls = row.get(index_last);
			if(!target_size.containsKey(cls)) {
				target_size.put(cls, 1);
			}else {
				target_size.put(cls, target_size.get(cls)+1);
			}
		}
//		打印target
		for(Entry<String, Integer> entry : target_size.entrySet()){
		    String mapKey = entry.getKey();
		    Integer mapValue = entry.getValue();
		    logger.debug("分类：{}，个数：{}\n",mapKey,mapValue);
		}
		return target_size;
	}
	
}
