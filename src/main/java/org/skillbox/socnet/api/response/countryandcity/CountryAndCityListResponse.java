package org.skillbox.socnet.api.response.countryandcity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CountryAndCityListResponse {

    private List<CountryAndCityResponse> data;
}