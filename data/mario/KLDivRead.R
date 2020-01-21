fw = c(2,3,4,5)
fh = c(2,3,4,5)
s = c(1,1,1,1)
files = paste("KLDivLog-fw",fw,"-fH",fh,"-s",s,".txt", sep="")
col_names = c("level_id", "level_type", files)
data = matrix(0, nrow=320, ncol=length(col_names)) ##fixed nrow for now to make it easier
dist_mat_names = character(192)
dist_list = list()
dist_list_sym = list()
colnames(data) = col_names
for(f in files){
  dist_mat = matrix(0, nrow=192, ncol=192) ##fixed nrow for now to make it easier
  dist_mat_sym = matrix(0, nrow=192, ncol=192) ##fixed nrow for now to make it easier
  con = file(f, "r")
  id = 0
  col_dist = 1
  row_dist = 0
  prev = ""
  while ( TRUE ) {
    line = readLines(con, n = 1)
    if ( length(line) == 0 ) {
      break
    }else if (startsWith(line,"1-") && grepl("\t", line)){
      id=id+1
      kldiv = as.numeric(strsplit(line, "\t")[[1]][2])
      col = which(col_names==f)
      if(data[id,1]==0){
        data[id,1] =id
        type = strsplit(line, "\t")[[1]][1]
        type = as.numeric(strsplit(type, "-")[[1]][2])
        data[id,2]=type
      }
      data[id,col]=kldiv
     }else if (startsWith(line,"2") && grepl("\t", line) && !grepl("/app", line)){
       from = strsplit(line, "\t")[[1]][2]
       to = strsplit(line, "\t")[[1]][3]
       kldiv = as.numeric(strsplit(line, "\t")[[1]][4])
       if(from!=prev){
         col_dist=1
         row_dist=row_dist+1
         dist_mat_names[row_dist] = from
         prev=from
       }
       dist_mat[row_dist,col_dist] = kldiv
       dist_mat_sym[row_dist,col_dist] = dist_mat_sym[row_dist,col_dist]+kldiv
       dist_mat_sym[col_dist,row_dist] = dist_mat_sym[col_dist,row_dist]+kldiv
       col_dist = col_dist +1
     }
  }
  
  close(con)
  rownames(dist_mat) = dist_mat_names
  colnames(dist_mat) = dist_mat_names
  dist_list = c(dist_list, list(dist_mat))
  rownames(dist_mat_sym) = dist_mat_names
  colnames(dist_mat_sym) = dist_mat_names
  dist_list_sym = c(dist_list_sym, list(dist_mat_sym))
  
}

save(data, dist_list, dist_list_sym, file="KLDivData.RData")
