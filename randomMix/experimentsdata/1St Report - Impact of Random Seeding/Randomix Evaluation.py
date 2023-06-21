import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

data = pd.read_csv("experiments_t3.csv")
print (data)

# Remove all rows having 'timeout' in the TSSize column
data = data[data.TSSize != 'timeout'] 

# For each different FileName in the data, plot the TSSize
# against the SeedSize, all in the same figure
data['TSTime'] = data['TSTime'].astype(float)
data['TSSize'] = data['TSSize'].astype(float)

plt.figure()
for name in data.FileName.unique():
    plt.plot(data[data.FileName == name].SeedSize, data[data.FileName == name].TSSize, label=name)
    
# adding title to the plot
plt.title('TS Size vs Seed Size')
plt.yscale('log')
  
# adding Label to the x-axis
plt.xlabel('Seed Size')
plt.ylabel('TS Size')
plt.legend()
plt.show()

plt.figure()
for name in data.FileName.unique():
    plt.plot(data[data.FileName == name].SeedSize, data[data.FileName == name].TSTime, label=name)
    
# adding title to the plot
plt.title('TS Time vs Seed Size')
  
# adding Label to the x-axis
plt.xlabel('Seed Size')
plt.ylabel('Time (ms)')
plt.legend()

plt.show()

plt.figure()
for name in data.FileName.unique():
    plt.plot(data[data.FileName == name].SeedSize, data[data.FileName == name].UsedSeedSize, label=name)
    
# adding title to the plot
plt.title('Used Seed Size vs Seed Size')
  
# adding Label to the x-axis
plt.xlabel('Seed Size')
plt.ylabel('Used Seed Size')
plt.legend()

plt.show()


