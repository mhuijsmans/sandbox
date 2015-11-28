package org.mahu.proto.mongodb;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDbClient {

	private MongoClient mongo;

	public static void main(String[] arg) {
		MongoDbClient mc = new MongoDbClient();
		mc.init();
		mc.listDbNames();
		mc.listTablesInDb();
		mc.addRowToTableUser();
		mc.printRowsInApplog1();
	}

	private void init() {
		try {
			mongo = new MongoClient("10.0.0.13", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void printRowsInApplog1() {
		String dbName = "syslog";
		String tableName = "appl1Log";
		printTableInfo(dbName, tableName,0);
	}

	private void printTableInfo(String dbName, String tableName, int maxRows) {
		System.out.println("--- Info on table "+tableName+" in DB "+dbName+" ---");
		DB db = mongo.getDB(dbName);
		//
		DBCollection table = db.getCollection(tableName);
		System.out.println("Number of rows " + table.getCount());
		DBCursor cursor = table.find();
		//
		int cnt = 0;
		while(cursor.hasNext() && (maxRows<=0 || (maxRows>0 && cnt <maxRows))) {
			DBObject obj = cursor.next();
			System.out.println(obj);
			cnt++;
		}
		System.out.println("---");
	}

	private void addRowToTableUser() {
		String dbName = "syslog";
		String tableName = "user";
		System.out.println("--- Adding entry to table "+tableName+" in DB "+dbName+" ---");
		DB db = mongo.getDB(dbName);
		//
		DBCollection table = db.getCollection(tableName);
		BasicDBObject document = new BasicDBObject();
		document.put("name", "martien");
		document.put("age", 17);
		document.put("createdDate", new Date());
		table.insert(document);
		System.out.println("---");
		//
		printTableInfo(dbName, tableName,5);
	}

	private void listTablesInDb() {
		String dbName = "syslog";
		System.out.println("--- list of tables in DB "+dbName+" ---");
		DB db = mongo.getDB(dbName);
		//
		Set<String> tables = db.getCollectionNames();
		for (String coll : tables) {
			System.out.println(coll);
		}
		System.out.println("---");
	}

	private void listDbNames() {
		System.out.println("--- list of databases ---");
		List<String> dbs = mongo.getDatabaseNames();
		for (String db : dbs) {
			System.out.println(db);
		}
		System.out.println("---");
	}

}
