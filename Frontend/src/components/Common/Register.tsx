import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../../axios.tsx";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Switch from "@mui/material/Switch";
import "./Common.css";
import axios from "axios";

const Register: React.FC = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isVendor, setIsVendor] = useState(true);
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState("");

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        const data = {
            username: username,
            pass: password,
        };
        const url = isVendor ? "/vendor" : "/customer";
        console.log("Sending params:", data); // Log the params being sent
        try {
            const response = await API.post(url, data);
            console.log("Response:", response.data);
            alert("Going back to login...");
            setTimeout(() => {
                navigate("/");
            }, 1500); // Wait for 2 seconds before redirecting
        } catch (error: unknown) {
            console.error("Error:", error);
            if (axios.isAxiosError(error) && error.response) {
                if (error.response.status === 409) {
                    setErrorMessage("Username already exists");
                }
            } else {
                setErrorMessage("An error occurred. Please try again.");
            }
        }
    };

    const handleBackToLogin = () => {
        navigate("/");
    };

    return (
        <form onSubmit={handleSubmit}>
            <h1>Register</h1>
            <div className="role-switch-container">
                <div className={isVendor ? "small" : "large"}>Customer</div>
                <div className="switch">
                    <Switch
                        color="default"
                        checked={isVendor}
                        onChange={(e) => setIsVendor(e.target.checked)}
                        name="roleSwitch"
                        inputProps={{ "aria-label": "role switch" }}
                    />
                </div>
                <div className={isVendor ? "large" : "small"}>Vendor</div>
            </div>
            <div style={{ marginBottom: 20 }}>
                <TextField
                    required={true}
                    id="username"
                    label="Username"
                    variant="outlined"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>
            <div style={{ marginBottom: 30 }}>
                <TextField
                    id="password"
                    required={true}
                    label="Password"
                    variant="outlined"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            {errorMessage && (
                <div style={{ marginBottom: 10, color: "red" }}>
                    {errorMessage}
                </div>
            )}
            <div style={{ marginBottom: 20 }}>
                <Button type="submit" variant="outlined" color="primary">
                    Submit
                </Button>
            </div>
            <div style={{ marginBottom: 20 }}>
                <Button
                    onClick={handleBackToLogin}
                    variant="text"
                    color="primary"
                >
                    Back to Login
                </Button>
            </div>
        </form>
    );
};

export default Register;
