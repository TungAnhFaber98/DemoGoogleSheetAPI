package com.faber.googleapisheetproject.controller;

//<editor-fold defaultstate="collapsed" desc="IMPORT">
import com.faber.googleapisheetproject.entity.Account;
import com.faber.googleapisheetproject.entity.Airport;
import com.faber.googleapisheetproject.serviceImpl.AccountServiceImpl;
import com.faber.googleapisheetproject.serviceImpl.AirportServiceImpl;
import java.util.ArrayList;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//</editor-fold>

@Controller
@RequestMapping(value = "")
public class MainController {

    @Autowired
    AccountServiceImpl accountServiceImpl;

    @Autowired
    AirportServiceImpl airportServiceImpl;

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    //<editor-fold defaultstate="collapsed" desc="WELCOME">
    @RequestMapping
    public String welcome(Model model) {
        Account account = new Account();
        model.addAttribute("account", account);
        return "welcome";
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="LOGIN">
    @PostMapping(path = "/login")
    public String login(@ModelAttribute("account") Account account) {
        Boolean checkFlag = accountServiceImpl.checkAccount(account);
        if (checkFlag) {
            return "redirect:/airport";
        } else {
            return "redirect:/";
        }
    }
    //</editor-fold>

    @GetMapping(path = "/airport")
    public String viewAll(Model model, @ModelAttribute("airport") Airport airport) {
        ArrayList<Airport> airportList = (ArrayList<Airport>) airportServiceImpl.findAllAirport();
        model.addAttribute("airportList", airportList);
        return "airport/view-all";
    }

    @GetMapping(path = {"/edit-airport", "/edit-airport/{id}"})
    public String moveToAddAirport(Model model, @PathVariable("id") Optional<String> id) {
        Airport airport;
        if (!id.isPresent()) {
            airport = new Airport();

        } else {
            String stringId = id.get();
            airport = airportServiceImpl.getAirportById(stringId);

        }
        model.addAttribute("airport", airport);
        return "/airport/add-edit";
    }

    @PostMapping(path = "/edit-airport")
    public String addAirport(@ModelAttribute("airport") Airport airport, Model model) {
        String id = airport.getId();
        if (id == null || id.isEmpty()) {
            airportServiceImpl.addAirport(airport);
        }
        else{
            airportServiceImpl.updateAirport(airport);
        }
        return "redirect:/airport";
    }

    @GetMapping(path = "/delete-airport/{id}")
    public String deleteAirport(@PathVariable(value = "") String id, Model model) {
        airportServiceImpl.deleteAirport(id);
        return "redirect:/airport";

    }
    
    
    @GetMapping(path="/test")
    public String test(){
        airportServiceImpl.test();
        return "success";
    }
}
