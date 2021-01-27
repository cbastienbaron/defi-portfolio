package sample;

import javafx.beans.property.*;

public class TransactionModel {

    private final StringProperty ownerProperty = new SimpleStringProperty("");
    private final IntegerProperty blockHeightProperty = new SimpleIntegerProperty(0);
    private final StringProperty blockHashProperty = new SimpleStringProperty("");
    private final LongProperty blockTimeProperty = new SimpleLongProperty(0L);
    private final StringProperty typeProperty = new SimpleStringProperty("");
    private final StringProperty poolIDProperty = new SimpleStringProperty("");
    private final ObjectProperty<String[]> amountProperty = new SimpleObjectProperty<>();
    private final StringProperty cryptoCurrencyProperty = new SimpleStringProperty("");
    private final DoubleProperty cryptoValueProperty = new SimpleDoubleProperty(0.0);
    private final DoubleProperty fiatValueProperty = new SimpleDoubleProperty(0.0);
    private final StringProperty fiatCurrencyProperty = new SimpleStringProperty("");
    private final StringProperty txIDProperty = new SimpleStringProperty("");

    public TransactionModel(Long blockTime, String owner, String type, String[] amounts, String blockHash, int blockHeight, String poolID, String txid, TransactionController transactionController) {
        this.blockTimeProperty.set(blockTime);
        this.ownerProperty.set(owner);
        this.typeProperty.set(type);
        this.amountProperty.set(amounts);
        this.blockHashProperty.set(blockHash);
        this.blockHeightProperty.set(blockHeight);
        this.poolIDProperty.set(poolID);
        this.cryptoValueProperty.set(Double.parseDouble(transactionController.splitCoinsAndAmounts(amounts[0])[0]));
        this.cryptoCurrencyProperty.set(transactionController.splitCoinsAndAmounts(amounts[0])[1]);
        this.txIDProperty.set(txid);
        this.fiatCurrencyProperty.set(transactionController.settingsController.selectedFiatCurrency.getValue());
        if(this.amountProperty.getValue()[0].split("@")[1].length() == 3)this.fiatValueProperty.set(this.cryptoValueProperty.getValue() * transactionController.coinPriceController.getPriceFromTimeStamp(this.amountProperty.getValue()[0].split("@")[1]+transactionController.settingsController.selectedFiatCurrency.getValue(),this.blockTimeProperty.getValue() * 1000L));
    }

    public void setOwner(String owner) {
        this.ownerProperty.set(owner);
    }

    public String getOwnerValue() {
        return ownerProperty.get();
    }

    public StringProperty getOwner() {
        return ownerProperty;
    }

    public void setBlockHeight(int blockHeight) {
        this.blockHeightProperty.set(blockHeight);
    }

    public int getBlockHeightValue() {
        return blockHeightProperty.get();
    }

    public IntegerProperty getBlockHeight() {
        return blockHeightProperty;
    }

    public void setBlockHash(String blockHash) {
        this.blockHashProperty.set(blockHash);
    }

    public StringProperty getBlockHash() {
        return blockHashProperty;
    }

    public String getBlockHashValue() {
        return blockHashProperty.get();
    }

    public void setBlockTime(Long blockTime) {
        this.blockTimeProperty.set(blockTime);
    }

    public LongProperty getBlockTime() {
        return blockTimeProperty;
    }

    public Long getBlockTimeValue() {
        return blockTimeProperty.get();
    }

    public void setType(String type) {
        this.typeProperty.set(type);
    }

    public StringProperty getType() {
        return typeProperty;
    }

    public String getTypeValue() {
        return typeProperty.get();
    }

    public String getPoolIDValue() {
        return poolIDProperty.get();
    }

    public StringProperty getPoolID() {
        return poolIDProperty;
    }


    public void setPoolID(String type) {
        this.poolIDProperty.set(type);
    }


    public ObjectProperty<String[]> getAmount() {
        return amountProperty;
    }

    public void setAmount(String[] amount) {
        this.amountProperty.set(amount);
    }

    public String[] getAmountValue() {
        return amountProperty.get();
    }

    public StringProperty getCrypto() {
        return cryptoCurrencyProperty;
    }

    public String getCryptoyValue() {
        return cryptoCurrencyProperty.get();
    }

    public void setCrypto(String currency) {
        this.cryptoCurrencyProperty.set(currency);
    }

    public String getFiatCurrencyValue() {
        return fiatCurrencyProperty.get();
    }

    public StringProperty getFiatCurrency() {
        return fiatCurrencyProperty;
    }

    public void setFiatCurrency(String currency) {
        this.fiatCurrencyProperty.set(currency);
    }

    public StringProperty getTxID() {
        return txIDProperty;
    }

    public String getTxIDValue() {
        return txIDProperty.get();
    }

    public void setTxID(String currency) {
        this.txIDProperty.set(currency);
    }

    public Double getCryptoValueValue() {
        return cryptoValueProperty.get();
    }

    public DoubleProperty getCryptoValue() {
        return cryptoValueProperty;
    }

    public void setCrypto(Double value) {
        this.cryptoValueProperty.set(value);
    }

    public DoubleProperty getFiat() {
        return fiatValueProperty;
    }

    public Double getFiatValueValue() {
        return fiatValueProperty.get();
    }

    public void setFiatValue(Double value) {
        this.fiatValueProperty.set(value);
    }
}

