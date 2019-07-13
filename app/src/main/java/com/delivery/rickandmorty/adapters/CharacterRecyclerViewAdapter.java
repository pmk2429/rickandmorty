package com.delivery.rickandmorty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.delivery.rickandmorty.R;
import com.delivery.rickandmorty.models.RMCharacter;

import java.util.ArrayList;
import java.util.List;

public class CharacterRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  // View Holder Type
  private static final int CHARACTER_LIST_TYPE = 1;
  private static final int LOADING_INDICATOR_TYPE = 2;

  private static final String LOADING = "LOADING...";
  private static final String EXHAUSTED = "EXHAUSTED..."; // for when network query is exhausted

  private List<RMCharacter> mCharacters;
  private ICharacterItemClickListener mCharacterItemClickListener;

  public CharacterRecyclerViewAdapter(ICharacterItemClickListener characterItemClickListener) {
    mCharacterItemClickListener = characterItemClickListener;
    mCharacters = new ArrayList<>();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view;
    Context context = parent.getContext();

    switch (viewType) {
      case CHARACTER_LIST_TYPE: {
        view = LayoutInflater.from(context).inflate(R.layout.character_recycler_list_item, parent, false);
        return new CharacterViewHolder(view, mCharacterItemClickListener);
      }

      case LOADING_INDICATOR_TYPE: {
        view = LayoutInflater.from(context).inflate(R.layout.layout_loading_list_item, parent, false);
        return new LoadingIndicatorViewHolder(view);
      }

      default: {
        view = LayoutInflater.from(context).inflate(R.layout.character_recycler_list_item, parent, false);
        return new CharacterViewHolder(view, mCharacterItemClickListener);
      }
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    int itemViewType = getItemViewType(position);

    if (itemViewType == CHARACTER_LIST_TYPE) {
      CharacterViewHolder characterViewHolder = (CharacterViewHolder) holder;

      RMCharacter rmCharacter = mCharacters.get(position);

      String coverImageUrl = rmCharacter.getImage();
      RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);
      Glide.with(characterViewHolder.itemView.getContext())
              .setDefaultRequestOptions(requestOptions)
              .load(coverImageUrl)
              .into(characterViewHolder.mCharacterImage);

      characterViewHolder.mCharacterName.setText(rmCharacter.getName());
      characterViewHolder.mCharacterStatus.setText(rmCharacter.getStatus());
      characterViewHolder.mCharacterType.setText(rmCharacter.getType());
      characterViewHolder.mCharacterSpecies.setText(rmCharacter.getSpecies());
    }
  }

  @Override
  public int getItemViewType(int position) {
    RMCharacter rmCharacter = mCharacters.get(position);
    if (rmCharacter.getName().equals(LOADING)) {
      return LOADING_INDICATOR_TYPE;
    } else if (position == 0) {
      return CHARACTER_LIST_TYPE;
    } else if (position == mCharacters.size() - 1
            && position != 0
            && !rmCharacter.getName().equals(EXHAUSTED)) {
      return LOADING_INDICATOR_TYPE;
    } else {
      return CHARACTER_LIST_TYPE;
    }
  }

  @Override
  public int getItemCount() {
    return mCharacters.size();
  }

  public boolean isLoading() {
    if (mCharacters != null) {
      if (mCharacters.size() > 0) {
        if (mCharacters.get(mCharacters.size() - 1).getName().equals(LOADING)) {
          return true;
        }
      }
    }
    return false;
  }

  public void displayLoadingIndicator() {
    if (!isLoading()) {
      RMCharacter dummyCharacter = new RMCharacter();
      dummyCharacter.setName(LOADING);
      List<RMCharacter> loadingCharacterList = new ArrayList<>();
      loadingCharacterList.add(dummyCharacter);
      mCharacters = loadingCharacterList;
      notifyDataSetChanged();
    }
  }

  private void hideLoadingIndicator() {
    if (isLoading()) {
      for (RMCharacter rmCharacter : mCharacters) {
        if (rmCharacter.getName().equals(LOADING)) {
          mCharacters.remove(rmCharacter);
        }
      }
      notifyDataSetChanged();
    }
  }

  public RMCharacter getSelectedCharacter(int position) {
    if (mCharacters != null && mCharacters.size() > 0) {
      return mCharacters.get(position);
    }
    return null;
  }

  public void setcharacters(List<RMCharacter> characters) {
    mCharacters = characters;
    notifyDataSetChanged();
  }

  public void setQueryExhausted() {
    hideLoadingIndicator();
    // create a dummy characters EXHAUSTED... for RecyclerView to understand and render accordingly
    RMCharacter exhaustedDummyCharacter = new RMCharacter();
    exhaustedDummyCharacter.setName(EXHAUSTED);
    mCharacters.add(exhaustedDummyCharacter);
    notifyDataSetChanged();
  }
}
