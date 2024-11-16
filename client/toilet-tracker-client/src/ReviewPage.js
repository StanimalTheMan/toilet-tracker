import React from "react";
import ReviewList from "./ReviewList";

function ReviewPage({ reviews, type }) {
    return (
        <div>
            <ReviewList reviews={reviews} type={type}/>
        </div>
    );
}

export default ReviewPage;
