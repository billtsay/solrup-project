package com.solrup.gora.jackson.store;

import java.io.IOException;

import org.apache.gora.persistency.impl.PersistentBase;
import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.query.impl.FileSplitPartitionQuery;
import org.apache.gora.store.impl.FileBackedDataStoreBase;
import org.apache.gora.util.OperationNotSupportedException;
import org.apache.hadoop.conf.Configurable;

public class JacksonStore<K, T extends PersistentBase> extends FileBackedDataStoreBase<K, T> implements Configurable {

	@Override
	public String getSchemaName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T get(K key, String[] fields) {
	    throw new OperationNotSupportedException();
	}

	@Override
	public void put(K key, T obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean delete(K key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long deleteByQuery(Query<K, T> query) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Query<K, T> newQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Result<K, T> executeQuery(Query<K, T> query) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Result<K, T> executePartial(FileSplitPartitionQuery<K, T> query) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
