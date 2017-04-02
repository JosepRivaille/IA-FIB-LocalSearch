# Relative path where all the experiments are
filePath <- "../experiments/"

### Experiment 1: Operators
operatorsCost <- read.table(paste(filePath, "operators/Cost.txt", sep = ""), header = TRUE, sep = "\t")
operatorsExps <- read.table(paste(filePath, "operators/Expansions.txt", sep = ""), header = TRUE, sep = "\t")
operatorsInfo <- read.table(paste(filePath, "operators/Information.txt", sep = ""), header = TRUE, sep = "\t")
operatorsTime <- read.table(paste(filePath, "operators/Time.txt", sep = ""), header = TRUE, sep = "\t")

# Boxplots
boxplot(operatorsCost)
boxplot(operatorsExps)
boxplot(operatorsTime)

### Experment 2: Initial State
initialStateCost <- read.table(paste(filePath, "initialStates/Cost.txt", sep = ""), header = TRUE, sep = "\t")
initialStateExps <- read.table(paste(filePath, "initialStates/Expansions.txt", sep = ""), header = TRUE, sep = "\t")
initialStateInfo <- read.table(paste(filePath, "initialStates/Information.txt", sep = ""), header = TRUE, sep = "\t")
initialStateTime <- read.table(paste(filePath, "initialStates/Time.txt", sep = ""), header = TRUE, sep = "\t")

# Mean by initial state algorithm
colMeans(initialStateCost)
colMeans(initialStateExps)
colMeans(initialStateTime)

# Charts initial states
boxplot(initialStateCost)
boxplot(initialStateExps)
boxplot(initialStateTime)

### Experiment 3:
parametersTime <- read.table(paste(filePath, "parameters/Time.txt", sep = ""), header = TRUE, sep = "\t")
parametersCost <- read.table(paste(filePath, "parameters/Cost.txt", sep = ""), header = TRUE, sep = "\t")
parametersInfo <- read.table(paste(filePath, "parameters/Information.txt", sep = ""), header = TRUE, sep = "\t")

# Data parsing by equal k and lambda
plotData <- c()
plotData <- c(plotData, colMeans(subset(parametersCost, k == 1 & lambda == 0.001))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 1 & lambda == 0.01))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 1 & lambda == 0.1))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 1 & lambda == 1))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 5 & lambda == 0.001))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 5 & lambda == 0.01))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 5 & lambda == 0.1))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 5 & lambda == 1))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 25 & lambda == 0.001))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 25 & lambda == 0.01))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 25 & lambda == 0.1))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 25 & lambda == 1))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 125 & lambda == 0.001))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 125 & lambda == 0.01))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 125 & lambda == 0.1))["Cost"])
plotData <- c(plotData, colMeans(subset(parametersCost, k == 125 & lambda == 1))["Cost"])

# Matrix data generator
plotDataMatrix <- matrix(plotData, ncol = 4, nrow = 4, byrow = TRUE)
rownames(plotDataMatrix) <- c(1, 5, 25, 125)
colnames(plotDataMatrix) <- c(0.001, 0.01, 0.1, 1)

# Histograms
hist3D(z=plotDataMatrix, border="black", xaxt = "n", yaxt = "n", xlab = "K", ylab = "Lambda", zlab = "Cost")
plotdev(theta = -65, phi = 30)
image2D(z=plotDataMatrix, border="black", xaxt="n", yaxt="n", xlab = "K", ylab = "Lambda")

### Experiment 4: Increments
incrementsTime <- read.table(paste(filePath, "increments/fileTime.txt", sep = ""), header = FALSE, sep = "\t")
incrementsCost <- read.table(paste(filePath, "increments/fileCost.txt", sep = ""), header = FALSE, sep = "\t")
incrementsInfo <- read.table(paste(filePath, "increments/fileInfo.txt", sep = ""), header = FALSE, sep = "\t")

incrementsInfo <- rowMeans(incrementsInfo)
xAxis <- seq(from = 100, to = 99 + length(incrementsInfo) * 50, by = 50)
plot(x = xAxis, y = incrementsInfo, xlab = "Number of sensors", ylab = "Cost", type = "b")

### Experiment 5: 

### Experiment 6: Data centers
dataCentersHC <- read.table(paste(filePath, "dataCenters/HillClimbing.txt", sep = ""), header = TRUE, sep = "\t")
dataCentersSA <- read.table(paste(filePath, "dataCenters/SimulatedAnnealing.txt", sep = ""), header = TRUE, sep = "\t")

# Mean of replications
dataCentersHCCost <- colMeans(subset(dataCentersHC, select = c("Cost1", "Cost2", "Cost3", "Cost4")))
dataCentersHCTime <- colMeans(subset(dataCentersHC, select = c("Time1", "Time2", "Time3", "Time4")))
dataCentersHCUses <- colMeans(subset(dataCentersHC, select = c("UC1", "UC2", "UC3", "UC4")))
dataCentersSACost <- colMeans(subset(dataCentersSA, select = c("Cost1", "Cost2", "Cost3", "Cost4")))
dataCentersSATime <- colMeans(subset(dataCentersSA, select = c("Time1", "Time2", "Time3", "Time4")))
dataCentersSAUses <- colMeans(subset(dataCentersSA, select = c("UC1", "UC2", "UC3", "UC4")))
xAxis = c(4, 6, 8, 10)

# Plots with Hill Climbing
plot(x = xAxis, y = dataCentersHCCost, xlab = "Centers", ylab = "Cost", type = "o")
plot(x = xAxis, y = dataCentersHCTime, xlab = "Centers", ylab = "Time(ms)", type = "o")
plot(x = xAxis, y = dataCentersHCUses, xlab = "Centers", ylab = "Used centers", type = "o")

# Plots with Hill Simmulated Annealing
plot(x = xAxis, y = dataCentersSACost, xlab = "Centers", ylab = "Cost", type = "o")
plot(x = xAxis, y = dataCentersSATime, xlab = "Centers", ylab = "Time(ms)", type = "o")
plot(x = xAxis, y = dataCentersSAUses, xlab = "Centers", ylab = "Used centers", type = "o")

### Experiment 7: