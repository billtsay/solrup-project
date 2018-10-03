package com.solrup.gora.lucene.store;

import java.util.List;

import org.apache.avro.Schema.Field;
import org.apache.gora.persistency.Persistent;
import org.apache.gora.persistency.Tombstone;

public class TextPersistent implements Persistent {
	private String text = null;
	
	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void clearDirty() {
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean isDirty(int fieldIndex) {
		return false;
	}

	@Override
	public boolean isDirty(String field) {
		return false;
	}

	@Override
	public void setDirty() {
	}

	@Override
	public void setDirty(int fieldIndex) {
	}

	@Override
	public void setDirty(String field) {
	}

	@Override
	public void clearDirty(int fieldIndex) {
	}

	@Override
	public void clearDirty(String field) {
	}

	@Override
	public Tombstone getTombstone() {
		return null;
	}

	@Override
	public List<Field> getUnmanagedFields() {
		return null;
	}

	@Override
	public Persistent newInstance() {
		return new TextPersistent();
	}

}
