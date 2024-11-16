import { Container, Row, Col, ListGroup } from "react-bootstrap";
import { useParams, Link } from "react-router-dom";
import { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import ReviewPage from "./ReviewPage";
import './index.css';
import { useAuth } from "./context/AuthContext";

const RESTROOM_DEFAULT = {
    name: "",
    latitude: "",
    longitude: "",
    address: "",
    directions: "",
    description: "",
    userId: "",
    amenities: [],
    reviews: []
};

// Custom marker icon for Leaflet
const customIcon = new L.Icon({
    iconUrl: require("leaflet/dist/images/marker-icon.png"),
    iconSize: [25, 41],
    iconAnchor: [12, 41],
});

function RestroomReviews() {
    const { restroomId } = useParams();
    const [restroom, setRestroom] = useState(RESTROOM_DEFAULT);
    const [map, setMap] = useState(null); // Track the map instance
    const { token } = useAuth(); // Get role and token from context

    useEffect(() => {
        const fetchRestroom = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/restroom/${restroomId}`);
                if (response.ok) {
                    const data = await response.json();
                    setRestroom(data);
                } else {
                    console.error("Error fetching restroom data");
                }
            } catch (error) {
                console.error("Error fetching restroom:", error);
            }
        };
        fetchRestroom();
    }, [restroomId]);

    useEffect(() => {
        if (map && restroom.latitude && restroom.longitude) {
            // Update map view with new coordinates once data is fetched
            map.setView([restroom.latitude, restroom.longitude], 13);
        }
    }, [map, restroom.latitude, restroom.longitude]); // Trigger on map and coords change

    return (
        <Container fluid>
            <Row>
                <Col md={6} className="p-4">
                    <div className="bathroom-details mt-5">
                        <h3 className="bathroom-name">Bathroom Details</h3>
                        <div className="detail">
                            <span className="detail-label">Name: </span>
                            <span className="detail-value">{restroom.name}</span>
                        </div>
                        <div className="detail">
                            <span className="detail-label">Address: </span>
                            <span className="detail-value">{restroom.address}</span>
                        </div>
                        <div className="detail">
                            <span className="detail-label">Description: </span>
                            <span className="detail-value">{restroom.description}</span>
                        </div>
                        <div className="detail">
                            <span className="detail-label">Directions: </span>
                            <span className="detail-value">{restroom.directions}</span>
                        </div>
                    </div>
                    <div className="amenities">
                        <h5>Amenities:</h5>
                        {restroom.amenities.length > 0 ? (
                            <ListGroup variant="flush">
                                {restroom.amenities.map((item, index) => (
                                    <ListGroup.Item key={index} className="amenity-item">
                                        {item.amenity.amenityName}
                                    </ListGroup.Item>
                                ))}
                            </ListGroup>
                        ) : (
                            <p>No amenities available</p>
                        )}
                    </div>




                </Col>
                <Col md={6} className="p-4">

                    <h2>Location</h2>
                    {restroom.latitude && restroom.longitude ? (
                        <MapContainer
                            center={[restroom.latitude, restroom.longitude]}
                            zoom={13}
                            style={{ height: "400px", width: "100%" }}
                            whenCreated={setMap}  // Store map instance in state
                        >
                            <TileLayer
                                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                                attribution='&copy; <a href="https://osm.org/copyright">OpenStreetMap</a> contributors'
                            />
                            <Marker position={[restroom.latitude, restroom.longitude]} icon={customIcon}>
                                <Popup>
                                    {restroom.name || "Restroom"} <br /> {restroom.address}
                                </Popup>
                            </Marker>
                        </MapContainer>
                    ) : (
                        <p>Loading map...</p>
                    )}

                </Col>
            </Row>

            {token && (
                <Link className="btn btn-outline-success mb-4" to={`/reviews/${restroomId}/new`}>Add a Review</Link>
            )}

            <ReviewPage reviews={restroom.reviews}  type="restroom"/>
        </Container>
    );
}

export default RestroomReviews;
