package client;

import java.io.IOException;
import java.io.ObjectInputStream;

public class MessageHandler {

	/** 
	 * Extract the XML message and construct validated Message object based on
	 * the terminator string (either "</request>" or "</response>"). Returns 
	 * null if communication is interrupted in any way.
	 */
	static Object extractMessage(ObjectInputStream in, String terminator) {
		try {			
			Object obj = in.readObject();
			if (obj == null) { return null; }
			return obj;
		} catch (IOException ioe) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public Object process(Object message){
		Object response = null;
		return response;
	}
	
}
