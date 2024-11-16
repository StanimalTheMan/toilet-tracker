import React from "react";
import ReactDOM from "react-dom";
import { RestroomProvider } from "./context/RestroomContext"; // Adjust the path as needed
import App from "./App";
import { AuthProvider } from "./context/AuthContext";
import { LocationProvider } from "./context/LocationContext";

ReactDOM.render(
  <AuthProvider>
    <RestroomProvider>
      <LocationProvider>
        <App />
      </LocationProvider>
    </RestroomProvider>
  </AuthProvider>,
  document.getElementById("root")
);
