 package mwmote;

import dataset.Datatype;

public class weight {
	private static Double[][] Sbmaj;
	private static String[][] NSbmaj;
	private static Double[][] Simin;
	private static String[][] NSimin;
	private static int[][] Nmin;
	private static Double[][] Sminf;
	private static String[][] NSminf;
	private static Double Med;
	private static Double[][] Iw;
	private static Double[] Sw;
	private static Double[] Sp;
	private static int datatype;
	public static void start(identify_miniority iden){
		//init 
		datatype = iden.getDatatype();
		Nmin = iden.getNmin();
		if (datatype == Datatype.Co.ordinal()) {
			Sbmaj = iden.getSbmaj();
			Simin = iden.getSimin();
			Sminf = iden.getSminf();
		}else if (datatype == Datatype.NC.ordinal()) {
			Sbmaj = iden.getSbmaj();
			NSbmaj = iden.getNSbmaj();
			Simin = iden.getSimin();
			NSimin = iden.getNSimin();
			Sminf = iden.getSminf();
			NSminf = iden.getNSminf();
			Med = iden.getMed();
		}else {
			NSbmaj = iden.getNSbmaj();
			NSimin = iden.getNSimin();
			NSminf = iden.getNSminf();
		}
		
		//7) For each yi ∈ Sbmaj and for each xi ∈ Simin, compute the information weight,
		//1. 确定Iw需要Cf和Df
		//1.1 确定Cf
//		Each majority class sample yi ∈ Sbmaj gives a weight to each minority class sample xi ∈ Simin. 
		int l = Sbmaj[0].length - 1;
//		 xi ∈ Nmin(yi)
		int k3 = iden.getK3();
		Double[][] Cf = new Double[Nmin.length][Nmin[0].length];
		Double[][] Df = new Double[Nmin.length][Nmin[0].length];
		//求Cf
		double f = 5;
		double CMAX = 2;
		if(datatype == Datatype.Co.ordinal()) {
			for(int yi = 0;yi<Nmin.length;yi++) {
				for(int xi = 0;xi<Nmin[0].length;xi++) {
					Double[] y = Sbmaj[yi];
					Double[] x = Sminf[Nmin[yi][xi]];
					//最后一列是class
					double tmp = 0;
					for (int k = 0; k < y.length-1; k++) {
						 tmp = tmp + Math.pow(y[k]-x[k], 2);
					}
					 double result = 1/(Math.sqrt(tmp)/l);
					 if(result > f) {
						 result = f;
					 }
					 Cf[yi][xi] = (result/f)*CMAX;
				}
			}
		}else if(datatype == Datatype.NC.ordinal()) {

			
		}
		
		//求Df
		for(int yi = 0;yi<Nmin.length;yi++) {
			for(int xi = 0;xi<Nmin[0].length;xi++) {
				Double result = Cf[yi][xi];
				Double sum = 0.0;
				for(int xii = 0;xii<Nmin[0].length;xii++) {
					sum += Cf[yi][xii];
				}
				 Df[yi][xi] = result/sum;
			}
		}
		//求Iw
		for(int yi = 0;yi<Nmin.length;yi++) {
			for(int xi = 0;xi<Nmin[0].length;xi++) {
				Double result = Cf[yi][xi];
				Double sum = 0.0;
				Iw[yi][xi] = Cf[yi][xi] *Df[yi][xi] ;
			}
		}
		//求Sw(xi)
		double Swzi = 0;
		for(int xi_num=0;xi_num<Simin.length;xi_num++) {
			double xi_sum = 0;
			for(int yi = 0;yi<Nmin.length;yi++) {
				for(int xi = 0;xi<Nmin[0].length;xi++) {
					if(Sminf[Nmin[yi][xi]] == Simin[xi_num]) {
						xi_sum += Iw[yi][xi];
						break;
					}
				}
			}
			Sw[xi_num] = xi_sum;
			Swzi += xi_sum;
		}
		//求Sp
		for(int xi_num=0;xi_num<Simin.length;xi_num++) {
			Sp[xi_num] = Sw[xi_num] / Swzi;
			}
	}
	
	
}
