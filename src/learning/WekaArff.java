package learning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dataset.get_dataset;
import dataset.handle_dataset;
import smote.SMOTE;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;


public class WekaArff {
	public static void main(String[] args) throws Exception {
		Logger logger = LoggerFactory.getLogger("WekaArff");
		ArrayList<Attribute>      attributes;
		ArrayList<String>      classVals;
		Map<String, ArrayList<String>> v = new LinkedHashMap<>();
		Instances       data;
		double[]        values;
		SMOTE S1 = new SMOTE(100,5);
		// Set up attributes
		//每一列是一个点
		 ArrayList<String> continue_features_headers = handle_dataset.continue_features_headers;
		 ArrayList<String> nominal_features_headers = handle_dataset.nominal_features_headers;
		attributes = new ArrayList<Attribute>();
		// Numeric attribute
		if(continue_features_headers.size()!=0){
			for (String continue_features_header : continue_features_headers) {
				attributes.add(new Attribute(continue_features_header));
			}
		}
		// String attribute
		if(nominal_features_headers.size()!=0){
			Map<String, Set<String>> features_value = handle_dataset.features_value;
			 for (String nominal_features_header : nominal_features_headers) {
				 Set<String> value = features_value.get(nominal_features_header);
//				 v =  new ArrayList<>(value);
				 v.put(nominal_features_header, new ArrayList<>(value));
				 Attribute fv = new Attribute(nominal_features_header, new ArrayList<>(value));
				 attributes.add(fv);
			}
		}
//		分类值
		classVals = new ArrayList<String>();
		for (int i = 0; i < get_dataset.get_target_enum().size(); i++){
			classVals.add(i+"");
		}
		Attribute classVal = new Attribute("class", classVals);
		attributes.add(classVal);
		// Create Instances object
		data = new Instances("adult", attributes, 0);
		logger.info(data.toString()+"\n");
		// Data fill up
		//分别获取连续值和离散值data
		Double[][] Sample = null;
		String[][] Samplen = null;
		//1. 如果存在连续值
		if(continue_features_headers.size()!=0){
			Double[][] Synthetic = S1.getSynthetic();
			Double[][] mincls_features = get_dataset.get_c_cls_features(99999);
			Double[][] maxcls_features =  get_dataset.get_c_cls_features(0);
			//先全整合进Sample_min中
			
			int synLen=Synthetic.length;//保存第一个数组长度
			int smLen=mincls_features.length;//保存第二个数组长度
			logger.info("未合并前{},长度为{}\n",mincls_features,mincls_features.length);
			
			mincls_features= Arrays.copyOf(mincls_features,synLen+smLen);//扩容
			System.arraycopy(Synthetic, 0, mincls_features, smLen,synLen );//将第二个数组合并到第一个数组中
			Sample = mincls_features;
			logger.info("第一次{}\n,长度为{}\n",Sample,Sample.length);
			
			int smaLen=maxcls_features.length;
			int sLen=Sample.length;
			Sample= Arrays.copyOf(Sample,sLen+smaLen);//扩容
			System.arraycopy(maxcls_features, 0, Sample, sLen,smaLen );//将第二个数组合并到第一个数组中
			logger.info("第二次{}\n，长度为{}\n",Sample,Sample.length);
		}
		
		if(nominal_features_headers.size()!=0){
			String[][] Synthetic_n = S1.getSynthetic_n();
			String[][] min_nominal_features = get_dataset.get_n_cls_features(99999);
			String[][] maxcls_features =  get_dataset.get_n_cls_features(0);
			//先全整合进Samplen中
			int synLen=Synthetic_n.length;//保存第一个数组长度
			int smLen=min_nominal_features.length;//保存第二个数组长度
			logger.info("未合并前{},长度为{}\n",min_nominal_features,min_nominal_features.length);
			
			min_nominal_features= Arrays.copyOf(min_nominal_features,synLen+smLen);//扩容
			System.arraycopy(Synthetic_n, 0, min_nominal_features, smLen,synLen );//将第二个数组合并到第一个数组中
			Samplen = min_nominal_features;
			logger.info("第一次{}\n,长度为{}\n",Samplen,Samplen.length);
			
			int smaLen=maxcls_features.length;
			int sLen=Samplen.length;
			Samplen= Arrays.copyOf(Samplen,sLen+smaLen);//扩容
			System.arraycopy(maxcls_features, 0, Samplen, sLen,smaLen );//将第二个数组合并到第一个数组中
			logger.info("第二次{}\n，长度为{}\n",Samplen,Samplen.length);
		}
		
		//有连续数据时
		if(Sample != null) {
			for (int i = 0; i < Sample.length; i++){
				values = new double[data.numAttributes()];
				for(int value_index = 0;value_index < Sample[i].length-1;value_index++) {
					values[value_index] = Sample[i][value_index];
				}
				//有离散数据时
				if(Samplen != null) {
					for(int value_index = Sample[i].length-1;value_index < Sample[i].length-1+Samplen[i].length-1;value_index++) {
						values[value_index]  = v.get(nominal_features_headers.get(value_index-Sample[i].length+1)).indexOf(Samplen[i][value_index-Sample[i].length+1]+"");
					}
					values[Sample[0].length+Samplen[0].length-2] = classVals.indexOf(Sample[i][Sample[i].length-1].intValue()+"");
					data.add(new DenseInstance(1.0, values));
				}
			}
		}else {
			//全离散时
			for (int i = 0; i < Samplen.length; i++){
				values = new double[data.numAttributes()];
				for(int value_index = 0;value_index < Samplen[i].length-1;value_index++) {
					values[value_index]  = v.get(nominal_features_headers.get(value_index)).indexOf(Samplen[i][value_index]);
				}
					values[Samplen[i].length-1] = classVals.indexOf(Samplen[i][Samplen[i].length-1]+"");
					data.add(new DenseInstance(1.0, values));
				}
			}
		
		//writing arff file to disk
		BufferedWriter writer = new BufferedWriter(new FileWriter("D://training.arff"));
		writer.write(data.toString());
		writer.close();

		// Output data
		logger.info(data.toString()+"\n");
	}
}
