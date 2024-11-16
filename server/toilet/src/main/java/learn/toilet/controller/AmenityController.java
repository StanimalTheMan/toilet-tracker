package learn.toilet.controller;

import learn.toilet.domain.AmenityService;
import learn.toilet.domain.Result;
import learn.toilet.models.Amenity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/api/amenity")
public class AmenityController {
    private final AmenityService service;


    public AmenityController(AmenityService service) {
        this.service = service;
    }

    @GetMapping()
    public List<Amenity> findAll() {
        return service.findAll();
    }

    @GetMapping("/{amenityId}")
    public ResponseEntity<Amenity> findById(@PathVariable int amenityId) {
        Amenity amenity = service.findById(amenityId);
        if (amenity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(amenity);
    }

    @PostMapping("/admin")
    public ResponseEntity<Object> add(@RequestBody Amenity amenity) {
        Result<Amenity> result = service.add(amenity);
        if(result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/admin/{amenityId}")
    public ResponseEntity<Object> update(@PathVariable int amenityId, @RequestBody Amenity amenity) {
        if(amenityId != amenity.getAmenityId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<Amenity> result = service.update(amenity);
        if(result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return ErrorResponse.build(result);
    }

    @DeleteMapping("/admin/{amenityId}")
    public ResponseEntity<Object> deleteById(@PathVariable int amenityId) {
        if(service.deleteById(amenityId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
