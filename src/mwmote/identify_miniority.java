package mwmote;

import java.util.HashSet;
import java.util.Set;
import dataset.Datatype;
import dataset.get_dataset;
import smote.helper;

public class identify_miniority {
	/*
	 * Input:
	 *  1) Smaj: Set of majority class samples
	 *  2) Smin: Set of minority class samples 
	 *  3) N: Number of synthetic samples to be generated 
	 *  4) k1: Number of neighbors used for predicting noisy minority class samples 
	 *  5) k2: Number of majority neighbors used for constructing informative minority set 
	 *  6) k3: Number of minority neighbors used for constructing informative minority set
	 * 
	 */
	private int k1;
	private int k2;
	private int k3;
	private Double[][] Smaj;
	public int getDatatype() {
		return datatype;
	}

	private Double[][] Smin;
	private String[][] NSmaj;
	public Double[][] getSbmaj() {
		return Sbmaj;
	}

	public String[][] getNSbmaj() {
		return NSbmaj;
	}

	public Double[][] getSimin() {
		return Simin;
	}

	public String[][] getNSimin() {
		return NSimin;
	}

	private String[][] NSmin;
	public int getK1() {
		return k1;
	}

	public int getK2() {
		return k2;
	}

	public int getK3() {
		return k3;
	}

	private Double[][] Sall;
	private String[][] NSall;
//	private int N;
//	private int k1;
//	private int k2;
//	private int k3;
	//continue or nominal
	private int datatype;
	private int[][] NN;
//	 removing those minority class samples which have no minority example in their neighborhood:
//	Sminf = Smin −{xi ∈ Smin : NN(xi) contains no minority example} 
	private Double[][] Sminf;
	private String[][] NSminf;
//	 Nmaj(xi) consists of the nearest k2 majority samples from xi according to Euclidean distance. 
	private int[][] Nmaj;
public Double[][] getSminf() {
		return Sminf;
	}

	public String[][] getNSminf() {
		return NSminf;
	}

	//	Sbmaj, as the union of all Nmaj(xi)s
	private Double[][] Sbmaj;
	private String[][] NSbmaj;
//	 Nmin(yi) consists of the nearest k3 minority examples from Sbmaj according to Euclidean distance. 
	private int[][] Nmin;
public int[][] getNmin() {
		return Nmin;
	}

	//	Simin, as the union of all Nmin(yi)s 
	private Double[][] Simin;
	private String[][] NSimin;
	private int numattrs;
	private int n_numattrs;
	private Double Med;
	
	identify_miniority(int N,int k1,int k2,int k3){
		//initialize input
		this.k1 = k1;
		this.k2 = k2;
		this.k3 = k3;
		this.Smaj = get_dataset.get_c_cls_features(0);
		this.Smin = get_dataset.get_c_cls_features(99999);
		this.NSmaj = get_dataset.get_n_cls_features(0);
		this.NSmin = get_dataset.get_n_cls_features(99999);
		// 合并两个数组
		this.Sall = new Double[Smin.length+Smaj.length][Smin[0].length];
		System.arraycopy(Smin, 0, Sall, 0, Smin.length);
		System.arraycopy(Smaj, 0, Sall, Smin.length,Smaj.length );//将第二个数组合并到第一个数组中
		this.NSall = new String[NSmin.length+NSmaj.length][NSmin[0].length];
		System.arraycopy(NSmin, 0, NSall, 0, NSmin.length);
		System.arraycopy(NSmaj, 0, NSall, NSmin.length,NSmaj.length );//将第二个数组合并到第一个数组中
//		this.N = N;
		datatype = get_dataset.get_type(Smin, NSmin);
		
//		 特征数据类型不同，距离计算方式不同
//		 No：全离散，使用出现次数比进行计算
//		 NC: 离散加连续，将离散值使用Med替代
//		 Co：连续，使用欧氏距离
		//离散和连续
		if(datatype == Datatype.NC.ordinal()) {	 
			Med = helper.cal_Med(numattrs, Smin);
			// 得到属性数，因为预设class属性所以需要-1
			numattrs  = Smin[0].length-1;
			n_numattrs  = NSmin[0].length-1;
		//只有离散值
		}else if(datatype == Datatype.No.ordinal()){
			n_numattrs  = NSmin[0].length-1;
		//只有连续值
		}else if(datatype == Datatype.Co.ordinal()) {
			numattrs  = Smin[0].length-1;
		}
		
		//  Construction of the set Simin 
		//样本数
		int T = Smin.length;
		int[] minf_flag = new int[T];
		int mif_cnt = 0;
//		Double[] dis_matrix = new Double[k1*2]; // 距离矩阵，用于保存
		for(int i = 0;i<T;i++) {
			int[] nnarray = null; 
			// 1. 从min和maj中选出k1个最近邻
			if(datatype == Datatype.NC.ordinal()) {
				nnarray = helper.Get_nk_nearest(Smin[i],NSmin[i],k1, Sall,NSall);
			}else if(datatype == Datatype.Co.ordinal()){
				nnarray = helper.Get_k_nearest(Smin[i],k1, Sall);
			/*
				int[] nnarray1 = helper.Get_k_nearest(Smin[i],k1, Smin);
				int[] nnarray2 = helper.Get_k_nearest(Smin[i],k1, Smaj);
				// 2.1 计算k1*2个最近邻与当前样本点的距离
				for (int j = 0; j < k1*2; j++) {
					double tmp = 0;
					if(j<k1) {
						int index = nnarray1[j];
						for (int k = 0; k < numattrs; k++) {
							tmp = tmp + Math.pow(Smin[i][k]-Smin[index][k], 2);
						}
					}else {
						int index = nnarray2[j-k1];
						for (int k = 0; k < numattrs; k++) {
							tmp = tmp + Math.pow(Smin[i][k]-Smaj[index][k], 2);
						}
					}
					dis_matrix[j] = Math.sqrt(tmp);
				}
				// 2. 从两个最近邻中选出k1个
				//2.2 choose minority class samples which have no minority example in their neighborhood
				int locat = 0;
				for (int k = 0; k < k1; k++) { // 查找k次最小值及下标
					double min = Double.POSITIVE_INFINITY;
					for (int j = 0; j < k1*2; j++) { // 查找k次最小值及下标
						if(dis_matrix[j]<min && dis_matrix[j] != 0) {
							min = dis_matrix[j];
							locat = j;
						}
					}
					nnarray[k] = locat;
					dis_matrix[locat] = (double) 0; // 每找到一个非0的最小值，设为0，下一次遍历寻找次小值
				}
			*/
			}else {
				nnarray = helper.Get_alln_k_nearest_distance(NSmin[i], k1, NSmin,NSmaj);
			}
			NN[i] = nnarray;
			for (int k = 0; k < k1; k++) {
				if(nnarray[k]<Smin.length) {
					minf_flag[i] = 1;
					mif_cnt++;
					break;
				}else {
					minf_flag[i] = 0;
				}
			}
		}
		if(datatype != Datatype.No.ordinal()) {
			//4 填充Sminf
			Sminf = new Double[mif_cnt][numattrs+1];
			if(datatype == Datatype.NC.ordinal()) {
				NSminf = new String[mif_cnt][n_numattrs+1];
			}
			int f_cnt = 0;
			for(int i = 0;i<T;i++) {
				if(minf_flag[i] == 1) {
					if(datatype == Datatype.NC.ordinal()) {
						NSminf[f_cnt] = NSmin[i];
					}
					Sminf[f_cnt] = Smin[i];
					f_cnt ++;
				}
			}
			Nmaj = new int[mif_cnt][k2];
			//5 计算Nmaj
			Set<Integer> nmaj = new HashSet<>();
			for(int i = 0;i<mif_cnt;i++) {
				// 5.1. 从maj中分别选出k2个最近邻
				int[] nnarray;
				if(datatype == Datatype.NC.ordinal()) {
					nnarray = helper.Get_nk_nearest(Sminf[i],NSminf[i],k2, Smaj,NSmaj);
				}else {
					nnarray = helper.Get_k_nearest(Sminf[i],k2, Smaj);
				}
				for (int j : nnarray) {
					// 5.2 放入集合
					nmaj.add(j);
				}
				// 5.3 放入Nmaj
				Nmaj[i] = nnarray;
			}
			//6 放入Sbmaj
			int sbmaj_cnt = 0;
			Sbmaj = new Double[nmaj.size()][numattrs+1];
			if(datatype == Datatype.NC.ordinal()) {
				NSbmaj = new String[nmaj.size()][n_numattrs+1];
			}
			 for (int value : nmaj) {
			     Sbmaj[sbmaj_cnt] = Smaj[value];
				if(datatype == Datatype.NC.ordinal()) {
					NSbmaj[sbmaj_cnt] = NSmaj[value];
				}
			     sbmaj_cnt++;
			 } 
			 //7 计算Nmin
				Nmin = new int[nmaj.size()][k3];
				Set<Integer> nmin = new HashSet<>();
				for(int i = 0;i<nmaj.size();i++) {
					// 7.1. 从min中分别选出k3个最近邻
					int[] nnarray;
					if(datatype == Datatype.NC.ordinal()) {
						nnarray = helper.Get_nk_nearest(Sbmaj[i],NSbmaj[i],k3, Sminf,NSminf);
					}else {
						nnarray = helper.Get_k_nearest(Sbmaj[i],k3, Sminf);
					}
					for (int j : nnarray) {
						// 7.2 放入集合
						nmin.add(j);
					}
					// 7.3 放入Nmin
					Nmin[i] = nnarray;
				}
				//8 放入Simin
				int sbmin_cnt = 0;
				Simin = new Double[nmin.size()][numattrs+1];
				if(datatype == Datatype.NC.ordinal()) {
					NSimin = new String[nmin.size()][n_numattrs+1];
				}
				 for (int value : nmaj) {
					 Simin[sbmin_cnt] = Sminf[value];
						if(datatype == Datatype.NC.ordinal()) {
							NSimin[sbmin_cnt] = NSminf[value];
						}
				     sbmin_cnt++;
				 } 
	}else {
		//4 填充NSminf
		NSminf = new String[mif_cnt][n_numattrs+1];
		int f_cnt = 0;
		for(int i = 0;i<T;i++) {
			if(minf_flag[i] == 1) {
				NSminf[f_cnt] = NSmin[i];
				f_cnt ++;
			}
		}
		Nmaj = new int[mif_cnt][k2];
		//5 计算Nmaj
		Set<Integer> nmaj = new HashSet<>();
		for(int i = 0;i<mif_cnt;i++) {
			// 5.1. 从maj中分别选出k2个最近邻
			int[] nnarray;
			nnarray = helper.Get_alln_k_nearest_distance(NSminf[i], k2, NSmin,NSmaj);
			for (int j : nnarray) {
				// 5.2 放入集合
				nmaj.add(j);
			}
			// 5.3 放入Nmaj
			Nmaj[i] = nnarray;
		}
		//6 放入Sbmaj
		int sbmaj_cnt = 0;
		NSbmaj = new String[nmaj.size()][n_numattrs+1];
		 for (int value : nmaj) {
			NSbmaj[sbmaj_cnt] = NSmaj[value];
		     sbmaj_cnt++;
		 } 
		 //7 计算Nmin
			Nmin = new int[nmaj.size()][k3];
			Set<Integer> nmin = new HashSet<>();
			for(int i = 0;i<nmaj.size();i++) {
				// 7.1. 从min中分别选出k3个最近邻
				int[] nnarray = helper.Get_alln_k_nearest_distance(NSbmaj[i], k3, NSminf,NSmaj);
				for (int j : nnarray) {
					// 7.2 放入集合
					nmin.add(j);
				}
				// 7.3 放入Nmin
				Nmin[i] = nnarray;
			}
			//8 放入Simin
			int sbmin_cnt = 0;
			NSimin = new String[nmin.size()][n_numattrs+1];
			 for (int value : nmaj) {
				NSimin[sbmin_cnt] = NSminf[value];
				sbmin_cnt++;
			 } 
	}
	}

	public Double getMed() {
		return Med;
	}

}
