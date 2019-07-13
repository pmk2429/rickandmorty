package com.delivery.rickandmorty;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delivery.rickandmorty.adapters.CharacterRecyclerViewAdapter;
import com.delivery.rickandmorty.adapters.ICharacterItemClickListener;
import com.delivery.rickandmorty.utils.ConnectivityHelper;
import com.delivery.rickandmorty.utils.StringUtils;
import com.delivery.rickandmorty.viewmodels.CharacterListViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CharacterListActivity extends BaseActivity implements ICharacterItemClickListener {

  private static final String TAG = "CharacterListActivity";

  @BindView(R.id.character_recycler_view)
  RecyclerView mCharacterRecyclerView;

  @BindView(R.id.network_unavailable)
  TextView mNetworkUnavailable;

  private CharacterListViewModel mCharacterListViewModel;
  private CharacterRecyclerViewAdapter mCharactersAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.character_list_activity);
    super.onCreateDrawer();
    ButterKnife.bind(this);

    mCharacterListViewModel = ViewModelProviders.of(this).get(CharacterListViewModel.class);

    initCharacterRecyclerView();
    subscribeObservers();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (ConnectivityHelper.isConnectedToNetwork(this)) {
      fetchCharacterList();
      mCharacterRecyclerView.setVisibility(View.VISIBLE);
    } else {
      mNetworkUnavailable.setVisibility(View.VISIBLE);
      mCharacterRecyclerView.setVisibility(View.GONE);
      //Toast.makeText(this, "Network Unavailable", Toast.LENGTH_SHORT);
    }
  }

  private void subscribeObservers() {
    mCharactersAdapter.displayLoadingIndicator();
    mCharacterListViewModel.getCharacters().observe(this, rmCharacters -> {
      if (rmCharacters != null) {
        if (mCharacterListViewModel.isViewingCharacters()) {
          mCharacterListViewModel.setIsMakingNetworkCall(false);
          mCharactersAdapter.setcharacters(rmCharacters);
        }
      }
    });

    mCharacterListViewModel.isNetworkQueryExhausted().observe(this, isExhausted -> {
      Log.d(TAG, "onChanged: the network call exhausted..." + isExhausted);
      if (isExhausted) {
        mCharactersAdapter.setQueryExhausted();
      }
    });
  }

  private void initCharacterRecyclerView() {
    mCharactersAdapter = new CharacterRecyclerViewAdapter(this);
    mCharacterRecyclerView.setAdapter(mCharactersAdapter);
    mCharacterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
    itemDecorator.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycler_divider));
    if (!mCharactersAdapter.isLoading()) {
      mCharacterRecyclerView.addItemDecoration(itemDecorator);
    }

    // get next page of results using pagination
    mCharacterRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        if (!mCharacterRecyclerView.canScrollVertically(1)) {
          // get next page results
          mCharacterListViewModel.searchNextPage();
        }
      }
    });
  }

  // initial fetch - runner call to fetch first set of data from API
  private void fetchCharacterList() {
    mCharacterListViewModel.fetchAllCharacters(1);
  }

  @Override
  public void onBackPressed() {
    if (mCharacterListViewModel.onBackPressed()) {
      super.onBackPressed();
    }
  }

  @Override
  public void onCharacterClick(int adapterPosition) {
    Intent characterDetailIntent = new Intent(this, CharacterDetailActivity.class);
    characterDetailIntent.putExtra("rmCharacter", mCharactersAdapter.getSelectedCharacter(adapterPosition));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.character_list_menu, menu);

    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
    SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    searchView.setQueryHint("Rick or Smith...");
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, query);
        // make an API call and fetch list of characters that matches DESC, NAME and all other details
        mCharactersAdapter.displayLoadingIndicator();
        // initial fetch - for filter characters
        mCharacterListViewModel.searchCharactersByName(query, 1);
        searchView.clearFocus();
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        if (StringUtils.isNullOrBlank(newText)) {
          Log.d(TAG, "Query cleared");
          fetchCharacterList();
        }
        return false;
      }
    });

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_search) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
