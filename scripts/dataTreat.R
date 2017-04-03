# Relative path where all the experiments are
filePath <- "/Users/josepdecidrodriguez/Dropbox/FIB/IA/Prac/LocalSearch/experiments/"

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

iterations1000 <- colMeans(subset(parametersCost, Total.iterations == 1000))["Cost"]
iterations2000 <- colMeans(subset(parametersCost, Total.iterations == 2000))["Cost"]
iterations3000 <- colMeans(subset(parametersCost, Total.iterations == 3000))["Cost"]
iterations4000 <- colMeans(subset(parametersCost, Total.iterations == 4000))["Cost"]
iterations5000 <- colMeans(subset(parametersCost, Total.iterations == 5000))["Cost"]
iterations6000 <- colMeans(subset(parametersCost, Total.iterations == 6000))["Cost"]
iterations7000 <- colMeans(subset(parametersCost, Total.iterations == 7000))["Cost"]
iterations8000 <- colMeans(subset(parametersCost, Total.iterations == 8000))["Cost"]
iterations9000 <- colMeans(subset(parametersCost, Total.iterations == 9000))["Cost"]
iterations10000 <- colMeans(subset(parametersCost, Total.iterations == 10000))["Cost"]

iterations <- c(iterations1000, iterations2000, iterations3000, iterations4000, iterations5000,
                iterations6000, iterations7000, iterations8000, iterations9000, iterations10000)

plot(seq(from = 1000, to = 10000, by = 1000), iterations)

### Experiment 4: Increments
incrementsHC <- read.table(paste(filePath, "increments/DataHC.txt", sep = ""), header = TRUE, sep = "\t")
incrementsHC4 <- c(colMeans(subset(incrementsHC, Centers == 4))["Time"])
incrementsHC6 <- c(colMeans(subset(incrementsHC, Centers == 6))["Time"])
incrementsHC8 <- c(colMeans(subset(incrementsHC, Centers == 8))["Time"])
incrementsHC10 <- c(colMeans(subset(incrementsHC, Centers == 10))["Time"])
incrementsHC12 <- c(colMeans(subset(incrementsHC, Centers == 12))["Time"])

incrementsHC <- c(incrementsHC4,incrementsHC6,incrementsHC8,incrementsHC10,incrementsHC12)

plot(x = c(4,6,8,10,12), y = incrementsHC, xlab = "Number of sensors", ylab = "Time", type = "b")

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
heuristic <- read.table(paste(filePath, "heuristic/Data.txt", sep = ""), header = TRUE, sep = "\t")

# Data parsing by equal weight
plotData <- c()
plotData <- c(plotData, colMeans(subset(heuristic, Weight == 1.0))[2:4])
plotData <- c(plotData, colMeans(subset(heuristic, Weight == 1.2))[2:4])
plotData <- c(plotData, colMeans(subset(heuristic, Weight == 1.4))[2:4])
plotData <- c(plotData, colMeans(subset(heuristic, Weight == 1.6))[2:4])
plotData <- c(plotData, colMeans(subset(heuristic, Weight == 1.8))[2:4])
plotData <- c(plotData, colMeans(subset(heuristic, Weight == 2.0))[2:4])
plotData <- c(plotData, colMeans(subset(heuristic, Weight == 2.2))[2:4])
plotData <- c(plotData, colMeans(subset(heuristic, Weight == 2.4))[2:4])
plotData <- c(plotData, colMeans(subset(heuristic, Weight == 2.6))[2:4])
plotData <- c(plotData, colMeans(subset(heuristic, Weight == 2.8))[2:4])

# Matrix data generator
plotDataMatrix <- matrix(plotData, ncol = 3, nrow = 10, byrow = TRUE)
xAxis <- seq(1.0, 2.8, by = 0.2)
rownames(plotDataMatrix) <- xAxis
colnames(plotDataMatrix) <- c("Cost", "Information", "Time")

# Plots
plot(x = xAxis, y = plotDataMatrix[,"Cost"], xlab = "Information Weight", ylab = "Cost", type = "o")
plot(x = xAxis, y = plotDataMatrix[,"Information"], xlab = "Information Weight", ylab = "Information", type = "o")
plot(x = xAxis, y = plotDataMatrix[,"Time"], xlab = "Information Weight", ylab = "Time(ms)", type = "o")

# Comparative with 100/4 sensors/centers
colMeans(subset(heuristic, Weight == 2.5))["Cost"] / colMeans(operatorsCost["Switch"])