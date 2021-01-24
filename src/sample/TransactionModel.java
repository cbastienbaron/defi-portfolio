package sample;

import javafx.beans.property.*;

public class TransactionModel {

    public final StringProperty ownerProperty = new SimpleStringProperty("");
    public final IntegerProperty blockHeightProperty = new SimpleIntegerProperty(0);
    public final StringProperty blockHashProperty = new SimpleStringProperty("");
    public final LongProperty blockTimeProperty = new SimpleLongProperty(0L);
    public final StringProperty typeProperty = new SimpleStringProperty("");
    public final StringProperty poolIDProperty = new SimpleStringProperty("");
    public final ObjectProperty<String[]> amountProperty = new SimpleObjectProperty<>();
    public final StringProperty cryptoCurrencyProperty = new SimpleStringProperty("");
    public final DoubleProperty cryptoValueProperty = new SimpleDoubleProperty(0.0);
    public final DoubleProperty fiatValueProperty = new SimpleDoubleProperty(0.0);
    public final StringProperty fiatCurrencyProperty = new SimpleStringProperty("");


    public TransactionModel(Long blockTime, String owner, String type, String[] amounts, String blockHash, int blockHeight, String poolID,TransactionController transactionController) {
        this.blockTimeProperty.set(blockTime);
        this.ownerProperty.set(owner);
        this.typeProperty.set(type);
        this.amountProperty.set(amounts);
        this.blockHashProperty.set(blockHash);
        this.blockHeightProperty.set(blockHeight);
        this.poolIDProperty.set(poolID);
        this.cryptoValueProperty.set(Double.parseDouble(transactionController.splitCoinsAndAmounts(amounts[0])[0]));
        this.cryptoCurrencyProperty.set(transactionController.splitCoinsAndAmounts(amounts[0])[0]);

    }

    public void setOwnerProperty(String owner) {
        this.ownerProperty.set(owner);
    }

    public String getOwnerProperty() {
        return ownerProperty.get();
    }

    public void setBlockHeightProperty(int blockHeight) {
        this.blockHeightProperty.set(blockHeight);
    }

    public int getBlockHeightProperty() {
        return blockHeightProperty.get();
    }

    public void setBlockHashProperty(String blockHash) {
        this.blockHashProperty.set(blockHash);
    }

    public String getBlockHashProperty() {
        return blockHashProperty.get();
    }

    public void setBlockTimeProperty(Long blockTime) {
        this.blockTimeProperty.set(blockTime);
    }

    public Long getBlockTimeProperty() {
        return blockTimeProperty.get();
    }

    public void setTypeProperty(String type) {
        this.typeProperty.set(type);
    }

    public String getPoolIDProperty() {
        return poolIDProperty.get();
    }

    public void setPoolIDProperty(String type) {
        this.poolIDProperty.set(type);
    }

    public String getTypeProperty() {
        return typeProperty.get();
    }

    public void setAmountProperty(String[] amount) {
        this.amountProperty.set(amount);
    }

    public String[] getAmountProperty() {
        return amountProperty.get();
    }

    public String getCryptoCurrencyProperty() {
        return cryptoCurrencyProperty.get();
    }
    public void setCryptoCurrencyProperty(String currency) {
        this.cryptoCurrencyProperty.set(currency);
    }

    public String getFiatCurrencyProperty() {
        return fiatCurrencyProperty.get();
    }
    public void setFiatCurrencyProperty(String currency) {
        this.fiatCurrencyProperty.set(currency);
    }

    public Double getCryptoValueProperty() {
        return cryptoValueProperty.get();
    }
    public void setCryptoCurrencyProperty(Double value) {
        this.cryptoValueProperty.set(value);
    }

    public Double getFiatValueProperty() {
        return fiatValueProperty.get();
    }
    public void setFiatValueProperty(Double value) {
        this.fiatValueProperty.set(value);
    }
}

