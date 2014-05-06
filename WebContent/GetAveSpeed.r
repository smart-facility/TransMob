GetAveSpeed <- function(filepath, links.file)  {
  # Gets average speeds for links in peak traffic times.
  # 
  # Args:
  #   filepath: The filepath location of the Transims output
  #   e.g. "C:/My Documents/James Documents/ABM/Performance" 
  #         Use forward slash, NOT back slash!!!
  #   links.file: The filepath of link IDs 

# Edited: Sat, 2011-07-23
#Author: <a href="mailto:qun@uow.edu.au">Qun CHEN</a>
  
morningPeak <- c(7,9)
afternoonPeak <- c(16,18)

# Read in the results/6.Alex.performance
  speeds <- read.table(filepath, header = TRUE, fill = TRUE)
  speed <- speeds[!is.na(speeds$AVG_SPEED), ]   # Remove 4-column rows

#Gets records within the peroid of morning and afternoon peak  
  speed.peak <- speed[is.element(as.numeric(substr(speed$START_TIME, 1, 1)), morningPeak[1]:morningPeak[2]) 
                           | is.element(as.numeric(substr(speed$START_TIME, 1, 2)), afternoonPeak[1]:afternoonPeak[2]), ]

#Reads the link_lists/53(0|[2-9])|54\d|55[0-8].csv  
  links <- read.csv(links.file, header = TRUE)$roadid

    
  link.speeds <- numeric(length(links))    # create empty vector

#Assigns mean AVG_SPEED of a centain link  
  for (i in 1:length(links)) {
    link.speeds[i] <- mean(speed.peak$AVG_SPEED[as.numeric(as.character(speed.peak$LINK)) == links[i]]) 
  }
  
  # Returns a matrix with two rows. The first is the link ID, the second
  #  is the average speed in peak hours (7-9am, 4-6pm).

avgSp=mean(link.speeds[!is.na(link.speeds)])
  return(avgSp)
} 

# filename <- "C:/Documents and Settings/jdawber/My Documents/James Documents/ABUM/Performance" 
# links <- sample(as.numeric(as.character(speed$LINK)),30)
   