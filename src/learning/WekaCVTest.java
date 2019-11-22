package learning;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaCVTest {
	Instances adult = null;
	Classifier nb;

	public void loadArff(String arffInput){
		DataSource source = null;
		try {
			source = new DataSource(arffInput);
			adult = source.getDataSet();
			adult.setClassIndex(adult.numAttributes() - 1);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void generateModel(){
		try {
			nb = new J48();
//		nb = new NaiveBayes();
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		try {
			nb.buildClassifier(adult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveModel(String modelPath){
		try {
			weka.core.SerializationHelper.write(modelPath, nb);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void crossValidate(){
		Evaluation eval = null;
		try {
			eval = new Evaluation(adult);
			eval.crossValidateModel(nb, adult, 10, new Random(1));
			System.out.println(eval.toSummaryString());
		} catch (Exception e1) {
			e1.printStackTrace();
		}	
	}
	
	public static void main(String[] args){
		WekaCVTest test = new WekaCVTest();
		test.loadArff("D://training.arff");
		test.generateModel();
		test.saveModel("D:/nb.model");
		test.crossValidate();
	}
}