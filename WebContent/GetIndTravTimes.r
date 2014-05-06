GetIndTravTimes <- function(events.filepath, syn.pop.filepath, veh.users.filepath) {
  # Creates an output table of the Ind ID and their Actual Travel Time
  # 
  # Args:
  #   events.filepath: The filepath location of the Transims Events output
  #   syn.pop.filepath: The filepath location of the synthetic population
  #   veh.users.filepath: The filepath location of the vehicle users

  events <- read.table(events.filepath, header=TRUE)
  syn.pop <- read.table(syn.pop.filepath, sep="\t", header=TRUE)
  veh.users <- read.table(veh.users.filepath, sep="\t", header=TRUE) 
  
  events2 <- events[events$EVENT == 2,]
      
  ind.merge <- merge(veh.users, events2, by.x = "vehicle", by.y = "HOUSEHOLD", incomparables=NA)
  
  return(data.frame(I(as.character(ind.merge[, 3])),ind.merge[, 12]))

}