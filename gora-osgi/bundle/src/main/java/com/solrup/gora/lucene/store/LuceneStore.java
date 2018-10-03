package com.solrup.gora.lucene.store;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.gora.persistency.BeanFactory;
import org.apache.gora.query.PartitionQuery;
import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.store.DataStore;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;

public class LuceneStore implements DataStore<UUID, TextPersistent> {
	public final String INDEX_DIRECTORY = "lucene.index.directory";
	private static StandardAnalyzer analyzer = new StandardAnalyzer();

	private Path indexDir;
	IndexWriterConfig config = new IndexWriterConfig(analyzer);
	private ArrayList<File> queue = new ArrayList<File>();

	@Override
	public void initialize(Class<UUID> keyClass, Class<TextPersistent> persistentClass, Properties properties) {
		indexDir = Paths.get(properties.getProperty(INDEX_DIRECTORY));
	}

	@Override
	public void setKeyClass(Class<UUID> keyClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<UUID> getKeyClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPersistentClass(Class<TextPersistent> persistentClass) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<TextPersistent> getPersistentClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSchemaName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createSchema() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSchema() {
		// TODO Auto-generated method stub

	}

	@Override
	public void truncateSchema() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean schemaExists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public UUID newKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextPersistent newPersistent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextPersistent get(UUID key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TextPersistent get(UUID key, String[] fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(UUID key, TextPersistent obj) {
		Document doc = new Document();
		doc.add(new StringField("content", obj.getText(), Field.Store.YES));
		doc.add(new StringField("uuid", key.toString(), Field.Store.YES));
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(FSDirectory.open(indexDir), config);
			writer.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
				}
		}
	}

	@Override
	public boolean delete(UUID key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long deleteByQuery(Query<UUID, TextPersistent> query) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Result<UUID, TextPersistent> execute(Query<UUID, TextPersistent> query) {
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(indexDir));
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(5);
			org.apache.lucene.search.Query q = new QueryParser("content", analyzer).parse(query);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			// 4. display results
			System.out.println("Found " + hits.length + " hits.");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				System.out.println((i + 1) + ". " + d.get("uuid") + " score=" + hits[i].score);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Query<UUID, TextPersistent> newQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PartitionQuery<UUID, TextPersistent>> getPartitions(Query<UUID, TextPersistent> query)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBeanFactory(BeanFactory<UUID, TextPersistent> beanFactory) {
		// TODO Auto-generated method stub

	}

	@Override
	public BeanFactory<UUID, TextPersistent> getBeanFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
