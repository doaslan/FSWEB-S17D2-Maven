package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;

    private final Taxable taxable;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Developer> getDeveloperById(@PathVariable int id) {
        Developer developer = developers.get(id);

        if (developer == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(developer);
    }

    @PostMapping
    public ResponseEntity<Developer> addDeveloper(@RequestBody Developer developer) {
        int id = developer.getId();
        String name = developer.getName();
        double salary = developer.getSalary();
        Experience experience = developer.getExperience();

        Developer newDeveloper;

        switch (experience) {
            case JUNIOR:
                salary -= salary * taxable.getSimpleTaxRate() / 100;
                newDeveloper = new JuniorDeveloper(id, name, salary);
                break;

            case MID:
                salary -= salary * taxable.getMiddleTaxRate() / 100;
                newDeveloper = new MidDeveloper(id, name, salary);
                break;

            case SENIOR:
                salary -= salary * taxable.getUpperTaxRate() / 100;
                newDeveloper = new SeniorDeveloper(id, name, salary);
                break;

            default:
                return ResponseEntity.badRequest().build();
        }

        developers.put(id, newDeveloper);

        return ResponseEntity.status(HttpStatus.CREATED).body(newDeveloper);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Developer> updateDeveloper(
            @PathVariable int id,
            @RequestBody Developer developer) {

        if (!developers.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        Developer updatedDeveloper = new Developer(
                id,
                developer.getName(),
                developer.getSalary(),
                developer.getExperience()
        );

        developers.put(id, updatedDeveloper);

        return ResponseEntity.ok(updatedDeveloper);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable int id) {
        if (!developers.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        developers.remove(id);

        return ResponseEntity.ok().build();
    }
}
