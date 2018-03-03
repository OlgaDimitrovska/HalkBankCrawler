package vtor.obid.crawler;

import java.awt.List;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import org.jsoup.Jsoup;

import com.mongodb.*;
import com.mongodb.util.JSON;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
    	MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
    	DB database = mongoClient.getDB("prvaDB");
    	DBCollection collection = database.getCollection("Valuti");
    	DBCollection collectionBan=database.getCollection("Banki");
    	
    	 org.jsoup.nodes.Document doc = Jsoup.connect("http://www.halkbank.mk/kursna-lista.nspx").get();

         org.jsoup.select.Elements rows = doc.select("tr.redica-so-valuti");
         
         
         
         String jsonMsg = "[";
         

         for(org.jsoup.nodes.Element row :rows)

         {

             org.jsoup.select.Elements columns = row.select("td");
             
             int i = 1;
             
             jsonMsg = jsonMsg + "{";

             for (org.jsoup.nodes.Element column:columns)

             {           	
                
                 if(i==3)
                 {
                 	jsonMsg = jsonMsg + "\"Валута\":";
                 	
                 	jsonMsg = jsonMsg + "\""+ column.text()+"\"";
                     
                 	jsonMsg = jsonMsg + ",";
                 	
                 	System.out.print(column.text());
                 }
                 if(i==4)
                 {
                 	jsonMsg = jsonMsg + "\"Куповен\":";
                 	
                 	jsonMsg = jsonMsg + "\""+ column.text()+"\"";
                     
                 	jsonMsg = jsonMsg + ",";
                 	
                 	System.out.print(column.text());
                 }
                 if(i==5)
                 {
                 	jsonMsg = jsonMsg + "\"Среден\":";
                 	
                 	jsonMsg = jsonMsg + "\""+ column.text()+"\"";
                     
                 	jsonMsg = jsonMsg + ",";
                 	
                 	System.out.print(column.text());
                 }
                 if(i==6)
                 {
                 	jsonMsg = jsonMsg + "\"Продажен\":";
                 	
                 	jsonMsg = jsonMsg + "\""+ column.text()+"\"";
                     
                 	
                 	System.out.print(column.text());
                 }

                 
                 
                 
                 
                 i++;
                 

             }
             
            
             
             jsonMsg = jsonMsg + "},";
             
             System.out.println();
              
         }

         
         jsonMsg = jsonMsg.substring(0, jsonMsg.length()-1);
         jsonMsg = jsonMsg + "]";

         System.out.print(jsonMsg);
         
         java.util.List<DBObject> docs = new ArrayList<DBObject>();
         
         BasicDBObject query=new BasicDBObject();
         query.put("Назив на Банка", "HalkBank");//go baram od kolekcijata
         DBObject cur=collectionBan.findOne(query);//go smestuvam vo variable
         
         DBObject jArray =  (DBObject) JSON.parse(jsonMsg);//ova kako eden objekt go glea cel json 
         
         System.out.println(jArray);
         Set<String> keys = jArray.keySet();//gi vadi klucevite od jsonot
         for (String key : keys) {//za sekoj kluc vo setot klucevi
        	 DBObject tmp= (DBObject)jArray.get(key);
        	 tmp.put("Назив на Банка", cur);       	 
			docs.add(tmp);
		 }
         
         collection.insert(docs);
         // kako hash mapa gi gledalo JSON bibliotekava, ali radi sea falaaaaaaaaaa! nsto
    	//collection.insert(dbObject);
    	
    	System.out.println("Uspesno vneseno vo baza");
    	
    	mongoClient.close();
    }
}
