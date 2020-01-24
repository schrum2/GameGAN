setwd("E:\\Users\\he_de\\workspace\\GameGAN")
map <- read.table("zeldacppntogan/MAPElites3/ZeldaCPPNtoGAN-MAPElites3_MAPElites_log.txt")
lastRow <- map[map$V1 == nrow(map) - 1, ]
archive <- data.frame(matrix(unlist(lastRow[2:length(lastRow)]), nrow=(length(lastRow)-1), byrow=T))
names(archive) <- "PercentTraversed"

wallBin <- append(rep(0, 10*101), rep(1, 10*101))
wallBin <- append(wallBin, rep(2, 10*101))
wallBin <- append(wallBin, rep(3, 10*101))
wallBin <- append(wallBin, rep(4, 10*101))
wallBin <- append(wallBin, rep(5, 10*101))
wallBin <- append(wallBin, rep(6, 10*101))
wallBin <- append(wallBin, rep(7, 10*101))
wallBin <- append(wallBin, rep(8, 10*101))
wallBin <- append(wallBin, rep(9, 10*101))

wallBin <- data.frame(wallBin)

waterBin <- append(rep(0, 101), rep(1, 101))
waterBin <- append(waterBin, rep(2, 101))
waterBin <- append(waterBin, rep(3, 101))
waterBin <- append(waterBin, rep(4, 101))
waterBin <- append(waterBin, rep(5, 101))
waterBin <- append(waterBin, rep(6, 101))
waterBin <- append(waterBin, rep(7, 101))
waterBin <- append(waterBin, rep(8, 101))
waterBin <- append(waterBin, rep(9, 101))

waterBin <- rep(waterBin, 10)
waterBin <- data.frame(waterBin)

roomBin <- rep(seq(0,100),10*10)
roomBin <- data.frame(roomBin)

allData <- data.frame(archive, wallBin, waterBin, roomBin)

library(reshape2)

room001 <- allData[allData$roomBin == 1, ]
room001$roomBin <- NULL
room001[room001 == -Inf] <- -1
room001 <- acast(room001, wallBin~waterBin, value.var="PercentTraversed")

heatmap(room001, Colv=NA, Rowv=NA, scale="none", revC=T)
text(0.7,-0.1,"1")
