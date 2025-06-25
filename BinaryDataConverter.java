/*  CSE2138 - SPRING 2025 - PROJECT #1
 *  Yasin Emre Çetin
 *  Eren Emre Aycibin
 *  Furkan Eren Gülçay
 */

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class BinaryDataConverter {

	static String inputFileName;
	static boolean isLittleEndian;
	static char type;
	static int size;
	
	static Scanner scanner;
	static PrintWriter output;
	static int counter = 0;
	
	/* Converts hexadecimal numbers according to input. */
	public static void main(String args[]) {
		processInput(args);
		try {
			scanner = new Scanner(new File(inputFileName));
			output = new PrintWriter(new File("output.txt"));
		} catch (Exception e) {
			System.out.println("There is an error about files!");
			System.exit(1);
		}
		while (scanner.hasNext()) {
			String number = getBinaryNumber();
			switch (type) {
				case 'f':
					String floating = binaryToFloatingPoint(number);
					printFile(floating, type);
					break;
				case 'i':
					String integer = "" + binaryToInt(number);
					printFile(integer, type);
					break;
				case 'u':
					String unsigned = "" + binaryToUnsigned(number);
					printFile(unsigned, type);
					break;
				default: break;
			}
		}
		scanner.close();
		output.close();
	}

	/* Reads and processes input file. */
	public static void processInput(String[] inputs) {
		if (inputs.length != 4) {
			System.out.println("Invalid input!");
			System.exit(1);
		}
		
		inputFileName = inputs[0];
		switch (inputs[1].toLowerCase()) {
			case "l":
				isLittleEndian = true;
				break;
			case "b":
				isLittleEndian = false;
				break;
			default:
				System.out.println("Invalid type!");
				System.exit(1);
		}
		switch (inputs[2].toLowerCase()) {
			case "fp":
				type = 'f';
				break;
			case "i":
				type = 'i';
				break;
			case "u":
				type = 'u';
				break;
			default:
				System.out.println("Invalid type!");
				System.exit(1);
		}
		try {
			size = Integer.parseInt(inputs[3]);
			if (!(0 < size && size < 5)) {
				System.out.println("Invalid size!");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("Invalid size!");
			System.exit(1);
		}
	}
	
	/* Returns the number in binary as a string. */
	public static String getBinaryNumber() {
		String[] number = new String[size];
		for (int i = 0; i < size; i++) 
			number[i] = hexToBinary(scanner.next());
		if (isLittleEndian) 
			convertToLittleEndian(number);
		String num = "";
		for (int i = 0; i < size; i++)
			num += number[i];
		return num;
	}
	
	/* Converts hexadecimal byte to binary byte. */
	public static String hexToBinary(String hexByte) {
		String binaryByte = "";
		for (int i = 0; i < 2; i++) {
			switch (hexByte.toLowerCase().charAt(i)) {
				case '0': binaryByte += "0000"; break; 
				case '1': binaryByte += "0001"; break; 
				case '2': binaryByte += "0010"; break; 
				case '3': binaryByte += "0011"; break; 
				case '4': binaryByte += "0100"; break; 
				case '5': binaryByte += "0101"; break; 
				case '6': binaryByte += "0110"; break; 
				case '7': binaryByte += "0111"; break; 
				case '8': binaryByte += "1000"; break; 
				case '9': binaryByte += "1001"; break; 
				case 'a': binaryByte += "1010"; break; 
				case 'b': binaryByte += "1011"; break; 
				case 'c': binaryByte += "1100"; break; 
				case 'd': binaryByte += "1101"; break; 
				case 'e': binaryByte += "1110"; break; 
				case 'f': binaryByte += "1111"; break;
				default: 
					System.out.println("Invalid file content!");
					System.exit(1);
					break;
			}
		}
		return binaryByte;
	}
	
	/* Changes the binary number into Little Endian format. */
	public static void convertToLittleEndian(String[] number) {
		int i = 0, j = number.length - 1;
		while (i < j) {
			String temp = number[i];
			number[i] = number[j];
			number[j] = temp;
			i++; j--;
		}
	}
	
	/* Converts the binary number to signed integer. */
	public static int binaryToInt(String number) {
		int integer = 0;
		number = reverse(number);
		for (int i = 0; i < number.length() - 1; i++) 
			if (number.charAt(i) == '1')
				integer += (int)Math.pow(2, i);
		if (number.charAt(number.length() - 1) == '1')
			integer -= (int)Math.pow(2, number.length() - 1);
		return integer;
	}
	
	/* Converts the binary number to unsigned integer. */
	public static int binaryToUnsigned(String number) {
		int unsigned = 0;
		number = reverse(number);
		for (int i = 0; i < number.length(); i++) 
			if (number.charAt(i) == '1')
				unsigned += (int)Math.pow(2, i);
		return unsigned;
	}
	
	/* Reverses the input string number. */
	public static String reverse(String s) {
		String temp = "";
		for (int i = s.length() - 1; i >= 0; i--) 
			temp += s.charAt(i);
		return temp;
	}
	
	/* Converts binary number to floating point number. */
	public static String binaryToFloatingPoint(String number) {
	    int exponentBits = getExponentBits(size);
	    int bias = getBias(exponentBits);
	    int sign = (number.charAt(0) == '1') ? -1 : 1;
	    
	    String exponentStr = number.substring(1, 1 + exponentBits);
	    String fraction = number.substring(1 + exponentBits);
	    
	    int exponentValue = binaryToUnsigned(exponentStr);
	    
	    /* Rounding is applied only to the digits after the 13th digit of the fraction. */
	    if (size >= 3) {
	    	number = round(number, exponentBits);
	    	exponentStr = number.substring(1, 1 + exponentBits);
		    fraction = number.substring(1 + exponentBits);
	    }

		double mantissa = (exponentValue == 0) ? 0.0 : 1.0;
		for (int i = 0; i < fraction.length(); i++) {
			if (fraction.charAt(i) == '1') {
				mantissa += Math.pow(2, -(i + 1));
			}
		}
		
		/* Infinity and NaN cases. */
		if (isAllOnes(exponentStr)) {
			String specialCase = "";
			if (sign == -1)
				specialCase += "-";
			if (binaryToUnsigned(fraction) == 0) {
				specialCase += "inf";
				return specialCase;
			}
			else return "NaN";
		}
		
		double value = (exponentValue == 0) ?
				sign * mantissa * Math.pow(2, 1 - bias) :
				sign * mantissa * Math.pow(2, exponentValue - bias);
		return (value + "").toLowerCase();
	}
	
	/* Returns the value of bias according to number of exponent bits. */
	public static int getBias(int exp) {
		return (int) (Math.pow(2, exp - 1) - 1);
	}
	
	/* Returns the number of exponent bits according to size. */
	public static int getExponentBits(int size) {
		int exponentBits = 0;
		switch (size) {
			case 1:
				exponentBits = 4;
				break;
			case 2:
				exponentBits = 6;
				break;
			case 3:
				exponentBits = 8;
				break;
			case 4:
				exponentBits = 10;
				break;
			default:
				break;
		}
		return exponentBits;
	}
	
	/* Rounds the number and returns the rounded binary number. */
	public static String round(String number, int exponentBits) {
		String signBit = number.substring(0, 1);
		String exponent = number.substring(1, 1 + exponentBits);
	    String fraction = number.substring(1 + exponentBits);
		String roundingBits = fraction.substring(13);
		fraction = fraction.substring(0, 13);
		
		/* Rounding down. */
		if (roundingBits.charAt(0) == '0')
			return (signBit + exponent + fraction);
		
		boolean needRoundUp = false;
		if (roundingBits.substring(1).contains("1"))
			needRoundUp = true;
		
		if (!needRoundUp) { /* Halfway, rounding to even. */
			if (fraction.charAt(12) == '0') /* Round down. */
				return (signBit + exponent + fraction);
		}
		
		/* Round up. */
		fraction = addOne(fraction);
		return (signBit + exponent + fraction);
	}
	
	/* Adds 1 to input binary number. */
	public static String addOne(String num) {
		String reversed = reverse(num);
		/* If number is 111...11, overflow occurs. Returns 000...00. */
		if (isAllOnes(num)) {
			String s = "";
			for (int i = 0; i < num.length(); i++)
				s += "0";
			return s;
		}
		for (int i = 0; i < reversed.length(); i++) {
			if (reversed.charAt(i) == '1') {
				reversed = reversed.substring(0, i) + "0" + reversed.substring(i + 1);
				continue;
			}
			else {
				reversed = reversed.substring(0, i) + "1" + reversed.substring(i + 1);
				break;
			}
		}
		return reverse(reversed);
	}
	
	/* Checks that if the input string is fully composed of 1s or not. */
	public static boolean isAllOnes(String num) {
		for (int i = 0; i < num.length(); i++)
			if (num.charAt(i) == '1')
				continue;
			else return false;
		return true;
	}
	
	public static void printFile(String num, char type) {
		counter = counter + 1;
		if (type == 'f') {
			/* Printing infinity and not a number. */
			if (num.contains("inf") || num.contains("NaN")) {
				output.print(num);
				printSpaces();
				return;
			}
			
			String truncatedNumber = "";
			int i = 0;
			while (num.charAt(i) != '.') {
				truncatedNumber += num.charAt(i);
				i++;
			}
			
			/* Prints as in integer format. -> 0 instead of 0.0 */
			if (num.endsWith(".0")) {
				output.print(truncatedNumber);
				printSpaces();
				return;
			}
			
			truncatedNumber += ".";
			i++;
			
			/* Prints it directly. */
			if (num.length() - i <= 5) {
				output.print(num);
				printSpaces();
			}
			
			/* Prints in scientific notation. */
			for (int count = 0; i < num.length() && count < 5; i++, count++) {
				truncatedNumber += num.charAt(i);
			}
			while (i < num.length() && num.charAt(i) != 'e') {
				i++;
			}
			if (i < num.length())
				truncatedNumber += num.substring(i);
			output.print(truncatedNumber);
		}
		else {
			output.print(num);
		}
		printSpaces();
	}
	
	public static void printSpaces() {
		if (scanner.hasNext()) {
			if ((counter % (12/size)) == 0) {
				output.print("\n");
			}
			else {
				output.print(" ");
			}
		}
	}
	
}
