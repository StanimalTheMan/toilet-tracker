package learn.toilet.controller;

import learn.toilet.domain.RestroomService;
import learn.toilet.domain.Result;
import learn.toilet.models.AppUser;
import learn.toilet.models.Restroom;
import learn.toilet.models.Review;
import learn.toilet.security.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/api/restroom")
public class RestroomController {

    private final AppUserService appUserService;
    private final RestroomService restroomService;

    public RestroomController(AppUserService appUserService, RestroomService restroomService) {
        this.appUserService = appUserService;
        this.restroomService = restroomService;
    }

    @GetMapping
    public List<Restroom> findAll() {
        return restroomService.findAll();
    }

    @GetMapping("/search")
    public List<Restroom> findByLocation(@RequestParam double latitude, @RequestParam double longitude) {
        return restroomService.findByLocation(latitude, longitude);
    }

    @GetMapping("/restrooms/{userId}")
    public ResponseEntity<List<Restroom>> findByUserId(@PathVariable int userId) {
        List<Restroom> restrooms = restroomService.findByUserId(userId);
        if (restrooms == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(restrooms);
    }

    @GetMapping("/{restroomId}")
    public ResponseEntity<Restroom> findById(@PathVariable int restroomId) {
        Restroom restroom = restroomService.findById(restroomId);
        if (restroom == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(restroom);
    }

    @GetMapping("/current")
    public ResponseEntity<List<Restroom>> findByUserId() {
        // Get the authenticated username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (String) authentication.getPrincipal();
        AppUser user = (AppUser) appUserService.loadUserByUsername(username);

        // Get user id from AppUser
        int authenticatedUserId = user.getAppUserId();

        List<Restroom> restrooms = restroomService.findByUserId(authenticatedUserId);
        if (restrooms.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(restrooms);
    }


    @PostMapping
    public ResponseEntity<Object> add(@RequestBody Restroom restroom) {
        // Get the authenticated username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (String) authentication.getPrincipal();
        AppUser user = (AppUser) appUserService.loadUserByUsername(username);

        // Get user id from AppUser
        int authenticatedUserId = user.getAppUserId();

        // Ensure that the restroom's userId matches the authenticated user's userId
        if (restroom.getUserId() != authenticatedUserId) {
            return new ResponseEntity<>("You are not authorized to create a restroom entry for another user.", HttpStatus.FORBIDDEN);
        }

        Result<Restroom> result = restroomService.add(restroom);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @PutMapping("/{restroomId}")
    public ResponseEntity<Object> update(@PathVariable int restroomId, @RequestBody Restroom restroom) {
        if (restroomId != restroom.getRestroomId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Get the authenticated username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (String) authentication.getPrincipal();
        AppUser user = (AppUser) appUserService.loadUserByUsername(username);

        // Get user id from AppUser
        int authenticatedUserId = user.getAppUserId();

        // Ensure that the restroom's userId matches the authenticated user's userId
        if (restroom.getUserId() != authenticatedUserId) {
            return new ResponseEntity<>("You are not authorized to update a restroom entry for another user.", HttpStatus.FORBIDDEN);
        }

        Result<Restroom> result = restroomService.update(restroom);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{restroomId}")
    public ResponseEntity<Object> deleteById(@PathVariable int restroomId) {

        // Get the authenticated username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (String) authentication.getPrincipal();
        AppUser user = (AppUser) appUserService.loadUserByUsername(username);

        // Get user id from AppUser
        int authenticatedUserId = user.getAppUserId();

        Restroom restroom = restroomService.findById(restroomId);

        // Ensure that the restroom's userId matches the authenticated user's userId
        if (restroom != null && restroom.getUserId() != authenticatedUserId) {
            return new ResponseEntity<>("You are not authorized to delete a restroom entry for another user.", HttpStatus.FORBIDDEN);
        }

        Result<Restroom> result = restroomService.deleteById(restroomId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
