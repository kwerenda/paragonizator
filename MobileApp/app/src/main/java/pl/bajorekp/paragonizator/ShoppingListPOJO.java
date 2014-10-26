package pl.bajorekp.paragonizator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bogna on 26/10/14.
 */
public class ShoppingListPOJO implements Serializable{
    @JsonProperty("email")
    public String email;

    @JsonProperty("radius")
    public Integer radius;

    @JsonProperty("shopsLimit")
    public Integer shopsLimit;

    @JsonProperty("itemNames")
    public ArrayList<String> itemNames;
}
