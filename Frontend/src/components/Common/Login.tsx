import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import API from "../../axios";
import { TextField, Button, Switch } from "@mui/material";
import { UserContext } from "./UserContext";
import "./Common.css";

/**
 * Login component for user authentication.
 * Allows users to log in as either a vendor or a customer.
 */
const Login: React.FC = () => {
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
    // Get the user context
    const userContext = useContext(UserContext);

    /**
     * Handles the form submission for login.
     * @param event - The form submission event.
     */
    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        const params = { username, pass: password };
        const url = isVendor ? "/vendor" : "/customer";
        try {
            const { data } = await API.get(url, {
                params,
                headers: { "Content-Type": "application/json" },
            });
            userContext?.setUserData({
                username: data.username,
                userId: isVendor ? data.vendorId : data.customerId,
                isVendor,
            });
            navigate("/dashboard");
        } catch (error: unknown) {
            if (axios.isAxiosError(error) && error.response) {
                setErrorMessage(
                    error.response.status === 400
                        ? "The password is not correct"
                        : "Account not found"
                );
            } else {
                setErrorMessage("An error occurred. Please try again.");
            }
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h1>Login</h1>
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
                    onClick={() => navigate("/register")}
                    variant="text"
                    color="primary"
                    style={{ marginBottom: 20 }}
                >
                    Create Account
                </Button>
            </div>
        </form>
    );
};

export default Login;