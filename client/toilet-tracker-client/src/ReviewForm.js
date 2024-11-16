import { useEffect, useState } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";
import { Form, Button, Container, Alert, Row, Col } from 'react-bootstrap';
import { jwtDecode } from "jwt-decode";

const REVIEW_DEFAULT = {
    rating: 1,
    reviewText: '',
    timeStamp: '',
    used: '',
    restroomId: null,
    userId: null
};

function ReviewForm() {
    const token = localStorage.getItem("token");
    let decodedToken;
    if (token) {
        decodedToken = jwtDecode(token);
    }

    const [errors, setErrors] = useState([]);
    const [success, setSuccess] = useState(false);
    const url = 'http://localhost:8080/api/review';
    const navigate = useNavigate();
    const { restroomId, reviewId } = useParams();
    const [review, setReview] = useState(REVIEW_DEFAULT);


    useEffect(() => {
        if (reviewId) {

            fetch(`${url}/${reviewId}`)
                .then(response => {
                    if (response.status === 200) {
                        return response.json();
                    } else {
                        return Promise.reject(`Unexpected Status code: ${response.status}`);
                    }
                })
                .then(data => {
                    setReview(data);
                })
                .catch(console.log);
        } else if (restroomId) {

            setReview(prevReview => ({
                ...prevReview,
                restroomId: parseInt(restroomId),
                userId: parseInt(decodedToken.appUserId)
            }));
        }
    }, [reviewId, restroomId, decodedToken?.appUserId]);

    // Handle form submission
    const handleSubmit = (event) => {
        event.preventDefault();
        setSuccess(false);
        if (reviewId) {
            updateReview();
        } else {
            addReview();
        }
    };

    const handleChange = (event) => {
        const newReview = { ...review };
        newReview[event.target.name] = event.target.value;
        setReview(newReview);
    };

    // Add a new review
    const addReview = () => {
        review.userId = parseInt(decodedToken.appUserId);
        const init = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                ...review,
                userId: parseInt(decodedToken.appUserId)
            }),
        };
        fetch(url, init)
            .then(response => {
                if (response.status === 201 || response.status === 400) {
                    return response.json();
                } else {
                    return Promise.reject(`Unexpected Status Code: ${response.status}`);
                }
            })
            .then(data => {
                if (data.reviewId) {
                    navigate(-1);
                } else {
                    setErrors(data);
                }
            })
            .catch(console.log);
    };

    // Update an existing review
    const updateReview = () => {
        review.reviewId = reviewId;
        review.userId = parseInt(decodedToken.appUserId);
        const init = {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({
                ...review,
                restroomId: parseInt(restroomId),
                userId: parseInt(decodedToken.appUserId)
            }),
        };
        fetch(`${url}/${restroomId}/${reviewId}`, init)
            .then(response => {
                if (response.status === 204) {
                    return null;
                } else if (response.status === 400) {
                    return response.json();
                } else {
                    return Promise.reject(`Unexpected Status Code ${response.status}`);
                }
            })
            .then(data => {
                if (!data) {
                    navigate(-1);
                    setSuccess(true);
                } else {
                    setErrors(data);
                }
            })
            .catch(console.log);
    };

    return (
        <Container fluid>
            <Row>
                <Col md={6} className="p-4">
                    <h2>{reviewId ? 'Update Review' : 'Add Review'}</h2>
                    {success && <Alert variant="success">Review added successfully!</Alert>}
                    {errors.length > 0 && (
                        <div className="alert alert-danger">
                            <p>The following Errors were found:</p>
                            <ul>
                                {errors.map(error => (
                                    <li key={error}>{error}</li>
                                ))}
                            </ul>
                        </div>
                    )}
                    <Form onSubmit={handleSubmit}>
                        <Form.Group controlId="name">
                            <Form.Label>Rating</Form.Label>
                            <Form.Control
                                type="number"
                                name="rating"
                                min={1}
                                max={5}
                                value={review.rating}
                                onChange={handleChange}
                                required
                            />
                        </Form.Group>
                        <Form.Group controlId="directions">
                            <Form.Label>Review Text</Form.Label>
                            <Form.Control
                                as="textarea"
                                name="reviewText"
                                value={review.reviewText}
                                onChange={handleChange}
                                rows={3}
                            />
                        </Form.Group>
                        <Form.Group controlId="description">
                            <Form.Label>Date Used</Form.Label>
                            <Form.Control
                                type="date"
                                name="used"
                                value={review.used}
                                onChange={handleChange}
                            />
                        </Form.Group>
                        <Form.Group controlId="submit" className="mt-3">
                            <Button variant="primary" type="submit" className="mr-2">
                                Submit
                            </Button>
                            <Link type="button" className="btn btn-outline-danger" to={-1}>Cancel</Link>
                        </Form.Group>
                    </Form>
                </Col>
            </Row>
        </Container>
    );
}

export default ReviewForm;
