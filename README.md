# BlackPearl Scripts
A hodge-podge of scripts for automatic interactions with the Spectra Logic BlackPearl through the ds3_java_cli. These scripts were coded in order to solve a few disperate problems. Other than the solution implementing Spectra Logic's ds3_java_cli in order to interact with the BlackPearl, there is no specific relationship for them. This repository was created with the goal of warehousing the scripts and utilzing a few base packages for the core commands instead of re-inventing the wheel for each requirement.

All scripts rely on Spectra Logic's ds3_java_cli, which can be downloaded at developer.spectralogic.com/clients.

The most advanced version of Java which can be used for these scripts is 15. It seems like changes in the JDK prevent the ds3_java_cli from executing on newer JREs. Conversely, these scripts were written with OpenJDK 14, and won't execute on an older JRE.

## Scripts
- database downloader: (spectraLogs) downloads the most recent database from the specified bucket and saves it to a target folder. Used for creating additional off-site backups of the database.
- migration verification: (bp_verify) Verifies second copies of assets are available to the BlackPearl by ejecting tapes that were specified in an export list and then initiates a restore request for assets stored on that tape.
- eject tapes: (bp_ejects) Ejects tapes that were included in a specified export list.

## Database Download (spectraLogs)

This script is designed to automate the downloading of BlackPearl database to a target directory. Files are renamed to match a specified prefix and the date of the file in prefix-YYYY-MM-DD.tar.xz format. Can be used with either cron or Windows Task Scheduler.

Sample: `./spectraLogs --ds3 /path/to/ds3_java_cli-5.1.2/bin -e 10.10.10.7 -a aCcEss -k 5eCr3T --bucket Spectra-Database-Backup-50030412cfebff --save-path /path/to/watch/dir --file-prefix BP1` 

#### Commands:

	--access-key	BlackPearl user access key. Can also use (-a)
	--bucket	BlackPearl database backup bucket name. Can also use (-b)
	--debug		Prints more detailed info to shell including commands and jsons.
	--ds3		Path to the ds3_java_cli bin directory. Omit the final /
	--endpoint	Data IP address of the BlackPearl. Can also use (-e)
	--file-prefix	Prefix to be used when naming the downloaded logset.
	--help		Print help commands. Can also use (-h)
	--print		Prints some information to the shell.
	--save-path	Directory to save the database backup Omit the final /
	--secret-key	BlackPearl user secret key. Can also use (-k)

## Migration Verification (bp_verify)

This script automates migration verification by ejecting tapes to be removed/deleted and then initiating restore requests to those tapes in order to verify the objects are accessible in a different location on the BlackPearl. A random group of tapes, specified by the --max-moves flag, are chosen from a provided list of tapes. After those tapes are moved to the corresponding library's EE slots, restore requests are issued for a number of files determined by the --restores flag. If all the files are restored successfully or if all files on the tape are restored successfully, the tape is okayed for ejection/reformat. If some of the files could not be restored, the check is failed. A report of each test is saved in the ../output directory.

BlackPearl performs CRC checks whenever an object enters cache. If the object is restored successfully, it will have passed this check and the integretity of the data will have been verified.

As this uses Spectra's ds3_java_cli, BlackPearl S3 credentials can be sourced from a resource file. More information can be found at developer.spectralogic.com/clients/

Sample: `./bp_verify --ds3 /path/to/ds3_java_cli-5.1.2/bin -e 10.10.10.7 -a AcCesS -k 5eCret --http --input-file /path/to/tapes-list.csv --moves 10 --restores 10 --max-size 50M --print --save-path /path/to/test/dir`

#### Commands:

	--access-key	BlackPearl access key for the desired user. (-a) also works.
	--debug		Messages and commands are printed to the shell.
	--ds3		specifiy the path to the ds3_java_cli. Omit final /. Example: path/to/bin
	--endpoint	BlackPearl data IP address. (-e) also works.
	--http		Use an insecure, non-SSL connection.
	--input-file	Path to the file holding the list of tapes.
	--max-moves	Maximum number of moves to execute. (-m/--moves) also work. This should not exceed the number of available entry exit slots.
	--max-size	Maximum size of the files to be restored. If the tape has less assets below the specified size than requested in restores, the tape will fail the check. Specify unit as P, T, G, M, K, or B without a space between teh number and unit (e.g. 50M).  --size also works.
	--print		Messages are printed to the shell.
	--restores	Number of restores to perform per tape.
	--save-path	Target location for the test restore files.

 #### Input File</b>
  
  The input file is just a single column of tape barcodes that can or cannot include a header. If a header is included, it must be bar_code.
  
  Sample: 

  bar_code  
  123456L8  
  124567JE

## Tape Ejection (bp_ejects)

This script automates the ejection of tapes from Spectra Logic's BlackPearl system via Spectra Logic's ds3_java_cli. Tapes are provided to the script in a single column CSV file along with the number of moves to execute. The number of moves specified should not exceed the number of available Entry/Exit (EE) slots in your tape library. This script does support the use of resource files on Linux systems. (I'm not quite sure how to load them on Windows.)

Sample: `./bp_ejects --ds3 /Users/foo/Downloads/ds3_java_cli-5.1.2/bin -e 10.10.10.7 -a AcCeS5 -k 5eCret --http --input-file tape-list.csv --moves 10 --print` 

##### Commands

	--access-key	BlackPearl access key for the desired user. (-a) also works. 
  
	--debug		Messages and commands are printed to the shell.
  
	--ds3		specifiy the path to the ds3_java_cli. Omit final /. Example: path/to/bin
  
	--endpoint	BlackPearl data IP address. (-e) also works.
  
 	--help		Prints help menu.
	
	--http		Use an insecure, non-SSL connection.
  
 	--input-file	Path to the file holding the list of tapes.
  
	--max-moves	Maximum number of moves to execute. (-m/--moves) also work.
  
	--print		Messages are printed to the shell. 
  
 #### Input File</b>
  
  The input file is just a single column of tape barcodes that can or cannot include a header. If a header is included, it must be bar_code.
  
  Sample: 

  bar_code  
  123456L8  
  124567JE
  
  
