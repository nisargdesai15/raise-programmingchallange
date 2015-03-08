package desai.com.raise;

/**
 * Created by ndesai on 3/4/15.
 */


//GiftCard Class to store Gift Card Details
public class GiftCard {

    private String mBrand;
    private String mValue;
    private String mCost;

    public String getBrand() {
        return mBrand;
    }

    public void setBrand(String brand) {
        mBrand = brand;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public String getCost() {
        return mCost;
    }

    public void setCost(String cost) {
        mCost = cost;
    }
}
