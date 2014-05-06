PlotLiveability <- function(filepath, filetype)  {
  # Plot time series of liveability and congestion.
  # 
  # Args:
  #   liveability: vector of liveabilty scores
  #   congestion: vector of congestion values 
  #   filetype: "pdf" or "png"
  
  liv.con <- read.table(paste(filepath,"Live_Cong.txt",sep=""), sep = "\t", header = FALSE)

  if (filetype == "pdf") {
    pdf(file = paste(filepath,"liveability_plot.pdf",sep=""))
  }
  if (filetype == "png") {
    png(file = paste(filepath,"liveability_plot.png",sep=""), width = 960, height = 960, res= 144)
  }
  
  plot(1:length(liv.con[,1]), liv.con[,1], type = "o", pch = 15, col = "darkgreen", 
       xlab = "Time (days)", ylab ="Liveability/Congestion Score", 
       ylim = range(0, liv.con[,1]+ 0.1, liv.con[,1]+ 0.1))
  lines(1:length(liv.con[,1]), liv.con[,2], type = "o", pch = 17, col = "red")
  legend('topright', c("Liveability","Congestion"), cex=0.8, 
   col=c("darkgreen","red"), pch=c(15,17))
   
  dev.off()

} 
