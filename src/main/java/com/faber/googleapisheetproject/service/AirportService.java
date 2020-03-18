/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.faber.googleapisheetproject.service;

import com.faber.googleapisheetproject.entity.Airport;
import java.util.List;

/**
 *
 * @author Engineer_Account
 */
public interface AirportService {
    public List<Airport> findAllAirport();
    
    public void addAirport(Airport airport);
    
    public void deleteAirport(String id);
    
    public Airport getAirportById(String id);
    
    public void updateAirport(Airport airport);
}
