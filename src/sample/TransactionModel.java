package sample;

public class TransactionModel implements java.io.Serializable{
    public String owner;
    public int blockHeight;
    public String blockHash;
    public long blockTime;
    public String type;
    public String poolID;
    public String amounts[];

    public TransactionModel() {

    }
}
