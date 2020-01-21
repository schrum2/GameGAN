load("KLDivData.RData")
library(plotly)
library(plyr)

id = rep(data[,1], 4)
type = as.factor(rep(data[,2],4))
type= revalue(type, c("1"="train-s", "2"="end-s", "3"="start-s", "4"="end-u", "5"="end-t", "6"="not", "7"="end", "8"="start"))
kldiv = c(data[,3:6])
file = as.factor(rep(1:4, rep(nrow(data),4)))
file = revalue(file, c("1"=colnames(data)[3], "2"=colnames(data)[4], "3"=colnames(data)[5],"4"=colnames(data)[6]))

df = data.frame(id=id, type=type, file=file, kldiv=kldiv)

df[df$type=="end-t",]


vline <- function(x = 0, color = "blue") {
  list(
    type = "line", 
    y0 = 0, 
    y1 = 1, 
    yref = "paper",
    x0 = x+0.5, 
    x1 = x+0.5, 
    line = list(color = color, dash="dot", width=1)
  )
}

start = sum(df$type=="train-s")/4

p = plot_ly(data=df, x= ~id, y=~kldiv, type='scatter',
            mode= 'markers', symbol=~file, symbols = c('circle','x','o','square'), color=~type) %>%
  layout(shapes=list(vline(start, color="black"), vline(start+1*20), vline(start+2*20),vline(start+3*20, color="black"),
                     vline(start+4*20), vline(start+5*20), vline(start+6*20, color="black"),
                     vline(start+7*20), vline(start+8*20), vline(start+9*20, color="black"),
                     vline(start+10*20), vline(start+11*20, color="black"),
                     vline(start+12*20), vline(start+13*20, color="black"),
                     vline(start+14*20), vline(start+15*20, color="black")))

p


p = plot_ly(data=df, x=~type, y=~kldiv, type='scatter',
            mode='markers', color=~file)

#p = plot_ly(data=df, x=~file, y=~kldiv, type='scatter',
#            mode='markers', color=~type)

p

colLab <- function(n) {
  if (is.leaf(n)) {
    a <- attributes(n)
    if(startsWith(a$label, "i")){
      attr(n, "nodePar") <- c(a$nodePar, lab.col="black")
    }else{
      attr(n, "nodePar") <- c(a$nodePar, lab.col="red")
    }
  }
  n
}

dist_mat = dist(dist_list_sym[[1]])
hc = hclust(dist_mat)
clus = dendrapply(as.dendrogram(hc), colLab)
plot(clus)


plot(hc,  color=c(rep("red",12), rep("black",180)))
dend1 <- as.dendrogram(hc)
plot_dendro(dend1)  %>% 
  highlight(persistent = TRUE, dynamic = TRUE)

#df
## id level_type file kl_div