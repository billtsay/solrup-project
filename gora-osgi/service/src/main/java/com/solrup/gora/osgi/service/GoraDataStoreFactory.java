package com.solrup.gora.osgi.service;
import org.apache.gora.persistency.Persistent;
import org.apache.gora.store.DataStore;
import org.apache.gora.util.GoraException;

import javax.security.auth.login.Configuration;


public interface GoraDataStoreFactory {
    <D extends DataStore<K,T>, K, T extends Persistent> D createDataStore(Class<D> dataStoreClass
            , Class<K> keyClass, Class<T> persistent, Configuration conf) throws GoraException;
}
