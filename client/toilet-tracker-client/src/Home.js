import React, { useState } from 'react';
import { Row, Col } from 'react-bootstrap';
import LocationSearchForm from './LocationSearchForm';
import { useLocation } from './context/LocationContext';

const HomePage = () => {
    const { updateLocation } = useLocation();

    const handleLocationChange = (latitude, longitude) => {
        updateLocation([latitude, longitude]); // Update map center
    };

    return (
        <div className="d-flex align-items-start justify-content-center" style={{ height: `calc(100vh - 60px)` }}>
            <Row className="justify-content-center w-100">
                <Col>
                    <div className="text-center">
                        <img src="/toilet_tracker-removebg-preview.png" alt="Logo" style={{ maxWidth: '500px', width:'100%', marginBottom: '20px' }} />
                        <h1 className='mb-3'>Toilet Tracker</h1>
                        <LocationSearchForm onLocationChange={handleLocationChange} />
                    </div>
                </Col>
            </Row>
        </div>
    )
};

export default HomePage;
