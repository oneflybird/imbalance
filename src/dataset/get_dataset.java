package dataset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class get_dataset {
	final static Logger logger = LoggerFactory.getLogger("adult");
	String file;
	//默认features是连续
	private static ArrayList<Double[]>features;
	private static ArrayList<String[]> nominal_features;
	public static ArrayList<String> features_headers;
	public static ArrayList<String> nominal_features_headers;
	private static Map<String, Integer> target_enum = new LinkedHashMap<>();
	private static BaseChange adult_csv;
	private static String[] adult_csv_headers;
	private static List<String[]>  adult_csv_rows;
	private static List<Integer> target = new ArrayList<>();
	private static Map<String, Integer> target_size = new LinkedHashMap<>();
	
	public static void start() {
		adult_csv = new BaseChange();
//		adult_csv.parseCsv("D:\\adult\\adult.data");
//		adult_csv.parseCsv("D:\\adult\\nominal_test.data");
		adult_csv.parseCsv("D:\\adult\\part.data");
		adult_csv_headers=adult_csv.getCsv_headers();
		adult_csv_rows = adult_csv.getCsv_rows();
		//分别获取离散和连续的属性和属性值
		handle_dataset.get_feature(adult_csv_headers, adult_csv_rows);
		features = handle_dataset.continue_features;
		nominal_features = handle_dataset.nominal_features;
		features_headers = handle_dataset.continue_features_headers;
		nominal_features_headers = handle_dataset.nominal_features_headers;
		// 分类目标的数字表示
		target_enum = handle_dataset.get_target_enum(adult_csv_headers, adult_csv_rows);
//		将分类目标转化为数字
		int index_last = adult_csv_headers.length-1;
		for (String[] csv_rows : adult_csv_rows) {
			List<String> row = Arrays.asList(csv_rows);
			String cls = row.get(index_last);
			target.add(target_enum.get(cls));
	}
		get_target_size();

}
	public static int get_type(Double[][] mincls_features,String[][] min_nominal_features) {
		//设置flag，0表示全离散，1表示离散和连续，2表示连续
				if (mincls_features == null && min_nominal_features!= null) {
					return 0;
				}else if(mincls_features != null && min_nominal_features!=null){
					return 1;
				}else if(mincls_features != null && min_nominal_features == null){
					return 2;
				}
				return 44;
	}

//	建立连续变量的索引矩阵
	public static ArrayList<Double[]> getFeatures() {
		return features;
	}
// 分类目标的数字表示
	public static Map<String, Integer> get_target_enum() {
		return target_enum;
	}
//	将分类目标转化为数字
	public static List<Integer> getTarget() {
		return target;

}

	public static void get_target_size() {
		target_size = handle_dataset.get_target_size(adult_csv_headers,adult_csv_rows);
	}
//	获取二分类中连续特征向量数组，每一行是一个样本,init为99999表示取最小，init为0表示取最大
	public static Double[][]  get_c_cls_features(int init_flag) {
		if(features.size() == 0) {
//			System.out.println("测试成功");
			return null;
		}
		//获取最小类的类别中文值
		String flag = null;
		if (init_flag==0){
			for(Entry<String, Integer> entry : target_size.entrySet()){
			    String target = entry.getKey();
			    Integer size = entry.getValue();
			    	if (init_flag<=size) {
				    	init_flag = size;
				    	flag = target;
				    }
		    }
		}else {
			for(Entry<String, Integer> entry : target_size.entrySet()){
			    String target = entry.getKey();
			    Integer size = entry.getValue();
			    if (init_flag>=size) {
			    	init_flag = size;
			    	flag = target;
			    }
		    }
		}
		logger.warn("min_size:{},cls:{}\n",init_flag,flag);
		//所要获取样本点的分类值
		int enums = target_enum.get(flag); 
		//构造分类列表，只包含连续值
		int cnt = 0;
		for(int i = 0;i<features.size();i++) {
			// 多出class类
		    if (enums == target.get(i)) {
			    cnt++;
		    }
		}
//		System.out.println(cnt+"");
		Double [][]continue_cls_matrix = new Double[cnt][];
		cnt = 0;
		for(int i = 0;i<features.size();i++) {
			// 多出class类
		    if (enums == target.get(i)) {
				continue_cls_matrix[cnt] = new Double[features.get(i).length+1];
		    	for(int j = 0;j<features.get(i).length;j++) {
		    		Double[] row = features.get(i);
		    		continue_cls_matrix[cnt][j] = row[j];
		    	}
		    	continue_cls_matrix[cnt][features.get(i).length] = Double.valueOf(enums);
		    	System.out.println(cnt);
			    cnt++;
		    }
		}
		logger.info("{}",(Object)continue_cls_matrix);
		return continue_cls_matrix;
	}
	
//	获取二分类中离散特征向量数组，每一行是一个样本,init为99999表示取最小，init为0表示取最大
	public static String[][]  get_n_cls_features(int init_flag) {
		String[][] nominal_cls_matrix = null;
		if(nominal_features.size() == 0) {
//			System.out.println("测试成功");
			return nominal_cls_matrix;
		}
		//获取最小类的类别中文值
		String flag = null;
		if (init_flag==0){
			for(Entry<String, Integer> entry : target_size.entrySet()){
			    String target = entry.getKey();
			    Integer size = entry.getValue();
			    	if (init_flag<=size) {
				    	init_flag = size;
				    	flag = target;
				    }
		    }
		}else {
			for(Entry<String, Integer> entry : target_size.entrySet()){
			    String target = entry.getKey();
			    Integer size = entry.getValue();
//			    System.out.println(size+"   "+init_flag);
			    if (init_flag>=size) {
			    	init_flag = size;
			    	flag = target;
			    }
		    }
		}
		logger.warn("max_size:{},cls:{}\n",init_flag,flag);
		//所要获取样本点的分类值
		int enums = target_enum.get(flag); 
		int cnt = 0;
		for(int i = 0;i<nominal_features.size();i++) {
			// 多出class类
		    if (enums == target.get(i)) {
			    cnt++;
		    }
		}
		//构造分类列表，只包含连续值
		nominal_cls_matrix = new String[cnt][];
		cnt = 0;
		for(int i = 0;i<nominal_features.size();i++) {
			// 多出class类
		    if (enums == target.get(i)) {
		    	nominal_cls_matrix[cnt] = new String[nominal_features.get(i).length+1];
		    	for(int j = 0;j<nominal_features.get(i).length;j++) {
		    		nominal_cls_matrix[cnt][j] = nominal_features.get(i)[j];
		    	}
		    	nominal_cls_matrix[cnt][nominal_features.get(i).length] = String.valueOf(enums);
		    	cnt++;
		    }
		}
		return nominal_cls_matrix;
	}
	
}
