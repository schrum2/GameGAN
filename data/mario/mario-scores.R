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

plot_lines_priv = function(dat,...){
  colors <- rainbow(nrow(dat)) 
  plot(c(1,num_seg),range(dat), type="n", xlab="Segment",...)
  for(i in 1:nrow(dat)){
    lines(1:num_seg, dat[i,], col=colors[i])
  }
}


plot_lines = function(dat, which,...){
  if(which!=0){
    dat = dat[,seq(6+which, length(col_names),length(other_scores))]
    plot_lines_priv(dat,  ylab=other_scores[which],...)
  }else{
    for(j in 1:length(other_scores)){
      dat = dat[,seq(6+j, length(col_names),length(other_scores))]
      plot_lines_priv(dat,  ylab=other_scores[j],...)
    }  
  }
}


dist_fun = function(a,b){
  offset = - sum(a-b)/length(a)
  d = sum((a-b+offset)^2)
  return(d)
}

for(i in 1:length(other_scores)){
  plot_lines(data[1:100,],i)
  dat = data[1:100,seq(6+i, length(col_names),length(other_scores))]
  dist_mat = matrix(0, nrow=nrow(dat), ncol=nrow(dat))
  for(r in 1:nrow(dat)){
    for(c in 1:nrow(dat)){
      dist_mat[r,c] = dist_fun(dat[r,], dat[c,])
    }
  }
  
  hc = hclust(dist(dist_mat))
  plot(hc)
  res = cutree(hc,k=10)
  
  for(c in unique(res)){
    dat_t = dat[res==c,,drop=FALSE]
    plot_lines_priv(dat_t,ylab=other_scores[i])
    print(which(res==c))
  }
}


