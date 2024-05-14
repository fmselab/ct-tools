# Replication material for the paper "On the completion of partial combinatorial test suites"

This repository contains the replication material for the paper "On the completion of partial combinatorial test suites", submitted to the section "Combinatorial Methods and Models in System Testing" in SNCS

## Repository structure

* `src` contains the source code executing the experiments
  - `IWCT2023Test.java` test cases generating the data used for the analysis presented in the paper "Incremental generation of combinatorial test suites starting from existing seed tests" submitted to IWCT 2023
  - `SpecialIssueIWCT2023Test.java` test cases generating the data used for the analysis presented in the paper "On the completion of partial combinatorial test suites"
* `examples/SI_IWCT_2023_MODELS` contains the model we used for the experiments, in the following categories: UNIFORM_ALL, UNIFORM_BOOLEAN, MCA, BOOLC, and MCAC. All models have been generated with the [BenCIGen](https://github.com/fmselab/CIT_Benchmark_Generator/tree/main) benchmark generator.
* `experiments` contains experiments results and the script we used for the analysis
  - `AnalysisExperimentsSINC.ipynb` script executing the experiments in the SINC scenario
  - `AnalysisExperimentsTCCP.ipynb` script executing the experiments in the TCCP scenario
  - `AnalysisExperimentsTSCP.ipynb` script executing the experiments in the TSCP scenario
  - `pictures` the folder containing pictures of plots produced by the analysis scripts
  - `resultsSINC.csv` the results of the executions in the SINC scenario
  - `resultsTCCP.csv` the results of the executions in the TCCP scenario
  - `resultsTSCP.csv` the results of the executions in the TSCP scenario
