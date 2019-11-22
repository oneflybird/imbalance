package dataset;

/*
No：全离散，使用出现次数比进行计算
NC: 离散加连续，将离散值使用Med替代
Co：连续，使用欧氏距离
*/
public enum Datatype { 
    No(0),NC(1),Co(2),Pon(44); 
	// 定义私有变量

    private int typeCode;

    // 构造函数，枚举类型只能为私有

    private Datatype(int typeCode) {

        this.typeCode = typeCode;

    }

    @Override
    public String toString() {

        return String.valueOf(this.typeCode);

    }
}
