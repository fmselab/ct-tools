import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

data = pd.read_csv("experiments_t4.csv")
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

# draw a boxplot to show the distribution of the cRnd column per SeedSize
data['cRnd'] = pd.to_numeric(data['cRnd'])
data.boxplot(column='cRnd', by='SeedSize')
plt.title('cRnd vs Seed Size')
# on the x-axis use a step of 10
plt.xticks(np.arange(1, 200, 10))
plt.ylabel('cRnd')
plt.xlabel('Seed Size')
plt.show()


# draw a boxplot to show the distribution of the cInc column per SeedSize
data['cInc'] = pd.to_numeric(data['cInc'])
data.boxplot(column='cInc', by='SeedSize')
plt.title('cInc vs Seed Size')
# on the x-axis use a step of 10
plt.xticks(np.arange(1, 200, 10))
plt.ylabel('cInc')
plt.xlabel('Seed Size')
plt.show()

# draw a boxplot to show the distribution of the cRnd column divided for the totTuples column per SeedSize
data['totTuples'] = pd.to_numeric(data['totTuples'])
data['cRnd/totTuples'] = data['cRnd']/data['totTuples']
data.boxplot(column='cRnd/totTuples', by='SeedSize')
plt.title('Relative cRnd vs Seed Size')
# on the x-axis use a step of 10
plt.xticks(np.arange(1, 200, 10))
plt.ylabel('cRnd')
plt.xlabel('Seed Size')
plt.show()

# draw a boxplot to show the distribution of the cInc column divided for the totTuples column per SeedSize
data['cInc/totTuples'] = data['cInc']/data['totTuples']
data.boxplot(column='cInc/totTuples', by='SeedSize')
plt.title('Relative cInc vs Seed Size')
# on the x-axis use a step of 10
plt.xticks(np.arange(1, 200, 10))
plt.ylabel('cInc')
plt.xlabel('Seed Size')
plt.show()