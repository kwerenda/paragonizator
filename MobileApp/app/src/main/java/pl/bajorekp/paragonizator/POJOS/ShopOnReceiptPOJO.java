package pl.bajorekp.paragonizator.POJOS;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by bogna on 26/10/14.
 */
public class ShopOnReceiptPOJO {

    @JsonProperty("name")
    public String name;

    @JsonProperty("nip")
    public String nip;

    @JsonProperty("location")
    public String location;

}
