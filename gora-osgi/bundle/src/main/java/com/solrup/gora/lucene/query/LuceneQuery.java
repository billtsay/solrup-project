package com.solrup.gora.lucene.query;

import java.util.UUID;

import org.apache.gora.filter.Filter;
import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.store.DataStore;

import com.solrup.gora.lucene.store.TextPersistent;

public class LuceneQuery implements Query<UUID, TextPersistent> {

	@Override
	public void setDataStore(DataStore<UUID, TextPersistent> dataStore) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DataStore<UUID, TextPersistent> getDataStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<UUID, TextPersistent> execute() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFields(String... fieldNames) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFilter(Filter<UUID, TextPersistent> filter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Filter<UUID, TextPersistent> getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocalFilterEnabled(boolean enable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLocalFilterEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setKey(UUID key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStartKey(UUID startKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEndKey(UUID endKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setKeyRange(UUID startKey, UUID endKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UUID getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UUID getStartKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UUID getEndKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTimestamp(long timestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStartTime(long startTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEndTime(long endTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTimeRange(long startTime, long endTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getStartTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getEndTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLimit(long limit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

}
