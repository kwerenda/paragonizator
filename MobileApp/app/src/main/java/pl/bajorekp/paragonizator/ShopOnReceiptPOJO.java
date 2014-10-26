package pl.bajorekp.paragonizator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by bogna on 26/10/14.
 */
public class ShopOnReceiptPOJO implements Serializable {

    @JsonProperty("name")
    public String name;

    @JsonProperty("nip")
    public String nip;

    @JsonProperty("location")
    public String location;

}
