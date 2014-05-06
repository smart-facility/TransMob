OutputSimData <- function(table.type, trip.mode, fn, events.filepath, syn.pop.filepath, veh.users.filepath) {

  # Creates an output table of the waiting time or journey time of trips within Sth Randwick
  # 
  # Args:
  #   table.type: either "wait" for waiting times or "travel" for travel times
  #   trip.mode: "car" or "bus"
  #   fn: function on the times, e.g. mean, min, max 
  #   events.filepath: The filepath location of the Transims Events output
  #   syn.pop.filepath: The filepath location of the synthetic population
  #   veh.users.filepath: The filepath location of the vehicle users

  events <- read.table(events.filepath, sep="\t", header=TRUE)
  syn.pop <- read.table(syn.pop.filepath, sep="\t", header=TRUE)
  veh.users <- read.table(veh.users.filepath, sep="\t", header=TRUE) 
  
  v.ind <- substr(as.character(veh.users$individual),1,nchar(as.character(veh.users$individual)))
  modes <- substr(as.character(veh.users$mode),1,nchar(as.character(veh.users$mode)))
    
  orig <- c(530, 532:558)
  dests <- c(530, 532:558, 1000001, 2000002, 3000003, 4000004, 5000005)

  fn <- match.fun(fn)

  if (table.type == "wait") {
    
    ##### WAITING TIMES #####
    wait <- matrix(0, length(orig), length(dests))

    for (i in orig)  {
      for (j in dests)  {
        indID <- which(syn.pop$origin == i & syn.pop$destination == j)
        vid <- veh.users$vehicle[is.element(v.ind, as.character(syn.pop$id[indID])) & modes==trip.mode]
        wait[which(orig==i), which(dests==j)] <- fn(events$ACTUAL[is.element(events$HOUSEHOLD, vid) & events$EVENT == 0] - events$SCHEDULE[is.element(events$HOUSEHOLD, vid) & events$EVENT == 0])
      }
    }
    wait[is.na(wait) | wait == -Inf | wait == Inf] <- 1e9 

    return(wait)

  } else if (table.type == "travel") {

    ##### TRAVEL TIMES #####
    travel <- matrix(0, length(orig), length(dests))

    for (i in orig)  {
      for (j in dests)  {
        indID <- which(syn.pop$origin == i & syn.pop$destination == j)
        vid <- veh.users$vehicle[is.element(v.ind, as.character(syn.pop$id[indID])) & modes==trip.mode]
        travel[which(orig==i), which(dests==j)] <- fn(events$ACTUAL[is.element(events$HOUSEHOLD, vid) & events$EVENT == 2])     
      }                                                
    }

    travel[is.na(travel) | travel == -Inf | travel == Inf] <- 1e9     

    return(travel)

  } else {

    return("Not a valid table type, use 'wait' or 'travel'")
   
   }
}

#syn.pop.filepath <- "C:/Documents and Settings/jdawber/My Documents/James Documents/ABUM/synthetic_population.txt"
#veh.users.filepath <- "C:/Documents and Settings/jdawber/My Documents/James Documents/ABUM/vehicle_users.txt"