package mwmote;

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
	private Double[][] Smaj;
	private Double[][] Smin;
	private String[][] NSmaj;
	private String[][] NSmin;
	private int N;
	private int k1;
	private int k2;
	private int k3;
	//continue or nominal
	private int datatype;
//	NN(xi) consists of the location of the nearest k1 neighbors of xi according to Euclidean distance
	private int[][] NN;
//	 removing those minority class samples which have no minority example in their neighborhood:
//	Sminf = Smin −{xi ∈ Smin : NN(xi) contains no minority example} 
	private Double[][] Sminf;
	private Double[][] NSminf;
//	 Nmaj(xi) consists of the nearest k2 majority samples from xi according to Euclidean distance. 
	private int[][] Nmaj;
//	Sbmaj, as the union of all Nmaj(xi)s
	private Double[][] Sbmaj;
	private String[][] NSbmaj;
//	 Nmin(yi) consists of the nearest k3 minority examples from Sbmaj according to Euclidean distance. 
	private int[][] Nmin;
//	Simin, as the union of all Nmin(yi)s 
	private Double[][] Simin;
	private Double[][] NSimin;
	private int numattrs;
	private int T;
	
	identify_miniority(int N,int k1,int k2,int k3){
		//initialize input
		this.Smaj = get_dataset.get_c_cls_features(0);
		this.Smin = get_dataset.get_c_cls_features(99999);
		this.NSmaj = get_dataset.get_n_cls_features(0);
		this.NSmin = get_dataset.get_n_cls_features(99999);
		this.N = N;
		this.k1 = k1;
		this.k2 = k2;
		this.k3 = k3;
		/*
		 特征数据类型不同，距离计算方式不同
		 No：全离散，使用出现次数比进行计算
		 NC: 离散加连续，将离散值使用Med替代
		 Co：连续，使用欧氏距离
		 */
		datatype = get_dataset.get_type(Smin, NSmin);
		//离散和连续
				if(datatype == Datatype.NC.ordinal()) {	       
		//只有离散值
				}else if(datatype == Datatype.No.ordinal()){
					
		//只有连续值
				}else if(datatype == Datatype.Co.ordinal()) {
					this.T = Smin.length;
					// 得到属性数，因为预设class属性所以需要-1
					numattrs  = Smin[0].length-1;
					for(int i = 0;i<T;i++) {
						if(datatype!= Datatype.No.ordinal()) {
							nnarray = SMOTE.Get_k_nearest(i);
							if(datatype == Datatype.NC.ordinal()) {
								narray = helper.Get_n_k_nearest(nnarray,n_numattrs,Sample_n);	
							}
						}else {
							nnarray = helper.Get_alln_k_nearest_distance(i, T, n_numattrs, Sample_n, K, Sample_maxn);
							narray = helper.Get_n_k_nearest(nnarray,n_numattrs,Sample_n);	
					}
					Populate(N,i,nnarray);
				}
				}
		
	}
	
}
