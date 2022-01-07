//
//	StringModifierClass
//
//	the purpose of this class is to write a program to create
//	and modify a file of text containing 50 words
//
//	Author:	Jafar Moukalled
//	Date:	10/20/2019
//
package stringModifierPackage;

import java.io.*;
import java.util.Scanner;

public class StringModifierClass
{

	public static void main(String[] args) throws IOException
	{
		
		Scanner s = new Scanner(System.in);
		
		// create a file of input with 50 words
		System.out.println("Input desired text: ");
		String s1 = s.nextLine();
		
		// ask for filename
		System.out.print("Enter a filename: ");
		String filename = s.nextLine();
		
		filename = filename.concat(".txt");
		
		Modifier.createFile((filename), s1);
		
		// check is the file exists
		Modifier.isFile(filename);
		
		// check if the file is readable
		Modifier.isReadable(filename);
		
		// count the number of chars in the file
		Modifier.countChars(filename);
		
		// determine file size in bytes
		long fileSize = Modifier.findSize(filename);
		
		// count the number of blocks
		// each block is 512 bytes
		final long BLOCK = 512;
		double numBlocks = (double)fileSize / (double)BLOCK;
		System.out.println("There are " + numBlocks + " blocks in the file.");
		
		// change the first word to upper case character
		Modifier.toUpperCase(filename);
		
		// count the number of 'a' and 'e' in the file
		Modifier.charCntr(filename);
		
		// replace the second word of the first line with a new word
		Modifier.replaceWord("file.txt", "favorite");
		
		// display file results
		String s2 = Modifier.toString(filename);
		System.out.println("\nWhat the file looks like so far:");
		System.out.println(s2);
		
		// add 10 more words using append
		Modifier.appendWords(filename);
		
		// find number of lines
		Modifier.findNumLines(filename);
		
		// convert file size in terms of bytes
		byte size = (byte)fileSize;
		System.out.println("The file is " + size + " bytes.");
		
		// delete the period of the last line
		Modifier.deleteChar(filename, '.');
		
		// move the first line of text to the end of the document
		Modifier.moveLine(filename);
		
		String s3 = Modifier.toString(filename);
		System.out.println("Final form of the file:");
		System.out.println("\n" + s3);
		s.close();
	}

}
