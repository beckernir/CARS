package com.amalitech.GTP.controlller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TraineeClass {

    @GetMapping("/names")
    public String getNames(){

        return "Names are here";
    }
}
