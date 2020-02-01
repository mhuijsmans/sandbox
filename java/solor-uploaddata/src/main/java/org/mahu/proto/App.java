package org.mahu.proto;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

/**
 * Used source: https://lucene.apache.org/solr/guide/8_3/using-solrj.html
 */
public class App {

	private static final String SOLR_CORE_SGSE = SolrUtils.SOLR_CORE_SGSE;

	public static void main(String[] args) throws SolrServerException, IOException {
//		querySgseCore();
//		addDocumentUsingMap();
//		querySgseCore();
//		querySgseCoreUsingBeans();
//		addDocumentUsingBean();
//
//		SolrUtils.deleteAllRecordsInSgseCore();
//		querySgseCore();
//		addDocumentUsingMap();
//		addDocumentUsingBean();
//		querySgseCoreUsingBeans();

		testSearchQuerySgseCore();
	}

	private static void querySgseCore() throws SolrServerException, IOException {
		final Map<String, String> queryParamMap = new HashMap<String, String>();
		// query
		queryParamMap.put("q", "*:*");
		// list of fields to return
		queryParamMap.put("fl", "id, name");
		queryParamMap.put("sort", "id asc");

		final SolrDocumentList documents = SolrUtils.querySgseCore(queryParamMap);

		System.out.println("NUM_INDEXED_DOCUMENTS=" + documents.getNumFound());
		for (SolrDocument document : documents) {
			final String id = (String) document.getFirstValue("id");
			final String name = (String) document.getFirstValue("name");

			System.out.println(" id=" + id + " name=" + name);

//			System.out.println("id=" + document.getFieldNames().contains("id"));
//			System.out.println("name=" + document.getFieldNames().contains("name"));
		}
	}

	private static void querySgseCoreUsingBeans() throws SolrServerException, IOException {
		final SolrClient client = SolrUtils.getSolrClient();

		final SolrQuery query = new SolrQuery("*:*");
		query.addField("id");
		query.addField("name");
		query.setSort("id", ORDER.asc);
		final int numResultsToReturn = 5;
		query.setRows(numResultsToReturn);

		final QueryResponse response = client.query(SOLR_CORE_SGSE, query);
		System.out.println(" responseStatus=" + response.getStatus());
		final List<ErrorInfos> products = response.getBeans(ErrorInfos.class);

		System.out.println("NUM_INDEXED_DOCUMENTS=" + products.size());
		for (ErrorInfos document : products) {
			System.out.println(" id=" + document.id + " name=" + document.name);
		}
	}

	private static void testSearchQuerySgseCore() throws SolrServerException, IOException {
		final SolrClient client = SolrUtils.getSolrClient();

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.set("defType", "edismax");
		solrQuery.set("q", "7000"); // ie. "blue the dress"
		solrQuery.set("qf", "ErrorId Cause Action");
		final int numResultsToReturn = 5;
		solrQuery.setRows(numResultsToReturn);

		final QueryResponse response = client.query(SOLR_CORE_SGSE, solrQuery);
		System.out.println(" responseStatus=" + response.getStatus());
		System.out.println(" responseSize=" + response.getResults().size());

	}

	private static void addDocumentUsingMap() throws SolrServerException, IOException {
		final SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", UUID.randomUUID().toString());
		doc.addField("name", "Amazon Kindle Paperwhite");
		// Both array and list work
		// doc.addField("action", new String[] {"action1", "action2"});
		doc.addField("action", toList("action1", "action2"));

		SolrUtils.addDocumentUsingMap(doc);
	}

	private static List<String> toList(String... strings) {
		return Arrays.asList(strings);
	}

	private static void addDocumentUsingBean() throws SolrServerException, IOException {
		final SolrClient client = SolrUtils.getSolrClient();
		final ErrorInfo kindle = new ErrorInfo(UUID.randomUUID().toString(), "Amazon Kindle Paperwhite");
		final UpdateResponse response = client.addBean(SOLR_CORE_SGSE, kindle);
		System.out.println(" responseStatus=" + response.getStatus());
		// Indexed documents must be committed
		client.commit(SOLR_CORE_SGSE);
	}

}
