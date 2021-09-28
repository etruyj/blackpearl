# BlackPearl Scripts
A hodge-podge of scripts for automatic interactions with the Spectra Logic BlackPearl through the ds3_java_cli. These scripts were coded in order to solve a few disperate problems. Other than the solution implementing Spectra Logic's ds3_java_cli in order to interact with the BlackPearl, there is no specific relationship for them. This repository was created with the goal of warehousing the scripts and utilzing a few base packages for the core commands instead of re-inventing the wheel for each requirement.

All scripts rely on Spectra Logic's ds3_java_cli, which can be downloaded at developer.spectralogic.com/clients.

The most advanced version of Java which can be used for these scripts is 15. It seems like changes in the JDK prevent the ds3_java_cli from executing on newer JREs. Conversely, these scripts were written with OpenJDK 14, and won't execute on an older JRE.

## tapeEjection

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
  
  <b>Input File</b>
  
  The input file is just a single column of tape barcodes that can or cannot include a header. If a header is included, it must be bar_code.
  
  Sample: 

  bar_code  
  123456L8  
  124567JE
  
  
