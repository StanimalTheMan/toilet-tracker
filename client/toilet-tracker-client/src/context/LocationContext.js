import React, { createContext, useState, useContext } from "react";

const LocationContext = createContext();

export const useLocation = () => {
  return useContext(LocationContext);
};

export const LocationProvider = ({ children }) => {
  const [location, setLocations] = useState([51.505, -0.09]);

  const updateLocation = (data) => {
    setLocations(data);
  };

  return (
    <LocationContext.Provider value={{ location, updateLocation }}>
      {children}
    </LocationContext.Provider>
  );
};
