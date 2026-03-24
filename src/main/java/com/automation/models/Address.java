package com.automation.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @JsonProperty("street")
    private String street;
    
    @JsonProperty("suite")
    private String suite;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("zipcode")
    private String zipcode;
    
    @JsonProperty("geo")
    private Geo geo;
}
