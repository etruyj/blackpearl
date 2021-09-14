# BlackPearl Scripts
A hodge-podge of scripts for automatic interactions with the Spectra Logic BlackPearl through the ds3_java_cli. These scripts were coded in order to solve a few disperate problems. Other than the solution implementing Spectra Logic's ds3_java_cli in order to interact with the BlackPearl, there is no specific relationship for them. This repository was created with the goal of warehousing the scripts and utilzing a few base packages for the core commands instead of re-inventing the wheel for each requirement.
All scripts rely on Spectra Logic's ds3_java_cli, which can be downloaded at developer.spectralogic.com/clients.
The most advanced version of Java which can be used for these scripts is 15. It seems like changes in the JDK prevent the ds3_java_cli from executing on newer JREs. Conversely, these scripts were written with OpenJDK 14, and won't execute on an older JRE.

# tapeEjection
This script parses a single column CSV file for tape barcodes and issues eject commands for those tapes. By default this script will only eject scratch tapes, but all specified tapes can be ejected with the --ignore-used-tapes flag.
