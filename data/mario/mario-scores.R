library(plotly)

num_seg = 30
files = list.files(".", full.names = TRUE)
other_scores = c("decorationFrequency", "leniency", "negativeSpace")
col_names = c("id", "idPop","Distance", "PercentDistance", "Time", "Jumps",
              paste(rep(c(1:num_seg), each=length(other_scores)), rep(other_scores,num_seg), sep="-"))
data = matrix(0, nrow=length(files)*100, ncol=length(col_names))
i=1
for(file in files){
  con = file(file, "r")
  while ( TRUE ) {
    line = readLines(con, n = 1)
    if ( length(line) == 0 ) {
      break
    }else{
      data[i,] = as.numeric(strsplit(line, "\t")[[1]])
      i=i+1
    }
  }
  close(con)
}


for(j in 1:length(other_scores)){
  dat = data[1:100,seq(6+j, length(col_names),length(other_scores))]
  colors <- rainbow(nrow(dat)) 
  
  plot(c(1,num_seg),range(dat), type="n", xlab="Segment", ylab=other_scores[j])
  for(i in 1:nrow(dat)){
    lines(1:num_seg, dat[i,], col=colors[i])
  }  
}

dist_fun = function(a,b){
  offset = - sum(a-b)/length(a)
  d = sum((a-b+offset)^2)
  return(d)
}

dat = data[1:100,seq(6+1, length(col_names),length(other_scores))]
dist_mat = matrix(0, nrow=nrow(dat), ncol=nrow(dat))
for(i in 1:nrow(dat)){
  for(j in 1:nrow(dat)){
    dist_mat[i,j] = dist_fun(dat[i,], dat[j,])
  }
}

hc = hclust(dist(dist_mat))
plot(hc)
