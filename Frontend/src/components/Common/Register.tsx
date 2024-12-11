import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../../axios.tsx";
import { TextField, Button, Switch } from "@mui/material";
import "./Common.css";
import axios from "axios";

/**
 * Register component for user registration.
 * Allows users to register as either a vendor or a customer.
 */
const Register: React.FC = () => {
    // State to store the username input
    const [username, setUsername] = useState("");
    // State to store the password input
    const [password, setPassword] = useState("");
    // State to toggle between vendor and customer roles
    const [isVendor, setIsVendor] = useState(true);
    // State to store error messages
    const [errorMessage, setErrorMessage] = useState("");
    // Hook to navigate to different routes
    const navigate = useNavigate();

    /**
     * Handles the form submission for registration.
     * @param event - The form submission event.
     */
    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        const data = { username, pass: password };
        const url = isVendor ? "/vendor" : "/customer";
        try {
            await API.post(url, data);
            alert("Going back to login...");
            setTimeout(() => navigate("/"), 1500);
        } catch (error: unknown) {
            if (axios.isAxiosError(error) && error.response?.status === 409) {
                setErrorMessage("Username already exists");
            } else {
                setErrorMessage("An error occurred. Please try again.");
            }
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h1>Register</h1>
            <div className="role-switch-container">
                <div className={isVendor ? "small" : "large"}>Customer</div>
                <Switch
                    color="default"
                    checked={isVendor}
                    onChange={(e) => setIsVendor(e.target.checked)}
                    name="roleSwitch"
                    inputProps={{ "aria-label": "role switch" }}
                />
                <div className={isVendor ? "large" : "small"}>Vendor</div>
            </div>
            <div>
                <TextField
                    required
                    id="username"
                    label="Username"
                    variant="outlined"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    style={{ marginBottom: 20 }}
                />
            </div>
            <div>
                <TextField
                    required
                    id="password"
                    label="Password"
                    variant="outlined"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    style={{ marginBottom: 30 }}
                />
            </div>
            {errorMessage && (
                <div style={{ marginBottom: 10, color: "red" }}>
                    {errorMessage}
                </div>
            )}
            <div>
                <Button
                    type="submit"
                    variant="outlined"
                    color="primary"
                    style={{ marginBottom: 20 }}
                >
                    Submit
                </Button>
            </div>
            <div>
                <Button
                    onClick={() => navigate("/")}
                    variant="text"
                    color="primary"
                    style={{ marginBottom: 20 }}
                >
                    Back to Login
                </Button>
            </div>
        </form>
    );
};

export default Register;