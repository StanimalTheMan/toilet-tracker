import React from "react";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link } from "react-router-dom";
import { useAuth } from "./context/AuthContext";

function NavBar() {
  const { token, logout, role } = useAuth(); // Get role and token from context

  return (
    <Navbar expand="lg" className="bg-body-tertiary" style={{ height: `60px` }}>
      <Container>
        <Navbar.Brand as={Link} to={"/"}>
          <img
            src="/toilet_tracker-removebg-preview.png"
            alt="Brand Logo"
            height="40"
            className="d-inline-block align-top"
          />
          Toilet Tracker
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav" style={{zIndex:'10000', background:'white'}}>
          <Nav className="ml-auto">
            {token && (
              <Nav.Link as={Link} to={"/restrooms/new"}>
                Add New Restroom
              </Nav.Link>
            )}
            <Nav.Link as={Link} to={"/about"}>
              About
            </Nav.Link>

            {role === "ROLE_ADMIN" && (
              <Nav.Link as={Link} to={"/amenity/new"}>
                Add Amenity
              </Nav.Link>
            )}

            {token ? (
              <>
                <Nav.Link as={Link} to={"/profile"}>
                  Profile
                </Nav.Link>
                <Nav.Link onClick={logout}>Logout</Nav.Link>
              </>
            ) : (
              <>
                <Nav.Link as={Link} to={"/login"}>
                  Log In
                </Nav.Link>
                <Nav.Link as={Link} to={"/register"}>
                  Register
                </Nav.Link>
              </>
            )}
          </Nav>

        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default NavBar;
