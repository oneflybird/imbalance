package mwmote;

import java.util.ArrayList;
import java.util.List;

//类簇Cluster.java
public class Cluster {
private List<Node> nodes = new ArrayList<Node>(); // 类簇中的样本点
private String clusterName;
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
}
