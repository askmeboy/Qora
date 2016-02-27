package lang;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import settings.Settings;
import utils.Pair;


public class Lang {

	private static Lang instance;

	private JSONObject langObj;	
	
	public static Lang getInstance()
	{
		if(instance == null)
		{
			instance = new Lang();
		}
		
		return instance;
	}
	
	private Lang() {
		loadLang();
	}
	
	public void loadLang() {
		langObj = OpenLangFile( Settings.getInstance().getLang());
	}
	
	public String[] translate(String[] Messages) 
	{
		String[] translateMessages = Messages.clone();
		for (int i = 0; i < translateMessages.length; i++) {
			translateMessages[i] = this.translate(translateMessages[i]);
		}
		return translateMessages; 
	}
	
	public String translate(String message) 
	{
		//COMMENT AFTER # FOR TRANSLATE THAT WOULD BE THE SAME TEXT IN DIFFERENT WAYS TO TRANSLATE
		String messageWithoutComment = message.replaceFirst("(?<!\\\\)#.*$", ""); 
		messageWithoutComment = messageWithoutComment.replace("\\#", "#");
		
		if (langObj == null) { 
			return messageWithoutComment;
		}

		if(!langObj.containsKey(message)) {

			//IF NO SUITABLE TRANSLATION WITH THE COMMENT THEN RETURN WITHOUT COMMENT
			if(!langObj.containsKey(messageWithoutComment)) {
				return messageWithoutComment;
			} else {
				return langObj.get(messageWithoutComment).toString();
			}
		}
			
		return langObj.get(message).toString();		
	}
	
	private JSONObject OpenLangFile(String filename)
	{
		File file = new File( "lang/" + filename );
		if (!file.isFile()) {
			return (JSONObject) JSONValue.parse("");
		}
		
		List<String> lines = null;
		try {
			lines = Files.readLines(file, Charsets.UTF_8);
		} catch(IOException e) {
			lines = new ArrayList<String>();
			e.printStackTrace();
		}
		
		String jsonString = "";
		for(String line : lines){
			jsonString += line;
		}
		
		return (JSONObject) JSONValue.parse(jsonString);
	};

	public List<Pair<String, String>> getListOfAvailable()
	{
		List<Pair<String, String>> lngList = new ArrayList<>();
		
		File[] fileList;        
        File f = new File("lang");
                
        fileList = f.listFiles();
                        
        for(int i=0; i<fileList.length; i++)           
        {
        	if(fileList[i].isFile() && fileList[i].getName().endsWith(".lng")) {
        		lngList.add(
        				new Pair<>(
        						fileList[i].getName(), 
        						(String)this.OpenLangFile(fileList[i].getName()).get("lang_name")
        						)
        				);
        	}
        	
        }
        
        return lngList;
	}
}