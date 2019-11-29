package smote;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import dataset.Datatype;
import dataset.get_dataset;

public class SMOTE {
	Logger logger = LoggerFactory.getLogger(getClass());
	private int N; //Amount of SMOTE
	private int K; //Number of nearest neighbors k 
	private int datatype; //continue or nominal
	Double[][] mincls_features;//min类连续性特征：每一行是一个sample，每一列是一个feature，最后一列是分类
	String[][] min_nominal_features;
	String[][] max_nominal_features;
	private Double[][] Sample;
	private String[][] Sample_n;
	private String[][] Sample_maxn;
	int numattrs;
	int n_numattrs;
	private int T;
	String[] narray;
	private Double[][] Synthetic;//生成样本
	private String[][] Synthetic_n;//生成样本
	public String[][] getSynthetic_n() {
		return Synthetic_n;
	}
	int newindex = 0;
	Double class_enum;
	
	public int getN() {
		return N;
	}
	public int getK() {
		return K;
	}
	public Double[][] getSynthetic() {
		return Synthetic;
	}
	public Double[][] getSample() {
		return Sample;
	}
	//the basic way to create synthenic samples for minclass
	/*
		N：Amount of SMOTE
		K：Number of nearest neighbors k 
	 */
	public SMOTE(int N,int K){
		this.N = N;
		this.K = K;
		//导入并合成数据
		get_dataset.start();
		mincls_features = get_dataset.get_c_cls_features(99999);
		min_nominal_features = get_dataset.get_n_cls_features(99999);
		
		//设置datatype，0表示全离散，1表示离散和连续，2表示连续
		datatype = get_dataset.get_type(mincls_features, min_nominal_features);
		/*
		 特征数据类型不同，距离计算方式不同
		 No：全离散，使用出现次数比进行计算
		 NC: 离散加连续，将离散值使用Med替代
		 Co：连续，使用欧氏距离
		 */
//离散和连续
		if(datatype == Datatype.NC.ordinal()) {
			this.T = mincls_features.length;
			// 得到属性数，因为预设class属性所以需要-1
			numattrs  = mincls_features[0].length-1;		
			//得到连续值矩阵
			Sample = mincls_features;
//			得到离散值矩阵
			n_numattrs = min_nominal_features[0].length-1;
			Sample_n =  min_nominal_features;
			//Compute the median of standard deviations of all continuous features for the minority class
//只有离散值
		}else if(datatype == Datatype.No.ordinal()){
			this.T = min_nominal_features.length;
			// 得到属性数，因为预设class属性所以需要-1
			n_numattrs  = min_nominal_features[0].length-1;		
			// 得到两个分类的离散值矩阵
			Sample_n = min_nominal_features;
			Sample_maxn =  get_dataset.get_n_cls_features(0);
//只有连续值
		}else if(datatype == Datatype.Co.ordinal()) {
			this.T = mincls_features.length;
			// 得到属性数，因为预设class属性所以需要-1
			numattrs  = mincls_features[0].length-1;		
			//得到连续值矩阵
			Sample = mincls_features;
		}
		
		//对N进行处理
		if(N < 100) {
			T = (N/100)*T;
			N = 100;
		}
		N = (int)(N/100);
		//生成样本矩阵
		Synthetic = new Double[T * N][numattrs+1];
		Synthetic_n = new String[T * N][n_numattrs+1];
		int[] nnarray;
		for(int i = 0;i<T;i++) {
			if(datatype == Datatype.Co.ordinal()) {
				nnarray = helper.Get_k_nearest(Sample[i], K,Sample);
			}else if(datatype == Datatype.NC.ordinal()){
				nnarray = helper.Get_nk_nearest(Sample[i],Sample_n[i], K,Sample,Sample_n);
				narray = helper.Get_n_k_nearest(nnarray,n_numattrs,Sample_n);	
			}else {
				nnarray = helper.Get_alln_k_nearest_distance(Sample_n[i], K , Sample_n,Sample_maxn);
				narray = helper.Get_n_k_nearest(nnarray,n_numattrs,Sample_n);	
		}
		Populate(N,i,nnarray);
	}
	}
	
	//	( ∗ Function to generate the synthetic samples. ∗)
	/*
	 * N1：Amount of SMOTE(处理过后的N)
	 * i: the index of current sample
	 * nnarray: the k nearest neighbors of current sample
	 */
	void Populate(int N1,int i,int[] nnarray){
		while(N1!=0) {
//			This step chooses one of the k nearest neighbors of i
			if(datatype != Datatype.No.ordinal() ) {
				Random random = new Random();
				int nn = random.nextInt(K);
				//合成样本点
				for(int attr = 0;attr<numattrs;attr++) {
					Double dif = Sample[nnarray[nn]][attr] - Sample[i][attr];
					float gap = random.nextFloat();
					Synthetic[newindex][attr] = Sample[i][attr]+gap*dif ;
				}
				Synthetic[newindex][numattrs] = Sample[i][numattrs];
				
			}
			if (datatype!=Datatype.Co.ordinal()) {
				for(int attr = 0;attr<n_numattrs;attr++) {
					Synthetic_n[newindex][attr] = narray[attr];
					logger.debug("{}",(Object)narray);
				}
			}
		Synthetic_n[newindex][n_numattrs] = Sample_n[i][n_numattrs];
		newindex++;
		N1--;
	}
}

	
}
