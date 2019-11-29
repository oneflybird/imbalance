package mwmote;

import java.util.ArrayList;
import java.util.List;

//算法主程序
public class hierCluster {
	ArrayList<Node> nodes;
	public List<Cluster> startAnalysis(ArrayList<Node> nodes,int ClusterNum){
		List<Cluster> finalClusters=new ArrayList<Cluster>();
		List<Cluster> originalClusters=InitialCluster(nodes);
		finalClusters=originalClusters;
		while(finalClusters.size()>ClusterNum){
			double min=Double.MAX_VALUE;
			int mergeIndexA=0;
			int mergeIndexB=0;
			for(int i=0;i<finalClusters.size();i++) {
				for(int j=0;j<finalClusters.size();j++) {
					if(i!=j){
						Cluster clusterA=finalClusters.get(i);
						Cluster clusterB=finalClusters.get(j);
						List<Node> nodesA=clusterA.getNodes();
						List<Node> nodesB=clusterB.getNodes();
						for(int m=0;m<nodesA.size();m++) {
							for(int n=0;n<nodesB.size();n++){
								double tempDis=dist(nodesA.get(m),nodesB.get(n));
								if(tempDis<min) {
									min=tempDis;
									mergeIndexA=i;
									mergeIndexB=j;
								}
							}
						}
					}
				} //end for j
			}// end for i
	//合并cluster[mergeIndexA]和cluster[mergeIndexB]
	finalClusters=mergeCluster(finalClusters,mergeIndexA,mergeIndexB);
		}//end while
		return finalClusters;
	}
	private List<Cluster> mergeCluster(List<Cluster> finalCluster, int mergeIndexA, int mergeIndexB) {
		if(mergeIndexA!=mergeIndexB){
			Cluster clusterA=finalCluster.get(mergeIndexA);
			Cluster clusterB=finalCluster.get(mergeIndexB);
			List<Node> nodesA=clusterA.getNodes();
			List<Node> nodesB=clusterB.getNodes();
		for (Node dp : nodesB) {
			Node tempDp = new Node();
			tempDp.setNodeName(dp.getNodeName());
			tempDp.setDimension(dp.getDimension());
			tempDp.setCluster(clusterA);
			nodesA.add(tempDp);
			}
			clusterA.setNodes(nodesA);
			finalCluster.remove(mergeIndexB);
			//System.out.println(" remove mergeIndexA"+mergeIndexA+";"+mergeIndexB);
		}
		return finalCluster;
	}
	public double dist(Node a,Node b){
		double[] dimensionA=a.getDimension();
		double[] dimensionB=b.getDimension();
		double distance=0;
		if(dimensionA.length==dimensionB.length){
			for(int j=0;j<dimensionA.length;j++){
				double temp=Math.pow(dimensionA[j]-dimensionB[j], 2);
				distance=distance+temp;
			}
		distance=Math.sqrt(distance);
		}
		return distance;	
	}	

	//每个node都是一个Cluster
	public ArrayList<Cluster> InitialCluster(List<Node> Listnodes){
		ArrayList<Cluster> originalCluster = new ArrayList<Cluster>();
		for(int i=0;i<Listnodes.size();i++){
			Node tempNode= nodes.get(i);
			ArrayList<Node> tempNodes = new ArrayList<Node>();
			tempNodes.add(tempNode);
			Cluster tempCluster=new Cluster();
			tempCluster.setClusterName("cluster"+String.valueOf(i));
			tempCluster.setNodes(tempNodes);
			tempNode.setCluster(tempCluster);
			originalCluster.add(tempCluster);
		}
		return originalCluster;
	}
	
public static void main(String[] args) {
ArrayList<Node> nodes = new ArrayList<Node>();
for(int i=0;i<20;i++){//随机产生点
	float tempx=(float) Math.random();
	float tempy=(float) Math.random();
	double[]a={tempx,tempy};
	Node tempNode=new Node(a,String.valueOf(i));
	nodes.add(tempNode);
}
int clusterNum=5; //类簇数
hierCluster hc=new hierCluster();
List<Cluster> clusters=hc.startAnalysis(nodes, clusterNum);
for(Cluster cl:clusters){
	System.out.println("------"+cl.getClusterName()+"------");
		List<Node> tempDps=cl.getNodes();
		for(Node tempdp:tempDps){
				System.out.println(tempdp.getNodeName());
			}
}
}

}