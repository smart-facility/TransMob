CreateTzTable <- function(filepath)  {
  # Creates table of liveability and congestion for the final time point, by TZ.
  #  Also creates overall Sth Randwick summary.
  #
  # Args:
  #   filepath: filepath of file with columns for liveability and congestion
  
  liv.con <- read.table(filepath, sep = "\t", header = TRUE)
  tz <- c(530, 532:558, "Total")
  all.liveability <- c(liv.con[, 1], mean(liv.con[,1]))
  all.congestion <- c(liv.con[, 2], mean(liv.con[,2]))
  
  return(data.frame(tz, all.liveability, all.congestion))

}  