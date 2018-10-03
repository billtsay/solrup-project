package com.solrup.gora.jackson.query;

import com.solrup.gora.jackson.store.JacksonStore;
import org.apache.gora.persistency.impl.PersistentBase;
import org.apache.gora.query.impl.QueryBase;

public class JacksonQuery<K, T extends PersistentBase> extends QueryBase<K,T> {

  public JacksonQuery() {
    super(null);
  }
  
  public JacksonQuery(JacksonStore<K,T> dataStore) {
    super(dataStore);
  }
  
}
