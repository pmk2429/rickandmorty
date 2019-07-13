package com.delivery.rickandmorty.utils;

import com.delivery.rickandmorty.models.RMCharacter;
import com.delivery.rickandmorty.networking.response.Result;

import java.util.function.Function;

public class Converter {

  public static Function<Result, RMCharacter> transform = apiCharacter -> {
    if (apiCharacter != null) {
      RMCharacter rmCharacter = new RMCharacter();
      rmCharacter.setId(apiCharacter.getId());
      rmCharacter.setName(apiCharacter.getName());
      rmCharacter.setImage(apiCharacter.getImage());
      rmCharacter.setGender(apiCharacter.getGender());
      rmCharacter.setSpecies(apiCharacter.getSpecies());
      rmCharacter.setStatus(apiCharacter.getStatus());
      rmCharacter.setType(apiCharacter.getType());
      rmCharacter.setCreated(apiCharacter.getCreated());
      rmCharacter.setUrl(apiCharacter.getUrl());

      return rmCharacter;
    }
    return null;
  };
}
