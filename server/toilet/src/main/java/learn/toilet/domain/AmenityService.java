package learn.toilet.domain;

import learn.toilet.data.AmenityRepository;
import learn.toilet.models.Amenity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmenityService {
    private final AmenityRepository amenityRepository;

    public AmenityService(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    public List<Amenity> findAll() {
        return amenityRepository.findAll();
    }

    public Amenity findById(int amenityId) {
        return amenityRepository.findById(amenityId);
    }

    public Amenity findByName(String amenityName) {
        return amenityRepository.findByName(amenityName);
    }

    public Result<Amenity> add(Amenity amenity) {
        Result<Amenity> result = validate(amenity);
        if (!result.isSuccess()) {
            return result;
        }

        if (amenity.getAmenityId() != 0) {
            result.addMessage("amenityId cannot be set for `add` operation", ResultType.INVALID);
            return result;
        }

        amenity = amenityRepository.add(amenity);
        result.setPayload(amenity);
        return result;
    }

    public Result<Amenity> update(Amenity amenity) {
        Result<Amenity> result = validate(amenity);
        if (!result.isSuccess()) {
            return result;
        }

        if (amenity.getAmenityId() <= 0) {
            result.addMessage("amenityId must be set for `update` operation", ResultType.INVALID);
            return result;
        }

        if (!amenityRepository.update(amenity)) {
            String msg = String.format("amenityId: %s, not found", amenity.getAmenityId());
            result.addMessage(msg, ResultType.NOT_FOUND);
        }

        return result;
    }

    public boolean deleteById(int amenityId) {
        return amenityRepository.deleteById(amenityId);
    }

    private Result<Amenity> validate(Amenity amenity) {
        Result<Amenity> result = new Result<>();
        if (amenity == null) {
            result.addMessage("amenity cannot be null", ResultType.INVALID);
            return result;
        }

        if (Validations.isNullOrBlank(amenity.getAmenityName())) {
            result.addMessage("amenity name is required", ResultType.INVALID);
        }

        Amenity existingAmenity = amenityRepository.findByName(amenity.getAmenityName());

        if (existingAmenity != null && existingAmenity.getAmenityId() != amenity.getAmenityId()) {
            result.addMessage("amenity name must be unique", ResultType.INVALID);
        }

        return result;
    }
}
