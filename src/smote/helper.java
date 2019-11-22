package smote;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class helper {
	
	//对于半离散半连续数据，生成样本的离散值narray取自其最近邻离散特征中出现次数最多的值
	/*
	 nnarray：  当前样本的连续特征的k个最近邻的索引列表
	 n_numattrs：离散属性个数，不包括类别
	 Sample_n：离散特征矩阵，最后一列是类别
	 */
	static String[] Get_n_k_nearest(int[] nnarray,int n_numattrs,String[][] Sample_n) {
		//创建离散值矩阵
		String[] narray = new String[n_numattrs];
		//1. count 
		for(int i = 0;i < n_numattrs;i++) {
			Map<String, Integer> cnt_flag = new LinkedHashMap<>();
			for(int index:nnarray) {
				String att= Sample_n[index][i];
				if(cnt_flag.containsKey(att)) {
					cnt_flag.put(att,cnt_flag.get(att)+1);
				}else {
					cnt_flag.put(att, 1);
				}
			}
//			2. 找出出现次数最大的属性
			String f = "null";
			int n_max = 0; 
			for(Entry<String, Integer> entry : cnt_flag.entrySet()){
			    String mapKey = entry.getKey();
			    Integer mapValue = entry.getValue();
			    if(n_max<=mapValue) {
			    	n_max = mapValue;
			    	f = mapKey;
			    }
			    
			}
			narray[i] = f; 
			}
		return narray;
	}
	
	//对于半离散半连续数据，需要对离散数据的距离差用Med替代，Med是连续特征方差的中位数
	/*
	 * numattrs: 连续值属性个数
	 * Sample：连续特征矩阵，最后一列是类别
	 */
	static Double cal_Med(int numattrs,Double[][] Sample) {
		//Compute the median of standard deviations of all continuous features for the minority class
		Double Med;
		//1. 求每个特征的平均值
		Double[] ave = new Double[numattrs];
        for(int i=0;i<numattrs;i++){//求和
        	double sum=0;
	        for(int j = 0;j < Sample.length;j++) {
	        	System.out.println(Sample[j][i]+"");
	        	sum+=Sample[j][i];
	        }
	        //求平均值
	        ave[i] = sum/Sample.length;
        }
        //2. 求每个特征值的方差
        Double[] var = new Double[numattrs];
        for(int i=0;i<numattrs ;i++){
        	double dVar=0;
	        for(int j = 0;j < Sample.length;j++) {
	        	dVar+=(Sample[j][i]-ave[i])*(Sample[j][i]-ave[i]);
	        }
	        var[i] = Math.sqrt(dVar/Sample.length);
        }   
        //3. 求中数
//        3.1 从小到大进行排序
        Arrays.sort(var);
//        3.2 求中位数
        if (var.length%2 == 0) {
        	Med = (var[var.length/2-1]+var[var.length/2])/2;
        }else {
        	Med = var[(int) Math.floor(var.length/2)];
        }
        return Med;
	}
	
	
	//对于全离散数据，其距离计算方法和连续数据不同，距离为类别值出现次数比例之差，返回的nnarry是离散样本点的k个最近邻在原样本点中的位置索引
	/*
	 * num: 当前样本在原样本集中的索引
	 * T：num of origin samples
	 * n_numattrs: 离散属性个数，不包括类别
	 * Sample_n: 离散特征矩阵
	 * Sample_max_n:另一个类别的离散特征矩阵
	 */
	static int[] Get_alln_k_nearest_distance(int num,int T,int n_numattrs,String[][] Sample_n,int K,String[][] Sample_maxn) {
		final Logger logger = LoggerFactory.getLogger("Get_alln_k_nearest_distance");
		Double[] dis_matrix = new Double[T]; // 距离矩阵，用于求取最近邻
//		1. distance δ 
//		1.1 在两个类别中的出现次数
		for (int j = 0; j < T; j++) {
			//当前属性值在类中的出现次数
			double tmp = 0;
			for (int k = 0; k < n_numattrs ; k++) {
				//在第一个类别的出现次数
				int count_1 = 0; 
				int countj_1 = 0; 
				//在第二个类别的出现次数
				int count_2 = 0; 
				int countj_2 = 0; 
				//总次数是两个类出现次数相加
				for (int j2 = 0; j2 < T; j2++) {
					if(Sample_n[num][k].equals(Sample_n[j2][k])) {
						count_1++;
					}
					if(Sample_n[j][k].equals(Sample_n[j2][k])) {
						countj_1++;
					}
				}
				for (int j2 = 0; j2 < Sample_maxn.length; j2++) {
					if(Sample_n[num][k].equals(Sample_maxn[j2][k])) {
						count_2++;
					}
					if(Sample_n[j][k].equals(Sample_maxn[j2][k])) {
						countj_2++;
					}
				}
				tmp = (count_1*1.0)/(count_1+count_2)-(countj_1*1.0)/(countj_2+countj_2)+(count_2*1.0)/(count_1+count_2)-(countj_2*1.0)/(countj_2+countj_2);
			}
			dis_matrix[j] = dis_matrix[j]+tmp*tmp;
		}
//		2. 求k最近邻
		// 下面求n个最近邻的下标，按照距离升序
		int locat = 0;
		double min;
		int[] nnarray = new int[K]; // nnarray存储近邻位置，用于存储位置
		for (int k = 0; k < K; k++) { // 查找k次最小值及下标
			min = Double.POSITIVE_INFINITY; // 初始值设为无限大
			for (int j = 0; j < T; j++) { // 查找最小值及下标
				//不能取到样本点本身
				if (dis_matrix[j] < min && dis_matrix[j] != 0) {
					min = dis_matrix[j];
					locat = j;
				}
			}
			nnarray[k] = locat;
			dis_matrix[locat] = (double) 0; // 每找到一个非0的最小值，设为0，下一次遍历寻找次小值
		}
			logger.debug("当前点的最近距离矩阵索引为{}",nnarray);
		return nnarray;
	}
	
}
