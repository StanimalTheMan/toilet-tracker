import React, { useState, useEffect } from "react";
import { Col, Row, Spinner } from "react-bootstrap";

function AmenityList() {
  const [amenities, setAmenities] = useState([]);
  const [loading, setLoading] = useState(true); // Loading state
  const amenitiesUrl = "http://localhost:8080/api/amenity";

  useEffect(() => {
    fetch(amenitiesUrl)
      .then((response) => response.json())
      .then((data) => {
        setAmenities(data); // Set amenities after fetching data
        setLoading(false); // Set loading to false after the data is fetched
      })
      .catch((error) => {
        console.error("Error fetching amenities:", error);
        setLoading(false); // Set loading to false in case of an error
      });
  }, []);

  return (
    <>
      <Row className="justify-content-center w-100 mb-4">
        <Col>
          <div className="d-flex justify-content-center align-items-center mb-3">
            <h2>Amenities</h2>
          </div>
        </Col>
      </Row>
      <section id="listContainer">
        {loading ? (
          <div className="d-flex justify-content-center align-items-center">
            <Spinner animation="border" role="status" />
            <span className="ml-2">Loading...</span>
          </div>
        ) : amenities.length === 0 ? (
          <p>No amenities found.</p>
        ) : (
          <table className="table table-striped">
            <thead>
              <tr className="table-dark">
                <th>Amenity Name</th>
              </tr>
            </thead>
            <tbody id="tableRows">
              {amenities.map((amenity) => (
                <tr key={amenity.amenityId}>
                  <td>{amenity.amenityName}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </>
  );
}

export default AmenityList;
