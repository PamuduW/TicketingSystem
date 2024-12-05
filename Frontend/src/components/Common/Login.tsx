import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import API from "../../axios";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Switch from "@mui/material/Switch";
import { UserContext } from "./UserContext";
import "./Common.css";

const Login: React.FC = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isVendor, setIsVendor] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const navigate = useNavigate();
    const userContext = useContext(UserContext);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        const params = {
            username: username,
            pass: password,
        };
        const url = isVendor ? "/vendor" : "/customer";
        console.log("Sending params:", params);
        try {
            const response = await API.get(url, {
                params: params,
                headers: {
                    "Content-Type": "application/json",
                },
            });
            console.log("Response:", response.data);
            if (userContext) {
                userContext.setUserData({
                    username: response.data.username,
                    userId: isVendor
                        ? response.data.vendorId
                        : response.data.customerId,
                    isVendor: isVendor,
                });
            }
            navigate("/dashboard");
        } catch (error: unknown) {
            console.error("Error:", error);
            if (axios.isAxiosError(error) && error.response) {
                if (error.response.status === 400) {
                    setErrorMessage("The password is not correct");
                } else if (error.response.status === 404) {
                    setErrorMessage("Account not found");
                }
            } else {
                setErrorMessage("An error occurred. Please try again.");
            }
        }
    };

    const handleCreateProfile = () => {
        navigate("/register");
    };

    return (
        <form onSubmit={handleSubmit}>
            <h1>Login</h1>
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
            <div style={{ paddingBottom: 10 }}>
                <TextField
                    required={true}
                    id="username"
                    label="Username"
                    variant="outlined"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>
            <div style={{ paddingBottom: 30 }}>
                <TextField
                    required={true}
                    id="password"
                    label="Password"
                    variant="outlined"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            {errorMessage && (
                <div style={{ paddingBottom: 10, color: "red" }}>
                    {errorMessage}
                </div>
            )}
            <div style={{ paddingBottom: 10 }}>
                <Button type="submit" variant="outlined" color="primary">
                    Submit
                </Button>
            </div>
            <div style={{ paddingBottom: 10 }}>
                <Button
                    onClick={handleCreateProfile}
                    variant="text"
                    color="primary"
                >
                    Create Account
                </Button>
            </div>
        </form>
    );
};

export default Login;
