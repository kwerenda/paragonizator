package pl.bajorekp.paragonizator.POJOS;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bogna on 26/10/14.
 */
public class ProductOnReceiptPOJO {

    @JsonProperty("id")
    public String id;

    @JsonProperty("price")
    public Double price;

    @JsonProperty("quantity")
    public Double quantity;

    @JsonProperty("name")
    public String name;

    @JsonProperty("unit_price")
    public Double pricePerUnit;


}
