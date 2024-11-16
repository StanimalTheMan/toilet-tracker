import React, { createContext, useState, useContext } from "react";

const RestroomContext = createContext();

export const useRestrooms = () => {
  return useContext(RestroomContext);
};

export const RestroomProvider = ({ children }) => {
  const [restrooms, setRestrooms] = useState([]);

  const updateRestrooms = (data) => {
    setRestrooms(data);
  };

  return (
    <RestroomContext.Provider value={{ restrooms, updateRestrooms }}>
      {children}
    </RestroomContext.Provider>
  );
};
