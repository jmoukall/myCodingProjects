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
	
	//
	//	replaceWord
	//
	//	the purpose of this method is to replace a word in the file
	//
	//	input:	filename
	//			word
	//	return:	none
	//
	public static void replaceWord(String filename, String word) throws IOException
	{
		// change to string using method i made
		String s1 = Modifier.toString(filename);
		
		// replace second word
		s1 = s1.replaceFirst(word, "dearest");
		
		// write the string back into the file
		FileWriter fw = new FileWriter(filename);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		pw.println(s1);
		pw.close();
		
		System.out.println("\nThe second word of the first line has changed to 'dearest'.");
	}

	//
	//	appendWords
	//
	//	the purpose of this method is to append 10 words to a file
	//
	//	input:	filename
	//	return:	none
	//
	public static void appendWords(String filename)
	{
		try(FileWriter fw = new FileWriter(filename, true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter pw = new PrintWriter(bw))
		{
			pw.println("hello");
			pw.println("my");
			pw.println("name");
			pw.println("is");
			pw.println("jafar");
			pw.println("and");
			pw.println("this");
			pw.println("is");
			pw.println("my");
			pw.println("project.");
			pw.close();
		} 
		catch (IOException e)
		{
			    //exception handling left as an exercise for the reader
		}
		
		System.out.println("10 words have been appended to the file");
	}
	
	//
	//	findNumLines
	//
	//	the purpose of this method is to find the number of lines in a file
	//
	//	input:	filename
	//	return:	none
	//
	public static void findNumLines(String filename) throws IOException
	{
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		
		int numLines = 0;
		String line;
		
		while((line = br.readLine()) != null)
		{
			++numLines;
		}
		
		System.out.println("\nThe file has " + numLines + " lines of text");
		
	}
	
	//
	//	deleteChar
	//
	//	the purpose of this method is to delete a certain char at a specific position
	//
	//	input:	filename
	//			c
	//	return:	none
	//
	public static void deleteChar(String filename, char c) throws IOException
	{
		// change to string
		String s1 = Modifier.toString(filename);
		StringBuilder sb = new StringBuilder();
		
		sb.append(s1);
		int index = s1.lastIndexOf(c);
		sb.replace(index, index + 1, "");
		s1 = sb.toString();
		
		// write it back into the file
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
		pw.println(s1);
		pw.close();
		
		System.out.println("The '" + c + "' has been deleted at the end of the file.");
	}
	
	//
	//	moveLine
	//
	//	the purpose of this method is to move the first line to the end of the file
	//
	//	input:	filename
	//	return:	none
	//
	public static void moveLine(String filename) throws IOException
	{
		// change to string
		String s1 = Modifier.toString(filename);
		StringBuilder sb = new StringBuilder();
		sb.append(s1);
		String s2 = "nothing yet";
		// find the first line and where it ends
		for(int i = 0; i <= s1.indexOf('\n'); ++i)
		{
			if(s1.charAt(i) == '\n')
			{
				s2 = s1.substring(0, i);
				sb.replace(0, i, "");
			}
		}
		
		// append it to the string builder and write to the file
		sb.append(s2);
		String statement = sb.toString();
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
		pw.println(statement);
		pw.close();
		
		System.out.println("The first line has been moved to the end of the file");
		
	}
	
	//
	//	toString
	//
	//	the purpose of this method is to convert the contents of the file to a string
	//
	//	input:	filename
	//	return:	statement
	//
	public static String toString(String filename) throws IOException
	{
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		
		// turn it into a string using Stringbuilder
		StringBuilder sb = new StringBuilder();
		String line;
		String ls = System.getProperty("line.separator");
		
		while((line = br.readLine()) != null)
		{
			sb.append(line);
			sb.append(ls);
		}
		// delete last line separator
		sb.deleteCharAt(sb.length() - 1);
		br.close();
		
		String statement = sb.toString();
		
		return statement;
	}
	
}// end Modifier
