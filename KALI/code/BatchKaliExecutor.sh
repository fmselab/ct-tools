#!/bin/bash

nExec=3
nThreads=32
strength=2
timeout=300
orders=(IN_ORDER_SIZE_DESC IN_ORDER_SIZE_ASC RANDOM AS_DECLARED)
solvers=(SMTINTERPOL Z3 PRINCESS)
examplesFolder="./examples/ctcomp/*.ctw"

# Remove the old output file
rm "output.txt"

# Fetch all the files
for file in $examplesFolder; do
	# Use all the ordering strategies
	for order in ${orders[@]}; do
		# Test all solvers
		for solver in ${solvers[@]}; do
			# Repeat for three times
			for (( i=1; i<=$nExec; i++ )); do 
				# Execute the test with ordering sorting enabled
				timeout 300 java -jar KALI.jar $strength $file -sort -n $nThreads -order $order -solver $solver
				EXIT_STATUS=$?
				if [ $EXIT_STATUS -eq 124 ]; then
				echo "${file};NA;timeout;true;${order};${nThreads};${solver}" >> output.txt
				fi
				
				
				# Execute the test with ordering sorting disabled
				timeout 300 java -jar KALI.jar $strength $file -n $nThreads -order $order -solver $solver
				EXIT_STATUS=$?
				if [ $EXIT_STATUS -eq 124 ]; then
				echo "${file};NA;timeout;false;${order};${nThreads};${solver}" >> output.txt
				fi
			done
			sleep 1
			scp output.txt arera.unibg.it:kali_output_angelo.txt
			sleep 1
		done

	done

done
