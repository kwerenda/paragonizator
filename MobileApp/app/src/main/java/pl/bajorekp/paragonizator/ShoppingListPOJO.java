package pl.bajorekp.paragonizator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by bogna on 26/10/14.
 */
public class ShoppingListPOJO {
    @JsonProperty("email")
    public String email;

    @JsonProperty("radius")
    public String radius;

    @JsonProperty("shopsLimit")
    public Integer shopsLimit;

    @JsonProperty("itemNames")
    public ArrayList<String> itemNames;
}
