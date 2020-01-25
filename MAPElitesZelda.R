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
library(grid)
library(gplots)
library(gridGraphics)
library(gridExtra)
library(RColorBrewer)

my_palette <- heat.colors(100, rev=TRUE)

# Making a 10 by 10 grid of heatmaps in R seems overkill.

#grab_grob <- function(){
#  grid.echo()
#  grid.grab()
#}

#gl <- lapply(1:10, function(i){

  i <- 50
  
  room <- allData[allData$roomBin == i, ]
  room$roomBin <- NULL ## All room entries are the same now
  room[room == -Inf] <- -0.5 ## Convert empty bins
  room <- acast(room, wallBin~waterBin, value.var="PercentTraversed")
  
  pdf(file="Rooms50.pdf",width=100,height=100)
  # Despite showing x/y labels in R Studio, the PDF version of this lacks them
  heatmap.2(room, 
            key=F,
            dendrogram="none", 
            col=my_palette, 
            Colv=NA, Rowv=NA,  
            scale="none", 
            tracecol="black", 
            trace="none", 
            rowsep = c(0:10),
            colsep = c(0:10),
            sepcolor = "black",
            lwid=c(0.01,4), lhei=c(0.01,4),
            #trace="both", 
            #xlab="50 rooms",
            hline=NA, vline=NA)

  #text(0.1,0.9,"50 rooms")
  
  dev.off()
  
#  grab_grob()
#})

#grid.newpage()
#grid.arrange(grobs=gl, ncol=5, clip=TRUE)

###############################################

# Trying a different approach here
  
library(ggplot2)
library(dplyr)

dropRooms0 <- filter(allData, roomBin > 0)
  
ggplot(dropRooms0, aes(x=waterBin, y=wallBin, fill=PercentTraversed)) +
  geom_tile() +
  facet_wrap(~roomBin) +
  xlab("Water Percentage Bin") +
  ylab("Wall Percentage Bin") +
  labs(fill = "Traversed") +
  #guides(fill=guide_legend(title="Traversed")) +
  theme(strip.background = element_blank(),
        strip.text = element_blank(),
        panel.spacing.x=unit(0.01, "points"),
        panel.spacing.y=unit(0.01, "points"),
        axis.ticks = element_blank(),
        axis.text = element_blank())
  

