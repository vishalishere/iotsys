/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

package at.ac.tuwien.auto.iotsys.gateway.connectors.weatherforecast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

//import obix.WeatherForcastObject;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.WeatherForcastObject;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForcastUpcomingWeatherImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherForecastRecordImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.weatherforecast.impl.WeatherSymbolImpl;


public class WeatherForecastConnector implements Connector {
	
	private static final Logger log = Logger.getLogger(WeatherForecastConnector.class.getName());
	
	private HttpURLConnection httpConnection = null;
	private DocumentBuilder docBuilder = null;
	
	public WeatherForecastConnector() throws FactoryConfigurationError, ParserConfigurationException {
		this.httpConnection = null;
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		
		this.docBuilder = docBuilderFactory.newDocumentBuilder();
	}
	
	public void connect() {
		// nothing to do
	}
	
	public void disconnect() {
		// nothing to do
	}
	
	public Document getWeatherForecastAsXML(String serviceURL) throws IOException, MalformedURLException, SAXException
	{ 
        log.info("Retrieving weather forecast from " + serviceURL + ".");
        
        Document result = null;
        
        if (docBuilder != null)
        {
			connectToURL(serviceURL);
			
			if (httpConnection.getResponseCode() == 200)						
				result = docBuilder.parse(httpConnection.getInputStream());
			
	        disconnectFromURL();
        }
      
        return result;
	}
	
	public List<WeatherForcastObject> getWeatherForecas(String serviceURL){
		
		
		ArrayList<WeatherForcastObject> resultWeatherList = new ArrayList<WeatherForcastObject>();
		
		
		log.info("Retrieving weather forecast from " + serviceURL + ".");
		try {
			Document doc = getWeatherForecastAsXML(serviceURL);
			
			if (doc != null)
			{
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
				

				
				Hashtable<Date, WeatherForcastObject> hashTable = new Hashtable<Date, WeatherForcastObject>(); 
				
			//	WeatherForecastRecordImpl forecast;
			//	WeatherForcastUpcomingWeatherImpl upcoming = null;
				
				NodeList elements = doc.getElementsByTagName("location");
				
				
				System.out.println("elments Length: "+elements.getLength());
				
				for (int i=0; i < elements.getLength(); i++)
				{
					
					
					
					Element location = (Element) elements.item(i);
					
					if (location != null)
					{
						Date from;
						Date to;
						NodeList tmp;
						Element time = (Element) location.getParentNode();

						try
						{
							// Z in xs:dateTime means UTC time!
							from = dateFormat.parse(time.getAttribute("from").replaceAll("Z", "+00:00"));
							to = dateFormat.parse(time.getAttribute("to").replaceAll("Z", "+00:00"));
							
							
						}
						catch (ParseException pe)
						{
							log.log(Level.WARNING, pe.getMessage());
							
							// ignore time element
							continue;
						}

						// check if a weather forecast record already exists for 
						// the given timestamp -- if not create a new one
//						if ((forecast = hashTable.get(to)) == null)
//						{
//							forecast = new WeatherForecastRecordImpl();
//							forecast.precipitation().setNull(true);
//							forecast.symbol().setNull(true);
//							
//						//	upcoming.setAlll();
//							
//							// append the record to the update array list
//							resultWeatherList.add(forecast);
//							
//							// add the record to the hash table
//							hashTable.put(to, forecast);
//							
//							/*
//							 * set timestamp
//							 */
//							String utcOffset = time.getAttribute("to");
//							 
//							// strip date
//							utcOffset = utcOffset.substring(utcOffset.indexOf('T'));
//							
//							// 'Z' means utc
//							utcOffset = utcOffset.replaceAll("Z", "+00:00");
//
//							// strip time (note that either '+' or '-' is present)
//							utcOffset = utcOffset.substring(utcOffset.lastIndexOf('+') + utcOffset.lastIndexOf('-') + 1);
//							
//							forecast.timestamp().set(to.getTime(), TimeZone.getTimeZone("GMT" + utcOffset));
//						}

						
						
						// check the time element's type
						System.out.println("Date from: "+from);
						System.out.println("Date to: "+to);
						if (from.equals(to))
						{		
							WeatherForcastObject weatherObject = new WeatherForcastObject();
							
							
							
							/*
//							 * set timestamp
//							 */
							String utcOffset = time.getAttribute("to");
							 
							// strip date
							utcOffset = utcOffset.substring(utcOffset.indexOf('T'));
							
							// 'Z' means utc
							utcOffset = utcOffset.replaceAll("Z", "+00:00");

							// strip time (note that either '+' or '-' is present)
							utcOffset = utcOffset.substring(utcOffset.lastIndexOf('+') + utcOffset.lastIndexOf('-') + 1);
							
							weatherObject.setTimestamp(to.getTime());
						//	forecast.timestamp().set(to.getTime(), TimeZone.getTimeZone("GMT" + utcOffset));
							
							
							
							//parse temperatureProbability
							tmp = location.getElementsByTagName("temperatureProbability");
							if (tmp.getLength() >= 1){
								weatherObject.setTemperatureProbability(Integer.parseInt(((Element) tmp.item(0)).getAttribute("value")));
							}
							else{
							}
							
							//parese windProbability
							tmp = location.getElementsByTagName("windProbability");
							if (tmp.getLength() >= 1){
								weatherObject.setWindProbability(Integer.parseInt(((Element) tmp.item(0)).getAttribute("value")));
							}
							else{
							}

							// parse temperature
							tmp = location.getElementsByTagName("temperature");
							if (tmp.getLength() >= 1){
								weatherObject.setTemperature(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
							}
							else{
							}
								
							// parse windDirection 
							tmp = location.getElementsByTagName("windDirection");
							if (tmp.getLength() >= 1){
								weatherObject.setWindDirection(((Element) tmp.item(0)).getAttribute("name")); 
							}
							else{
							}
							
							// parse wind speed
							tmp = location.getElementsByTagName("windSpeed");
							if (tmp.getLength() >= 1){
								weatherObject.setWindSpeed(Integer.parseInt(((Element) tmp.item(0)).getAttribute("beaufort")));
							}
							else{		
							}
							
							// parse humidity
							tmp = location.getElementsByTagName("humidity");
							if (tmp.getLength() >= 1){
								weatherObject.setHumidity(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
							}
							
							else{	
							}

							// parse pressure
							tmp = location.getElementsByTagName("pressure");
							if (tmp.getLength() >= 1){
								weatherObject.setPressure(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
							}
							else{	
							}
							
							// parse cloudiness
							tmp = location.getElementsByTagName("cloudiness");
							if (tmp.getLength() >= 1){
								weatherObject.setCloudiness(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
							}
							else{
							}
							
							// parse fog
							tmp = location.getElementsByTagName("fog");
							if (tmp.getLength() >= 1){
								weatherObject.setFog(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
							}
							else{
							}
							
							// parse lowClouds
							tmp = location.getElementsByTagName("lowClouds");
							if (tmp.getLength() >= 1){
								weatherObject.setLowClouds(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
							}
							else{
							}
							
							// parse lowClouds
							tmp = location.getElementsByTagName("mediumClouds");
							if (tmp.getLength() >= 1){
								weatherObject.setHighClouds(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
							}
							else{
							}
							
							// parse lowClouds
							tmp = location.getElementsByTagName("highClouds");
							if (tmp.getLength() >= 1){
								weatherObject.setMediumClouds(Double.parseDouble(((Element) tmp.item(0)).getAttribute("percent")));
							}
							else{
							}
							
							// parse dewpointTemperature 
							tmp = location.getElementsByTagName("dewpointTemperature");
							if (tmp.getLength() >= 1){
								weatherObject.setMediumClouds(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
							}
							else{
							}
							
							resultWeatherList.add(weatherObject);
						}
//						else // time element describes period of time
//						{
//							/*
//							 * there may exist multiple time periods; former periods are shorter and should be preferred
//							 */
//							//pare symbolProbability			
//							tmp = location.getElementsByTagName("symbolProbability");
//							if (tmp.getLength() >= 1){
//								weatherObject.setSymbolProbability(Integer.parseInt(((Element) tmp.item(0)).getAttribute("value")));
//							}
//							else{
//							}
//							
//							// parse precipitation
//							tmp = location.getElementsByTagName("precipitation");
//							if (tmp.getLength() >= 1){
//								weatherObject.setPrecipitation(Double.parseDouble(((Element) tmp.item(0)).getAttribute("value")));
//							}
//							else{
//							}
//							
//							// parse symbol
//							tmp = location.getElementsByTagName("symbol");
//							if (tmp.getLength() >= 1){
//								weatherObject.setSymbol(WeatherSymbolImpl.GetByID(Integer.parseInt(((Element) tmp.item(0)).getAttribute("number"))));
//							}
//							else{
//							}						
//						}
					}
					
					
					
				}
				
				// update the forecast array
				
			//	forecasts.update(new WeatherForecastUpdateInImpl(resultWeatherList));

			}
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultWeatherList;
	}
	
	
	public WeatherForcastObject getUpcomingWeather (String serviceURL){
		
		log.info("Retrieving upcoming weather forecast from " + serviceURL + ".");
		
		List<WeatherForcastObject> weatherList = getWeatherForecas(serviceURL);
		
		return weatherList.get(0);
	}
	
	/*
	public Document getUpcomingWeather(String serviceURL) throws MalformedURLException, IOException, SAXException{
		
		log.info("Retrieving upcoming weather forecast from " + serviceURL + ".");
		
		Document data = getWeatherForecastAsXML(serviceURL);
		
		//Document result = null;
	
		DOMImplementation impl = DOMImplementationImpl.getDOMImplementation();
		Document doc = impl.createDocument(null, "upcommingWeatherForcast", null);		
		Element root = doc.createElement("upcomming");
		
		
		
		if (data != null)
		{

			//DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
			NodeList elements = data.getElementsByTagName("location");
			
			for (int i=0; i < elements.getLength(); i++)
			{
				System.out.println("elements length: "+elements.getLength());
				Element location = (Element) elements.item(i);
				
				if (location != null)
				{
					
					//Date from = null;
					//Date to = null;
					
					NodeList tmp;
					
					
					Element time = (Element) location.getParentNode();

					
					
					//String from = time.getAttribute("from");
					//String to = time.getAttribute("to");

				String from = new String();
				String to =  new String();
					
					
						
						//from = dateFormat.parse(time.getAttribute("from").replaceAll("Z", "+00:00"));
						//to = dateFormat.parse(time.getAttribute("to").replaceAll("Z", "+00:00"));
						
						from = time.getAttribute("from").replaceAll("Z", "+00:00");
						to= time.getAttribute("to").replaceAll("Z", "+00:00");
						
						
						System.out.println("from  String: "+from.toString());
						System.out.println("to String: "+from.toString());
						
						
						// set timestamp
						 
						String utcOffset = time.getAttribute("to");
						 
						// strip date
						utcOffset = utcOffset.substring(utcOffset.indexOf('T'));
						
						// 'Z' means utc
						utcOffset = utcOffset.replaceAll("Z", "+00:00");

						// strip time (note that either '+' or '-' is present)
						utcOffset = utcOffset.substring(utcOffset.lastIndexOf('+') + utcOffset.lastIndexOf('-') + 1);
						System.out.println("utcOffset: " +TimeZone.getTimeZone("GMT" + utcOffset));
						
						
						
						
						
						//DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
						
						
					//	Date dFrom = f.parse(from.replaceAll("Z", "+01:00"));

						// get timezone
					//	String s = to.replaceAll("Z", "+00:00");  // time zone RFC 822 (+2)
						// strip date
				//		s = s.substring(time.getAttribute("to").indexOf('T'));

						// strip time
				//		s = s.substring(s.lastIndexOf('+') + s.lastIndexOf('-') + 1);
						
				//		System.out.println(TimeZone.getTimeZone("GMT" + s));
						
				//		System.out.println(dFrom.toString());
				
					
//					if ((forecast = dataTable.get(to)) == null)
//					{
//						// WeatherForecastRecordImpl
//						forecast = new String("");
//						
//						
//					}

					if (from.equals(to))
					{
						//
						
						// temperature
						tmp = location.getElementsByTagName("temperature");
						
						if (tmp.getLength() >= 1)
						{
							
							//result.createElement("temperature").setNodeValue((String) ((DocumentBuilderFactory) tmp.item(0)).getAttribute("value"));
							
							System.out.println("temperature: " + ((Element) tmp.item(0)).getAttribute("value"));
						}
						else
						{
							// TODO
						}
						
						// wind speed
						tmp = location.getElementsByTagName("windSpeed");
						
						if (tmp.getLength() >= 1)
						{
							System.out.println("wind speed: " + ((Element) tmp.item(0)).getAttribute("beaufort"));
						}
						else
						{
							// TODO
						}
						
						// humidity
						tmp = location.getElementsByTagName("humidity");
						
						if (tmp.getLength() >= 1)
						{
							System.out.println("humidity: " + ((Element) tmp.item(0)).getAttribute("value"));
						}
						else
						{
							// TODO
						}
						
						// pressure
						tmp = location.getElementsByTagName("pressure");
						
						if (tmp.getLength() >= 1)
						{
							System.out.println("pressure: " + ((Element) tmp.item(0)).getAttribute("value"));
						}
						else
						{
							// TODO
						}
						
						// cloudiness
						tmp = location.getElementsByTagName("cloudiness");
						
						if (tmp.getLength() >= 1)
						{
							System.out.println("cloudiness: " + ((Element) tmp.item(0)).getAttribute("percent"));
						}
						else
						{
							// TODO
						}
						
						// fog
						tmp = location.getElementsByTagName("fog");
						
						if (tmp.getLength() >= 1)
						{
							System.out.println("fog: " + ((Element) tmp.item(0)).getAttribute("percent"));
						}
						else
						{
							// TODO
						}
					}
					else
					{
						//
						
						// precipitation
						tmp = location.getElementsByTagName("precipitation");
						
						if (tmp.getLength() >= 1)
						{
							System.out.println("precipitation: " + ((Element) tmp.item(0)).getAttribute("value"));
						}
						else
						{
							// TODO
						}
						
						// humidity
						tmp = location.getElementsByTagName("symbol");
						
						if (tmp.getLength() >= 1)
						{
							System.out.println("symbol: " + ((Element) tmp.item(0)).getAttribute("number"));
						}
						else
						{
							// TODO
						}
					}
				}
			}
			
		}
		
		return data;
		
		
	}
	
	*/
		
	private void connectToURL(String serviceURL) throws IOException, MalformedURLException
	{
		log.info("Connecting to weather service.");
		
		httpConnection = (HttpURLConnection) (new URL(serviceURL)).openConnection();
		httpConnection.setRequestMethod("GET");
		httpConnection.setConnectTimeout(10000); // in milliseconds
		httpConnection.setDoInput(true); // use connection for input
				
		httpConnection.connect();
	}

	private void disconnectFromURL()
	{
		log.info("Disconnecting from weather service.");

		if (httpConnection != null)
			httpConnection.disconnect();
		
		httpConnection = null;
	}
}