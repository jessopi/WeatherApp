package com.weatherapp.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.plogitech.darksky.api.jackson.DarkSkyJacksonClient;
import tk.plogitech.darksky.forecast.*;
import tk.plogitech.darksky.forecast.model.*;

import static tk.plogitech.darksky.forecast.ForecastRequestBuilder.Language.en;

@Controller
public class WeatherController {

    @Autowired
    Location location = new Location();

    //Opens entry form on start
    @GetMapping(value = "/")
    public String home() {
        return "EntryForm";
    }

    @RequestMapping(value="About")
    public String about(){ return "About";}

    //Receives default post from entry form
    @RequestMapping(value ="/Forecast", method = RequestMethod.POST)
    public String selectedLocationInput(@RequestParam("locationTextField") String inputAddress, RedirectAttributes redir, Model model){

        //If entered location dosnt exist redirect back to home
        if(!location.setAddressInput(inputAddress)){
            redir.addFlashAttribute("Error","LOCATION NOT FOUND");
            return "redirect:/";
        }

        //Call to wrapper class Forecast and returns weather forecast object
        ForecastRequest req = new ForecastRequestBuilder().language(en).units(ForecastRequestBuilder.Units.us)
                .exclude(ForecastRequestBuilder.Block.alerts, ForecastRequestBuilder.Block.flags, ForecastRequestBuilder.Block.hourly, ForecastRequestBuilder.Block.minutely)
                .key(new APIKey("ce57b1f6144fcc06a99abb65ba339572"))
                .location(new GeoCoordinates(new Longitude(location.getLongitude()), new Latitude(location.getLatitude()))).build();

        DarkSkyJacksonClient client = new DarkSkyJacksonClient();
        try{
            Forecast forecast =  client.forecast(req);
            model.addAttribute("weatherObject",forecast.getDaily().getData());
            model.addAttribute("weatherLocation",location.getLocationName());
            model.addAttribute("currently",forecast.getCurrently());
        } catch (ForecastException ex){
            ex.printStackTrace();
            redir.addFlashAttribute("Error","FORECAST COULD NOT BE RETRIEVED.");
            return "redirect:/";
        }
        return "Forecast";
    }


    //Receives post when geoButton is pressed and mapped value = Forecast.
    @RequestMapping(value ="/Forecast", method = RequestMethod.POST,params = {"geoButton"})
    public String currentLocationInput(@RequestParam("geolocation") String geolocation,RedirectAttributes redir,Model model){

        //Splits input into latitude and longitude
        String[] input = geolocation.split(",");

        //If error in retrieving both coordinates return
        if(input.length != 2){
            redir.addFlashAttribute("Error","ERROR IN RECEIVING GEO COORDINATES.");
            return "redirect:/";
        }
        double latitude = Double.parseDouble(input[0]);
        double longitude = Double.parseDouble(input[1]);

        //Call to wrapper class Forecast and returns weather forecast object
        ForecastRequest req = new ForecastRequestBuilder().language(en).units(ForecastRequestBuilder.Units.us)
                .exclude(ForecastRequestBuilder.Block.alerts, ForecastRequestBuilder.Block.flags, ForecastRequestBuilder.Block.hourly, ForecastRequestBuilder.Block.minutely)
                .key(new APIKey("ce57b1f6144fcc06a99abb65ba339572"))
                .location(new GeoCoordinates(new Longitude(longitude), new Latitude(latitude))).build();

        DarkSkyJacksonClient client = new DarkSkyJacksonClient();
        try{
            Forecast forecast =  client.forecast(req);
            location.setCoordinateInput(input[0],input[1]);
            model.addAttribute("weatherObject",forecast.getDaily().getData());
            model.addAttribute("weatherLocation",location.getLocationName());
            model.addAttribute("currently",forecast.getCurrently());
        } catch (ForecastException ex){
            ex.printStackTrace();
            redir.addFlashAttribute("Error","FORECAST COULD NOT BE RETRIEVED.");
            return "redirect:/";
        }
        return "Forecast";
    }
}