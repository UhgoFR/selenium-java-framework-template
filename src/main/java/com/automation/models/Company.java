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
public class Company {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("catchPhrase")
    private String catchPhrase;
    
    @JsonProperty("bs")
    private String bs;
}
