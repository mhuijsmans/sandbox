package org.mahu.proto;

import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;

public class SolrUtils {
	
	private static final String SOLR_URL = "http://130.144.225.242:8983/solr";
	public static final String SOLR_CORE_SGSE = "sgse";	
	
	public static void deleteAllRecordsInSgseCore() throws SolrServerException, IOException {
		// for the delete the URL shall contain the core
		final SolrClient client = getSolSgseCoreClient();
		final UpdateResponse response = client.deleteByQuery("*:*");
		System.out.println(" responseStatus=" + response.getStatus());
	}
	
	public static void addDocumentUsingMap(final SolrInputDocument doc) throws SolrServerException, IOException {
		final SolrClient client = getSolrClient();

		final UpdateResponse response = client.add(SOLR_CORE_SGSE, doc);
		System.out.println(" httpStatus=" + response.getStatus());

		// Indexed documents must be committed
		client.commit(SOLR_CORE_SGSE);
	}
	
	public static SolrDocumentList querySgseCore(final Map<String, String> queryParamMap) throws SolrServerException, IOException {
		final SolrClient client = SolrUtils.getSolrClient();

     	MapSolrParams queryParams = new MapSolrParams(queryParamMap);

		final QueryResponse response = client.query(SOLR_CORE_SGSE, queryParams);
		final SolrDocumentList documents = response.getResults();
		return documents;
	}
	
	public static HttpSolrClient getSolrClient() {
		return new HttpSolrClient.Builder(SOLR_URL).withConnectionTimeout(10000).withSocketTimeout(60000).build();
	}	
	
	private static HttpSolrClient getSolSgseCoreClient() {
		final String solrUrl = SOLR_URL + "/sgse";
		return new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();
	}	

}
