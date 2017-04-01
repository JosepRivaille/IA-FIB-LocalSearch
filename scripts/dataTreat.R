# Relative path where all the experiments are
filePath <- "/Users/josepdecidrodriguez/Dropbox/FIB/IA/Prac/LocalSearch/experiments/"

# Experiment 1: Operators
operatorsTime <- read.table(paste(filePath, "operators/fileTime.txt", sep = ""), header = TRUE, sep = "\t")
operatorsCost <- read.table(paste(filePath, "operators/fileCost.txt", sep = ""), header = TRUE, sep = "\t")
operatorsInfo <- read.table(paste(filePath, "operators/fileTime.txt", sep = ""), header = TRUE, sep = "\t")

# Experment 2: Initial State
initialStateTime <- read.table(paste(filePath, "initialStates/fileTime.txt", sep = ""), header = TRUE, sep = "\t")
initialStateCost <- read.table(paste(filePath, "initialStates/fileCost.txt", sep = ""), header = TRUE, sep = "\t")
initialStateInfo <- read.table(paste(filePath, "initialStates/fileTime.txt", sep = ""), header = TRUE, sep = "\t")

# Experiment 3:
parametersTime <- read.table(paste(filePath, "parameters/fileTime.txt", sep = ""), header = TRUE, sep = "\t")
parametersCost <- read.table(paste(filePath, "parameters/fileTime.txt", sep = ""), header = TRUE, sep = "\t")
parametersInfo <- read.table(paste(filePath, "parameters/fileTime.txt", sep = ""), header = TRUE, sep = "\t")

# Experiment 4: Increments
incrementsTime <- read.table(paste(filePath, "increments/fileTime.txt", sep = ""), header = FALSE, sep = "\t")
incrementsCost <- read.table(paste(filePath, "increments/fileCost.txt", sep = ""), header = FALSE, sep = "\t")
incrementsInfo <- read.table(paste(filePath, "increments/fileInfo.txt", sep = ""), header = FALSE, sep = "\t")

incrementsInfo <- rowMeans(incrementsInfo)
xAxis <- seq(from = 100, to = 99 + length(incrementsInfo) * 50, by = 50)
plot(x = xAxis, y = incrementsInfo, xlab = "Number of sensors", ylab = "Cost", type = "b")


# Experiment 5: 

# Experiment 6: Data centers

# Experiment 7:

boxplot(operatorsCost)
