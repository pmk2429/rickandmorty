package com.delivery.rickandmorty;

import android.os.Bundle;

public class CharacterDetailActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_character_detail);
    super.onCreateDrawer();
  }
}
