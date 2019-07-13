package com.delivery.rickandmorty.models;


import android.os.Parcel;
import android.os.Parcelable;

public class RMCharacter implements Parcelable {
  private int id;
  private String name;
  private String status;
  private String species;
  private String type;
  private String gender;
  private String image;
  private String url;
  private String created;

  public RMCharacter() {
  }

  public RMCharacter(int id, String name, String status, String species, String type, String gender, String image, String url, String created) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.species = species;
    this.type = type;
    this.gender = gender;
    this.image = image;
    this.url = url;
    this.created = created;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSpecies() {
    return species;
  }

  public void setSpecies(String species) {
    this.species = species;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public static Creator<RMCharacter> getCREATOR() {
    return CREATOR;
  }

  protected RMCharacter(Parcel in) {
    id = in.readInt();
    name = in.readString();
    status = in.readString();
    species = in.readString();
    type = in.readString();
    gender = in.readString();
    image = in.readString();
    url = in.readString();
    created = in.readString();
  }

  public static final Creator<RMCharacter> CREATOR = new Creator<RMCharacter>() {
    @Override
    public RMCharacter createFromParcel(Parcel in) {
      return new RMCharacter(in);
    }

    @Override
    public RMCharacter[] newArray(int size) {
      return new RMCharacter[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(name);
    dest.writeString(status);
    dest.writeString(species);
    dest.writeString(type);
    dest.writeString(gender);
    dest.writeString(image);
    dest.writeString(url);
    dest.writeString(created);
  }
}
