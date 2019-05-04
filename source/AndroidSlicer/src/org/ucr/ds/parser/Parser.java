package org.ucr.ds.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Parser {

	
	public static void parse(String arg, int line, int block, int seq) throws IOException
	{
		String token = ",";
//		String splittedString[] = arg.split(token);
		List <String> lines = FileUtils.readLines(new File(arg), Charset.defaultCharset());
		//get block number, lets this represents line
		
		for (String line1: lines)
		{
			//get the block number and line number
			//gather seq no of sequences of block number of blocks
			
			//now create an array of objects
			
		}
	}
}
