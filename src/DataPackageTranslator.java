
public class DataPackageTranslator {
	public int port;
	public String userName;
	public String message;
	
	public static final String nameParseCode = "&$^#(@&$(%&#*@#$";
	public static final String messageParseCode = "@(#*$&!*@&#&^$@*#&";
	
	public static final String exitCode = "(*#@*$*(#&$)@(*#$&&$";
	public static final String enterCode = "*(#@*&$%^(#@*&$*&@#^%&)";

	
	public DataPackageTranslator(String data){
		port = Integer.parseInt(data.substring(0,data.indexOf(nameParseCode))); 
		userName = data.substring(data.indexOf(nameParseCode) + nameParseCode.length(),data.indexOf(messageParseCode));
		message = data.substring(data.indexOf(messageParseCode) + messageParseCode.length());
	}
	
	public String toString(){
		return port + "| " + userName + ": " + message;
	}
	
}
