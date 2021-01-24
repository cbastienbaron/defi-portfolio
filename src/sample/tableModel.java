package sample;

public class tableModel {

    private String Date;
    private String Operation;
    private String Amount;
    private String Cryptocurrency;
    private String FiatValue;
    private String FiatCurrency;
    private String BlockHash;
    private String BlockHeight;
    private String PoolID;

    private String Owner;

    public tableModel(String date, String operation, String amount, String cryptocurrency, String fiatValue, String fiatCurrency, String blockHash, String blockHeight, String poolID,String owner) {
        Date = date;
        Operation = operation;
        Amount = amount;
        Cryptocurrency = cryptocurrency;
        FiatValue = fiatValue;
        FiatCurrency = fiatCurrency;
        BlockHash = blockHash;
        BlockHeight = blockHeight;
        PoolID = poolID;
        Owner = owner;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getOperation() {
        return Operation;
    }

    public void setOperation(String operation) {
        Operation = operation;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCryptocurrency() {
        return Cryptocurrency;
    }

    public void setCryptocurrency(String cryptocurrency) {
        Cryptocurrency = cryptocurrency;
    }

    public String getFiatValue() {
        return FiatValue;
    }

    public void setFiatValue(String fiatValue) {
        FiatValue = fiatValue;
    }

    public String getFiatCurrency() {
        return FiatCurrency;
    }

    public void setFiatCurrency(String fiatCurrency) {
        FiatCurrency = fiatCurrency;
    }

    public String getBlockHash() {
        return BlockHash;
    }

    public void setBlockHash(String blockHash) {
        BlockHash = blockHash;
    }

    public String getBlockHeight() {
        return BlockHeight;
    }

    public void setBlockHeight(String blockHeight) {
        BlockHeight = blockHeight;
    }

    public String getPoolID() {
        return PoolID;
    }

    public void setPoolID(String poolID) {
        PoolID = poolID;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }


}
