package pl.bajorekp.paragonizator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by bogna on 26/10/14.
 */
public class ShopPOJO implements Serializable{

    @JsonProperty("name")
    public String name;

    @JsonProperty("longitude")
    public Double longitude;

    @JsonProperty("latitude")
    public Double latitude;
}
