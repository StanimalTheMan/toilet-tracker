import React, { useEffect, useCallback } from "react";
import { Row, Col } from "react-bootstrap";
import LocationSearchForm from "./LocationSearchForm";
import { useRestrooms } from "./context/RestroomContext";
import { MapContainer, TileLayer, Marker, Popup, Circle } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { Link } from "react-router-dom";
import { useLocation } from "./context/LocationContext";

function RestroomList() {
    const { restrooms, updateRestrooms } = useRestrooms();
    const { location, updateLocation } = useLocation();

    const handleLocationChange = (latitude, longitude) => {
        updateLocation([latitude, longitude]); // Update map center
    };

    const stableUpdateRestrooms = useCallback(updateRestrooms, []);

    useEffect(() => {
        if (location) {
            const [latitude, longitude] = location;
            fetch(
                `http://localhost:8080/api/restroom/search?latitude=${latitude}&longitude=${longitude}`
            )
                .then((response) => response.json())
                .then((data) => {
                    stableUpdateRestrooms(data);
                })
                .catch((error) => {
                    alert("Error fetching restrooms data");
                    console.error(error);
                });
        }
    }, [location, stableUpdateRestrooms]);

    return (
        <>
            <Row className="justify-content-center w-100 mb-4">
                <Col>
                    <div className="d-flex justify-content-center align-items-center mb-3">
                        <h2>Restrooms</h2>
                    </div>
                    <LocationSearchForm onLocationChange={handleLocationChange} />
                </Col>
            </Row>

            <section id="listContainer">
                <div className="map-container" style={{ height: "400px" }}>
                    <MapContainer
                        center={location}
                        zoom={11}
                        style={{ height: "100%", width: "100%" }}
                        key={location.join(",")}
                    >
                        <TileLayer
                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                        />
                        {/* Circle with 5-mile radius */}
                        <Circle
                            center={location}
                            radius={8046.72} // 5 miles in meters
                            color="blue"
                            fillColor="blue"
                            fillOpacity={0.2}
                        />
                        {restrooms.map((restroom) =>
                            restroom.latitude && restroom.longitude ? (
                                <Marker
                                    key={restroom.restroomId}
                                    position={[restroom.latitude, restroom.longitude]}
                                    icon={new L.Icon({
                                        iconUrl: require("leaflet/dist/images/marker-icon.png"),
                                        iconSize: [25, 41],
                                        iconAnchor: [12, 41],
                                        popupAnchor: [1, -34],
                                        shadowSize: [41, 41],
                                    })}
                                >
                                    <Popup>
                                        <strong>{restroom.name}</strong><br />
                                        {restroom.address || "--"}
                                    </Popup>
                                </Marker>
                            ) : null
                        )}
                    </MapContainer>
                </div>

                {restrooms.length === 0 ? (
                    <p>No restrooms found.</p>
                ) : (
                    <div className="table-responsive mt-2">
                        {restrooms.map((restroom) => (
                            <div key={restroom.restroomId} className="restroom-row d-flex justify-content-between align-items-center mb-3 p-3 border rounded">
                                <div className="restroom-info">
                                    <h5 className="mb-1">{restroom.name}</h5>
                                    <p>{restroom.address || "--"}</p>
                                </div>
                                <Link
                                    className="btn btn-outline-primary mr-2"
                                    to={`/restroom/${restroom.restroomId}`}
                                >
                                    More Details
                                </Link>
                            </div>
                        ))}
                    </div>
                )}
            </section>
        </>
    );
}

export default RestroomList;
