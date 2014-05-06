OutputSimData <- function(table.type, trip.mode, fn, origin, destination, s.id, v.individual, vehicle, household, event, modes, actual, schedule) { 
  # Creates an output table of the waiting time or journey time of trips within Sth Randwick
  # 
  # Args:
  #   table.type: either "wait" for waiting times or "travel" for travel times
  #   trip.mode: "car" or "bus"
  #   fn: function on the times, e.g. mean, min, max 
  #   
  #  DATA COLUMNS: 
  #   origin:       synthesised_population: origin
  #   destination:  synthesised_population: destination
  #   s.id:         synthesised_population: id
  #   v.individual: vehicle_users: individual
  #   vehicle:      vehicle_users: vehicle
  #   household:    6.Alex: HOUSEHOLD
  #   event:        6.Alex: EVENTS
  #   modes:        vehicle_users: mode
  #   actual:       6.Alex: ACTUAL
  #   schedule:     6.Alex: SCHEDULE

  orig <- c(530, 532:558)
  dests <- c(530, 532:558, 2000002, 3000003, 4000004, 5000005)

  fn <- match.fun(fn)

  if (table.type == "wait") {
    
    ##### WAITING TIMES #####
    wait <- matrix(0, length(orig), length(dests))

    for (i in orig)  {
      for (j in dests)  {
        indID <- which(origin == i & destination == j)
        vid <- vehicle[is.element(v.individual,as.character(s.id[indID])) & modes==trip.mode]
        wait[which(orig==i), which(dests==j)] <- fn(actual[is.element(household, vid) & event == 0] - schedule[is.element(household, vid) & event == 0])
      }
    }
    wait[is.na(wait) | wait == -Inf | wait == Inf] <- 1e9 

    return(wait)

  } else if (table.type == "travel") {

    ##### TRAVEL TIMES #####
    travel <- matrix(0, length(orig), length(dests))

    for (i in orig)  {
      for (j in dests)  {
        indID <- which(origin == i & destination == j)
        vid <- vehicle[is.element(v.individual,as.character(s.id[indID])) & modes==trip.mode]
        travel[which(orig==i), which(dests==j)] <- fn(actual[is.element(household, vid) & event == 2])     
      }                                                
    }

    travel[is.na(travel) | travel == -Inf | travel == Inf] <- 1e9     

    return(travel)

  } else {

    return("Not a valid table type, use 'wait' or 'travel'")
   
   }
}
