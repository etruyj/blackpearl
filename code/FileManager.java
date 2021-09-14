//=========================================================================
// FileManager
// 	Description: Handles reads to and writes from the file.
//=========================================================================

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileManager
{
	public boolean appendToFile(String path, String csv)
	{
		String line = "\n" + csv;
		try
		{
			Files.write(Paths.get(path), line.getBytes(), StandardOpenOption.APPEND);
		}
		catch (NoSuchFileException nada)
		{
			System.out.println(nada);
			System.out.println("Creating file " + path);
			// File doesn't exist
			try
			{
				// Write headers and try again.
				String headers = "Date,Time,Size of Bucket,Migrated";

				Files.write(Paths.get(path), headers.getBytes(), StandardOpenOption.CREATE);
				return appendToFile(path, csv);
			}
			catch(Exception f)
			{
				System.out.println(f.getMessage());
			}

		}
		catch (IOException e)
		{
			e.getMessage();
			e.printStackTrace();
			
			return false;
		}

		return true;
	}

	public String getLastLineOf(String path)
	{
		// Get just the last line of the file.
		String lastLine = "none";

		try
		{
			File inFile = new File(path);

			BufferedReader stdInput = new BufferedReader(new FileReader(inFile));

			String input = null;

			while((input = stdInput.readLine()) != null)
			{
				lastLine = input;
			}

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return lastLine;
	}
}
