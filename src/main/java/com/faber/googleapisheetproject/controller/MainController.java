package com.faber.googleapisheetproject.controller;

//<editor-fold defaultstate="collapsed" desc="IMPORT">
import com.faber.googleapisheetproject.entity.Account;
import com.faber.googleapisheetproject.entity.Airport;
import com.faber.googleapisheetproject.serviceImpl.AccountServiceImpl;
import com.faber.googleapisheetproject.serviceImpl.AirportServiceImpl;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping(path = "/add-airport")
    public String moveToAddAirport(Model model) {
        Airport airport = new Airport();
        model.addAttribute("airport", airport);
        return "/airport/add-edit";
    }

    @PostMapping(path = "/add-airport")
    public String addAirport(@ModelAttribute("airport") Airport airport, Model model) {
        airportServiceImpl.addAirport(airport);
        return "redirect:/airport";
    }

}
