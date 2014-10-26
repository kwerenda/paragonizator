package pl.bajorekp.paragonizator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bogna on 26/10/14.
 */
public class OptimizedShoppingListItemPOJO implements Serializable {

    @JsonProperty("shop")
    public ShopPOJO shop;

    @JsonProperty("products")
    public ArrayList<ProductPOJO> products;
//    public ArrayList<String> products;

}
