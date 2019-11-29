package mwmote;

public class Node {
String nodeName; // 样本点名
Cluster cluster; // 样本点所属类簇
private double dimension[]; // 样本点的维度
public Node(){}
	public Node(double[] dimension,String nodeName){
		this.nodeName=nodeName;
		this.dimension=dimension;
	}
	public double[] getDimension() {
		return dimension;
	}
	public void setDimension(double[] dimension) {
		this.dimension = dimension;
	}
	public Cluster getCluster() {
		return cluster;
	}
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
}
