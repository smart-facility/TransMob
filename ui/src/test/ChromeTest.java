/* This file is part of TransMob.

   TransMob is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   TransMob is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser Public License for more details.

   You should have received a copy of the GNU Lesser Public License
   along with TransMob.  If not, see <http://www.gnu.org/licenses/>.

*/
package core.test;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


@RunWith(BlockJUnit4ClassRunner.class)
public class ChromeTest  extends TestCase {

	 private WebDriver driver;
	 
	   @Before
	   public void createDriver() {
			System.setProperty("webdriver.chrome.driver", "C:/Documents and Settings/qun/My Documents/Downloads/chromedriver_win_16.0.902.0/chromedriver.exe");
		   driver = new ChromeDriver();
		   driver.get("http://localhost:8080/TransportNSW/");
	   }
	 
	   @After
	   public void quitDriver() {
	     driver.quit();
	   }
	   
	   @Test
	   public void testClick(){
		   testSequence(driver);
	   }
	   
	   public static void testSequence(WebDriver driver){
//		   
//			
//			Actions builder = new Actions(driver);    
			
			WebElement tabElement = driver.findElement(By.xpath("//html//body//div[@id='sam_tabs']//ul//li[2]//a//span"));
			tabElement.click();
			
			WebElement tickElement=driver.findElement(By.id("disp_subarea_features"));
			clickAndWait(2, tickElement);
			
			tickElement=driver.findElement(By.id("disp_subarea_boundaries"));
			clickAndWait(2, tickElement);
			
			tickElement=driver.findElement(By.id("dips_heat_map"));
			tickElement=tickElement.findElement(By.xpath("//input[3]"));
			clickAndWait(2, tickElement);
			
			tickElement=driver.findElement(By.id("dips_heat_map"));
			tickElement=tickElement.findElement(By.xpath("//input[4]"));
			clickAndWait(2, tickElement);
			
			tickElement=driver.findElement(By.id("dips_heat_map"));
			tickElement=tickElement.findElement(By.xpath("//input[2]"));
			clickAndWait(2, tickElement);
			
			tickElement=driver.findElement(By.id("dips_heat_map"));
			tickElement=tickElement.findElement(By.xpath("//input[5]"));
			clickAndWait(2, tickElement);
			
			tickElement=driver.findElement(By.id("disp_subarea_features"));
			tickElement.click();
			
			tickElement=driver.findElement(By.id("disp_road_network"));
			clickAndWait(3, tickElement);

//			builder.click(tickElement);
	
			tickElement.click();
			
			
			tickElement=driver.findElement(By.id("disp_bus_network_demo"));
			clickAndWait(3, tickElement);
			tickElement.click();
			
			tabElement = driver.findElement(By.xpath("//html//body//div[@id='sam_tabs']//ul//li[3]//a//span"));
			tabElement.click();
			
			tickElement=driver.findElement(By.id("edit_road_network"));
			clickAndWait(3, tickElement);
			
			@SuppressWarnings("unused")
			WebElement mapElement=driver.findElement(By.id("map_edit"));
			
			
			tickElement.click();
			
			tickElement=driver.findElement(By.id("edit_bus_network_demo"));
			clickAndWait(3, tickElement);
			clickAndWait(2, tickElement);
		}
	   
	   public static void clickAndWait(int second, WebElement webElement){
			webElement.click();
			try {
				Thread.sleep(second*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
