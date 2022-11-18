package org.polytech.covid;

import java.util.List;

import org.polytech.covid.entity.VaccinationCenter;
import org.polytech.covid.repository.VaccinationCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicVaccinationCenterRestControler {

    @Autowired
    private VaccinationCenterRepository centerRepository;

    @GetMapping(path="api/public/centers")
    public List<VaccinationCenter> getVaccinationCenter(
        @RequestParam(name="city", defaultValue = "") String city){
            return centerRepository.findAllByCityIgnoreCaseLike("%"+city+"%");
    }
}
