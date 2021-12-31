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
		
		
		s.close();
	}

}
