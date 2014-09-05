package messages;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import client.RequestType;

@SuppressWarnings("unchecked")
public class MessageHandler {

	/** 
	 * Extract the XML message and construct validated Message object based on
	 * the terminator string (either "</request>" or "</response>"). Returns 
	 * null if communication is interrupted in any way.
	 */
	public static Object extractMessage(ObjectInputStream in, String terminator) {
		try {			
			Object obj =  in.readObject();
			if (obj == null) { return null; }
			return obj;
		} catch (IOException ioe) {
			System.out.println(ioe.getLocalizedMessage());
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public JSONObject process(Object message){
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject((String) message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		RequestType type = null;
		try {
			type = RequestType.valueOf((String)jsonObject.get("type"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		switch(type){
		case BET:
			return processBid(jsonObject);
		default:
			return null;
		}
	}
	
	public JSONObject processBid(JSONObject jsonObject) {
		System.out.println("processing bid...");
		System.out.println(jsonObject);
		JSONObject response = new JSONObject();
		try {
			response.put("type", "BID_RESPONSE");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return response;
	}
	
}
