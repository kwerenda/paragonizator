package pl.bajorekp.paragonizator.POJOS;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by bogna on 26/10/14.
 */
public class ProductPOJO implements Serializable{

    @JsonProperty("name")
    public String name;

    @JsonProperty("gtin")
    public BigInteger gtin;

    @JsonProperty("price")
    public Double price;
}

