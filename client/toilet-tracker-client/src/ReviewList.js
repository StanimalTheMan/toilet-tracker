import { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import { useNavigate, useParams, Link } from "react-router-dom";

function ReviewList({ reviews, type }) {
    const [reviewState, setReviews] = useState(reviews);

    useEffect(() => {
        setReviews(reviews);
    }, [reviews]);  // This will update reviewState whenever reviews changes

    const url = `http://localhost:8080/api/review`;
    const token = localStorage.getItem("token");
    let decodedToken;


    if (token) {
        try {
            decodedToken = jwtDecode(token);
        } catch (error) {
            console.error("Invalid token:", error);
        }
    }


    const handleDeleteReview = (reviewId) => {

        if (window.confirm(`Delete Review?`)) {
            const token = localStorage.getItem("token");
            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            };

            const init = {
                method: 'DELETE',
                headers: headers,
            };

            fetch(`${url}/${reviewId}`, init)
                .then(response => {
                    if (response.status === 204) {

                        const newReviews = reviewState.filter(r => r.reviewId !== reviewId);

                        setReviews(newReviews);
                    } else {
                        return Promise.reject(`Unexpected Status Code ${response.status}`);
                    }
                })
                .catch(console.log);
        }
    };



    const getRatingColor = (rating) => {
        if (rating >= 4) return 'green';
        if (rating >= 3) return 'yellow';
        if (rating >= 2) return 'orange';
        return 'red';
    };


    return (
        <section className="container">
            <h2 className="mb-4">Reviews</h2>

            {reviewState.length === 0 ? (
                <p>No reviews available.</p>
            ) : (
                reviewState.map(review => (
                    <div key={review.reviewId} className="container my-1 py-1">
                        <div className="row d-flex justify-content-left">
                            <div className="col-md-12 col-lg-10">
                                <div className="card text-body">
                                    <div className="card-body p-4">
                                        <div className="rating-badge" style={{ backgroundColor: getRatingColor(review.rating) }}>
                                            <span className="rating-number">{review.rating}</span>
                                        </div>


                                        <div className="d-flex flex-start">
                                            <img
                                                className="rounded-circle shadow-1-strong me-3"
                                                src="https://static.vecteezy.com/system/resources/previews/020/765/399/non_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg"
                                                alt="avatar"
                                                width="60"
                                                height="60"
                                            />
                                            <div>
                                                <h4 className="fw-bold mb-">{type === "user" ? review.locationName : review.username}</h4>
                                                <div className="d-flex align-items-center mb-3">
                                                    <p className="mb-0" style={{fontSize: '0.8rem'}}>Date Last Used: {review.used}</p>

                                                </div>
                                                <div style={{ position: 'absolute', top: '32px', right: '80px' }}><footer style={{fontSize: '0.8rem'}}>{new Date(review.timeStamp).toLocaleString('en-US', { month: 'short', day: 'numeric', year: 'numeric', hour: 'numeric', minute: 'numeric', hour12: true })}</footer></div>
                                                <p className="mb-3" style={{fontSize: '1.8rem'}}>{review.reviewText}</p>
                                                {decodedToken && review.userId === decodedToken.appUserId && (
                                                    <div style={{ position: 'absolute', bottom: '10px', right: '10px' }}>
                                                        <Link className="btn btn-outline-success mr-2"  to={`/reviews/${review.restroomId}/${review.reviewId}`}>Update</Link>
                                                        <button className="btn btn-outline-danger" onClick={() => handleDeleteReview(review.reviewId)}>Delete</button>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                ))
            )}
        </section>
    );
}


export default ReviewList;
