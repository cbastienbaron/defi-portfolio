package sample;

import javafx.beans.property.*;

public class PoolPairModel {

    private final LongProperty blockTimeProperty = new SimpleLongProperty(0L);
    private final StringProperty typeProperty = new SimpleStringProperty("");
    private final DoubleProperty fiatValueProperty = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cryptoValue1Property = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cryptoValue2Property = new SimpleDoubleProperty(0.0);
    private final StringProperty cryptoPoolPair = new SimpleStringProperty("");

    public PoolPairModel(Long blockTime,String type, double fiatValue, double cryptoValue1, double cryptoValue2, String poolPair){
        setBlockTime(blockTime);
        setType(type);
        setFiat(fiatValue);
        setCrypto1(cryptoValue1);
        setCrypto2(cryptoValue2);
        setPoolPair(poolPair);
    }

    public void setBlockTime(Long blockTime) {
        this.blockTimeProperty.set(blockTime);
    }

    public LongProperty getBlockTime() {
        return blockTimeProperty;
    }

    public Long getBlockTimeValue() {
        return blockTimeProperty.getValue();
    }

    public void setType(String type) {
        this.typeProperty.set(type);
    }

    public StringProperty getType() {
        return typeProperty;
    }

    public String getTypeValue() {
        return typeProperty.getValue();
    }

    public Double getCryptoValueValue1() {
        return cryptoValue1Property.getValue();
    }

    public DoubleProperty getCryptoValue1() {
        return cryptoValue1Property;
    }

    public void setCrypto1(Double value) {
        this.cryptoValue1Property.set(value);
    }

    public Double getCryptoValueValue2() {
        return cryptoValue2Property.getValue();
    }

    public DoubleProperty getCryptoValue2() {
        return cryptoValue2Property;
    }

    public void setCrypto2(Double value) {
        this.cryptoValue2Property.set(value);
    }

    public Double getFiatValueValue() {
        return fiatValueProperty.getValue();
    }

    public DoubleProperty getFiatValue() {
        return fiatValueProperty;
    }

    public void setFiat(Double value) {
        this.fiatValueProperty.set(value);
    }

    public void setPoolPair(String pair) {
        this.cryptoPoolPair.set(pair);
    }

    public StringProperty getPoolPair() {
        return cryptoPoolPair;
    }

    public String getPoolPairValue() {
        return cryptoPoolPair.getValue();
    }


}