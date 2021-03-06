package com.example.flvb.model.usertree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import static com.mongodb.client.model.Filters.*;


public class UserTree {
	
	final String dir = System.getProperty("user.dir"); // get current working directory path
	String user_tree_json = "";
	PrintWriter writer = null;	// for print info into txt file (important info for each ip, actions account)
	Gson gson = new Gson(); // for convert object/hashmap to json format
	File file = new File(this.dir + "/json");
	
	// MongoDB commands based on MongoDB version 3.2.7
	MongoClient mongoClient = new MongoClient( "localhost" , 27017 ); // may need to change port (27017) if different in another computer
	MongoDatabase mongo_database = mongoClient.getDatabase("flax_log_visualization_db");
	MongoCollection<Document> user_map_collection = null;
	MongoCursor<Document> cursor = null;
	ArrayList<Document> user_map_documents = null;
	String user_map_collection_name = "user_map_collection";
	
	HashMap<String, USER> user_tree_map = new HashMap<String, USER>();
	
	
	//
	public void process(String[] str_line_abstract_info, String[] s1_paras) {
		this.file.mkdir(); // create a new directory to store json
		
		if (this.mongo_database.getCollection(this.user_map_collection_name) == null) {
			createMongodbCollection(this.user_map_collection_name);
		}
		
		// set user_map_collection
		setUserMapCollection(str_line_abstract_info[0] + " " + str_line_abstract_info[2], str_line_abstract_info[1], str_line_abstract_info[3], str_line_abstract_info[4], s1_paras);
		System.out.println ("UserTree:"+str_line_abstract_info[0] + " " + str_line_abstract_info[2]);
	}
	
	
	//
	public void writeJSON() throws FileNotFoundException, UnsupportedEncodingException {
		// write into json file
		if (this.writer != null) 
			this.writer.close();
		this.writer = new PrintWriter(this.file + "/" + "user_tree.json", "UTF-8");
		// this.user_tree_json = getCollectionString(this.user_map_collection_name);
		this.user_tree_json = getAllUserDocument(this.user_map_collection_name);
		// this.user_tree_json = getUserDocument(this.user_map_collection_name, "2014-09-09 uid=296").toJson();
		System.out.println ("UserTree:"+this.user_tree_json);
		this.writer.println(this.user_tree_json);	// write info into json
		this.writer.close();
		this.user_tree_json = "";
	}
	 	
	
	
	//
	public void setUserMapCollection(String userID, String time, String current_c_action, String current_s_action, String[] s1_paras) {
		USER temp_user = null;
		
		if (containUserDocument(this.user_map_collection_name, userID)) {
			temp_user = documentToUSERObject(getUserDocument(this.user_map_collection_name, userID));
			temp_user.actions_account_increased();
			temp_user.set_endTime(time);
			// Document temp_doc = userObjectToDocument(temp_user);
			// updateUserDocument(this.user_map_collection_name, userID, temp_doc);
		} else {
			temp_user = new USER();
			temp_user.setUserID(userID);
			temp_user.set_startTime(time);
			// insertUserDocument(this.user_map_collection_name, userObjectToDocument(temp_user));
		}
		
		temp_user.calculate_last_action_stayTime(time);
		temp_user.set_c_s_actions_map(current_c_action, current_s_action, time, s1_paras);
		temp_user.update_last_action(current_c_action, current_s_action);
		
		if (containUserDocument(this.user_map_collection_name, userID)) {
			Document temp_doc = userObjectToDocument(temp_user);
			updateUserDocument(this.user_map_collection_name, userID, temp_doc);
		} else {
			insertUserDocument(this.user_map_collection_name, userObjectToDocument(temp_user));
		}
		
		
		/* old version without MongoDB (set_user_tree_map)
		
		if (this.user_tree_map.containsKey(userID)) {
			USER temp_user = this.user_tree_map.get(userID);
			temp_user.actions_account_increased();
			this.user_tree_map.get(userID).set_endTime(time);
		} else {
			this.user_tree_map.put(userID, new USER());
			this.user_tree_map.get(userID).set_startTime(time);
		}
		
		USER temp_user = this.user_tree_map.get(userID);
				
		temp_user.calculate_last_action_stayTime(time);
		temp_user.set_c_s_actions_map(current_c_action, current_s_action, time, s1_paras);
		temp_user.update_last_action(current_c_action, current_s_action);
		*/
	}
	

	
	
	
	///////////////////////		MongoDB relevant Methods		///////////////////////
	
	//
	public void createMongodbCollection(String collection_name) {
		this.mongo_database.createCollection(collection_name);		
	}
	
	//
	public boolean containUserDocument(String collection_name, String userID) {
		Document queried_doc = null;
		this.user_map_collection = this.mongo_database.getCollection(collection_name);
		queried_doc = this.user_map_collection.find(eq("userID", userID)).first();
		if (queried_doc != null) {
			return true;
		} else {
			return false;
		}
	}
	
	//
	public String getAllUserDocument(String collection_name) {
		this.user_map_collection = this.mongo_database.getCollection(collection_name);
		MongoCursor<Document> cursor = this.user_map_collection.find().iterator();
		String temp_documents = "{";
		int i = 0;
		try {
		    while (cursor.hasNext()) {
		    	temp_documents += "\""+i+"\":" + cursor.next().toJson();
		    	if (cursor.hasNext()) {
		    		temp_documents += ",\n";
		    	}
		    	i++;
		    }
		} finally {
		    cursor.close();
		}
		temp_documents += "}";
		return temp_documents;
	}
	
	//
	public String getCollectionString(String collection_name) {
		this.user_map_collection = this.mongo_database.getCollection(collection_name);
		return this.user_map_collection.toString();
	}
	
	//
	public Document getUserDocument(String collection_name, String userID) { // get USER corresponding document
		this.user_map_collection = this.mongo_database.getCollection(collection_name);
		Document queried_doc = this.user_map_collection.find(eq("userID", userID)).first();
		return queried_doc;
	}
	
	//
	public void insertUserDocument(String collection_name, Document insert_doc) {
		this.user_map_collection = this.mongo_database.getCollection(collection_name);
		this.user_map_collection.insertOne(insert_doc); 
	}
	
	//
	public void updateUserDocument(String collection_name, String userID, Document update_doc) {
		this.user_map_collection = this.mongo_database.getCollection(collection_name);
        Bson filter = Filters.eq("userID", userID);
		this.user_map_collection.replaceOne(filter, update_doc);
	}
	
	//
	public USER documentToUSERObject(Document parse_doc) {
		USER to_user = new USER();
		String parse_doc_json = parse_doc.toJson();
		to_user = this.gson.fromJson(parse_doc_json, USER.class);
		return to_user;
	}
	
	//
	public Document userObjectToDocument(USER parse_user) {
		String parse_user_json = this.gson.toJson(parse_user);
		Document to_document = Document.parse(parse_user_json);
		return to_document;
	}
	
}
