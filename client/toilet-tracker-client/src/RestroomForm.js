import React, { useState, useEffect } from "react";
import { Form, Button, Container, Alert, Row, Col } from "react-bootstrap";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";
import { useNavigate, useParams, Link } from "react-router-dom";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import { jwtDecode } from "jwt-decode";
import AddressSearch from "./AddressSearch.js"; // Import the AddressSearch component
import { useLocation } from "./context/LocationContext.js";

// Marker icon configuration for compatibility with Leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require("leaflet/dist/images/marker-icon-2x.png"),
  iconUrl: require("leaflet/dist/images/marker-icon.png"),
  shadowUrl: require("leaflet/dist/images/marker-shadow.png"),
});

const RESTROOM_DEFAULT = {
  name: "",
  latitude: 0,
  longitude: 0,
  address: "",
  directions: "",
  description: "",
  userId: "",
  amenities: [],
};

const RestroomForm = () => {
  const [restroom, setRestroom] = useState(RESTROOM_DEFAULT);
  const { updateLocation } = useLocation();
  const [success, setSuccess] = useState(false);
  const [errors, setErrors] = useState([]);
  const [position, setPosition] = useState([51.505, -0.09]);
  const [manualEntry, setManualEntry] = useState(false);
  const [amenities, setAmenities] = useState([]);
  const navigate = useNavigate();
  const { restroomId } = useParams();
  const url = "http://localhost:8080/api/restroom";
  const amenitiesUrl = "http://localhost:8080/api/amenity";
  const restroomAmenitiesUrl = "http://localhost:8080/api/restroom/amenity"

  useEffect(() => {
    fetch(amenitiesUrl)
      .then((response) => {
        if (response.status === 200) {
          return response.json();
        } else {
          return Promise.reject(`Unexpected Status Code: ${response.status}`);
        }
      })
      .then((data) => {
        setAmenities(data); // Assuming data is an array of amenities with id and name
      })
      .catch(console.log);

    if (restroomId) {
      fetch(`${url}/${restroomId}`)
        .then((response) => {
          if (response.status === 200) {
            return response.json();
          } else {
            return Promise.reject(`Unexpected Status Code: ${response.status}`);
          }
        })
        .then((data) => {
          setRestroom(data);
        })
        .catch(console.log);
    } else {
      setRestroom(RESTROOM_DEFAULT);
    }
  }, [restroomId]);

  const handleManualEntryToggle = () => {
    setManualEntry(!manualEntry);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setRestroom((prev) => ({
      ...prev,
      [name]: value,
    }));

    if (name === "latitude" || name === "longitude") {
      const lat = parseFloat(name === "latitude" ? value : restroom.latitude);
      const lng = parseFloat(name === "longitude" ? value : restroom.longitude);
      if (!isNaN(lat) && !isNaN(lng)) {
        setPosition([lat, lng]);
      }
    }
  };

  const handleAddressSelect = ({ latitude, longitude, address }) => {
    setRestroom((prev) => ({
      ...prev,
      latitude: latitude.toString(),
      longitude: longitude.toString(),
      address,
    }));
    setPosition([latitude, longitude]);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSuccess(false);
    setErrors([]);
    updateLocation([restroom.latitude, restroom.longitude]); // Update map center
    if (restroomId) {
      // checks to see if there is an restroomId in the url
      await updateRestroom();
    } else {
      await addRestroom();
    }
  };

  const addRestroom = async () => {
    const token = localStorage.getItem("token");
    let decodedToken;
    if (token) {
      decodedToken = jwtDecode(token);
    }
    try {
      const init = {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          ...restroom,
          latitude: parseFloat(restroom.latitude),
          longitude: parseFloat(restroom.longitude),
          userId: parseInt(decodedToken.appUserId),
        }),
      };
      fetch(url, init)
        .then((response) => {
          if (response.status === 201) {
            return response.json();
          } else if (response.status === 400) {
            return response.json();
          } else {
            return Promise.reject(`Unexpected Status Code ${response.status}`);
          }
        })
        .then((data) => {
          if (data.restroomId) {
            // happy path
            updateAmenities(data)
            navigate("/restrooms");
          } else {
            //unhappy
            // get our errors messages and diplay them
            setErrors(data);
          }
        })
        .catch(console.log);
    } catch (err) {
      setErrors([
        "Failed to add restroom. You may need to log in and / or input required fields.  Please try again.",
      ]);
    }
  };

  const updateRestroom = async () => {
    restroom.restroomId = restroomId;
    const token = localStorage.getItem("token");
    const init = {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        ...restroom,
        latitude: parseFloat(restroom.latitude),
        longitude: parseFloat(restroom.longitude),
        userId: parseInt(restroom.userId),
      }),
    };
    fetch(`${url}/${restroomId}`, init)
      .then((response) => {
        if (response.status === 204) {
          return null;
        } else if (response.status === 400) {
          return response.json();
        } else {
          return Promise.reject(`Unexpected Status Code ${response.status}`);
        }
      })
      .then((data) => {
        if (!data) {
          // happy path
          updateAmenities(restroom);
          navigate("/profile");
        } else {
          //unhappy
          // get our errors messages and diplay them
          setErrors(data);
        }
      })
      .catch(console.log);
  };

  // Function to update amenities
  const updateAmenities = async (restroomArg) => {
    console.log(restroomArg)
    const token = localStorage.getItem("token");
    const currentAmenities = restroom.amenities || [];
    const currentAmenityIds = currentAmenities.map((amenity) => amenity.amenity.amenityId)
    console.log(currentAmenities)
    console.log(currentAmenityIds)
    // Get the amenities that need to be removed
    const amenitiesToRemove = amenities.filter(
      (amenity) => !currentAmenityIds.includes(amenity.amenityId)
    );
    console.log(amenitiesToRemove)
    try {
      // Add amenities
      for (const amenity of currentAmenities) {
        await fetch(restroomAmenitiesUrl, {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            restroomId: restroomArg.restroomId,
            amenity: amenity.amenity,
          }),
        });
      }
      if (restroom.restroomId) {
        // Remove amenities
        for (const amenity of amenitiesToRemove) {
          await fetch(`${restroomAmenitiesUrl}/${restroom.restroomId}/${amenity.amenityId}`, {
            method: "DELETE",
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
        }
      }
      setSuccess(true); // Set success if all amenity updates succeed
    } catch (error) {
      console.error("Error updating amenities:", error);
      setErrors(["Failed to update amenities. Please try again."]);
    }
  };

  const handleAmenityChange = (e) => {
    const { value, checked } = e.target;
    const amenityId = Number(value); // Convert value to a number

    // Find the selected amenity based on the amenityId
    const selectedAmenity = amenities.find((amenity) => amenity.amenityId === amenityId);

    setRestroom((prev) => {
      const updatedAmenities = checked
        ? [
            ...prev.amenities,
            { restroomId: prev.restroomId, amenity: selectedAmenity }, // Add the selected amenity object to the array
          ]
        : prev.amenities.filter(
            (amenity) => amenity.amenity.amenityId !== amenityId // Remove the selected amenity object if unchecked
          );

      return { ...prev, amenities: updatedAmenities };
    });
  };




  // Custom component to update map center
  const MapView = ({ position }) => {
    const map = useMap();
    useEffect(() => {
      if (position[0] && position[1]) {
        map.setView(position, map.getZoom());
      }
    }, [position, map]);
    return null;
  };

  return (
    <Container fluid>
      <Row>
        <Col md={6} className="p-4">
          <h2>{restroomId ? "Edit Restroom" : "Add Restroom"}</h2>
          {success && (
            <Alert variant="success">Restroom added successfully!</Alert>
          )}
          {errors.length > 0 && (
            <div className="alert alert-danger">
              <p>The following Errors were found:</p>
              <ul>
                {errors.map((error) => (
                  <li key={error}>{error}</li>
                ))}
              </ul>
            </div>
          )}
          <Form onSubmit={handleSubmit}>
            <Form.Group controlId="name">
              <Form.Label>Name</Form.Label>
              <Form.Control
                type="text"
                name="name"
                value={restroom.name}
                onChange={handleChange}
                required
              />
            </Form.Group>
            <Form.Group controlId="directions">
              <Form.Label>Directions</Form.Label>
              <Form.Control
                as="textarea"
                name="directions"
                value={restroom.directions}
                onChange={handleChange}
                rows={3}
              />
            </Form.Group>
            <Form.Group controlId="description">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                name="description"
                value={restroom.description}
                onChange={handleChange}
                rows={3}
              />
            </Form.Group>
            <Form.Group controlId="amenities">
              <Form.Label>Amenities</Form.Label>
              {amenities.map((amenity) => (
                <Form.Check
                  key={amenity.amenityId} // Use amenityId here
                  type="checkbox"
                  label={amenity.amenityName} // Use amenityName here
                  value={amenity.amenityId} // Use amenityId here
                  checked={restroom.amenities.some((a) => a.amenity.amenityId === amenity.amenityId)} // Check if the amenityId exists in restroom.amenities
                  onChange={handleAmenityChange}
                />
              ))}
            </Form.Group>
            <Form.Group controlId="submit" className="mt-3">
              <Button variant="primary" type="submit" className="mr-2">
                Submit
              </Button>
              <Link type="button" className="btn btn-outline-danger" to={"/"}>
                Cancel
              </Link>
            </Form.Group>
          </Form>
        </Col>
        <Col md={6} className="p-4">
          <h2>Location Preview</h2>
          <MapContainer
            center={position}
            zoom={13}
            style={{ height: "400px", width: "100%" }}
          >
            <MapView position={position} />
            <TileLayer
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              attribution='&copy; <a href="https://osm.org/copyright">OpenStreetMap</a> contributors'
            />
            <AddressSearch onAddressSelect={handleAddressSelect} />
            {position[0] && position[1] && (
              <Marker position={position}>
                <Popup>
                  {restroom.name || "Restroom"} <br /> {restroom.address}
                </Popup>
              </Marker>
            )}
          </MapContainer>
          <Form.Group controlId="manualEntry">
            <Form.Check
              type="checkbox"
              label="Enter location manually"
              checked={manualEntry}
              onChange={handleManualEntryToggle}
            />
          </Form.Group>
          {manualEntry && (
            <>
              <Form.Group controlId="address">
                <Form.Label>Address</Form.Label>
                <Form.Control
                  type="text"
                  name="address"
                  value={restroom.address}
                  onChange={handleChange}
                  required
                />
              </Form.Group>
              <Form.Group controlId="latitude">
                <Form.Label>Latitude</Form.Label>
                <Form.Control
                  type="number"
                  name="latitude"
                  value={restroom.latitude}
                  onChange={handleChange}
                  required
                />
              </Form.Group>
              <Form.Group controlId="longitude">
                <Form.Label>Longitude</Form.Label>
                <Form.Control
                  type="number"
                  name="longitude"
                  value={restroom.longitude}
                  onChange={handleChange}
                  required
                />
              </Form.Group>
            </>
          )}
        </Col>
      </Row>
    </Container>
  );
};

export default RestroomForm;
