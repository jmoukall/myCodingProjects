//
//	Modifier
//
//	the purpose of this class is to create and modify a file of input
//
//	Author:	Jafar Moukalled
//	Date:	10/20/2019
//
package stringModifierPackage;

import java.io.*;

public class Modifier
{
	
	
	public Modifier()
	{
		
	}
	
	
	//
	//	createFile
	//
	//	the purpose of this method is to create a text file
	//
	//	input:	filename
	//			statement
	//	return:	none
	//
	public static void createFile(String filename, String statement) throws IOException
	{
		// create the file and set the contents equal to the statement
		FileWriter fw = new FileWriter(filename);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		
		pw.write(statement);
		pw.close();
		System.out.println(filename + " has been created.");
	}
	
	
	//
	//	isFile
	//
	//	the purpose of this method is to check if the file exists
	//
	//	input:	filename
	//	return:	none
	//
	public static void isFile(String filename) throws IOException
	{
		// check if file exists using File.exists() method
		File f = new File(filename);
		
		if(f.exists())
		{
			System.out.println(filename + " exists.");
		}
		else
		{
			System.out.println(filename + " doesn't exist.");
		}
	}
	
	
	//
	//	isReadable
	//
	//	the purpose of this method is to check if the file is readable
	//
	//	input:	filename
	//	return:	none
	//
	public static void isReadable(String filename)
	{
		// figure out if readable using a simple try/catch statement
		 try 
		 {
			 FileReader fr = new FileReader("file.txt");
			 fr.read();
			 fr.close();
		 } 
		 catch (Exception e)
		 {
			System.out.println(filename + " is not readable.");
		 }
		 
		 System.out.println(filename + " is readable.");
	}
	
	
	//
	//	countChars
	//
	//	the purpose of this method is to count the number of chars in the file
	//
	//	input:	filename
	//	return:	none
	//
	public static void countChars(String filename) throws IOException
	{
		// count number of chars using readLine() from BufferedReader class
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		
		int numChars = 0;
		String line;
		
		while((line = br.readLine()) != null)
		{
			numChars += line.length();
		}
		
		System.out.println("\nThere are " + numChars + " characters in the file.");
		br.close();
	}
	
	
	//
	//	findSize
	//
	//	the purpose of this method is to find the size of a file in bytes
	//
	//	input:	filename
	//	return:	none
	//
	public static long findSize(String filename) throws IOException
	{
		// find file size by using File.length()
		File f = new File(filename);
		
		long size = f.length();
		
		System.out.println("\nFile Size: " + size + " bytes");
		
		return size;
	}
	
	//
	//	toUpperCase
	//
	//	the purpose of this method is to make certain char uppercase in a file
	//
	//	input:	filename
	//	return:	none
	//
	public static void toUpperCase(String filename) throws IOException
	{
		// change it using randomaccessfile
		RandomAccessFile raf = new RandomAccessFile(new File(filename), "rw");
		char first = (char) raf.read();
		raf.seek(0);
		raf.writeByte((byte) Character.toUpperCase(first));
		 
		System.out.println("\n" + filename + "'s first character has been changed to upper case.");
		raf.close();
	}
	
	
	//
	//	charCntr
	//
	//	the purpose of this method is to count the number of 'a' and 'e' in a file
	//
	//	input:	filename
	//	return:	none
	//
	public static void charCntr(String filename) throws IOException
	{
		File f = new File(filename);
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		
		// convert the file contents to string then count the desired chars
		@SuppressWarnings("unused")
		String s1 = "nothing yet";
		
		while((br.readLine()) != null)
		{
			s1 = br.readLine();
		}
		br.close();
		
	}
	
}// end Modifier
