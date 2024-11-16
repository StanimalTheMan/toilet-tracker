// AddressSearch.js
import { useEffect } from 'react';
import { GeoSearchControl, OpenStreetMapProvider } from 'leaflet-geosearch';
import { useMap } from 'react-leaflet';
import 'leaflet-geosearch/dist/geosearch.css';

const AddressSearch = ({ onAddressSelect }) => {
    const map = useMap();

    useEffect(() => {
        const provider = new OpenStreetMapProvider();

        const searchControl = new GeoSearchControl({
            provider,
            style: 'bar',
            showMarker: false,
            showPopup: false,
            autoClose: true,
            retainZoomLevel: false,
            animateZoom: true,
            keepResult: true,
            searchLabel: 'Enter address'
        });

        map.addControl(searchControl);

        map.on('geosearch/showlocation', (result) => {
            const { x: longitude, y: latitude, label: address } = result.location;
            onAddressSelect({ latitude, longitude, address });
        });

        return () => map.removeControl(searchControl);
    }, [map, onAddressSelect]);

    return null;
};

export default AddressSearch;
