import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import API from "../../axios";
import { TextField, Button, Switch } from "@mui/material";
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
