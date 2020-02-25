import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class RSA {

	private BigInteger primeA;
	private BigInteger primeB;
	private BigInteger r;
	private BigInteger publicKeyN;
	private BigInteger publicKeyE;
	private BigInteger privateKeyD;

	private BigInteger[] segmentedCipherText;
	private int segmentLength; 
	
	public RSA(int length){
		primeA = generatePrime(length);
		primeB = generatePrime(length);
		
		publicKeyN = primeA.multiply(primeB);
		r = primeA.subtract(new BigInteger("1")).multiply(primeB.subtract(new BigInteger("1")));
		publicKeyE = r.subtract(new BigInteger("1"));
		privateKeyD = publicKeyE.modInverse(r);

		try {
			FileWriter fw = new FileWriter("RSAKeys.txt");
			fw.write("public key N: " + publicKeyN);
			fw.write("\npublic key E: " + publicKeyE);
			fw.write("\nprivate key: " + privateKeyD);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		primeA = null;
		primeB = null;
		r = null;
	}
	
	public BigInteger generatePrime(int length){
		if(length<1){
			System.out.println("Length must be a number more than 1.");
			System.exit(0);
		}
		
		String numString = "";
		double x = Math.random();
		int num = (int)Math.ceil((x * 10)-1);
		numString = numString + num;
		for(int i = 1 ; i < length; i++){
			x = Math.random();
			num = (int)Math.ceil((x * 10));
			numString = numString + num;
		}
		
		BigInteger prime = new BigInteger(numString);
		while(prime.isProbablePrime(1)!=true){
			prime = prime.add(new BigInteger("1"));
		}
		return prime;
	}
	
	public BigInteger getPublicKeyE(){
		return publicKeyE; 
	}
	
	public BigInteger getPublicKeyN(){
		return publicKeyN; 
	}
	
	public BigInteger getprivateKeyD(){
		return privateKeyD;
	}
	
	public void sendCipher(BigInteger[] segmentedCipherText, int messageSegmentLength){
		this.segmentedCipherText = segmentedCipherText;
		this.segmentLength = messageSegmentLength;
	}
	
	public String decryptCipher(){
		String segmentedText = "";
		for(int i = 0; i < segmentedCipherText.length; i++){
			BigInteger segment = segmentedCipherText[i].modPow(getprivateKeyD(), getPublicKeyN());
			if((segment+"").length()!= 3 * segmentLength){
				segmentedText = segmentedText + "0";
			}
			segmentedText = segmentedText + segment;
		}
		return segmentedText;
	}
	
	public String codeToText(){
		String text = "";
		String messageCode = decryptCipher();
		while(messageCode.length()>0){
			String character = messageCode.substring(0, 3);
			char c = (char) Integer.parseInt(character);
			text = text + c;
			messageCode = messageCode.substring(3);
		}
		return text;
	}
	public static void main(String[] args){
		int keyLength = 100; //Select a key length
		
		int messageSegmentLength = keyLength/2;
		RSA keygen1 = new RSA(keyLength);

		
		String message = "The drawing is made by the drawLine function from the java.awt.Graphics class, it takes four parameters: the first two are the starting point of the line, the others are the ending point. I position these points using pixels as unit, I use dims to reduce the 500 pixels translation factor: say the maze dimensions are 50x50, then dims = 50. Plus, the JPanel is created with a especial stroke and margin depending on the dimensions. All this is what makes our mazes occupy the same space, and remember our starts and ends arrays? This is where we use them.";
		
		System.out.println("Initial Message: " + message);
		
		int padding = messageSegmentLength - Math.floorMod(message.length(), messageSegmentLength);
		for(int i = 0; i < padding; i++){
			message = message + " ";
		}
		
		BigInteger[] segmentedMessageCode = new BigInteger[message.length()/messageSegmentLength];
		BigInteger[] segmentedCipherTextCode = new BigInteger[segmentedMessageCode.length];
		String[] segmentedMessage = new String[message.length()/messageSegmentLength];
		int index = 0;
		System.out.print("Inital Message to Segments: ");
		for(int i = 0; i < segmentedMessage.length; i++){
			segmentedMessage[i] = "";
			for(int j = 0; j < messageSegmentLength; j++){
				segmentedMessage[i] = segmentedMessage[i] + message.charAt(index + j);
			}
			index = index + messageSegmentLength;
			System.out.print("["+segmentedMessage[i]+"] ");
		}
		System.out.println();
		
		String messageCode = "";
		for(int i = 0; i < segmentedMessage.length; i++){
			String temp = "";
			for(int j = 0; j < segmentedMessage[i].length(); j++){
				if((int)segmentedMessage[i].charAt(j) < 100){
					temp = temp + "0";
				}
				temp = temp +  (int)segmentedMessage[i].charAt(j);
			}
			segmentedMessageCode[i] = new BigInteger(temp);
			messageCode = messageCode + segmentedMessageCode[i];
			temp = "";
		}
		System.out.println("MessageCode: " + messageCode);
		
		String cipherTextCode = "";
		for(int i = 0; i < segmentedMessageCode.length; i++){
			segmentedCipherTextCode[i] = segmentedMessageCode[i].modPow(keygen1.getPublicKeyE(),keygen1.getPublicKeyN());
			cipherTextCode = cipherTextCode + segmentedCipherTextCode[i];
		}
		System.out.println("CipherText: " + cipherTextCode);

		
		keygen1.sendCipher(segmentedCipherTextCode, messageSegmentLength);
		System.out.println("Decrypted MessageCode: " + keygen1.decryptCipher() + "[Decrypted]");
		System.out.println("Decrypted Message: " + keygen1.codeToText());

	}
}


