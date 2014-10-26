package pl.bajorekp.paragonizator.POJOS;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bogna on 26/10/14.
 */
public class ReceiptPOJO implements Serializable {

    @JsonProperty("shop")
    public ShopOnReceiptPOJO shop;

    @JsonProperty("products")
    public ArrayList<ProductPOJO> products;



}
