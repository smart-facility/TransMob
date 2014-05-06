#Created: Fri, 29-07-2011
#Author: <a href="mailto:qun@uow.edu.au">Qun CHEN</a>
#
#Get average travel time for a certain travel zone
#

GetAveTravelTime <-  function(events.filepath, links.file){

events <- read.table(events.filepath, sep="\t", header=TRUE)

links <- read.csv(links.file, header = TRUE)$roadid

links.travel <- numeric(length(links))

 for (i in 1:length(links)) {
links.travel[i]<-mean(events$ACTUAL[as.numeric(as.character(abs(events$LINK))) == links[i]&events$EVENT == 2]) 
}


avgTravelTime=mean(links.travel[!is.na(links.travel)])
  return(avgTravelTime)

}