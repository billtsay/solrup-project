package com.solrup.persistence.store;

import java.io.IOException;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.mapred.FsInput;
import org.apache.gora.avro.query.DataFileAvroResult;
import org.apache.gora.persistency.impl.PersistentBase;
import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.query.impl.FileSplitPartitionQuery;
import org.apache.gora.util.GoraException;
import org.apache.gora.util.OperationNotSupportedException;
import org.apache.hadoop.fs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFileJacksonStore<K, T extends PersistentBase> extends JacksonStore<K, T> {

  public static final Logger LOG = LoggerFactory.getLogger(JacksonStore.class);
  
  public DataFileJacksonStore() {
  }
  
  private DataFileWriter<T> writer;
  
  @Override
  public T get(K key, String[] fields) throws GoraException {
    throw new OperationNotSupportedException(
        "Avro DataFile's does not support indexed retrieval");
  }

  @Override
  public void put(K key, T obj) throws GoraException {
    try{
      getWriter().append(obj);
    } catch (Exception e) {
      throw new GoraException(e);
    }
  }

  private DataFileWriter<T> getWriter() throws IOException {
    if(writer == null) {
      writer = new DataFileWriter<>(getDatumWriter());
      writer.create(schema, getOrCreateOutputStream());
    }
    return writer;
  }
  
  @Override
  protected Result<K, T> executeQuery(Query<K, T> query) throws IOException {
      return new DataFileAvroResult<>(this, query
          , createReader(createFsInput()));
  }
 
  @Override
  protected Result<K,T> executePartial(FileSplitPartitionQuery<K,T> query) throws IOException {
      FsInput fsInput = createFsInput();
      DataFileReader<T> reader = createReader(fsInput);
      return new DataFileAvroResult<>(this, query, reader, fsInput
          , query.getStart(), query.getLength());
  }
  
  private DataFileReader<T> createReader(FsInput fsInput) throws IOException {
    return new DataFileReader<>(fsInput, getDatumReader());
  }
  
  private FsInput createFsInput() throws IOException {
    Path path = new Path(getInputPath());
    return new FsInput(path, getConf());
  }
  
  @Override
  public void flush() throws GoraException {
    try{
      super.flush();
      if(writer != null) {
        writer.flush();
      }
    } catch (GoraException e) {
      throw e;
    } catch (Exception e) {
      throw new GoraException(e);
    }
  }
  
  @Override
  public void close() {
    try{
      if(writer != null)  
        writer.close(); //hadoop 0.20.2 HDFS streams do not allow 
                        //to close twice, so close the writer first 
      writer = null;
      super.close();
    } catch(IOException ex){
      LOG.error(ex.getMessage(), ex);
    }
  }
}
