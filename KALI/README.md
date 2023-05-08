# KALI
Replication package and repository for the paper "Multi-thread Combinatorial Test Generation with SMT solvers"

## Folder content

- `code`: the source code of the KALI tool
	- `.\examples`: some CTWedge model that can be used for testing KALI

- `documents`: all data and code used for evaluating the tool performance
	- `.\experiments.ipynb`: a Jupyter Notebook that contains all the tests and operations used for answering the research questions presented in the paper
	- `.\output.csv` and `.\outputWIndex.csv`: all results of the tests performed on the example benchmarks of the CT-Competition
	- `.\output_solvers.csv`: the data used for comparing different SMT solvers for test generation
	- `.\tools_comparison`: the python script and data used for comparing KALI with other tools such as pMEDICI and ACTS
		- `.\data_aggregator.py`: the python script that aggregates the data of all the execution for a single model
		- `.\best_results.csv`: the data containing, for each model, the best execution of each test generator
		- `.\aggregated_best.csv`: the aggregated data produced by the script `data_aggregator.py`
		- `.\aggregated_best.csv`: the aggregated data produced by the script `data_aggregator.py`, excluding the timeouts

## How to generate test cases with KALI
Provided that you have the executable JAR of KALI, it can be executed with the following command

`java -jar KALI.jar [strength] [CTWedge Model]`

Moreover, additional parameters can be set through the command line:
- `-n [int]` sets the number of threads used for test generation. By default, the number of threads is automatically determined by the HW architecture;
- `-verbose` sets the verbose mode, which prints additional information on the console. By default KALI execute in non-verbose mode;
- `-sort` activates the sort optimization. By default it is non active;
- `-order [NAME]` sets which kind of parameters ordering has to be used for tuple generation. It can be chosen between IN_ORDER_SIZE_DESC, IN_ORDER_SIZE_ASC, RANDOM, AS_DECLARED. By default IN_ORDER_SIZE_DESC is used;
- `-solver [NAME]` sets the SMT solver used by text contexts. It can be chosen between MATHSAT, SMTINTERPOL, Z3, PRINCESS, BOOLECTOR, CVC4, YICES2. By default, SMTINTERPOL is used.
