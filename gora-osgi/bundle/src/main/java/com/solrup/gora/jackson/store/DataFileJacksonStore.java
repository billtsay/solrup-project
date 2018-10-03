package com.solrup.gora.jackson.store;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solrup.gora.jackson.query.DataFileJacksonResult;
import org.apache.gora.persistency.impl.PersistentBase;
import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.query.impl.FileSplitPartitionQuery;
import org.apache.gora.util.GoraException;
import org.apache.gora.util.OperationNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFileJacksonStore<K, T extends PersistentBase> extends JacksonStore<K, T> {

	public static final Logger LOG = LoggerFactory.getLogger(JacksonStore.class);
	public static final String FILE_NAME = "instance.json";

	public DataFileJacksonStore(Path path, String fileName) {
		this.path = path;
		this.fileName = fileName;
	}

	public DataFileJacksonStore(Path path) {
		this.path = path;
		this.fileName = FILE_NAME;
	}

	private String fileName;
	private Path path;

	@Override
	public T get(K key, String[] fields) {
		throw new OperationNotSupportedException("Avro DataFile's does not support indexed retrieval");
	}

	private Path getFile(K key) throws IOException {
		Path p = Paths.get(path.toString(), key.toString());
		if (!Files.exists(p)) Files.createDirectory(p);
		if (!Files.isDirectory(p)) throw new IOException();
		Path f = Paths.get(p.toString(), fileName);
		if (!Files.exists(f)) Files.createFile(f);
		if (!Files.isRegularFile(f)) throw new IOException();
		
		return f;
	}
	
	@Override
	public void put(K key, T obj) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(getFile(key).toFile(), obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Result<K, T> executeQuery(Query<K, T> query) throws IOException {
		return new DataFileJacksonResult<>(this, query, createReader(createFsInput()));
	}

	@Override
	protected Result<K, T> executePartial(FileSplitPartitionQuery<K, T> query) throws IOException {
		FsInput fsInput = createFsInput();
		DataFileReader<T> reader = createReader(fsInput);
		return new DataFileAvroResult<>(this, query, reader, fsInput, query.getStart(), query.getLength());
	}

	@Override
	public void flush() throws GoraException {
		try {
			super.flush();
			if (writer != null) {
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
		try {
			if (writer != null)
				writer.close(); // hadoop 0.20.2 HDFS streams do not allow
								// to close twice, so close the writer first
			writer = null;
			super.close();
		} catch (IOException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}
}
