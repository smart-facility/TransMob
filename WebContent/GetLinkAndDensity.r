GetLinkAndDensity <- function(filepath)  {
  # Gets Link ID and its Density from the Performance Output
  # 
  # Args:
  #   filepath: The filepath location of the Transims output
  #   e.g. "C:/My Documents/James Documents/ABM/Performance" 
  #         Use forward slash, NOT back slash!!!


# Edited 2011-07-22
# Author: <a href="mailto:qun@uow.edu.au">Qun CHEN</a>
  
morningPeak <- c(7,9)
afternoonPeak <- c(16,18)


  performance <- read.table(filepath, header = TRUE, fill = TRUE)
  performance <- performance[!is.na(performance$AVG_DENSITY), ]   # Remove 4-column rows
#Remove the rows AVG_DENSITY is NA
  
  perf.peak <- performance[is.element(as.numeric(substr(performance$START_TIME, 1, 1)), morningPeak[1]:morningPeak[2]) 
                           | is.element(as.numeric(substr(performance$START_TIME, 1, 2)), afternoonPeak[1]:afternoonPeak[2]), ]
# find rows in peak time
  
  link.id <- sort(unique(as.numeric(as.character(perf.peak$LINK))))
# sort link id within the peak time  
  link.density <- numeric(length(link.id))    # create empty vector
   
  for (i in 1:length(link.id)) {
    link.density[i] <- mean(perf.peak$AVG_DENSITY[as.numeric(as.character(perf.peak$LINK)) == link.id[i]])                      
  }# traverse the whole road density and fill an array with the mean of same link 
  
  return(cbind(link.id, link.density))# a 2d array with pairs of link id and its mean density
}
  