//===================================================================
// FileInterface.java
// 	This script handles the import of list of tapes or objects
// 	and provides them to the script as an ArrayList<String>
//
// 	Includes main()
//===================================================================

package com.socialvagrancy.blackpearl.utils;

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

public class FileInterface
{
	private FileManager files;

	public static ArrayList<String[]> importSingleColumnList(String listPath, Logger logbook, boolean printToShell, boolean debug)
	{
		ArrayList<String[]> list = new ArrayList<String[]>();
		String[] item;
		int count = 0;		

		if(printToShell || debug)
		{
			System.out.println("Building list...");
		}

		try
		{
			File inFile = new File(listPath);

			BufferedReader stdInput = new BufferedReader(new FileReader(inFile));

			String input = null;

			while((input = stdInput.readLine()) != null)
			{
				item = new String[2];

				item[0] = input;
				item[1] = "waiting";

				list.add(item);

				count++;
				
				if(debug)
				{
					System.out.println("Importing " + input 
						+ " with status " + item[1] + ".");
				}
			}
			
			stdInput.close();
		}
		catch(Exception e)
		{
			if(printToShell || debug)
			{
				System.out.println(e.getMessage());
			}

			logbook.logWithSizedLogRotation(e.getMessage(), 3);
		}

		if(count != list.size())
		{
			if(printToShell || debug)
			{
				System.out.println("Warning: list does not match number of copied items.");
			}
			
			logbook.logWithSizedLogRotation("WARN: list does not match number of copied items.", 2);
			logbook.logWithSizedLogRotation("Items counted: " + count + " List Size: " + list.size(), 2);
		}

		return list;
	}

	public static void updateSingleColumnInputFile(ArrayList<String[]> list, String input_path, String output_path, boolean debug)
	{
		FileManager outFiles = new FileManager();

		outFiles.createFileDeleteOld(input_path, debug);

		for(int i=0; i<list.size(); i++)
		{
			if(list.get(i)[1].equals("waiting"))
			{
				outFiles.appendToFile(input_path, list.get(i)[0]);
			}
			else
			{
				outFiles.appendToFile(output_path, list.get(i)[0] + "," + list.get(i)[1]);
			}
		}
	}

	public void printAll(ArrayList<String[]> list)
	{
		for(int i=0; i<list.size(); i++)
		{
			System.out.println(list.get(i)[0] + " " + list.get(i)[1]);
		}
	}
}
