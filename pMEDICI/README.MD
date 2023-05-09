# pMEDICI

pMEDICI is a multi-threaded tool for generating combinatorial test suites, based on a Multi-Valued Decision Diagrams.

## Folder content

- `code`: the source code of the pMEDICI tool
	- `.\examples`: some CTWedge model that can be used for testing pMEDICI

- `tool`: the folder containing the tool
	- `pMEDICI.jar`: the executable JAR for the pMEDICI tool
	
Dowload the jar file: https://github.com/fmselab/ct-tools/raw/main/pMEDICI/tool/pMEDICI.jar

## How to generate test cases with pMEDICI
Provided that you have the executable JAR of pMEDICI, it can be executed with the following command

`java -jar pMEDICI.jar [options] [strength] [CTWedge Model]`

Additional options can be set through the command line:
- `-n [int]` sets the number of threads used for test generation. By default, the number of threads is automatically determined by the HW architecture;
- `-verb` sets the verbose mode, which prints additional information on the console. By default pMEDICI execute in non-verbose mode;
- `-expand` sets the expand mode, i.e., if some partial tests is given as input and need to be completed. Note that no new test case wil be created if this mode is used. By default it is not enabled;
- `-old [fileName]` specifies a former test suite to be used as test seed. The file containing the previous test suite is a CSV file.
