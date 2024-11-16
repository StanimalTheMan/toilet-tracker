package learn.toilet.controller;

import learn.toilet.domain.RestroomService;
import learn.toilet.domain.Result;
import learn.toilet.models.AppUser;
import learn.toilet.models.Restroom;
import learn.toilet.models.RestroomAmenity;
import learn.toilet.security.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"})
@RequestMapping("/api/restroom/amenity")
public class RestroomAmenityController {

    private final AppUserService appUserService;
    private final RestroomService service;

    public RestroomAmenityController(AppUserService appUserService, RestroomService restroomService, RestroomService service) {
        this.appUserService = appUserService;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody RestroomAmenity restroomAmenity) {
        // Get the authenticated username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (String) authentication.getPrincipal();
        AppUser user = (AppUser) appUserService.loadUserByUsername(username);

        // Get user id from AppUser
        int authenticatedUserId = user.getAppUserId();

        // Get restroom
        Restroom restroom = service.findById(restroomAmenity.getRestroomId());

        // Ensure that the restroom's userId matches the authenticated user's userId
        if (restroom != null && restroom.getUserId() != authenticatedUserId) {
            return new ResponseEntity<>("You are not authorized to associate amenity with bathroom created by another user.", HttpStatus.FORBIDDEN);
        }

        Result<Void> result = service.addAmenity(restroomAmenity);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return ErrorResponse.build(result);
    }

    @DeleteMapping("/{restroomId}/{amenityId}")
    public ResponseEntity<Object> deleteByKey(@PathVariable int restroomId, @PathVariable int amenityId) {
        // Get the authenticated username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = (String) authentication.getPrincipal();
        AppUser user = (AppUser) appUserService.loadUserByUsername(username);

        // Get user id from AppUser
        int authenticatedUserId = user.getAppUserId();

        // Get restroom
        Restroom restroom = service.findById(restroomId);

        // Ensure that the restroom's userId matches the authenticated user's userId
        if (restroom != null && restroom.getUserId() != authenticatedUserId) {
            return new ResponseEntity<>("You are not authorized to delete association between amenity and bathroom created by another user.", HttpStatus.FORBIDDEN);
        }

        if (service.deleteAmenityByKey(restroomId, amenityId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
