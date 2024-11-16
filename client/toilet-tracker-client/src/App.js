import { Navigate, Route, Routes } from "react-router-dom";
import { BrowserRouter as Router } from "react-router-dom";
import About from "./About";
import Login from "./Login";
import Register from "./Register";
import RestroomForm from "./RestroomForm";
import NavBar from "./NavBar";
import AmenityForm from "./AmenityForm";
import AmenityList from "./AmenityList";
import ReviewForm from "./ReviewForm";
import { Container } from "react-bootstrap";
import HomePage from "./Home";
import RestroomList from "./RestroomList";
import { jwtDecode } from "jwt-decode";
import ReviewPage from "./ReviewPage";
import UserProfile from "./UserProfile";
import RestroomReviews from "./RestroomReviews";
import ProtectedRoute from "./ProtectedRoute";

function App() {
  // Get the token from localStorage (or wherever it is stored)
  const token = localStorage.getItem("token");

  // Decode the token if it exists
  let role = null;
  let isAuthenticated = false;

  if (token) {
    try {
      const decodedToken = jwtDecode(token);
      role = decodedToken.authorities; // Assuming the authorities are stored under "authorities"
      isAuthenticated = true; // If token exists, the user is authenticated
    } catch (e) {
      console.error("Invalid token", e);
    }
  }

  return (
    <>
      <Router>
        <NavBar />
        <Container>
          <Routes>
            <Route path="/about" element={<About />} />
            <Route
              path="/register"
              element={
                <ProtectedRoute requireAuth={false} redirectTo="/">
                  <Register />
                </ProtectedRoute>
              }
            />
            <Route
              path="/login"
              element={
                <ProtectedRoute requireAuth={false} redirectTo="/">
                  <Login />
                </ProtectedRoute>
              }
            />
            <Route path="/" element={<HomePage />} />
            <Route
              path="/reviews/:restroomId/new"
              element={
                <ProtectedRoute requireAuth={true} redirectTo="/login">
                  <ReviewForm />
                </ProtectedRoute>
              }
            />
            <Route
              path="/reviews/:restroomId/:reviewId"
              element={
                <ProtectedRoute requireAuth={true} redirectTo="/login">
                  <ReviewForm />
                </ProtectedRoute>
              }
            />
            <Route
              path="/restrooms/new"
              element={
                <ProtectedRoute requireAuth={true} redirectTo="/login">
                  <RestroomForm />
                </ProtectedRoute>
              }
            />
            <Route path="/restrooms" element={<RestroomList />} />
            <Route path="/restroom/:restroomId" element={<RestroomReviews />} />
            <Route
              path="/profile"
              element={
                <ProtectedRoute requireAuth={true} redirectTo="/login">
                  <UserProfile type="user" />
                </ProtectedRoute>
              }
            />
            <Route
              path="/restrooms/edit/:restroomId"
              element={
                <ProtectedRoute requireAuth={true} redirectTo="/login">
                  <RestroomForm />
                </ProtectedRoute>
              }
            />
            <Route path="/amenities" element={<AmenityList />} />
            <Route
              path="/amenity/new"
              element={
                <ProtectedRoute requireAuth={true} redirectTo="/login">
                  {isAuthenticated && role?.includes("ROLE_ADMIN") ? (
                    <AmenityForm />
                  ) : (
                    <Navigate to="/" replace />
                  )}
                </ProtectedRoute>
              }
            />
          </Routes>
        </Container>
      </Router>
    </>
  );
}

export default App;
