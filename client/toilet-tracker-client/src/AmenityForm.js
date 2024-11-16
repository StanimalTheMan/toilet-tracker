import { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { Link } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";

function AmenityForm() {
  const [amenityName, setAmenityName] = useState("");
  const [success, setSuccess] = useState(false);
  const [errors, setErrors] = useState("");
  const navigate = useNavigate();

  const amenitiesUrl = "http://localhost:8080/api/amenity/admin";

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSuccess(false);
    setErrors("");

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
          amenityName: amenityName,
        }),
      };

      const response = await fetch(amenitiesUrl, init);

      // Check the response status after awaiting
      if (response.status === 201) {
        setSuccess(true);
        setAmenityName("");
        navigate("/amenities"); // Navigate after a successful response
      } else {
        const data = await response.json();
        setErrors(data || "Failed to add amenity. Please try again.");
      }
    } catch (err) {
      setErrors("Failed to add amenity. Please try again.");
    }
  };

  const handleChange = (e) => {
    const { value } = e.target;
    setAmenityName(value);
  };

  return (
    <>
      <Form onSubmit={handleSubmit}>
        <Form.Group controlId="name">
          <Form.Label>Amenity Name</Form.Label>
          <Form.Control
            type="text"
            name="name"
            value={amenityName}
            onChange={handleChange}
            required
          />
          {errors && <div className="text-danger">{errors}</div>}{" "}
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

      {success && (
        <div className="alert alert-success">Amenity added successfully!</div>
      )}
    </>
  );
}

export default AmenityForm;
