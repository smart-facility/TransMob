GetPostgreSQLStats <- function(field, tablename, db_name, username, pswd) { 
   
  library(RPostgreSQL) # Load package to link with PostgreSQL

  # Set driver to PostgreSQL
  drv <- dbDriver("PostgreSQL")

  # Create and open a connection to the database implemented by the driver drv. 
  con <- dbConnect(drv, dbname=deparse(substitute(db_name)), 
                          user=deparse(substitute(username)), 
                          password=deparse(substitute(pswd)))  

  dframe <- dbReadTable(con, deparse(substitute(tablename)))  # Read in table
  attach(dframe)
  stattab <- data.frame(text = I(c(paste(deparse(substitute(field)),"_mean",sep=""),
                        paste(deparse(substitute(field)),"_var",sep=""))), 
            stats = c(mean(field),var(field))) 
  detach(dframe)
  dbDisconnect(con)   # Close the connection
  dbUnloadDriver(drv) # Frees all resources on the driver
  return(stattab)                         
}


GetHist <- function(field, tablename, db_name, username, pswd, path) {
  library(RPostgreSQL) # Load package to link with PostgreSQL
  setwd(path) #Set directory to desired path
  # Set driver to PostgreSQL
  drv <- dbDriver("PostgreSQL")

  # Create and open a connection to the database implemented by the driver drv. 
  con <- dbConnect(drv, dbname=deparse(substitute(db_name)), 
                          user=deparse(substitute(username)), 
                          password=deparse(substitute(pswd)))  

  dframe <- dbReadTable(con, deparse(substitute(tablename)))  # Read in table
  attach(dframe)
  # Plots and saves a histogram and then returns the filepath location.
  #
  # Set up file. Filename is traveltime_hist.png. 
  png(file = paste(deparse(substitute(field)),"_hist.png",sep=""))
  hist(field)
  dev.off()
  
  detach(dframe)
  dbDisconnect(con)
  dbUnloadDriver(drv)

  # Return filepath, which is the directory.
  return(paste(getwd(),"/",deparse(substitute(field)),"_hist.png",sep=""))  

}