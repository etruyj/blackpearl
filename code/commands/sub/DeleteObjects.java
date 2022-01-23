//===================================================================
// DeleteObjects.java
// 	Description:
// 		Handles the deletion of objects from the BlackPearl.
//===================================================================

package com.socialvagrancy.blackpearl.commands.sub;

import com.socialvagrancy.blackpearl.commands.BasicCommands;
import com.socialvagrancy.blackpearl.utils.FileInterface;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;

public class DeleteObjects
{
	public static void fromSingleColumnList(BasicCommands java_cli, String bucket, String input_path, String output_path, Logger logbook, boolean printToShell, boolean debug)
	{
		int success = 0;
		int failed = 0;

		logbook.logWithSizedLogRotation("Deleting objects from single column list [" + input_path + "].", 1);

		if(printToShell || debug)
		{
			System.out.println("Deleting objects from single column list [" + input_path + "].");
		
		}

		ArrayList<String[]> objectList = FileInterface.importSingleColumnList(input_path, logbook, printToShell, debug);

		logbook.logWithSizedLogRotation("List contains (" + objectList.size() + ") objects.", 1);

		System.out.println("List contains " + objectList.size() + " objects. Are you sure you want to delete all of them? Type DELETE ALL to confirm or DELETE to review each object.");
		String confirmation = System.console().readLine();

		boolean confirm = false;

		if(confirmation.equals("DELETE ALL"))
		{
			confirm = true;
		}

		if(confirmation.substring(0,6).equals("DELETE"))
		{
			for(int i=0; i<objectList.size(); i++)
			{
				logbook.logWithSizedLogRotation("Issuing delete command for " + bucket + "/" + objectList.get(i)[0] + "...", 1);
				
				if(printToShell || debug)
				{
					System.out.print("Issuing delete command for " + bucket + "/" + objectList.get(i)[0] + "...\t\t");
				}

				if(java_cli.deleteObject(bucket, objectList.get(i)[0], confirm))
				{
					logbook.logWithSizedLogRotation("delete successful", 1);
				
					if(printToShell || debug)
					{
						System.out.println("[SUCCESS]");
					}

					objectList.get(i)[1] = "deleted";

					success++;
				}
				else
				{	
					logbook.logWithSizedLogRotation("delete failed", 1);
					
					if(printToShell || debug)
					{
						System.out.println("[FAILED]");
					}

					objectList.get(i)[1] = "failed";

					failed++;
				}
			}
			
			logbook.logWithSizedLogRotation("Successfully completed " + success + "/" + objectList.size() + " deletions", 1);

			if(printToShell || debug)
			{
				System.out.println("Successfully completed " + success + "/" + objectList.size() + " deletions");
			}

			FileInterface.updateSingleColumnInputFile(objectList, input_path, output_path + "/object_deletions.csv", logbook, printToShell, debug);
		}
		else
		{
			logbook.logWithSizedLogRotation("Deletes cancelled by user input.", 1);
				
			if(printToShell || debug)
			{
				System.out.println("Deletes cancelled by user input.");
			}
		}	
	}
}
