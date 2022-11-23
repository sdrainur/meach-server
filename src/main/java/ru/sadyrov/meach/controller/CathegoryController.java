package ru.sadyrov.meach.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sadyrov.meach.domain.Cathegory;
import ru.sadyrov.meach.repository.CathegoryRepository;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:8080", "http://10.17.33.199:8080/"})
@RequestMapping("/cathegory")
public class CathegoryController {

    private CathegoryRepository cathegoryRepository;

    public CathegoryController(CathegoryRepository cathegoryRepository) {
        this.cathegoryRepository = cathegoryRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addCathegory(@RequestBody Cathegory cathegory){
        if(cathegory!=null){
            cathegoryRepository.save(cathegory);
            return new ResponseEntity<>("Cathegory successfully saved", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cathegory is not saved", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCathegory(@RequestBody Cathegory cathegory){
        if(cathegory!=null){
            cathegoryRepository.delete(cathegory);
            return new ResponseEntity<>("Cathegory successfully saved", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cathegory is not saved", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<String> getAllCathegories(){
        List<Cathegory> cathegories = cathegoryRepository.findAll();
        JSONArray jsonArray = new JSONArray();
        for (Cathegory cathegory : cathegories) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cathegory.getId());
            jsonObject.put("name", cathegory.getName());
            jsonArray.put(jsonObject);
        }
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }
}
