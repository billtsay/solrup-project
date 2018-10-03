package com.solrup.gora.jackson.query;

import java.io.IOException;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableInput;
import org.apache.gora.persistency.impl.PersistentBase;
import org.apache.gora.query.Query;
import org.apache.gora.query.impl.ResultBase;
import org.apache.gora.store.DataStore;

/**
 * An Avro {@link DataFileReader} backed Result.
 */
public class DataFileJacksonResult<K, T extends PersistentBase> extends ResultBase<K, T> {

  private SeekableInput in;
  private MappingIterator<T> iterator;
  private long start;
  private long end;
  
  public DataFileJacksonResult(DataStore<K, T> dataStore, Query<K, T> query, MappingIterator<T> iterator) throws IOException {
    this(dataStore, query, mapper, null, 0, 0);
  }
  
  public DataFileAvroResult(DataStore<K, T> dataStore, Query<K, T> query, MappingIterator<T> iterator, SeekableInput in,
                            long start, long length) throws IOException {
    super(dataStore, query);
    this.iterator = iterator;
    this.start = start;
    this.end = start + length;
    this.in = in;
    if(start > 0) {
      reader.sync(start);
    }
  }

  @Override
  public void close() throws IOException {
	  if(reader != null)
		  reader.close();
	  reader = null;
  }

  @Override
  public float getProgress() throws IOException {
    if (end == start) {
      return 0.0f;
    } else {
      return Math.min(1.0f, (in.tell() - start) / (float)(end - start));
    }
  }

  @Override
  public boolean nextInner() throws IOException {
    if (!reader.hasNext())
      return false;
    if(end > 0 && reader.pastSync(end))
      return false;
    persistent = reader.next(persistent);
    return true;
  }

  @Override
  public int size() {
    return (int) (end - start);
  }
}
