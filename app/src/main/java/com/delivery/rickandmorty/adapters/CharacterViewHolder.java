package com.delivery.rickandmorty.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delivery.rickandmorty.R;

public class CharacterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  ImageView mCharacterImage;
  TextView mCharacterName;
  TextView mCharacterStatus;
  TextView mCharacterSpecies;
  TextView mCharacterType;

  private ICharacterItemClickListener mCharacterItemClickListener;

  public CharacterViewHolder(@NonNull View itemView, ICharacterItemClickListener characterItemClickListener) {
    super(itemView);
    mCharacterItemClickListener = characterItemClickListener;
    mCharacterImage = itemView.findViewById(R.id.character_image);
    mCharacterName = itemView.findViewById(R.id.character_name);
    mCharacterStatus = itemView.findViewById(R.id.character_status);
    mCharacterSpecies = itemView.findViewById(R.id.character_species);
    mCharacterType = itemView.findViewById(R.id.character_type);

    itemView.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    mCharacterItemClickListener.onCharacterClick(getAdapterPosition());
  }
}
